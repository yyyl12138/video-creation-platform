-- ============================================
-- 自动化短视频创作平台 - Hive 建表语句
-- 基于 MySQL dump 自动转换生成
-- 数据库: video_generation_platform
-- 源文件: 2026_02_04_11_26_00-dump.sql
-- 共 29 张表
-- ============================================

CREATE DATABASE IF NOT EXISTS video_generation_platform;
USE video_generation_platform;

-- --------------------------------------------
-- ai生成任务表（ai_generation_tasks）
-- --------------------------------------------
DROP TABLE IF EXISTS ai_generation_tasks;
CREATE TABLE IF NOT EXISTS ai_generation_tasks (
    id                             STRING               COMMENT '主键ID(业务任务ID)',
    user_id                        STRING               COMMENT '用户ID',
    task_type                      TINYINT              COMMENT '任务类型: 1-文生文, 2-文生图, 3-文生视频, 4-文加图生视频',
    template_id                    STRING               COMMENT '关联模板ID',
    prompt                         STRING               COMMENT '用户提示词',
    negative_prompt                STRING               COMMENT '负面提示词',
    input_config                   STRING               COMMENT '输入配置',
    output_config                  STRING               COMMENT '输出配置',
    status                         TINYINT              COMMENT '状态: 1-等待中, 2-处理中, 3-成功, 4-失败, 5-取消',
    created_at                     TIMESTAMP            COMMENT '创建时间/开始时间',
    end_time                       TIMESTAMP            COMMENT '结束时间',
    result_file_path               STRING               COMMENT '结果文件路径(图片/视频)',
    result_cover_path              STRING               COMMENT '结果封面图路径',
    result_file_size               BIGINT               COMMENT '结果文件大小',
    error_message                  STRING               COMMENT '错误信息',
    retry_count                    INT                  COMMENT '重试次数',
    priority                       INT                  COMMENT '优先级',
    model_id                       BIGINT               COMMENT '模型ID',
    external_task_id               STRING               COMMENT '第三方任务ID',
    cost_token                     DOUBLE        COMMENT '消耗Token',
    is_deleted                     BOOLEAN              COMMENT '逻辑删除标识',
    updated_at                     TIMESTAMP            COMMENT '更新时间'
)
COMMENT 'ai生成任务表'
ROW FORMAT DELIMITED FIELDS TERMINATED BY ','
STORED AS ORC
TBLPROPERTIES ('orc.compress'='SNAPPY');

-- --------------------------------------------
-- AI模型表（ai_models）
-- --------------------------------------------
DROP TABLE IF EXISTS ai_models;
CREATE TABLE IF NOT EXISTS ai_models (
    id                             BIGINT               COMMENT '主键ID',
    model_name                     STRING               COMMENT '模型名称',
    model_key                      STRING               COMMENT '模型标识',
    provider                       STRING               COMMENT '提供商',
    model_type                     STRING               COMMENT '类型: TEXT, IMAGE等',
    api_endpoint                   STRING               COMMENT '接口地址',
    api_config                     STRING               COMMENT 'API配置',
    billing_mode                   TINYINT              COMMENT '计费: 1-按次, 2-Token',
    unit_price                     DOUBLE        COMMENT '单价',
    is_active                      BOOLEAN              COMMENT '是否启用',
    created_at                     TIMESTAMP            COMMENT '创建时间',
    updated_at                     TIMESTAMP            COMMENT '更新时间',
    is_deleted                     BOOLEAN              COMMENT '逻辑删除标识'
)
COMMENT 'AI模型表'
ROW FORMAT DELIMITED FIELDS TERMINATED BY ','
STORED AS ORC
TBLPROPERTIES ('orc.compress'='SNAPPY');

-- --------------------------------------------
-- 音频素材表（audio_materials）
-- --------------------------------------------
DROP TABLE IF EXISTS audio_materials;
CREATE TABLE IF NOT EXISTS audio_materials (
    id                             STRING               COMMENT '主键ID(业务音频ID)',
    audio_name                     STRING               COMMENT '音频名称',
    type                           STRING               COMMENT '类型',
    duration_seconds               DOUBLE        COMMENT '时长(秒)',
    file_size                      BIGINT               COMMENT '文件大小',
    file_path                      STRING               COMMENT '文件路径',
    bitrate                        INT                  COMMENT '比特率',
    sample_rate                    STRING               COMMENT '采样率',
    uploader_id                    STRING               COMMENT '上传者ID',
    status                         TINYINT              COMMENT '状态: 1-正常, 2-封禁, 3-禁用',
    copyright_status               TINYINT              COMMENT '版权: 1-免费商用, 2-付费授权, 3-个人使用',
    tags                           STRING               COMMENT '标签',
    description                    STRING               COMMENT '描述',
    created_at                     TIMESTAMP            COMMENT '创建时间',
    updated_at                     TIMESTAMP            COMMENT '更新时间',
    is_deleted                     BOOLEAN              COMMENT '逻辑删除标识'
)
COMMENT '音频素材表'
ROW FORMAT DELIMITED FIELDS TERMINATED BY ','
STORED AS ORC
TBLPROPERTIES ('orc.compress'='SNAPPY');

