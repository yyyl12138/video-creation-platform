"""
====================================================
大数据平台 - 统一后端服务（离线 + 实时）
技术栈：Flask + PySpark(Hive) + Kafka Consumer
端口：5001（统一）

API 路由：
  /api/offline/*    - 离线计算（PySpark 查询 Hive）
  /api/realtime/*   - 实时计算（消费 Kafka CDC 数据）
  /api/health       - 健康检查

部署：在 node1 上运行 python3 server.py
====================================================
"""

from flask import Flask, jsonify
from flask_cors import CORS
from flask_socketio import SocketIO
from kafka import KafkaConsumer
import json
import threading
import time
from datetime import datetime, timedelta
from collections import deque

app = Flask(__name__)
CORS(app)
socketio = SocketIO(app, cors_allowed_origins="*", async_mode="threading")

# ============================================
# 全局配置
# ============================================
KAFKA_SERVERS = "node1:9092,node2:9092,node3:9092"
HIVE_METASTORE = "thrift://192.168.88.161:9083"
DB = "video_generation_platform"

# ============================================
# ========== 第一部分：离线计算（PySpark + Hive） ==========
# ============================================

_spark_lock = threading.Lock()
_spark = None

def get_spark():
    """懒加载 SparkSession 单例"""
    global _spark
    if _spark is None or _spark._jsc.sc().isStopped():
        with _spark_lock:
            if _spark is None or _spark._jsc.sc().isStopped():
                from pyspark.sql import SparkSession
                _spark = SparkSession.builder \
                    .appName("BigdataPlatform_API") \
                    .master("spark://node1:7077") \
                    .config("spark.sql.warehouse.dir", "/user/hive/warehouse") \
                    .config("hive.metastore.uris", HIVE_METASTORE) \
                    .config("spark.sql.hive.metastore.version", "3.1.2") \
                    .config("spark.sql.hive.metastore.jars", "/export/server/hive-3.1.2/lib/*") \
                    .config("spark.driver.memory", "1g") \
                    .config("spark.executor.memory", "1g") \
                    .enableHiveSupport() \
                    .getOrCreate()
                _spark.sql(f"USE {DB}")
                print(f"[Spark] 已连接 Hive: {HIVE_METASTORE}/{DB}")
    return _spark

def safe_val(v, default=0):
    return v if v is not None else default

def get_stat_date():
    """离线统计日期（昨天）"""
    return (datetime.now() - timedelta(days=1)).strftime("%Y-%m-%d")


# ---------- 离线 API ----------

@app.route("/api/offline/kpi", methods=["GET"])
def offline_kpi():
    spark = get_spark()
    stat_date = get_stat_date()
    tf = f"to_date(created_at) = '{stat_date}'"

    total_users = spark.sql("SELECT COUNT(*) AS cnt FROM users").collect()[0]["cnt"]
    dau = spark.sql(f"""
        SELECT COUNT(DISTINCT user_id) AS cnt FROM user_activity_logs
        WHERE is_deleted=0 AND to_date(activity_time)='{stat_date}'
    """).collect()[0]["cnt"]
    daily_tasks = spark.sql(f"""
        SELECT COUNT(*) AS cnt FROM ai_generation_tasks WHERE is_deleted=0 AND {tf}
    """).collect()[0]["cnt"]
    ss = spark.sql(f"""
        SELECT COUNT(CASE WHEN status=3 THEN 1 END) AS s,
               COUNT(CASE WHEN status IN(3,4) THEN 1 END) AS t
        FROM ai_generation_tasks WHERE is_deleted=0 AND {tf}
    """).collect()[0]
    sr = round(ss["s"] / ss["t"] * 100, 1) if ss["t"] > 0 else 0
    rev = spark.sql(f"""
        SELECT COALESCE(SUM(amount),0) AS t FROM wallet_transactions
        WHERE is_deleted=0 AND type=1 AND {tf}
    """).collect()[0]["t"]

    return jsonify({
        "stat_date": stat_date,
        "total_users": int(total_users), "dau": int(dau),
        "daily_tasks": int(daily_tasks), "success_rate": float(sr),
        "daily_revenue": float(safe_val(rev)),
    })

