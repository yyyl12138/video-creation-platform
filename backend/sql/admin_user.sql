USE video_generation_platform;

INSERT INTO `roles` (`role_name`,`description`,`is_deleted`,`created_at`,`updated_at`)
SELECT 'ROLE_ADMIN','管理员',0,NOW(),NOW()
WHERE NOT EXISTS (SELECT 1 FROM `roles` WHERE `role_name`='ROLE_ADMIN' LIMIT 1);

INSERT INTO `users` (
  `id`,`username`,`email`,`password_hash`,`avatar_url`,`phone`,`last_login_time`,
  `status`,`role_id`,`created_at`,`updated_at`
)
SELECT
  'U2026030300001','admin','admin@example.com',
  '$2a$10$yysTnFHEVjsA7YfwMQu9m.cbCikMvMVz67SeXRAUPR9n.xzs6bM3u',
  NULL,NULL,NULL,1,
  (SELECT `id` FROM `roles` WHERE `role_name`='ROLE_ADMIN' LIMIT 1),
  NOW(),NOW()
WHERE NOT EXISTS (SELECT 1 FROM `users` WHERE `username`='admin' LIMIT 1);

SET @admin_user_id := (SELECT `id` FROM `users` WHERE `username`='admin' LIMIT 1);

INSERT INTO `user_wallets` (
  `id`,`user_id`,`balance`,`total_recharged`,`total_consumed`,
  `created_at`,`updated_at`,`is_deleted`
)
SELECT CONCAT('W', SUBSTRING(@admin_user_id, 2)), @admin_user_id,
  0.0000,0.0000,0.0000,NOW(),NOW(),0
WHERE @admin_user_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM `user_wallets` WHERE `user_id`=@admin_user_id LIMIT 1);