-- --------------------------------------------
-- 内容审核表（content_reviews）
-- --------------------------------------------
DROP TABLE IF EXISTS content_reviews;
CREATE TABLE IF NOT EXISTS content_reviews (
    id                             BIGINT               COMMENT '主键ID',
    content_id                     STRING               COMMENT '内容ID(UGC表ID)',
    reviewer_id                    STRING               COMMENT '审核员ID(User表ID)',
    review_status                  TINYINT              COMMENT '状态: 1-待审核, 2-通过, 3-驳回, 4-复审',
    review_time                    TIMESTAMP            COMMENT '审核时间',
    reject_reason                  STRING               COMMENT '驳回原因',
    suggestions                    STRING               COMMENT '建议',
    next_review_time               TIMESTAMP            COMMENT '下次审核时间',
    version                        INT                  COMMENT '版本号',
    created_at                     TIMESTAMP            COMMENT '创建时间',
    updated_at                     TIMESTAMP            COMMENT '更新时间',
    is_deleted                     BOOLEAN              COMMENT '逻辑删除标识'
)
COMMENT '内容审核表'
ROW FORMAT DELIMITED FIELDS TERMINATED BY ','
STORED AS ORC
TBLPROPERTIES ('orc.compress'='SNAPPY');

-- --------------------------------------------
-- 创作者信息表（creators）
-- --------------------------------------------
DROP TABLE IF EXISTS creators;
CREATE TABLE IF NOT EXISTS creators (
    id                             STRING               COMMENT '主键ID(业务创作者ID)',
    user_id                        STRING               COMMENT '用户ID',
    platform_account               STRING               COMMENT '平台账号',
    platform_type                  STRING               COMMENT '平台类型',
    follower_count                 INT                  COMMENT '粉丝数',
    verification_status            TINYINT              COMMENT '认证状态: 1-未认证, 2-已认证, 3-认证中',
    bio                            STRING               COMMENT '简介',
    contact_info                   STRING               COMMENT '联系信息',
    created_at                     TIMESTAMP            COMMENT '创建时间',
    updated_at                     TIMESTAMP            COMMENT '更新时间',
    is_deleted                     BOOLEAN              COMMENT '逻辑删除标识'
)
COMMENT '创作者信息表'
ROW FORMAT DELIMITED FIELDS TERMINATED BY ','
STORED AS ORC
TBLPROPERTIES ('orc.compress'='SNAPPY');

-- --------------------------------------------
-- 每日统计表（daily_statistics）
-- --------------------------------------------
DROP TABLE IF EXISTS daily_statistics;
CREATE TABLE IF NOT EXISTS daily_statistics (
    stat_date                      DATE                 COMMENT '统计日期',
    total_users                    INT                  COMMENT '总用户数',
    new_users                      INT                  COMMENT '新增用户数',
    active_users                   INT                  COMMENT '活跃用户数',
    total_video_generated          INT                  COMMENT '生成视频总数',
    successful_tasks               INT                  COMMENT '成功任务数',
    failed_tasks                   INT                  COMMENT '失败任务数',
    task_success_rate              DOUBLE         COMMENT '成功率',
    total_storage_used             BIGINT               COMMENT '存储使用量',
    peak_concurrent_tasks          INT                  COMMENT '峰值并发',
    created_at                     TIMESTAMP            COMMENT '创建时间',
    updated_at                     TIMESTAMP            COMMENT '更新时间',
    is_deleted                     BOOLEAN              COMMENT '逻辑删除标识'
)
COMMENT '每日统计表'
ROW FORMAT DELIMITED FIELDS TERMINATED BY ','
STORED AS ORC
TBLPROPERTIES ('orc.compress'='SNAPPY');