@app.route("/api/offline/users/trend", methods=["GET"])
def offline_users_trend():
    spark = get_spark()
    sd = get_stat_date()
    start = (datetime.strptime(sd, "%Y-%m-%d") - timedelta(days=13)).strftime("%Y-%m-%d")
    nu = spark.sql(f"""
        SELECT to_date(created_at) AS dt, COUNT(*) AS new_users FROM users
        WHERE to_date(created_at) BETWEEN '{start}' AND '{sd}'
        GROUP BY to_date(created_at) ORDER BY dt
    """).collect()
    da = spark.sql(f"""
        SELECT to_date(activity_time) AS dt, COUNT(DISTINCT user_id) AS dau
        FROM user_activity_logs WHERE is_deleted=0
        AND to_date(activity_time) BETWEEN '{start}' AND '{sd}'
        GROUP BY to_date(activity_time) ORDER BY dt
    """).collect()
    dm = {str(r["dt"]): r["dau"] for r in da}
    return jsonify([{"date": str(r["dt"])[5:], "newUsers": int(r["new_users"]), "dau": int(dm.get(str(r["dt"]), 0))} for r in nu])

@app.route("/api/offline/users/retention", methods=["GET"])
def offline_retention():
    spark = get_spark()
    sd = get_stat_date()
    active = spark.sql(f"SELECT DISTINCT user_id FROM user_activity_logs WHERE is_deleted=0 AND to_date(activity_time)='{sd}'")
    active.createOrReplaceTempView("active_today")
    result = []
    for days, label in [(1,"次日"),(3,"3日"),(7,"7日"),(14,"14日"),(30,"30日")]:
        rd = (datetime.strptime(sd,"%Y-%m-%d")-timedelta(days=days)).strftime("%Y-%m-%d")
        r = spark.sql(f"""
            SELECT COUNT(DISTINCT a.user_id) AS retained,
            (SELECT COUNT(*) FROM users WHERE to_date(created_at)='{rd}') AS total
            FROM active_today a JOIN users u ON a.user_id=u.id
            WHERE to_date(u.created_at)='{rd}'
        """).collect()[0]
        t = safe_val(r["total"])
        result.append({"name": label, "rate": round(r["retained"]/t*100,1) if t>0 else 0})
    return jsonify(result)

@app.route("/api/offline/tasks/overview", methods=["GET"])
def offline_tasks_overview():
    spark = get_spark()
    sd = get_stat_date()
    tf = f"to_date(created_at)='{sd}'"
    s = spark.sql(f"""
        SELECT COUNT(*) AS total,
        COUNT(CASE WHEN status=3 THEN 1 END) AS success,
        COUNT(CASE WHEN status IN(3,4) THEN 1 END) AS completed,
        COALESCE(AVG(CASE WHEN status=3 AND end_time IS NOT NULL
            THEN unix_timestamp(end_time)-unix_timestamp(created_at) END),0) AS avg_dur,
        COALESCE(SUM(cost_token),0) AS token
        FROM ai_generation_tasks WHERE is_deleted=0 AND {tf}
    """).collect()[0]
    dau = spark.sql(f"SELECT COUNT(DISTINCT user_id) AS c FROM user_activity_logs WHERE is_deleted=0 AND to_date(activity_time)='{sd}'").collect()[0]["c"]
    sr = round(s["success"]/s["completed"]*100,1) if s["completed"]>0 else 0
    return jsonify({
        "daily_tasks":int(s["total"]), "success_rate":float(sr),
        "avg_duration":round(float(safe_val(s["avg_dur"])),1),
        "tasks_per_user":round(s["total"]/dau,1) if dau>0 else 0,
        "total_token":float(safe_val(s["token"])),
    })

@app.route("/api/offline/tasks/type_distribution", methods=["GET"])
def offline_task_types():
    spark = get_spark()
    tm = {1:"文生文",2:"文生图",3:"文生视频",4:"图生视频"}
    cl = {1:"#6366f1",2:"#06b6d4",3:"#f59e0b",4:"#ec4899"}
    rows = spark.sql("SELECT task_type, COUNT(*) AS cnt FROM ai_generation_tasks WHERE is_deleted=0 GROUP BY task_type ORDER BY task_type").collect()
    return jsonify([{"name":tm.get(r["task_type"],str(r["task_type"])),"value":int(r["cnt"]),"color":cl.get(r["task_type"],"#94a3b8")} for r in rows])

@app.route("/api/offline/tasks/trend", methods=["GET"])
def offline_tasks_trend():
    spark = get_spark()
    sd = get_stat_date()
    start = (datetime.strptime(sd,"%Y-%m-%d")-timedelta(days=13)).strftime("%Y-%m-%d")
    rows = spark.sql(f"""
        SELECT to_date(created_at) AS dt, COUNT(*) AS tasks, COALESCE(SUM(cost_token),0) AS tokens
        FROM ai_generation_tasks WHERE is_deleted=0
        AND to_date(created_at) BETWEEN '{start}' AND '{sd}'
        GROUP BY to_date(created_at) ORDER BY dt
    """).collect()
    return jsonify([{"date":str(r["dt"])[5:],"tasks":int(r["tasks"]),"tokens":float(r["tokens"])} for r in rows])

