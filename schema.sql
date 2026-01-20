-- Database Initialization
CREATE DATABASE IF NOT EXISTS video_generation_platform DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE video_generation_platform;

-- 1. 角色表 (roles)
DROP TABLE IF EXISTS roles;
CREATE TABLE roles (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    role_name VARCHAR(50) NOT NULL COMMENT '角色名称，唯一',
    description TEXT COMMENT '角色描述',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除标识',
    PRIMARY KEY (id),
    UNIQUE KEY uk_role_name (role_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='角色表';

-- 2. 权限表 (permissions)
DROP TABLE IF EXISTS permissions;
CREATE TABLE permissions (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    permission_code VARCHAR(100) NOT NULL COMMENT '权限编码，唯一',
    permission_name VARCHAR(100) NOT NULL COMMENT '权限名称',
    category VARCHAR(50) DEFAULT NULL COMMENT '权限分类',
    description TEXT COMMENT '权限描述',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除标识',
    PRIMARY KEY (id),
    UNIQUE KEY uk_permission_code (permission_code),
    INDEX idx_category (category)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='权限表';

-- 3. 角色权限关联表 (role_permissions)
DROP TABLE IF EXISTS role_permissions;
CREATE TABLE role_permissions (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    role_id BIGINT UNSIGNED NOT NULL COMMENT '角色ID',
    permission_id BIGINT UNSIGNED NOT NULL COMMENT '权限ID',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除标识',
    PRIMARY KEY (id),
    UNIQUE KEY uk_role_permission (role_id, permission_id),
    INDEX idx_role_id (role_id),
    INDEX idx_permission_id (permission_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='角色权限关联表';

-- 4. 用户表 (users)
DROP TABLE IF EXISTS users;
CREATE TABLE users (
    id VARCHAR(32) NOT NULL COMMENT '主键ID(业务用户ID)',
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    email VARCHAR(100) NOT NULL COMMENT '邮箱',
    password_hash VARCHAR(255) NOT NULL COMMENT '密码哈希',
    avatar_url VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
    phone VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    last_login_time DATETIME DEFAULT NULL COMMENT '最后登录时间',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 1-正常, 2-封禁, 3-注销',
    role_id BIGINT UNSIGNED NOT NULL COMMENT '角色ID',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (username),
    UNIQUE KEY uk_email (email),
    INDEX idx_role_id (role_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户表';

-- 5. 用户详情表 (user_profiles)
DROP TABLE IF EXISTS user_profiles;
CREATE TABLE user_profiles (
    id VARCHAR(32) NOT NULL COMMENT '主键ID',
    user_id VARCHAR(32) NOT NULL COMMENT '用户ID',
    real_name VARCHAR(50) DEFAULT NULL COMMENT '真实姓名',
    gender TINYINT DEFAULT 0 COMMENT '性别: 0-未知, 1-男, 2-女',
    birthday DATE DEFAULT NULL COMMENT '出生日期',
    country VARCHAR(50) DEFAULT NULL COMMENT '国家',
    city VARCHAR(50) DEFAULT NULL COMMENT '城市',
    bio TEXT COMMENT '个人简介',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除标识',
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_id (user_id),
    INDEX idx_country_city (country, city)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户详情表';

-- 6. 图片素材表 (image_materials)
DROP TABLE IF EXISTS image_materials;
CREATE TABLE image_materials (
    id VARCHAR(32) NOT NULL COMMENT '主键ID(业务素材ID)',
    image_name VARCHAR(200) NOT NULL COMMENT '图片名称',
    category VARCHAR(50) DEFAULT NULL COMMENT '分类',
    file_path VARCHAR(500) NOT NULL COMMENT '文件路径',
    file_size BIGINT DEFAULT 0 COMMENT '文件大小(字节)',
    resolution VARCHAR(20) DEFAULT NULL COMMENT '分辨率',
    format VARCHAR(10) DEFAULT NULL COMMENT '格式',
    uploader_id VARCHAR(32) NOT NULL COMMENT '上传者ID',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 1-正常, 2-封禁, 3-禁用',
    copyright_status TINYINT NOT NULL DEFAULT 1 COMMENT '版权: 1-免费商用, 2-付费授权, 3-个人使用',
    tags TEXT COMMENT '标签',
    description TEXT COMMENT '描述',
    source_type TINYINT NOT NULL DEFAULT 2 COMMENT '来源: 1-官方, 2-用户上传, 3-AI生成',
    is_public TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否公开',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除标识',
    PRIMARY KEY (id),
    INDEX idx_uploader_id (uploader_id),
    INDEX idx_category (category),
    INDEX idx_status (status),
    INDEX idx_copyright_status (copyright_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='图片素材表';

-- 7. 视频素材表 (video_materials)
DROP TABLE IF EXISTS video_materials;
CREATE TABLE video_materials (
    id VARCHAR(32) NOT NULL COMMENT '主键ID(业务视频ID)',
    video_name VARCHAR(200) NOT NULL COMMENT '视频名称',
    type VARCHAR(50) DEFAULT NULL COMMENT '视频类型',
    resolution VARCHAR(50) NOT NULL COMMENT '分辨率',
    format VARCHAR(20) DEFAULT NULL COMMENT '格式',
    duration_seconds DECIMAL(10, 2) DEFAULT 0 COMMENT '时长(秒)',
    file_path VARCHAR(255) DEFAULT NULL COMMENT '文件路径',
    file_size BIGINT DEFAULT 0 COMMENT '文件大小',
    uploader_id VARCHAR(32) NOT NULL COMMENT '上传者ID',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 1-正常, 2-封禁, 3-禁用',
    copyright_status TINYINT NOT NULL DEFAULT 1 COMMENT '版权: 1-免费商用, 2-付费授权, 3-个人使用',
    tags TEXT COMMENT '标签',
    description TEXT COMMENT '描述',
    source_type TINYINT NOT NULL DEFAULT 2 COMMENT '来源: 1-官方, 2-用户上传, 3-AI生成',
    is_public TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否公开',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除标识',
    PRIMARY KEY (id),
    INDEX idx_uploader_id (uploader_id),
    INDEX idx_type (type),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='视频素材表';

-- 8. 音频素材表 (audio_materials)
DROP TABLE IF EXISTS audio_materials;
CREATE TABLE audio_materials (
    id VARCHAR(32) NOT NULL COMMENT '主键ID(业务音频ID)',
    audio_name VARCHAR(200) NOT NULL COMMENT '音频名称',
    type VARCHAR(50) DEFAULT NULL COMMENT '类型',
    duration_seconds DECIMAL(10, 2) NOT NULL DEFAULT 0 COMMENT '时长(秒)',
    file_size BIGINT DEFAULT 0 COMMENT '文件大小',
    file_path VARCHAR(255) DEFAULT NULL COMMENT '文件路径',
    bitrate INT DEFAULT NULL COMMENT '比特率',
    sample_rate VARCHAR(20) DEFAULT NULL COMMENT '采样率',
    uploader_id VARCHAR(32) NOT NULL COMMENT '上传者ID',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 1-正常, 2-封禁, 3-禁用',
    copyright_status TINYINT NOT NULL DEFAULT 1 COMMENT '版权: 1-免费商用, 2-付费授权, 3-个人使用',
    tags TEXT COMMENT '标签',
    description TEXT COMMENT '描述',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除标识',
    PRIMARY KEY (id),
    INDEX idx_uploader_id (uploader_id),
    INDEX idx_type (type),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='音频素材表';

-- 9. 视频模板表 (video_templates)
DROP TABLE IF EXISTS video_templates;
CREATE TABLE video_templates (
    id VARCHAR(32) NOT NULL COMMENT '主键ID(业务模板ID)',
    template_name VARCHAR(200) NOT NULL COMMENT '模板名称',
    type VARCHAR(50) DEFAULT NULL COMMENT '类型',
    aspect_ratio VARCHAR(20) NOT NULL COMMENT '宽高比',
    preview_image_path VARCHAR(255) DEFAULT NULL COMMENT '预览图路径',
    template_file_path VARCHAR(255) DEFAULT NULL COMMENT '模板文件路径',
    creator_id VARCHAR(32) DEFAULT NULL COMMENT '创建者ID',
    usage_count INT DEFAULT 0 COMMENT '使用次数',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 1-启用, 2-禁用, 3-草稿',
    tags TEXT COMMENT '标签',
    description TEXT COMMENT '描述',
    config_json JSON DEFAULT NULL COMMENT '模板配置',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除标识',
    PRIMARY KEY (id),
    INDEX idx_creator_id (creator_id),
    INDEX idx_type (type),
    INDEX idx_status (status),
    INDEX idx_usage_count (usage_count)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='视频模板表';

-- 10. TTS 配置表 (tts_configs)
DROP TABLE IF EXISTS tts_configs;
CREATE TABLE tts_configs (
    id VARCHAR(32) NOT NULL COMMENT '主键ID',
    model_name VARCHAR(100) NOT NULL COMMENT '模型名称',
    voice_type VARCHAR(20) DEFAULT NULL COMMENT '声音类型',
    language_type VARCHAR(50) NOT NULL COMMENT '语言类型',
    speed_words_per_min INT DEFAULT NULL COMMENT '语速',
    sample_rate VARCHAR(20) DEFAULT NULL COMMENT '采样率',
    model_file_path VARCHAR(500) DEFAULT NULL COMMENT '模型路径',
    config_json JSON DEFAULT NULL COMMENT '配置详情',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 1-启用, 2-禁用',
    creator_id VARCHAR(32) NOT NULL COMMENT '创建者ID',
    description TEXT COMMENT '描述',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除标识',
    PRIMARY KEY (id),
    INDEX idx_creator_id (creator_id),
    INDEX idx_voice_type (voice_type),
    INDEX idx_language_type (language_type),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='TTS 配置表';

-- 11. 视频生成任务表 (video_generation_tasks)
DROP TABLE IF EXISTS video_generation_tasks;
CREATE TABLE video_generation_tasks (
    id VARCHAR(32) NOT NULL COMMENT '主键ID(业务任务ID)',
    task_name VARCHAR(200) NOT NULL COMMENT '任务名称',
    user_id VARCHAR(32) NOT NULL COMMENT '用户ID',
    task_type TINYINT NOT NULL COMMENT '任务类型: 1-文生文, 2-文生图, 3-文生视频, 4-文加图生视频',
    template_id VARCHAR(32) DEFAULT NULL COMMENT '关联模板ID',
    input_config JSON NOT NULL COMMENT '输入配置',
    output_config JSON DEFAULT NULL COMMENT '输出配置',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 1-等待中, 2-处理中, 3-成功, 4-失败, 5-取消',
    progress INT NOT NULL DEFAULT 0 COMMENT '进度(0-100)',
    start_time DATETIME DEFAULT NULL COMMENT '开始时间',
    end_time DATETIME DEFAULT NULL COMMENT '结束时间',
    duration_seconds INT DEFAULT NULL COMMENT '耗时(秒)',
    result_video_path VARCHAR(500) DEFAULT NULL COMMENT '结果视频路径',
    result_video_size BIGINT DEFAULT NULL COMMENT '结果大小',
    error_message TEXT COMMENT '错误信息',
    retry_count INT DEFAULT 0 COMMENT '重试次数',
    priority INT DEFAULT 5 COMMENT '优先级',
    model_id BIGINT UNSIGNED DEFAULT NULL COMMENT '模型ID',
    cost_token DECIMAL(10, 4) DEFAULT NULL COMMENT '消耗Token',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除标识',
    PRIMARY KEY (id),
    INDEX idx_user_id (user_id),
    INDEX idx_template_id (template_id),
    INDEX idx_status (status),
    INDEX idx_priority (priority)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='视频生成任务表';

-- 12. 任务资源关联表 (task_resources)
DROP TABLE IF EXISTS task_resources;
CREATE TABLE task_resources (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    task_id VARCHAR(32) NOT NULL COMMENT '任务ID',
    resource_type TINYINT NOT NULL COMMENT '资源类型: 1-图片, 2-视频, 3-音频',
    resource_id VARCHAR(32) NOT NULL COMMENT '资源业务ID', 
    usage_type VARCHAR(50) DEFAULT NULL COMMENT '使用类型',
    sequence INT DEFAULT NULL COMMENT '顺序',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除标识',
    PRIMARY KEY (id),
    INDEX idx_task_id (task_id),
    INDEX idx_resource (resource_type, resource_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='任务资源关联表';

-- 13. 创作者信息表 (creators)
DROP TABLE IF EXISTS creators;
CREATE TABLE creators (
    id VARCHAR(32) NOT NULL COMMENT '主键ID(业务创作者ID)',
    user_id VARCHAR(32) NOT NULL COMMENT '用户ID',
    platform_account VARCHAR(100) DEFAULT NULL COMMENT '平台账号',
    platform_type VARCHAR(50) DEFAULT NULL COMMENT '平台类型',
    follower_count INT NOT NULL DEFAULT 0 COMMENT '粉丝数',
    verification_status TINYINT NOT NULL DEFAULT 1 COMMENT '认证状态: 1-未认证, 2-已认证, 3-认证中',
    bio TEXT COMMENT '简介',
    contact_info JSON DEFAULT NULL COMMENT '联系信息',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除标识',
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_id (user_id),
    INDEX idx_verification_status (verification_status),
    INDEX idx_follower_count (follower_count)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='创作者信息表';

-- 14. 用户生成内容表 (user_generated_content)
DROP TABLE IF EXISTS user_generated_content;
CREATE TABLE user_generated_content (
    id VARCHAR(32) NOT NULL COMMENT '主键ID(业务内容ID)',
    creator_id VARCHAR(32) NOT NULL COMMENT '创作者ID(creators表ID)',
    content_name VARCHAR(200) NOT NULL COMMENT '内容名称',
    task_id VARCHAR(32) DEFAULT NULL COMMENT '关联任务ID',
    format VARCHAR(20) DEFAULT NULL COMMENT '格式',
    resolution VARCHAR(20) DEFAULT NULL COMMENT '分辨率',
    file_path VARCHAR(500) NOT NULL COMMENT '文件路径',
    file_size BIGINT DEFAULT 0 COMMENT '文件大小',
    thumbnail_path VARCHAR(500) DEFAULT NULL COMMENT '缩略图',
    description TEXT COMMENT '描述',
    tags TEXT COMMENT '标签',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除标识',
    PRIMARY KEY (id),
    INDEX idx_creator_id (creator_id),
    INDEX idx_task_id (task_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户生成内容表';

-- 15. 内容审核表 (content_reviews)
DROP TABLE IF EXISTS content_reviews;
CREATE TABLE content_reviews (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    content_id VARCHAR(32) NOT NULL COMMENT '内容ID(UGC表ID)',
    reviewer_id VARCHAR(32) NOT NULL COMMENT '审核员ID(User表ID)',
    review_status TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 1-待审核, 2-通过, 3-驳回, 4-复审',
    review_time DATETIME DEFAULT NULL COMMENT '审核时间',
    reject_reason TEXT COMMENT '驳回原因',
    suggestions TEXT COMMENT '建议',
    next_review_time DATETIME DEFAULT NULL COMMENT '下次审核时间',
    version INT NOT NULL DEFAULT 1 COMMENT '版本号',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除标识',
    PRIMARY KEY (id),
    INDEX idx_content_id (content_id),
    INDEX idx_reviewer_id (reviewer_id),
    INDEX idx_review_status (review_status),
    INDEX idx_version (version)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='内容审核表';

-- 16. 审核规则表 (review_rules)
DROP TABLE IF EXISTS review_rules;
CREATE TABLE review_rules (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    rule_name VARCHAR(100) NOT NULL COMMENT '规则名称',
    rule_type VARCHAR(50) DEFAULT NULL COMMENT '规则类型',
    conditions JSON NOT NULL COMMENT '审核条件',
    actions JSON NOT NULL COMMENT '审核动作',
    priority INT NOT NULL DEFAULT 5 COMMENT '优先级',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 1-启用, 2-禁用',
    creator_id VARCHAR(32) NOT NULL COMMENT '创建者ID',
    description TEXT COMMENT '描述',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除标识',
    PRIMARY KEY (id),
    INDEX idx_creator_id (creator_id),
    INDEX idx_rule_type (rule_type),
    INDEX idx_priority (priority),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='审核规则表';

-- 17. 系统配置表 (system_configs)
DROP TABLE IF EXISTS system_configs;
CREATE TABLE system_configs (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    config_key VARCHAR(100) NOT NULL COMMENT '配置键，唯一',
    config_value TEXT NOT NULL COMMENT '配置值',
    config_type VARCHAR(50) DEFAULT NULL COMMENT '配置类型',
    category VARCHAR(50) DEFAULT NULL COMMENT '分类',
    description TEXT COMMENT '描述',
    is_public TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否公开',
    created_by VARCHAR(32) NOT NULL COMMENT '创建人ID',
    updated_by VARCHAR(32) DEFAULT NULL COMMENT '更新人ID',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除标识',
    PRIMARY KEY (id),
    UNIQUE KEY uk_config_key (config_key),
    INDEX idx_category (category)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='系统配置表';

-- 18. 存储路径配置表 (storage_configs)
DROP TABLE IF EXISTS storage_configs;
CREATE TABLE storage_configs (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    storage_type VARCHAR(50) NOT NULL COMMENT '存储类型',
    base_path VARCHAR(500) NOT NULL COMMENT '基础路径',
    max_capacity BIGINT DEFAULT NULL COMMENT '最大容量',
    current_usage BIGINT NOT NULL DEFAULT 0 COMMENT '当前使用量',
    file_types JSON DEFAULT NULL COMMENT '允许文件类型',
    retention_days INT DEFAULT NULL COMMENT '保留天数',
    backup_policy JSON DEFAULT NULL COMMENT '备份策略',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除标识',
    PRIMARY KEY (id),
    UNIQUE KEY uk_storage_type (storage_type),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='存储路径配置表';

-- 19. 转码配置表 (transcoding_configs)
DROP TABLE IF EXISTS transcoding_configs;
CREATE TABLE transcoding_configs (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    config_name VARCHAR(100) NOT NULL COMMENT '配置名称',
    video_codec VARCHAR(50) NOT NULL COMMENT '视频编码',
    audio_codec VARCHAR(50) NOT NULL COMMENT '音频编码',
    video_bitrate_kbps INT NOT NULL COMMENT '视频码率',
    audio_bitrate_kbps INT NOT NULL COMMENT '音频码率',
    frame_rate INT NOT NULL COMMENT '帧率',
    resolution_preset JSON DEFAULT NULL COMMENT '分辨率预设',
    quality_preset TINYINT NOT NULL DEFAULT 2 COMMENT '质量: 1-低, 2-中, 3-高, 4-自定义',
    is_default TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否默认',
    description TEXT COMMENT '描述',
    created_by VARCHAR(32) NOT NULL COMMENT '创建人ID',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除标识',
    PRIMARY KEY (id),
    INDEX idx_created_by (created_by),
    INDEX idx_is_default (is_default)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='转码配置表';

-- 20. 任务调度配置表 (task_scheduling_configs)
DROP TABLE IF EXISTS task_scheduling_configs;
CREATE TABLE task_scheduling_configs (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    max_concurrent_transcoding INT NOT NULL DEFAULT 5 COMMENT '并发转码数',
    max_concurrent_generation INT NOT NULL DEFAULT 3 COMMENT '并发生成数',
    task_queue_limit INT NOT NULL DEFAULT 50 COMMENT '队列限制',
    cpu_threshold_percent INT NOT NULL DEFAULT 80 COMMENT 'CPU阈值',
    memory_threshold_percent INT NOT NULL DEFAULT 80 COMMENT '内存阈值',
    retry_policy JSON DEFAULT NULL COMMENT '重试策略',
    timeout_policy JSON DEFAULT NULL COMMENT '超时策略',
    schedule_algorithm VARCHAR(50) NOT NULL DEFAULT 'FIFO' COMMENT '调度算法',
    description TEXT COMMENT '描述',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除标识',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='任务调度配置表';

-- 21. 每日统计表 (daily_statistics)
DROP TABLE IF EXISTS daily_statistics;
CREATE TABLE daily_statistics (
    stat_date DATE NOT NULL COMMENT '统计日期',
    total_users INT NOT NULL DEFAULT 0 COMMENT '总用户数',
    new_users INT NOT NULL DEFAULT 0 COMMENT '新增用户数',
    active_users INT NOT NULL DEFAULT 0 COMMENT '活跃用户数',
    total_video_generated INT NOT NULL DEFAULT 0 COMMENT '生成视频总数',
    successful_tasks INT NOT NULL DEFAULT 0 COMMENT '成功任务数',
    failed_tasks INT NOT NULL DEFAULT 0 COMMENT '失败任务数',
    task_success_rate DECIMAL(5, 2) NOT NULL DEFAULT 0.00 COMMENT '成功率',
    total_storage_used BIGINT NOT NULL DEFAULT 0 COMMENT '存储使用量',
    peak_concurrent_tasks INT NOT NULL DEFAULT 0 COMMENT '峰值并发',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除标识',
    PRIMARY KEY (stat_date),
    INDEX idx_new_users (new_users)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='每日统计表';

-- 22. 用户活跃度表 (user_activity_logs)
DROP TABLE IF EXISTS user_activity_logs;
CREATE TABLE user_activity_logs (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    user_id VARCHAR(32) NOT NULL COMMENT '用户ID',
    activity_type VARCHAR(50) NOT NULL COMMENT '活动类型',
    description TEXT COMMENT '描述',
    activity_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '活动时间',
    ip_address VARCHAR(45) DEFAULT NULL COMMENT 'IP地址',
    user_agent TEXT COMMENT 'UA信息',
    duration_seconds INT DEFAULT NULL COMMENT '时长',
    resource_id VARCHAR(32) DEFAULT NULL COMMENT '资源ID',
    additional_info JSON DEFAULT NULL COMMENT '额外信息',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除标识',
    PRIMARY KEY (id),
    INDEX idx_user_id (user_id),
    INDEX idx_activity_time (activity_time),
    INDEX idx_activity_type (activity_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户活跃度表';

-- 23. 任务统计表 (task_statistics)
DROP TABLE IF EXISTS task_statistics;
CREATE TABLE task_statistics (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    task_type TINYINT NOT NULL COMMENT '任务类型: 1-文生文, 2-文生图, 3-文生视频, 4-文加图生视频',
    stat_date DATE DEFAULT NULL COMMENT '统计日期',
    total_count INT NOT NULL DEFAULT 0 COMMENT '总任务数',
    success_count INT NOT NULL DEFAULT 0 COMMENT '成功数',
    fail_count INT NOT NULL DEFAULT 0 COMMENT '失败数',
    avg_duration_seconds INT DEFAULT NULL COMMENT '平均耗时',
    peak_hour INT DEFAULT NULL COMMENT '峰值时段',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除标识',
    PRIMARY KEY (id),
    UNIQUE KEY uk_type_date (task_type, stat_date),
    INDEX idx_stat_date (stat_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='任务统计表';

-- 24. 财务统计表 (financial_statistics)
DROP TABLE IF EXISTS financial_statistics;
CREATE TABLE financial_statistics (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    stat_period VARCHAR(20) NOT NULL COMMENT '周期: daily, weekly',
    stat_date DATE DEFAULT NULL COMMENT '统计日期',
    total_revenue DECIMAL(15, 2) NOT NULL DEFAULT 0.00 COMMENT '总营收',
    total_expense DECIMAL(15, 2) NOT NULL DEFAULT 0.00 COMMENT '总支出',
    user_count INT NOT NULL DEFAULT 0 COMMENT '用户数',
    premium_user_count INT NOT NULL DEFAULT 0 COMMENT '付费用户数',
    task_revenue DECIMAL(15, 2) NOT NULL DEFAULT 0.00 COMMENT '任务营收',
    storage_revenue DECIMAL(15, 2) NOT NULL DEFAULT 0.00 COMMENT '存储营收',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除标识',
    PRIMARY KEY (id),
    UNIQUE KEY uk_period_date (stat_period, stat_date),
    INDEX idx_stat_date (stat_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='财务统计表';

-- 25. 系统操作日志表 (operation_logs)
DROP TABLE IF EXISTS operation_logs;
CREATE TABLE operation_logs (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    user_id VARCHAR(32) NOT NULL COMMENT '用户ID',
    operation_type VARCHAR(100) NOT NULL COMMENT '操作类型',
    operation_target VARCHAR(100) DEFAULT NULL COMMENT '操作对象',
    target_id VARCHAR(32) DEFAULT NULL COMMENT '对象ID',
    operation_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    ip_address VARCHAR(45) DEFAULT NULL COMMENT 'IP地址',
    user_agent TEXT COMMENT 'UA信息',
    request_method VARCHAR(10) DEFAULT NULL COMMENT '请求方法',
    request_path VARCHAR(500) DEFAULT NULL COMMENT '请求路径',
    request_params TEXT COMMENT '请求参数',
    response_code INT DEFAULT NULL COMMENT '响应码',
    response_time_ms INT DEFAULT NULL COMMENT '响应耗时',
    error_message TEXT COMMENT '错误信息',
    additional_info JSON DEFAULT NULL COMMENT '额外信息',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除标识',
    PRIMARY KEY (id),
    INDEX idx_user_id (user_id),
    INDEX idx_time (operation_time),
    INDEX idx_type (operation_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='系统操作日志表';

-- 26. AI 模型表 (ai_models)
DROP TABLE IF EXISTS ai_models;
CREATE TABLE ai_models (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    model_name VARCHAR(100) NOT NULL COMMENT '模型名称',
    model_key VARCHAR(100) NOT NULL COMMENT '模型标识',
    provider VARCHAR(50) DEFAULT NULL COMMENT '提供商',
    model_type VARCHAR(20) NOT NULL COMMENT '类型: TEXT, IMAGE等',
    api_endpoint VARCHAR(255) NOT NULL COMMENT '接口地址',
    api_config JSON DEFAULT NULL COMMENT 'API配置',
    billing_mode TINYINT NOT NULL DEFAULT 1 COMMENT '计费: 1-按次, 2-Token',
    unit_price DECIMAL(10, 4) DEFAULT NULL COMMENT '单价',
    is_active TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否启用',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除标识',
    PRIMARY KEY (id),
    UNIQUE KEY uk_model_key (model_key),
    INDEX idx_type (model_type),
    INDEX idx_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='AI模型表';

-- 27. 用户钱包表 (user_wallets)
DROP TABLE IF EXISTS user_wallets;
CREATE TABLE user_wallets (
    id VARCHAR(32) NOT NULL COMMENT '主键ID',
    user_id VARCHAR(32) NOT NULL COMMENT '用户ID',
    balance DECIMAL(15, 4) NOT NULL DEFAULT 0.0000 COMMENT '余额',
    total_recharged DECIMAL(15, 4) DEFAULT 0.0000 COMMENT '累计充值',
    total_consumed DECIMAL(15, 4) NOT NULL DEFAULT 0.0000 COMMENT '累计消费',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除标识',
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户钱包表';

-- 28. 交易流水表 (wallet_transactions)
DROP TABLE IF EXISTS wallet_transactions;
CREATE TABLE wallet_transactions (
    id VARCHAR(32) NOT NULL COMMENT '主键ID(业务流水ID)',
    wallet_id VARCHAR(32) NOT NULL COMMENT '钱包ID',
    type TINYINT NOT NULL COMMENT '类型: 1-充值, 2-消费, 3-退款',
    amount DECIMAL(15, 4) NOT NULL COMMENT '金额',
    related_task_id VARCHAR(32) DEFAULT NULL COMMENT '关联任务ID',
    description VARCHAR(255) DEFAULT NULL COMMENT '描述',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除标识',
    PRIMARY KEY (id),
    INDEX idx_wallet_id (wallet_id),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='交易流水表';

-- 29. 剪辑工程表 (video_edit_projects)
DROP TABLE IF EXISTS video_edit_projects;
CREATE TABLE video_edit_projects (
    id VARCHAR(32) NOT NULL COMMENT '主键ID(业务工程ID)',
    user_id VARCHAR(32) NOT NULL COMMENT '用户ID',
    project_name VARCHAR(100) NOT NULL DEFAULT 'untitled' COMMENT '工程名称',
    timeline_data JSON DEFAULT NULL COMMENT '时间轴数据',
    cover_image VARCHAR(255) DEFAULT NULL COMMENT '封面图',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 1-草稿, 2-合成中, 3-完成',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除标识',
    PRIMARY KEY (id),
    INDEX idx_user_id (user_id),
    INDEX idx_updated_at (updated_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='剪辑工程表';