-- --------------------------------------------
-- 财务统计表（financial_statistics）
-- --------------------------------------------
DROP TABLE IF EXISTS financial_statistics;
CREATE TABLE IF NOT EXISTS financial_statistics (
    id                             BIGINT               COMMENT '主键ID',
    stat_period                    STRING               COMMENT '周期: daily, weekly',
    stat_date                      DATE                 COMMENT '统计日期',
    total_revenue                  DOUBLE        COMMENT '总营收',
    total_expense                  DOUBLE        COMMENT '总支出',
    user_count                     INT                  COMMENT '用户数',
    premium_user_count             INT                  COMMENT '付费用户数',
    task_revenue                   DOUBLE        COMMENT '任务营收',
    storage_revenue                DOUBLE        COMMENT '存储营收',
    created_at                     TIMESTAMP            COMMENT '创建时间',
    updated_at                     TIMESTAMP            COMMENT '更新时间',
    is_deleted                     BOOLEAN              COMMENT '逻辑删除标识'
)
COMMENT '财务统计表'
ROW FORMAT DELIMITED FIELDS TERMINATED BY ','
STORED AS ORC
TBLPROPERTIES ('orc.compress'='SNAPPY');

-- --------------------------------------------
-- 图片素材表（image_materials）
-- --------------------------------------------
DROP TABLE IF EXISTS image_materials;
CREATE TABLE IF NOT EXISTS image_materials (
    id                             STRING               COMMENT '主键ID(业务素材ID)',
    image_name                     STRING               COMMENT '图片名称',
    category                       STRING               COMMENT '分类',
    file_path                      STRING               COMMENT '文件路径',
    file_size                      BIGINT               COMMENT '文件大小(字节)',
    resolution                     STRING               COMMENT '分辨率',
    format                         STRING               COMMENT '格式',
    uploader_id                    STRING               COMMENT '上传者ID',
    status                         TINYINT              COMMENT '状态: 1-正常, 2-封禁, 3-禁用',
    copyright_status               TINYINT              COMMENT '版权: 1-免费商用, 2-付费授权, 3-个人使用',
    tags                           STRING               COMMENT '标签',
    description                    STRING               COMMENT '描述',
    source_type                    TINYINT              COMMENT '来源: 1-官方, 2-用户上传, 3-AI生成',
    is_public                      BOOLEAN              COMMENT '是否公开',
    created_at                     TIMESTAMP            COMMENT '创建时间',
    updated_at                     TIMESTAMP            COMMENT '更新时间',
    is_deleted                     BOOLEAN              COMMENT '逻辑删除标识'
)
COMMENT '图片素材表'
ROW FORMAT DELIMITED FIELDS TERMINATED BY ','
STORED AS ORC
TBLPROPERTIES ('orc.compress'='SNAPPY');

-- --------------------------------------------
-- 系统操作日志表（operation_logs）
-- --------------------------------------------
DROP TABLE IF EXISTS operation_logs;
CREATE TABLE IF NOT EXISTS operation_logs (
    id                             BIGINT               COMMENT '主键ID',
    user_id                        STRING               COMMENT '用户ID',
    operation_type                 STRING               COMMENT '操作类型',
    operation_target               STRING               COMMENT '操作对象',
    target_id                      STRING               COMMENT '对象ID',
    operation_time                 TIMESTAMP            COMMENT '操作时间',
    ip_address                     STRING               COMMENT 'IP地址',
    user_agent                     STRING               COMMENT 'UA信息',
    request_method                 STRING               COMMENT '请求方法',
    request_path                   STRING               COMMENT '请求路径',
    request_params                 STRING               COMMENT '请求参数',
    response_code                  INT                  COMMENT '响应码',
    response_time_ms               INT                  COMMENT '响应耗时',
    error_message                  STRING               COMMENT '错误信息',
    additional_info                STRING               COMMENT '额外信息',
    created_at                     TIMESTAMP            COMMENT '创建时间',
    updated_at                     TIMESTAMP            COMMENT '更新时间',
    is_deleted                     BOOLEAN              COMMENT '逻辑删除标识'
)
COMMENT '系统操作日志表'
ROW FORMAT DELIMITED FIELDS TERMINATED BY ','
STORED AS ORC
TBLPROPERTIES ('orc.compress'='SNAPPY');