@app.route("/api/offline/tasks/model_performance", methods=["GET"])
def offline_model_perf():
    spark = get_spark()
    rows = spark.sql("""
        SELECT m.model_name AS model,
        COUNT(CASE WHEN t.status=3 THEN 1 END) AS success,
        COUNT(CASE WHEN t.status IN(3,4) THEN 1 END) AS total,
        COALESCE(AVG(CASE WHEN t.status=3 AND t.end_time IS NOT NULL
            THEN unix_timestamp(t.end_time)-unix_timestamp(t.created_at) END),0) AS avg_time,
        COALESCE(SUM(t.cost_token),0) AS tokens
        FROM ai_generation_tasks t JOIN ai_models m ON t.model_id=m.id
        WHERE t.is_deleted=0 GROUP BY m.model_name ORDER BY success DESC
    """).collect()
    return jsonify([{
        "model":r["model"],
        "success":round(r["success"]/r["total"]*100,1) if r["total"]>0 else 0,
        "avgTime":round(float(r["avg_time"]),1),
        "tokens":float(r["tokens"]),
    } for r in rows])

@app.route("/api/offline/revenue/overview", methods=["GET"])
def offline_revenue():
    spark = get_spark()
    sd = get_stat_date()
    tf = f"to_date(created_at)='{sd}'"
    daily = spark.sql(f"SELECT COALESCE(SUM(amount),0) AS t FROM wallet_transactions WHERE is_deleted=0 AND type=1 AND {tf}").collect()[0]["t"]
    monthly = spark.sql(f"SELECT COALESCE(SUM(amount),0) AS t FROM wallet_transactions WHERE is_deleted=0 AND type=1 AND date_format(created_at,'yyyy-MM')='{sd[:7]}'").collect()[0]["t"]
    paying = spark.sql("SELECT COUNT(DISTINCT w.user_id) AS c FROM wallet_transactions t JOIN user_wallets w ON t.wallet_id=w.id WHERE t.is_deleted=0 AND t.type=1").collect()[0]["c"]
    tu = spark.sql("SELECT COUNT(*) AS c FROM users").collect()[0]["c"]
    return jsonify({
        "daily_revenue":float(safe_val(daily)), "monthly_revenue":float(safe_val(monthly)),
        "paying_users":int(paying), "pay_rate":round(paying/tu*100,1) if tu>0 else 0,
        "arppu":round(float(safe_val(monthly))/paying,1) if paying>0 else 0, "total_users":int(tu),
    })

@app.route("/api/offline/revenue/trend", methods=["GET"])
def offline_revenue_trend():
    spark = get_spark()
    sd = get_stat_date()
    start = (datetime.strptime(sd,"%Y-%m-%d")-timedelta(days=13)).strftime("%Y-%m-%d")
    rows = spark.sql(f"""
        SELECT to_date(created_at) AS dt, COALESCE(SUM(amount),0) AS revenue
        FROM wallet_transactions WHERE is_deleted=0 AND type=1
        AND to_date(created_at) BETWEEN '{start}' AND '{sd}'
        GROUP BY to_date(created_at) ORDER BY dt
    """).collect()
    return jsonify([{"date":str(r["dt"])[5:],"revenue":float(r["revenue"])} for r in rows])

@app.route("/api/offline/materials/overview", methods=["GET"])
def offline_materials():
    spark = get_spark()
    img = spark.sql("SELECT COUNT(*) AS c FROM image_materials WHERE is_deleted=0").collect()[0]["c"]
    vid = spark.sql("SELECT COUNT(*) AS c FROM video_materials WHERE is_deleted=0").collect()[0]["c"]
    aud = spark.sql("SELECT COUNT(*) AS c FROM audio_materials WHERE is_deleted=0").collect()[0]["c"]
    total = img+vid+aud
    ai_img = spark.sql("SELECT COUNT(*) AS c FROM image_materials WHERE is_deleted=0 AND source_type=3").collect()[0]["c"]
    ai_vid = spark.sql("SELECT COUNT(*) AS c FROM video_materials WHERE is_deleted=0 AND source_type=3").collect()[0]["c"]
    return jsonify({
        "total":int(total),
        "distribution":[
            {"name":"图片","value":int(img),"color":"#818cf8"},
            {"name":"视频","value":int(vid),"color":"#34d399"},
            {"name":"音频","value":int(aud),"color":"#fb923c"},
        ],
        "ai_ratio":round((ai_img+ai_vid)/total*100,1) if total>0 else 0,
    })


