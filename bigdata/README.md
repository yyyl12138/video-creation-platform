# 大数据平台看板（离线 + 实时）

## 架构

```
┌─────────────────────── 后端统一服务 (node1:5001) ───────────────────────┐
│                                                                         │
│  /api/offline/*   ──► PySpark ──► Hive (昨日离线指标)                   │
│                                                                         │
│  /api/realtime/*  ──► 3个Kafka消费线程 (user/task/payment_events)       │
│                       实时聚合 → 内存 → JSON 返回                       │
│                                                                         │
│  /api/health      ──► 健康检查                                          │
└─────────────────────────────────────────────────────────────────────────┘
                            │
                            ▼
┌──────────────── 前端 (localhost:3000) ──────────────────┐
│                                                         │
│  [📊 离线计算]  ←→ /api/offline/* (点击刷新)            │
│  [⚡ 实时计算]  ←→ /api/realtime/snapshot (3秒轮询)     │
│                                                         │
└─────────────────────────────────────────────────────────┘
```

## 启动
###  部署后端需部署集群才能运行
### JAVA-1.8,Mysql-8.0.36,Hadoop-3.3.0,Hive-3.1.2,Spark3.4.1,Flink-1.72.2,Kafka-3.4.1,Python-3.6.8
### 后端（node1）
```bash
cd backend
pip3 install -r requirements.txt
python3 server.py
```