-- --------------------------------------------
-- 权限表（permissions）
-- --------------------------------------------
DROP TABLE IF EXISTS permissions;
CREATE TABLE IF NOT EXISTS permissions (
    id                             BIGINT               COMMENT '主键ID',
    permission_code                STRING               COMMENT '权限编码，唯一',
    permission_name                STRING               COMMENT '权限名称',
    category                       STRING               COMMENT '权限分类',
    description                    STRING               COMMENT '权限描述',
    created_at                     TIMESTAMP            COMMENT '创建时间',
    updated_at                     TIMESTAMP            COMMENT '更新时间',
    is_deleted                     BOOLEAN              COMMENT '逻辑删除标识'
)
COMMENT '权限表'
ROW FORMAT DELIMITED FIELDS TERMINATED BY ','
STORED AS ORC
TBLPROPERTIES ('orc.compress'='SNAPPY');

-- --------------------------------------------
-- 审核规则表（review_rules）
-- --------------------------------------------
DROP TABLE IF EXISTS review_rules;
CREATE TABLE IF NOT EXISTS review_rules (
    id                             BIGINT               COMMENT '主键ID',
    rule_name                      STRING               COMMENT '规则名称',
    rule_type                      STRING               COMMENT '规则类型',
    conditions                     STRING               COMMENT '审核条件',
    actions                        STRING               COMMENT '审核动作',
    priority                       INT                  COMMENT '优先级',
    status                         TINYINT              COMMENT '状态: 1-启用, 2-禁用',
    creator_id                     STRING               COMMENT '创建者ID',
    description                    STRING               COMMENT '描述',
    created_at                     TIMESTAMP            COMMENT '创建时间',
    updated_at                     TIMESTAMP            COMMENT '更新时间',
    is_deleted                     BOOLEAN              COMMENT '逻辑删除标识'
)
COMMENT '审核规则表'
ROW FORMAT DELIMITED FIELDS TERMINATED BY ','
STORED AS ORC
TBLPROPERTIES ('orc.compress'='SNAPPY');

-- --------------------------------------------
-- 角色权限关联表（role_permissions）
-- --------------------------------------------
DROP TABLE IF EXISTS role_permissions;
CREATE TABLE IF NOT EXISTS role_permissions (
    id                             BIGINT               COMMENT '主键ID',
    role_id                        BIGINT               COMMENT '角色ID',
    permission_id                  BIGINT               COMMENT '权限ID',
    created_at                     TIMESTAMP            COMMENT '创建时间',
    updated_at                     TIMESTAMP            COMMENT '更新时间',
    is_deleted                     BOOLEAN              COMMENT '逻辑删除标识'
)
COMMENT '角色权限关联表'
ROW FORMAT DELIMITED FIELDS TERMINATED BY ','
STORED AS ORC
TBLPROPERTIES ('orc.compress'='SNAPPY');

-- --------------------------------------------
-- 角色表（roles）
-- --------------------------------------------
DROP TABLE IF EXISTS roles;
CREATE TABLE IF NOT EXISTS roles (
    id                             BIGINT               COMMENT '主键ID',
    role_name                      STRING               COMMENT '角色名称，唯一',
    description                    STRING               COMMENT '角色描述',
    created_at                     TIMESTAMP            COMMENT '创建时间',
    updated_at                     TIMESTAMP            COMMENT '更新时间',
    is_deleted                     BOOLEAN              COMMENT '逻辑删除标识'
)
COMMENT '角色表'
ROW FORMAT DELIMITED FIELDS TERMINATED BY ','
STORED AS ORC
TBLPROPERTIES ('orc.compress'='SNAPPY');

-- --------------------------------------------
-- 存储路径配置表（storage_configs）
-- --------------------------------------------
DROP TABLE IF EXISTS storage_configs;
CREATE TABLE IF NOT EXISTS storage_configs (
    id                             BIGINT               COMMENT '主键ID',
    storage_type                   STRING               COMMENT '存储类型',
    base_path                      STRING               COMMENT '基础路径',
    max_capacity                   BIGINT               COMMENT '最大容量',
    current_usage                  BIGINT               COMMENT '当前使用量',
    file_types                     STRING               COMMENT '允许文件类型',
    retention_days                 INT                  COMMENT '保留天数',
    backup_policy                  STRING               COMMENT '备份策略',
    status                         TINYINT              COMMENT '状态',
    created_at                     TIMESTAMP            COMMENT '创建时间',
    updated_at                     TIMESTAMP            COMMENT '更新时间',
    is_deleted                     BOOLEAN              COMMENT '逻辑删除标识'
)
COMMENT '存储路径配置表'
ROW FORMAT DELIMITED FIELDS TERMINATED BY ','
STORED AS ORC
TBLPROPERTIES ('orc.compress'='SNAPPY');