# ============================================
# ========== 第二部分：实时计算（Kafka Consumer） ==========
# ============================================

TASK_TYPE_MAP = {1:"文生文",2:"文生图",3:"文生视频",4:"图生视频"}
TASK_STATUS_MAP = {1:"等待中",2:"处理中",3:"成功",4:"失败",5:"取消"}
MODEL_MAP = {2:"Qwen Flash",3:"GLM Flash",4:"Wanx",6:"Minimax",7:"Doubao Seedance"}

rt = {
    "total_users":0, "active_users":set(), "new_users_today":0, "users_seen":set(),
    "total_tasks":0, "tasks_waiting":0, "tasks_running":0,
    "tasks_success":0, "tasks_failed":0, "tasks_cancelled":0,
    "total_token":0.0, "total_duration":0.0, "success_dur_count":0,
    "task_by_type":{}, "task_by_model":{}, "task_ids":set(),
    "total_recharge":0.0, "total_consume":0.0, "recharge_count":0, "consume_count":0, "pay_ids":set(),
    "last_update":"", "start_time":datetime.now().strftime("%Y-%m-%d %H:%M:%S"),
}
recent_events = deque(maxlen=100)
minute_stats = deque(maxlen=60)
_cm = {"minute":"","tasks":0,"success":0,"failed":0,"token":0.0,"recharge":0.0,"consume":0.0,"new_users":0}
rt_lock = threading.Lock()

def cur_min(): return datetime.now().strftime("%H:%M")
def flush_min():
    global _cm
    nm = cur_min()
    if _cm["minute"] and _cm["minute"] != nm:
        minute_stats.append(dict(_cm))
        _cm = {"minute":nm,"tasks":0,"success":0,"failed":0,"token":0.0,"recharge":0.0,"consume":0.0,"new_users":0}
    elif not _cm["minute"]:
        _cm["minute"] = nm

def add_evt(t, s):
    recent_events.appendleft({"type":t,"time":datetime.now().strftime("%Y-%m-%d %H:%M:%S"),"summary":s})

def handle_user(data):
    with rt_lock:
        flush_min()
        uid = data.get("id","")
        if uid and uid not in rt["users_seen"]:
            rt["users_seen"].add(uid); rt["total_users"]+=1; rt["new_users_today"]+=1; _cm["new_users"]+=1
            add_evt("user", f"👤 新用户: {data.get('username','')} ({uid})")
        if uid and data.get("status")==1: rt["active_users"].add(uid)
        rt["last_update"] = datetime.now().strftime("%Y-%m-%d %H:%M:%S")

def handle_task(data):
    with rt_lock:
        flush_min()
        tid = data.get("id","")
        if not tid: return
        uid = data.get("user_id",""); tt = data.get("task_type"); st = data.get("status")
        mid = data.get("model_id"); tok = float(data.get("cost_token") or 0)
        tn = TASK_TYPE_MAP.get(tt,f"类型{tt}"); mn = MODEL_MAP.get(mid,f"模型{mid}"); sn = TASK_STATUS_MAP.get(st,"")
        is_new = tid not in rt["task_ids"]; rt["task_ids"].add(tid)
        if is_new:
            rt["total_tasks"]+=1; _cm["tasks"]+=1
            rt["task_by_type"][tn] = rt["task_by_type"].get(tn,0)+1
            if mn: rt["task_by_model"][mn] = rt["task_by_model"].get(mn,0)+1
        if st==1: rt["tasks_waiting"]+=1
        elif st==2: rt["tasks_running"]+=1
        elif st==3:
            rt["tasks_success"]+=1; _cm["success"]+=1; rt["total_token"]+=tok; _cm["token"]+=tok
            ca=data.get("created_at"); et=data.get("end_time")
            if et and ca:
                try:
                    d=(datetime.strptime(et,"%Y-%m-%d %H:%M:%S")-datetime.strptime(ca,"%Y-%m-%d %H:%M:%S")).total_seconds()
                    if d>0: rt["total_duration"]+=d; rt["success_dur_count"]+=1
                except: pass
        elif st==4: rt["tasks_failed"]+=1; _cm["failed"]+=1
        elif st==5: rt["tasks_cancelled"]+=1
        if uid: rt["active_users"].add(uid)
        add_evt("task", f"⚡ {tn} [{sn}] {uid} | {mn} | Token:{tok}")
        rt["last_update"] = datetime.now().strftime("%Y-%m-%d %H:%M:%S")

