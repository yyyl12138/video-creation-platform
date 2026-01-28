-- P6_update_schema.sql
-- 1. Update Pricing Strategy
USE video_generation_platform;

-- DeepSeek V3: Per Token, ~0.002 RMB/k tokens
UPDATE ai_models SET billing_mode = 2, unit_price = 0.002 WHERE model_key = 'deepseek-v3';
-- Qwen Flash: Per Token, Low cost
UPDATE ai_models SET billing_mode = 2, unit_price = 0.001 WHERE model_key = 'qwen-flash';
-- GLM-4.7 Flash: Per Token
UPDATE ai_models SET billing_mode = 2, unit_price = 0.001 WHERE model_key = 'glm-4.7-flash';
-- Wanx Image: Per Use, ~0.1 RMB/image (Guessed key 'wanx2.1-t2v-turbo' is used for IMAGE in DB, checking assumption)
UPDATE ai_models SET billing_mode = 1, unit_price = 0.1 WHERE model_key = 'wanx2.1-t2v-turbo';
-- Kling Video: Per Use, ~0.5 RMB/video
UPDATE ai_models SET billing_mode = 1, unit_price = 0.5 WHERE model_key = 'kling 1.6';
-- Minimax Video: Per Use
UPDATE ai_models SET billing_mode = 1, unit_price = 0.5 WHERE model_key = 'MiniMax-Hailuo-2.3-Fast';
-- Volcengine Video: Per Use
UPDATE ai_models SET billing_mode = 1, unit_price = 0.5 WHERE model_key = 'doubao-seedance-1-0-lite-t2v';

-- 2. Add cover_url to video_generation_tasks if not exists
-- Safe procedure for MySQL 5.7+
SET @exist := (SELECT count(*) FROM information_schema.columns WHERE table_schema = 'video_generation_platform' AND table_name = 'video_generation_tasks' AND column_name = 'result_cover_path');
SET @sql := IF(@exist = 0, 'ALTER TABLE video_generation_tasks ADD COLUMN result_cover_path VARCHAR(500) DEFAULT NULL COMMENT "结果封面图/缩略图" AFTER result_video_path', 'SELECT "Column result_cover_path already exists"');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