-- --------------------------------------------
-- 系统配置表（system_configs）
-- --------------------------------------------
DROP TABLE IF EXISTS system_configs;
CREATE TABLE IF NOT EXISTS system_configs (
    id                             BIGINT               COMMENT '主键ID',
    config_key                     STRING               COMMENT '配置键，唯一',
    config_value                   STRING               COMMENT '配置值',
    config_type                    STRING               COMMENT '配置类型',
    category                       STRING               COMMENT '分类',
    description                    STRING               COMMENT '描述',
    is_public                      BOOLEAN              COMMENT '是否公开',
    created_by                     STRING               COMMENT '创建人ID',
    updated_by                     STRING               COMMENT '更新人ID',
    created_at                     TIMESTAMP            COMMENT '创建时间',
    updated_at                     TIMESTAMP            COMMENT '更新时间',
    is_deleted                     BOOLEAN              COMMENT '逻辑删除标识'
)
COMMENT '系统配置表'
ROW FORMAT DELIMITED FIELDS TERMINATED BY ','
STORED AS ORC
TBLPROPERTIES ('orc.compress'='SNAPPY');

-- --------------------------------------------
-- 任务资源关联表（task_resources）
-- --------------------------------------------
DROP TABLE IF EXISTS task_resources;
CREATE TABLE IF NOT EXISTS task_resources (
    id                             BIGINT               COMMENT '主键ID',
    task_id                        STRING               COMMENT '任务ID',
    resource_type                  TINYINT              COMMENT '资源类型: 1-图片, 2-视频, 3-音频',
    resource_id                    STRING               COMMENT '资源业务ID',
    usage_type                     STRING               COMMENT '使用类型',
    sequence                       INT                  COMMENT '顺序',
    created_at                     TIMESTAMP            COMMENT '创建时间',
    updated_at                     TIMESTAMP            COMMENT '更新时间',
    is_deleted                     BOOLEAN              COMMENT '逻辑删除标识'
)
COMMENT '任务资源关联表'
ROW FORMAT DELIMITED FIELDS TERMINATED BY ','
STORED AS ORC
TBLPROPERTIES ('orc.compress'='SNAPPY');

-- --------------------------------------------
-- 任务调度配置表（task_scheduling_configs）
-- --------------------------------------------
DROP TABLE IF EXISTS task_scheduling_configs;
CREATE TABLE IF NOT EXISTS task_scheduling_configs (
    id                             BIGINT               COMMENT '主键ID',
    max_concurrent_transcoding     INT                  COMMENT '并发转码数',
    max_concurrent_generation      INT                  COMMENT '并发生成数',
    task_queue_limit               INT                  COMMENT '队列限制',
    cpu_threshold_percent          INT                  COMMENT 'CPU阈值',
    memory_threshold_percent       INT                  COMMENT '内存阈值',
    retry_policy                   STRING               COMMENT '重试策略',
    timeout_policy                 STRING               COMMENT '超时策略',
    schedule_algorithm             STRING               COMMENT '调度算法',
    description                    STRING               COMMENT '描述',
    created_at                     TIMESTAMP            COMMENT '创建时间',
    updated_at                     TIMESTAMP            COMMENT '更新时间',
    is_deleted                     BOOLEAN              COMMENT '逻辑删除标识'
)
COMMENT '任务调度配置表'
ROW FORMAT DELIMITED FIELDS TERMINATED BY ','
STORED AS ORC
TBLPROPERTIES ('orc.compress'='SNAPPY');

-- --------------------------------------------
-- 任务统计表（task_statistics）
-- --------------------------------------------
DROP TABLE IF EXISTS task_statistics;
CREATE TABLE IF NOT EXISTS task_statistics (
    id                             BIGINT               COMMENT '主键ID',
    task_type                      TINYINT              COMMENT '任务类型: 1-文生文, 2-文生图, 3-文生视频, 4-文加图生视频',
    stat_date                      DATE                 COMMENT '统计日期',
    total_count                    INT                  COMMENT '总任务数',
    success_count                  INT                  COMMENT '成功数',
    fail_count                     INT                  COMMENT '失败数',
    avg_duration_seconds           INT                  COMMENT '平均耗时',
    peak_hour                      INT                  COMMENT '峰值时段',
    created_at                     TIMESTAMP            COMMENT '创建时间',
    updated_at                     TIMESTAMP            COMMENT '更新时间',
    is_deleted                     BOOLEAN              COMMENT '逻辑删除标识'
)
COMMENT '任务统计表'
ROW FORMAT DELIMITED FIELDS TERMINATED BY ','
STORED AS ORC
TBLPROPERTIES ('orc.compress'='SNAPPY');

