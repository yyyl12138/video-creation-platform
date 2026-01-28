-- update_pricing_strategy.sql
-- Update pricing for AI models based on realistic hypothetical costs (1 Coin = 1 CNY)

USE video_generation_platform;

-- 1. DeepSeek V3 (Text)
-- Billing: Per Token (Mode 2)
-- Cost: ~2 RMB / 1M tokens -> 0.002 RMB / 1k tokens
UPDATE ai_models 
SET billing_mode = 2, unit_price = 0.002 
WHERE model_key = 'deepseek-v3';

-- 2. Qwen Flash (Text)
-- Billing: Per Token (Mode 2)
-- Cost: Low cost
UPDATE ai_models 
SET billing_mode = 2, unit_price = 0.001 
WHERE model_key = 'qwen-flash';

-- 3. GLM-4.7 Flash (Text)
-- Billing: Per Token (Mode 2)
UPDATE ai_models 
SET billing_mode = 2, unit_price = 0.001 
WHERE model_key = 'glm-4.7-flash';

-- 4. Wanx v1 (Image)
-- Billing: Per Use (Mode 1)
-- Cost: ~0.1 RMB per image
UPDATE ai_models 
SET billing_mode = 1, unit_price = 0.1 
WHERE model_key = 'wanx2.1-t2v-turbo'; -- Note: Key name in markdown was 'wanx2.1-t2v-turbo' but labeled IMAGE. Assuming it's the image model.

-- 5. Kling (Video)
-- Billing: Per Use (Mode 1)
-- Cost: ~0.5 RMB per video
UPDATE ai_models 
SET billing_mode = 1, unit_price = 0.5 
WHERE model_key = 'kling 1.6';

-- 6. Minimax (Video)
-- Billing: Per Use (Mode 1)
UPDATE ai_models 
SET billing_mode = 1, unit_price = 0.5 
WHERE model_key = 'MiniMax-Hailuo-2.3-Fast';

-- 7. Volcengine (Video)
-- Billing: Per Use (Mode 1)
UPDATE ai_models 
SET billing_mode = 1, unit_price = 0.5 
WHERE model_key = 'doubao-seedance-1-0-lite-t2v';
