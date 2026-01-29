-- 测试账号SQL语句
-- 用户名: testuser
-- 密码: 123456
-- 邮箱: testuser@example.com

-- 1. 插入用户记录
INSERT INTO `users` (
    `id`,
    `username`,
    `email`,
    `password_hash`,
    `avatar_url`,
    `phone`,
    `last_login_time`,
    `status`,
    `role_id`,
    `created_at`,
    `updated_at`
) VALUES (
    'U2026012800001',                    -- 用户ID（格式：U + 日期 + 序号）
    'testuser',                           -- 用户名
    'testuser@example.com',               -- 邮箱
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',  -- 密码：123456 (BCrypt加密)
    NULL,                                 -- 头像URL（可选）
    '13800138000',                        -- 手机号（可选）
    NULL,                                 -- 最后登录时间
    1,                                    -- 状态：1-正常
    1,                                    -- 角色ID：1 (ROLE_USER)
    NOW(),                                -- 创建时间
    NOW()                                 -- 更新时间
);

-- 2. 插入用户钱包记录（注册时会自动创建钱包）
INSERT INTO `user_wallets` (
    `id`,
    `user_id`,
    `balance`,
    `total_recharged`,
    `total_consumed`,
    `created_at`,
    `updated_at`,
    `is_deleted`
) VALUES (
    'W2026012800001',                     -- 钱包ID（格式：W + 日期 + 序号）
    'U2026012800001',                     -- 用户ID（与上面的用户ID对应）
    0.0000,                               -- 余额：0
    0.0000,                               -- 累计充值：0
    0.0000,                               -- 累计消费：0
    NOW(),                                -- 创建时间
    NOW(),                                -- 更新时间
    0                                     -- 逻辑删除标识：0-未删除
);

-- 验证插入结果
SELECT * FROM `users` WHERE `username` = 'testuser';
SELECT * FROM `user_wallets` WHERE `user_id` = 'U2026012800001';