-- --------------------------------------------
-- 转码配置表（transcoding_configs）
-- --------------------------------------------
DROP TABLE IF EXISTS transcoding_configs;
CREATE TABLE IF NOT EXISTS transcoding_configs (
    id                             BIGINT               COMMENT '主键ID',
    config_name                    STRING               COMMENT '配置名称',
    video_codec                    STRING               COMMENT '视频编码',
    audio_codec                    STRING               COMMENT '音频编码',
    video_bitrate_kbps             INT                  COMMENT '视频码率',
    audio_bitrate_kbps             INT                  COMMENT '音频码率',
    frame_rate                     INT                  COMMENT '帧率',
    resolution_preset              STRING               COMMENT '分辨率预设',
    quality_preset                 TINYINT              COMMENT '质量: 1-低, 2-中, 3-高, 4-自定义',
    is_default                     BOOLEAN              COMMENT '是否默认',
    description                    STRING               COMMENT '描述',
    created_by                     STRING               COMMENT '创建人ID',
    created_at                     TIMESTAMP            COMMENT '创建时间',
    updated_at                     TIMESTAMP            COMMENT '更新时间',
    is_deleted                     BOOLEAN              COMMENT '逻辑删除标识'
)
COMMENT '转码配置表'
ROW FORMAT DELIMITED FIELDS TERMINATED BY ','
STORED AS ORC
TBLPROPERTIES ('orc.compress'='SNAPPY');

-- --------------------------------------------
-- TTS 配置表（tts_configs）
-- --------------------------------------------
DROP TABLE IF EXISTS tts_configs;
CREATE TABLE IF NOT EXISTS tts_configs (
    id                             STRING               COMMENT '主键ID',
    model_name                     STRING               COMMENT '模型名称',
    voice_type                     STRING               COMMENT '声音类型',
    language_type                  STRING               COMMENT '语言类型',
    speed_words_per_min            INT                  COMMENT '语速',
    sample_rate                    STRING               COMMENT '采样率',
    model_file_path                STRING               COMMENT '模型路径',
    config_json                    STRING               COMMENT '配置详情',
    status                         TINYINT              COMMENT '状态: 1-启用, 2-禁用',
    creator_id                     STRING               COMMENT '创建者ID',
    description                    STRING               COMMENT '描述',
    created_at                     TIMESTAMP            COMMENT '创建时间',
    updated_at                     TIMESTAMP            COMMENT '更新时间',
    is_deleted                     BOOLEAN              COMMENT '逻辑删除标识'
)
COMMENT 'TTS 配置表'
ROW FORMAT DELIMITED FIELDS TERMINATED BY ','
STORED AS ORC
TBLPROPERTIES ('orc.compress'='SNAPPY');

-- --------------------------------------------
-- 用户活跃度表（user_activity_logs）
-- --------------------------------------------
DROP TABLE IF EXISTS user_activity_logs;
CREATE TABLE IF NOT EXISTS user_activity_logs (
    id                             BIGINT               COMMENT '主键ID',
    user_id                        STRING               COMMENT '用户ID',
    activity_type                  STRING               COMMENT '活动类型',
    description                    STRING               COMMENT '描述',
    activity_time                  TIMESTAMP            COMMENT '活动时间',
    ip_address                     STRING               COMMENT 'IP地址',
    user_agent                     STRING               COMMENT 'UA信息',
    duration_seconds               INT                  COMMENT '时长',
    resource_id                    STRING               COMMENT '资源ID',
    additional_info                STRING               COMMENT '额外信息',
    created_at                     TIMESTAMP            COMMENT '创建时间',
    updated_at                     TIMESTAMP            COMMENT '更新时间',
    is_deleted                     BOOLEAN              COMMENT '逻辑删除标识'
)
COMMENT '用户活跃度表'
ROW FORMAT DELIMITED FIELDS TERMINATED BY ','
STORED AS ORC
TBLPROPERTIES ('orc.compress'='SNAPPY');