def handle_payment(data):
    with rt_lock:
        flush_min()
        pid = data.get("id","")
        if pid in rt["pay_ids"]: return
        rt["pay_ids"].add(pid)
        pt = data.get("pay_type"); amt = float(data.get("amount") or 0)
        if pt==1: rt["total_recharge"]+=amt; rt["recharge_count"]+=1; _cm["recharge"]+=amt; add_evt("payment",f"💰 充值 +¥{amt:.2f}")
        elif pt==2: rt["total_consume"]+=amt; rt["consume_count"]+=1; _cm["consume"]+=amt; add_evt("payment",f"🔥 消费 -¥{amt:.2f}")
        elif pt==3: rt["total_recharge"]-=amt; add_evt("payment",f"↩️ 退款 ¥{amt:.2f}")
        rt["last_update"] = datetime.now().strftime("%Y-%m-%d %H:%M:%S")

def kafka_thread(topic, handler):
    print(f"[Kafka] 启动消费: {topic}")
    while True:
        try:
            consumer = KafkaConsumer(topic, bootstrap_servers=KAFKA_SERVERS,
                group_id=f"bigdata-{topic}", auto_offset_reset="latest",
                value_deserializer=lambda v: json.loads(v.decode("utf-8")) if v else None,
                consumer_timeout_ms=3000, session_timeout_ms=30000)
            print(f"[Kafka] 已连接: {topic}")
            while True:
                recs = consumer.poll(timeout_ms=2000)
                for tp, msgs in recs.items():
                    for m in msgs:
                        if m.value:
                            try: handler(m.value)
                            except Exception as e: print(f"[Err] {topic}: {e}")
                socketio.emit("realtime_update", get_rt_snapshot())
        except Exception as e:
            print(f"[Kafka Err] {topic}: {e}, 5s后重连..."); time.sleep(5)

def get_rt_snapshot():
    with rt_lock:
        flush_min()
        s=rt["tasks_success"]; f=rt["tasks_failed"]
        return {
            "total_users":rt["total_users"], "online_users":len(rt["active_users"]),
            "new_users_today":rt["new_users_today"],
            "total_tasks":rt["total_tasks"], "tasks_waiting":rt["tasks_waiting"],
            "tasks_running":rt["tasks_running"], "tasks_success":s,
            "tasks_failed":f, "tasks_cancelled":rt["tasks_cancelled"],
            "success_rate":round(s/(s+f)*100,1) if (s+f)>0 else 0,
            "avg_duration":round(rt["total_duration"]/rt["success_dur_count"],1) if rt["success_dur_count"]>0 else 0,
            "total_token":round(rt["total_token"],2),
            "task_by_type":dict(rt["task_by_type"]), "task_by_model":dict(rt["task_by_model"]),
            "total_recharge":round(rt["total_recharge"],2), "total_consume":round(rt["total_consume"],2),
            "recharge_count":rt["recharge_count"], "consume_count":rt["consume_count"],
            "last_update":rt["last_update"], "start_time":rt["start_time"],
            "recent_events":list(recent_events)[:30], "trend_minutes":list(minute_stats)[-30:],
        }

# ---------- 实时 API ----------

@app.route("/api/realtime/snapshot", methods=["GET"])
def rt_snapshot():
    return jsonify(get_rt_snapshot())

# ---------- WebSocket ----------

@socketio.on("connect")
def ws_connect():
    socketio.emit("realtime_update", get_rt_snapshot())

@socketio.on("disconnect")
def ws_disconnect():
    pass

# ---------- 健康检查 ----------

@app.route("/api/health", methods=["GET"])
def health():
    return jsonify({"status":"ok","kafka":KAFKA_SERVERS,"hive":f"{HIVE_METASTORE}/{DB}",
        "time":datetime.now().strftime("%Y-%m-%d %H:%M:%S")})


# ============================================
# 启动
# ============================================

if __name__ == "__main__":
    print("="*60)
    print("大数据平台 - 统一后端服务")
    print(f"Hive:  {HIVE_METASTORE}/{DB}")
    print(f"Kafka: {KAFKA_SERVERS}")
    print("离线API: http://192.168.88.161:5000/api/offline/*")
    print("实时API: http://192.168.88.161:5000/api/realtime/snapshot")
    print("WebSocket: ws://192.168.88.161:5000")
    print("="*60)

    # 预热 Spark（后台线程，避免阻塞Kafka消费）
    threading.Thread(target=get_spark, daemon=True).start()

    # 启动 Kafka 消费者
    for topic, handler in [("user_events",handle_user),("task_events",handle_task),("payment_events",handle_payment)]:
        threading.Thread(target=kafka_thread, args=(topic,handler), daemon=True).start()

    socketio.run(app, host="192.168.88.161", port=5000, debug=False, allow_unsafe_werkzeug=True)