-- --------------------------------------------
-- 用户生成内容表（user_generated_content）
-- --------------------------------------------
DROP TABLE IF EXISTS user_generated_content;
CREATE TABLE IF NOT EXISTS user_generated_content (
    id                             STRING               COMMENT '主键ID(业务内容ID)',
    creator_id                     STRING               COMMENT '创作者ID(creators表ID)',
    content_name                   STRING               COMMENT '内容名称',
    task_id                        STRING               COMMENT '关联任务ID',
    format                         STRING               COMMENT '格式',
    resolution                     STRING               COMMENT '分辨率',
    file_path                      STRING               COMMENT '文件路径',
    file_size                      BIGINT               COMMENT '文件大小',
    thumbnail_path                 STRING               COMMENT '缩略图',
    description                    STRING               COMMENT '描述',
    tags                           STRING               COMMENT '标签',
    created_at                     TIMESTAMP            COMMENT '创建时间',
    updated_at                     TIMESTAMP            COMMENT '更新时间',
    is_deleted                     BOOLEAN              COMMENT '逻辑删除标识'
)
COMMENT '用户生成内容表'
ROW FORMAT DELIMITED FIELDS TERMINATED BY ','
STORED AS ORC
TBLPROPERTIES ('orc.compress'='SNAPPY');

-- --------------------------------------------
-- 用户详情表（user_profiles）
-- --------------------------------------------
DROP TABLE IF EXISTS user_profiles;
CREATE TABLE IF NOT EXISTS user_profiles (
    id                             STRING               COMMENT '主键ID',
    user_id                        STRING               COMMENT '用户ID',
    real_name                      STRING               COMMENT '真实姓名',
    gender                         TINYINT              COMMENT '性别: 0-未知, 1-男, 2-女',
    birthday                       DATE                 COMMENT '出生日期',
    country                        STRING               COMMENT '国家',
    city                           STRING               COMMENT '城市',
    bio                            STRING               COMMENT '个人简介',
    created_at                     TIMESTAMP            COMMENT '创建时间',
    updated_at                     TIMESTAMP            COMMENT '更新时间',
    is_deleted                     BOOLEAN              COMMENT '逻辑删除标识'
)
COMMENT '用户详情表'
ROW FORMAT DELIMITED FIELDS TERMINATED BY ','
STORED AS ORC
TBLPROPERTIES ('orc.compress'='SNAPPY');

-- --------------------------------------------
-- 用户钱包表（user_wallets）
-- --------------------------------------------
DROP TABLE IF EXISTS user_wallets;
CREATE TABLE IF NOT EXISTS user_wallets (
    id                             STRING               COMMENT '主键ID',
    user_id                        STRING               COMMENT '用户ID',
    balance                        DOUBLE        COMMENT '余额',
    total_recharged                DOUBLE        COMMENT '累计充值',
    total_consumed                 DOUBLE        COMMENT '累计消费',
    created_at                     TIMESTAMP            COMMENT '创建时间',
    updated_at                     TIMESTAMP            COMMENT '更新时间',
    is_deleted                     BOOLEAN              COMMENT '逻辑删除标识'
)
COMMENT '用户钱包表'
ROW FORMAT DELIMITED FIELDS TERMINATED BY ','
STORED AS ORC
TBLPROPERTIES ('orc.compress'='SNAPPY');

-- --------------------------------------------
-- 用户表（users）
-- --------------------------------------------
DROP TABLE IF EXISTS users;
CREATE TABLE IF NOT EXISTS users (
    id                             STRING               COMMENT '主键ID(业务用户ID)',
    username                       STRING               COMMENT '用户名',
    email                          STRING               COMMENT '邮箱',
    password_hash                  STRING               COMMENT '密码哈希',
    avatar_url                     STRING               COMMENT '头像URL',
    phone                          STRING               COMMENT '手机号',
    last_login_time                TIMESTAMP            COMMENT '最后登录时间',
    status                         TINYINT              COMMENT '状态: 1-正常, 2-封禁, 3-注销',
    role_id                        BIGINT               COMMENT '角色ID',
    created_at                     TIMESTAMP            COMMENT '创建时间',
    updated_at                     TIMESTAMP            COMMENT '更新时间'
)
COMMENT '用户表'
ROW FORMAT DELIMITED FIELDS TERMINATED BY ','
STORED AS ORC
TBLPROPERTIES ('orc.compress'='SNAPPY');

-- --------------------------------------------
-- 剪辑工程表（video_edit_projects）
-- --------------------------------------------
DROP TABLE IF EXISTS video_edit_projects;
CREATE TABLE IF NOT EXISTS video_edit_projects (
    id                             STRING               COMMENT '主键ID(业务工程ID)',
    user_id                        STRING               COMMENT '用户ID',
    project_name                   STRING               COMMENT '工程名称',
    timeline_data                  STRING               COMMENT '时间轴数据',
    cover_image                    STRING               COMMENT '封面图',
    status                         TINYINT              COMMENT '状态: 1-草稿, 2-合成中, 3-完成',
    created_at                     TIMESTAMP            COMMENT '创建时间',
    updated_at                     TIMESTAMP            COMMENT '更新时间',
    is_deleted                     BOOLEAN              COMMENT '逻辑删除标识'
)
COMMENT '剪辑工程表'
ROW FORMAT DELIMITED FIELDS TERMINATED BY ','
STORED AS ORC
TBLPROPERTIES ('orc.compress'='SNAPPY');

-- --------------------------------------------
-- 视频素材表（video_materials）
-- --------------------------------------------
DROP TABLE IF EXISTS video_materials;
CREATE TABLE IF NOT EXISTS video_materials (
    id                             STRING               COMMENT '主键ID(业务视频ID)',
    video_name                     STRING               COMMENT '视频名称',
    type                           STRING               COMMENT '视频类型',
    resolution                     STRING               COMMENT '分辨率',
    format                         STRING               COMMENT '格式',
    duration_seconds               DOUBLE        COMMENT '时长(秒)',
    file_path                      STRING               COMMENT '文件路径',
    cover_path                     STRING               COMMENT '封面路径',
    file_size                      BIGINT               COMMENT '文件大小',
    uploader_id                    STRING               COMMENT '上传者ID',
    status                         TINYINT              COMMENT '状态: 1-正常, 2-封禁, 3-禁用',
    copyright_status               TINYINT              COMMENT '版权: 1-免费商用, 2-付费授权, 3-个人使用',
    tags                           STRING               COMMENT '标签',
    description                    STRING               COMMENT '描述',
    source_type                    TINYINT              COMMENT '来源: 1-官方, 2-用户上传, 3-AI生成',
    is_public                      BOOLEAN              COMMENT '是否公开',
    created_at                     TIMESTAMP            COMMENT '创建时间',
    updated_at                     TIMESTAMP            COMMENT '更新时间',
    is_deleted                     BOOLEAN              COMMENT '逻辑删除标识'
)
COMMENT '视频素材表'
ROW FORMAT DELIMITED FIELDS TERMINATED BY ','
STORED AS ORC
TBLPROPERTIES ('orc.compress'='SNAPPY');

-- --------------------------------------------
-- 视频模板表（video_templates）
-- --------------------------------------------
DROP TABLE IF EXISTS video_templates;
CREATE TABLE IF NOT EXISTS video_templates (
    id                             STRING               COMMENT '主键ID(业务模板ID)',
    template_name                  STRING               COMMENT '模板名称',
    type                           STRING               COMMENT '类型',
    aspect_ratio                   STRING               COMMENT '宽高比',
    preview_image_path             STRING               COMMENT '预览图路径',
    template_file_path             STRING               COMMENT '模板文件路径',
    creator_id                     STRING               COMMENT '创建者ID',
    usage_count                    INT                  COMMENT '使用次数',
    status                         TINYINT              COMMENT '状态: 1-启用, 2-禁用, 3-草稿',
    tags                           STRING               COMMENT '标签',
    description                    STRING               COMMENT '描述',
    config_json                    STRING               COMMENT '模板配置',
    created_at                     TIMESTAMP            COMMENT '创建时间',
    updated_at                     TIMESTAMP            COMMENT '更新时间',
    is_deleted                     BOOLEAN              COMMENT '逻辑删除标识'
)
COMMENT '视频模板表'
ROW FORMAT DELIMITED FIELDS TERMINATED BY ','
STORED AS ORC
TBLPROPERTIES ('orc.compress'='SNAPPY');

-- --------------------------------------------
-- 交易流水表（wallet_transactions）
-- --------------------------------------------
DROP TABLE IF EXISTS wallet_transactions;
CREATE TABLE IF NOT EXISTS wallet_transactions (
    id                             STRING               COMMENT '主键ID(业务流水ID)',
    wallet_id                      STRING               COMMENT '钱包ID',
    type                           TINYINT              COMMENT '类型: 1-充值, 2-消费, 3-退款',
    amount                         DOUBLE        COMMENT '金额',
    related_task_id                STRING               COMMENT '关联任务ID',
    description                    STRING               COMMENT '描述',
    created_at                     TIMESTAMP            COMMENT '创建时间',
    updated_at                     TIMESTAMP            COMMENT '更新时间',
    is_deleted                     BOOLEAN              COMMENT '逻辑删除标识'
)
COMMENT '交易流水表'
ROW FORMAT DELIMITED FIELDS TERMINATED BY ','
STORED AS ORC
TBLPROPERTIES ('orc.compress'='SNAPPY');

