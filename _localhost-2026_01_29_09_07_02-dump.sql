-- MySQL dump 10.13  Distrib 9.2.0, for macos15 (arm64)
--
-- Host: 127.0.0.1    Database: video_generation_platform
-- ------------------------------------------------------
-- Server version	9.2.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `ai_generation_tasks`
--

DROP TABLE IF EXISTS `ai_generation_tasks`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ai_generation_tasks` (
  `id` varchar(32) COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键ID(业务任务ID)',
  `user_id` varchar(32) COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户ID',
  `task_type` tinyint NOT NULL COMMENT '任务类型: 1-文生文, 2-文生图, 3-文生视频, 4-文加图生视频',
  `template_id` varchar(32) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '关联模板ID',
  `prompt` text COLLATE utf8mb4_general_ci COMMENT '用户提示词',
  `negative_prompt` text COLLATE utf8mb4_general_ci COMMENT '负面提示词',
  `input_config` json NOT NULL COMMENT '输入配置',
  `output_config` json DEFAULT NULL COMMENT '输出配置',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态: 1-等待中, 2-处理中, 3-成功, 4-失败, 5-取消',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间/开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间',
  `result_file_path` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '结果文件路径(图片/视频)',
  `result_cover_path` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '结果封面图路径',
  `result_file_size` bigint DEFAULT NULL COMMENT '结果文件大小',
  `error_message` text COLLATE utf8mb4_general_ci COMMENT '错误信息',
  `retry_count` int DEFAULT '0' COMMENT '重试次数',
  `priority` int DEFAULT '5' COMMENT '优先级',
  `model_id` bigint unsigned DEFAULT NULL COMMENT '模型ID',
  `external_task_id` varchar(128) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '第三方任务ID',
  `cost_token` decimal(10,4) DEFAULT NULL COMMENT '消耗Token',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除标识',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='ai生成任务表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ai_generation_tasks`
--

LOCK TABLES `ai_generation_tasks` WRITE;
/*!40000 ALTER TABLE `ai_generation_tasks` DISABLE KEYS */;
INSERT INTO `ai_generation_tasks` VALUES ('1598cf40faf0412d9b1eca4a907ce207','test-user-001',3,NULL,'一辆红色跑车在山间公路上飞驰',NULL,'{\"prompt\": \"一辆红色跑车在山间公路上飞驰\", \"duration\": \"5\", \"modelKey\": \"doubao-seedance-1-0-lite-t2v-250428\", \"resolution\": \"720p\"}','{}',3,'2026-01-27 18:50:20','2026-01-27 18:51:19','/creation_tasks/generations/1598cf40faf0412d9b1eca4a907ce207.mp4',NULL,NULL,NULL,0,5,7,'cgt-20260127185021-xn4tz',5.0000,0,'2026-01-27 18:50:20'),('21b1104af7b846eb8aab48d658be8720','test-user-001',3,NULL,'一辆红色跑车在山间公路上飞驰',NULL,'{\"prompt\": \"一辆红色跑车在山间公路上飞驰\", \"duration\": \"5\", \"modelKey\": \"doubao-seedance-1-0-lite-t2v-250428\", \"resolution\": \"720p\"}','{}',3,'2026-01-27 18:44:10','2026-01-27 18:44:39',NULL,NULL,NULL,NULL,0,5,7,'cgt-20260127184411-x2tx9',5.0000,0,'2026-01-27 18:44:10'),('46bafecdb0d84c7baad73e94f7153324','test-user-001',3,NULL,'一辆红色跑车在山间公路上飞驰',NULL,'{\"prompt\": \"一辆红色跑车在山间公路上飞驰\", \"duration\": \"5\", \"modelKey\": \"doubao-seedance-1-0-lite-t2v-250428\", \"resolution\": \"720p\"}','{}',3,'2026-01-27 18:39:48','2026-01-27 18:40:10',NULL,NULL,NULL,NULL,0,5,7,'cgt-20260127183950-wd4z2',5.0000,0,'2026-01-27 18:39:48'),('488cb85d168d4b9999a3df9912722dbc','test-user-001',3,NULL,'一个宇航员漂浮在太空中，背景是地球',NULL,'{\"prompt\": \"一个宇航员漂浮在太空中，背景是地球\", \"modelKey\": \"MiniMax-Hailuo-2.3\"}','{}',2,'2026-01-27 20:16:18',NULL,NULL,NULL,NULL,NULL,0,5,6,'360297697042820',5.0000,0,'2026-01-27 20:16:18'),('59a5bd2956264337b057e43c88f9ac11','test-user-001',2,NULL,'一只可爱的小猫在阳光下玩耍，水彩画风格','模糊, 低质量, 变形','{\"size\": \"1024*1024\", \"prompt\": \"一只可爱的小猫在阳光下玩耍，水彩画风格\", \"modelKey\": \"wanx-v1\", \"negativePrompt\": \"模糊, 低质量, 变形\"}','{}',3,'2026-01-27 17:21:25','2026-01-27 17:22:18','/creation_tasks/generations/59a5bd2956264337b057e43c88f9ac11.png',NULL,NULL,NULL,0,5,4,'b2fa74c9-52ab-472b-9c86-a2c4c05748af',1.0000,0,'2026-01-27 17:21:25'),('97bb9f708f5d4f1f854b25d521c69540','test-user-001',3,NULL,'一辆红色跑车在山间公路上飞驰',NULL,'{\"prompt\": \"一辆红色跑车在山间公路上飞驰\", \"duration\": \"5\", \"modelKey\": \"doubao-seedance-1-0-lite-t2v-250428\", \"resolution\": \"720p\"}','{}',3,'2026-01-27 18:31:15','2026-01-27 18:31:34',NULL,NULL,NULL,NULL,0,5,7,'cgt-20260127183117-4hvhx',5.0000,0,'2026-01-27 18:31:15'),('a97b989b476b48b6bca7479a30ead59f','test-user-001',3,NULL,'一辆红色跑车在山间公路上飞驰',NULL,'{\"prompt\": \"一辆红色跑车在山间公路上飞驰\", \"duration\": \"5\", \"modelKey\": \"doubao-seedance-1-0-lite-t2v-250428\", \"resolution\": \"720p\"}','{}',3,'2026-01-27 18:35:59','2026-01-27 18:36:28',NULL,NULL,NULL,NULL,0,5,7,'cgt-20260127183601-trhb7',5.0000,0,'2026-01-27 18:35:59'),('b7036822e53f4a4db11d64424293d0d7','test-user-001',3,NULL,'一个宇航员漂浮在太空中，背景是地球',NULL,'{\"prompt\": \"一个宇航员漂浮在太空中，背景是地球\", \"duration\": 6, \"modelKey\": \"MiniMax-Hailuo-2.3\", \"resolution\": \"1080P\"}','{}',3,'2026-01-27 20:32:53','2026-01-27 20:34:27',NULL,NULL,NULL,NULL,0,5,6,'360300219359316',5.0000,0,'2026-01-27 20:32:53'),('dc97860b0ff74e98a92ea6003b09b993','test-user-001',3,NULL,'一个宇航员漂浮在太空中，背景是地球',NULL,'{\"prompt\": \"一个宇航员漂浮在太空中，背景是地球\", \"duration\": 6, \"modelKey\": \"MiniMax-Hailuo-2.3\"}','{}',3,'2026-01-27 20:26:35','2026-01-27 20:28:31',NULL,NULL,NULL,NULL,0,5,6,'360300134470023',5.0000,0,'2026-01-27 20:26:35');
/*!40000 ALTER TABLE `ai_generation_tasks` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ai_models`
--

DROP TABLE IF EXISTS `ai_models`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ai_models` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `model_name` varchar(100) COLLATE utf8mb4_general_ci NOT NULL COMMENT '模型名称',
  `model_key` varchar(100) COLLATE utf8mb4_general_ci NOT NULL COMMENT '模型标识',
  `provider` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '提供商',
  `model_type` varchar(20) COLLATE utf8mb4_general_ci NOT NULL COMMENT '类型: TEXT, IMAGE等',
  `api_endpoint` varchar(255) COLLATE utf8mb4_general_ci NOT NULL COMMENT '接口地址',
  `api_config` json DEFAULT NULL COMMENT 'API配置',
  `billing_mode` tinyint NOT NULL DEFAULT '1' COMMENT '计费: 1-按次, 2-Token',
  `unit_price` decimal(10,4) DEFAULT NULL COMMENT '单价',
  `is_active` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否启用',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除标识',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_model_key` (`model_key`),
  KEY `idx_type` (`model_type`),
  KEY `idx_active` (`is_active`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='AI模型表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ai_models`
--

LOCK TABLES `ai_models` WRITE;
/*!40000 ALTER TABLE `ai_models` DISABLE KEYS */;
INSERT INTO `ai_models` VALUES (1,'DeepSeek V3','deepseek-v3','DEEPSEEK','TEXT','https://api.deepseek.com/chat/completions','{\"apiKey\": \"sk-bfa9eeee945e438fb797f7c8c03e2d07\"}',2,5.0000,0,'2026-01-21 17:16:36','2026-01-28 09:10:06',0),(2,'Qwen Flash','qwen-flash','ALIYUN','TEXT','https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions','{\"apiKey\": \"sk-545cec301e214b9a97bfee1042a7fb7f\"}',2,0.0010,1,'2026-01-21 17:16:36','2026-01-27 10:44:28',0),(3,'GLM Flash','glm-4.7-flash','ZHIPU','TEXT','https://open.bigmodel.cn/api/paas/v4/chat/completions','{\"apiKey\": \"6765a9cdfec44780aecaf33b8db0138d.UMblHyXEfCJSpui8\"}',2,0.0010,1,'2026-01-21 17:16:36','2026-01-28 09:13:17',0),(4,'Wanx','wanx-v1','ALIYUN','IMAGE','https://dashscope.aliyuncs.com/api/v1/services/aigc/text2image/image-synthesis','{\"apiKey\": \"sk-545cec301e214b9a97bfee1042a7fb7f\"}',1,1.0000,1,'2026-01-21 17:16:36','2026-01-28 09:13:17',0),(5,'Kling','kling 1.6','KUAISHOU','VIDEO','https://api.klingai.com/v1/videos/text2video','{\"accessKey\": \"ADBEBJpdPhbd388yRAfR8TbkJbNnkk3m\", \"secretKey\": \"bGhym3tT3hrNQ9epAP4fEGAPeRBDrTJP\"}',1,5.0000,0,'2026-01-21 17:16:36','2026-01-27 17:49:30',0),(6,'Minimax','MiniMax-Hailuo-2.3','MINIMAX','VIDEO','https://api.minimax.chat/v1/video_generation','{\"apiKey\": \"sk-api-I6J3Gf9sbv1zqySnlhWzBFFM7R2oPUxPTjLprEnokRHBqPvx1UJyfKXdA5x1hOBJHmGQespitx0mBR2yjGugPq8KWWTvJcI1MAPswyM-iI8wtaLDjgv7G7s\", \"groupId\": \"\"}',1,5.0000,1,'2026-01-21 17:16:36','2026-01-27 17:57:04',0),(7,'Doubao Seedance','doubao-seedance-1-0-lite-t2v-250428','VOLCENGINE','VIDEO','https://ark.cn-beijing.volces.com/api/v3','{\"accessKey\": \"7b0a222d-d6fe-4447-adb7-a73be1613198\", \"endpointId\": \"ep-m-20251230173313-5pdxt\"}',1,5.0000,1,'2026-01-21 17:16:36','2026-01-27 18:30:55',0);
/*!40000 ALTER TABLE `ai_models` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `audio_materials`
--

DROP TABLE IF EXISTS `audio_materials`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `audio_materials` (
  `id` varchar(32) COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键ID(业务音频ID)',
  `audio_name` varchar(200) COLLATE utf8mb4_general_ci NOT NULL COMMENT '音频名称',
  `type` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '类型',
  `duration_seconds` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '时长(秒)',
  `file_size` bigint DEFAULT '0' COMMENT '文件大小',
  `file_path` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '文件路径',
  `bitrate` int DEFAULT NULL COMMENT '比特率',
  `sample_rate` varchar(20) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '采样率',
  `uploader_id` varchar(32) COLLATE utf8mb4_general_ci NOT NULL COMMENT '上传者ID',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态: 1-正常, 2-封禁, 3-禁用',
  `copyright_status` tinyint NOT NULL DEFAULT '1' COMMENT '版权: 1-免费商用, 2-付费授权, 3-个人使用',
  `tags` text COLLATE utf8mb4_general_ci COMMENT '标签',
  `description` text COLLATE utf8mb4_general_ci COMMENT '描述',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除标识',
  PRIMARY KEY (`id`),
  KEY `idx_uploader_id` (`uploader_id`),
  KEY `idx_type` (`type`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='音频素材表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `audio_materials`
--

LOCK TABLES `audio_materials` WRITE;
/*!40000 ALTER TABLE `audio_materials` DISABLE KEYS */;
/*!40000 ALTER TABLE `audio_materials` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `content_reviews`
--

DROP TABLE IF EXISTS `content_reviews`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `content_reviews` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `content_id` varchar(32) COLLATE utf8mb4_general_ci NOT NULL COMMENT '内容ID(UGC表ID)',
  `reviewer_id` varchar(32) COLLATE utf8mb4_general_ci NOT NULL COMMENT '审核员ID(User表ID)',
  `review_status` tinyint NOT NULL DEFAULT '1' COMMENT '状态: 1-待审核, 2-通过, 3-驳回, 4-复审',
  `review_time` datetime DEFAULT NULL COMMENT '审核时间',
  `reject_reason` text COLLATE utf8mb4_general_ci COMMENT '驳回原因',
  `suggestions` text COLLATE utf8mb4_general_ci COMMENT '建议',
  `next_review_time` datetime DEFAULT NULL COMMENT '下次审核时间',
  `version` int NOT NULL DEFAULT '1' COMMENT '版本号',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除标识',
  PRIMARY KEY (`id`),
  KEY `idx_content_id` (`content_id`),
  KEY `idx_reviewer_id` (`reviewer_id`),
  KEY `idx_review_status` (`review_status`),
  KEY `idx_version` (`version`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='内容审核表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `content_reviews`
--

LOCK TABLES `content_reviews` WRITE;
/*!40000 ALTER TABLE `content_reviews` DISABLE KEYS */;
/*!40000 ALTER TABLE `content_reviews` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `creators`
--

DROP TABLE IF EXISTS `creators`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `creators` (
  `id` varchar(32) COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键ID(业务创作者ID)',
  `user_id` varchar(32) COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户ID',
  `platform_account` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '平台账号',
  `platform_type` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '平台类型',
  `follower_count` int NOT NULL DEFAULT '0' COMMENT '粉丝数',
  `verification_status` tinyint NOT NULL DEFAULT '1' COMMENT '认证状态: 1-未认证, 2-已认证, 3-认证中',
  `bio` text COLLATE utf8mb4_general_ci COMMENT '简介',
  `contact_info` json DEFAULT NULL COMMENT '联系信息',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除标识',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`),
  KEY `idx_verification_status` (`verification_status`),
  KEY `idx_follower_count` (`follower_count`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='创作者信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `creators`
--

LOCK TABLES `creators` WRITE;
/*!40000 ALTER TABLE `creators` DISABLE KEYS */;
/*!40000 ALTER TABLE `creators` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `daily_statistics`
--

DROP TABLE IF EXISTS `daily_statistics`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `daily_statistics` (
  `stat_date` date NOT NULL COMMENT '统计日期',
  `total_users` int NOT NULL DEFAULT '0' COMMENT '总用户数',
  `new_users` int NOT NULL DEFAULT '0' COMMENT '新增用户数',
  `active_users` int NOT NULL DEFAULT '0' COMMENT '活跃用户数',
  `total_video_generated` int NOT NULL DEFAULT '0' COMMENT '生成视频总数',
  `successful_tasks` int NOT NULL DEFAULT '0' COMMENT '成功任务数',
  `failed_tasks` int NOT NULL DEFAULT '0' COMMENT '失败任务数',
  `task_success_rate` decimal(5,2) NOT NULL DEFAULT '0.00' COMMENT '成功率',
  `total_storage_used` bigint NOT NULL DEFAULT '0' COMMENT '存储使用量',
  `peak_concurrent_tasks` int NOT NULL DEFAULT '0' COMMENT '峰值并发',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除标识',
  PRIMARY KEY (`stat_date`),
  KEY `idx_new_users` (`new_users`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='每日统计表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `daily_statistics`
--

LOCK TABLES `daily_statistics` WRITE;
/*!40000 ALTER TABLE `daily_statistics` DISABLE KEYS */;
/*!40000 ALTER TABLE `daily_statistics` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `financial_statistics`
--

DROP TABLE IF EXISTS `financial_statistics`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `financial_statistics` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `stat_period` varchar(20) COLLATE utf8mb4_general_ci NOT NULL COMMENT '周期: daily, weekly',
  `stat_date` date DEFAULT NULL COMMENT '统计日期',
  `total_revenue` decimal(15,2) NOT NULL DEFAULT '0.00' COMMENT '总营收',
  `total_expense` decimal(15,2) NOT NULL DEFAULT '0.00' COMMENT '总支出',
  `user_count` int NOT NULL DEFAULT '0' COMMENT '用户数',
  `premium_user_count` int NOT NULL DEFAULT '0' COMMENT '付费用户数',
  `task_revenue` decimal(15,2) NOT NULL DEFAULT '0.00' COMMENT '任务营收',
  `storage_revenue` decimal(15,2) NOT NULL DEFAULT '0.00' COMMENT '存储营收',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除标识',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_period_date` (`stat_period`,`stat_date`),
  KEY `idx_stat_date` (`stat_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='财务统计表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `financial_statistics`
--

LOCK TABLES `financial_statistics` WRITE;
/*!40000 ALTER TABLE `financial_statistics` DISABLE KEYS */;
/*!40000 ALTER TABLE `financial_statistics` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `image_materials`
--

DROP TABLE IF EXISTS `image_materials`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `image_materials` (
  `id` varchar(32) COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键ID(业务素材ID)',
  `image_name` varchar(200) COLLATE utf8mb4_general_ci NOT NULL COMMENT '图片名称',
  `category` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '分类',
  `file_path` varchar(500) COLLATE utf8mb4_general_ci NOT NULL COMMENT '文件路径',
  `file_size` bigint DEFAULT '0' COMMENT '文件大小(字节)',
  `resolution` varchar(20) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '分辨率',
  `format` varchar(10) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '格式',
  `uploader_id` varchar(32) COLLATE utf8mb4_general_ci NOT NULL COMMENT '上传者ID',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态: 1-正常, 2-封禁, 3-禁用',
  `copyright_status` tinyint NOT NULL DEFAULT '1' COMMENT '版权: 1-免费商用, 2-付费授权, 3-个人使用',
  `tags` text COLLATE utf8mb4_general_ci COMMENT '标签',
  `description` text COLLATE utf8mb4_general_ci COMMENT '描述',
  `source_type` tinyint NOT NULL DEFAULT '2' COMMENT '来源: 1-官方, 2-用户上传, 3-AI生成',
  `is_public` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否公开',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除标识',
  PRIMARY KEY (`id`),
  KEY `idx_uploader_id` (`uploader_id`),
  KEY `idx_category` (`category`),
  KEY `idx_status` (`status`),
  KEY `idx_copyright_status` (`copyright_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='图片素材表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `image_materials`
--

LOCK TABLES `image_materials` WRITE;
/*!40000 ALTER TABLE `image_materials` DISABLE KEYS */;
/*!40000 ALTER TABLE `image_materials` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `operation_logs`
--

DROP TABLE IF EXISTS `operation_logs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `operation_logs` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` varchar(32) COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户ID',
  `operation_type` varchar(100) COLLATE utf8mb4_general_ci NOT NULL COMMENT '操作类型',
  `operation_target` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '操作对象',
  `target_id` varchar(32) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '对象ID',
  `operation_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  `ip_address` varchar(45) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'IP地址',
  `user_agent` text COLLATE utf8mb4_general_ci COMMENT 'UA信息',
  `request_method` varchar(10) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '请求方法',
  `request_path` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '请求路径',
  `request_params` text COLLATE utf8mb4_general_ci COMMENT '请求参数',
  `response_code` int DEFAULT NULL COMMENT '响应码',
  `response_time_ms` int DEFAULT NULL COMMENT '响应耗时',
  `error_message` text COLLATE utf8mb4_general_ci COMMENT '错误信息',
  `additional_info` json DEFAULT NULL COMMENT '额外信息',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除标识',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_time` (`operation_time`),
  KEY `idx_type` (`operation_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='系统操作日志表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `operation_logs`
--

LOCK TABLES `operation_logs` WRITE;
/*!40000 ALTER TABLE `operation_logs` DISABLE KEYS */;
/*!40000 ALTER TABLE `operation_logs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `permissions`
--

DROP TABLE IF EXISTS `permissions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `permissions` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `permission_code` varchar(100) COLLATE utf8mb4_general_ci NOT NULL COMMENT '权限编码，唯一',
  `permission_name` varchar(100) COLLATE utf8mb4_general_ci NOT NULL COMMENT '权限名称',
  `category` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '权限分类',
  `description` text COLLATE utf8mb4_general_ci COMMENT '权限描述',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除标识',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_permission_code` (`permission_code`),
  KEY `idx_category` (`category`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='权限表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `permissions`
--

LOCK TABLES `permissions` WRITE;
/*!40000 ALTER TABLE `permissions` DISABLE KEYS */;
/*!40000 ALTER TABLE `permissions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `review_rules`
--

DROP TABLE IF EXISTS `review_rules`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `review_rules` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `rule_name` varchar(100) COLLATE utf8mb4_general_ci NOT NULL COMMENT '规则名称',
  `rule_type` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '规则类型',
  `conditions` json NOT NULL COMMENT '审核条件',
  `actions` json NOT NULL COMMENT '审核动作',
  `priority` int NOT NULL DEFAULT '5' COMMENT '优先级',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态: 1-启用, 2-禁用',
  `creator_id` varchar(32) COLLATE utf8mb4_general_ci NOT NULL COMMENT '创建者ID',
  `description` text COLLATE utf8mb4_general_ci COMMENT '描述',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除标识',
  PRIMARY KEY (`id`),
  KEY `idx_creator_id` (`creator_id`),
  KEY `idx_rule_type` (`rule_type`),
  KEY `idx_priority` (`priority`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='审核规则表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `review_rules`
--

LOCK TABLES `review_rules` WRITE;
/*!40000 ALTER TABLE `review_rules` DISABLE KEYS */;
/*!40000 ALTER TABLE `review_rules` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `role_permissions`
--

DROP TABLE IF EXISTS `role_permissions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `role_permissions` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `role_id` bigint unsigned NOT NULL COMMENT '角色ID',
  `permission_id` bigint unsigned NOT NULL COMMENT '权限ID',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除标识',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_permission` (`role_id`,`permission_id`),
  KEY `idx_role_id` (`role_id`),
  KEY `idx_permission_id` (`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='角色权限关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `role_permissions`
--

LOCK TABLES `role_permissions` WRITE;
/*!40000 ALTER TABLE `role_permissions` DISABLE KEYS */;
/*!40000 ALTER TABLE `role_permissions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `roles`
--

DROP TABLE IF EXISTS `roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `roles` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `role_name` varchar(50) COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色名称，唯一',
  `description` text COLLATE utf8mb4_general_ci COMMENT '角色描述',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除标识',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_name` (`role_name`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='角色表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `roles`
--

LOCK TABLES `roles` WRITE;
/*!40000 ALTER TABLE `roles` DISABLE KEYS */;
INSERT INTO `roles` VALUES (1,'ROLE_USER','普通用户','2026-01-27 15:16:21','2026-01-27 15:16:21',0);
/*!40000 ALTER TABLE `roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `storage_configs`
--

DROP TABLE IF EXISTS `storage_configs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `storage_configs` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `storage_type` varchar(50) COLLATE utf8mb4_general_ci NOT NULL COMMENT '存储类型',
  `base_path` varchar(500) COLLATE utf8mb4_general_ci NOT NULL COMMENT '基础路径',
  `max_capacity` bigint DEFAULT NULL COMMENT '最大容量',
  `current_usage` bigint NOT NULL DEFAULT '0' COMMENT '当前使用量',
  `file_types` json DEFAULT NULL COMMENT '允许文件类型',
  `retention_days` int DEFAULT NULL COMMENT '保留天数',
  `backup_policy` json DEFAULT NULL COMMENT '备份策略',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除标识',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_storage_type` (`storage_type`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='存储路径配置表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `storage_configs`
--

LOCK TABLES `storage_configs` WRITE;
/*!40000 ALTER TABLE `storage_configs` DISABLE KEYS */;
/*!40000 ALTER TABLE `storage_configs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `system_configs`
--

DROP TABLE IF EXISTS `system_configs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_configs` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `config_key` varchar(100) COLLATE utf8mb4_general_ci NOT NULL COMMENT '配置键，唯一',
  `config_value` text COLLATE utf8mb4_general_ci NOT NULL COMMENT '配置值',
  `config_type` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '配置类型',
  `category` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '分类',
  `description` text COLLATE utf8mb4_general_ci COMMENT '描述',
  `is_public` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否公开',
  `created_by` varchar(32) COLLATE utf8mb4_general_ci NOT NULL COMMENT '创建人ID',
  `updated_by` varchar(32) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '更新人ID',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除标识',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_config_key` (`config_key`),
  KEY `idx_category` (`category`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='系统配置表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `system_configs`
--

LOCK TABLES `system_configs` WRITE;
/*!40000 ALTER TABLE `system_configs` DISABLE KEYS */;
/*!40000 ALTER TABLE `system_configs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `task_resources`
--

DROP TABLE IF EXISTS `task_resources`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `task_resources` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `task_id` varchar(32) COLLATE utf8mb4_general_ci NOT NULL COMMENT '任务ID',
  `resource_type` tinyint NOT NULL COMMENT '资源类型: 1-图片, 2-视频, 3-音频',
  `resource_id` varchar(32) COLLATE utf8mb4_general_ci NOT NULL COMMENT '资源业务ID',
  `usage_type` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '使用类型',
  `sequence` int DEFAULT NULL COMMENT '顺序',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除标识',
  PRIMARY KEY (`id`),
  KEY `idx_task_id` (`task_id`),
  KEY `idx_resource` (`resource_type`,`resource_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='任务资源关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `task_resources`
--

LOCK TABLES `task_resources` WRITE;
/*!40000 ALTER TABLE `task_resources` DISABLE KEYS */;
/*!40000 ALTER TABLE `task_resources` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `task_scheduling_configs`
--

DROP TABLE IF EXISTS `task_scheduling_configs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `task_scheduling_configs` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `max_concurrent_transcoding` int NOT NULL DEFAULT '5' COMMENT '并发转码数',
  `max_concurrent_generation` int NOT NULL DEFAULT '3' COMMENT '并发生成数',
  `task_queue_limit` int NOT NULL DEFAULT '50' COMMENT '队列限制',
  `cpu_threshold_percent` int NOT NULL DEFAULT '80' COMMENT 'CPU阈值',
  `memory_threshold_percent` int NOT NULL DEFAULT '80' COMMENT '内存阈值',
  `retry_policy` json DEFAULT NULL COMMENT '重试策略',
  `timeout_policy` json DEFAULT NULL COMMENT '超时策略',
  `schedule_algorithm` varchar(50) COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'FIFO' COMMENT '调度算法',
  `description` text COLLATE utf8mb4_general_ci COMMENT '描述',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除标识',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='任务调度配置表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `task_scheduling_configs`
--

LOCK TABLES `task_scheduling_configs` WRITE;
/*!40000 ALTER TABLE `task_scheduling_configs` DISABLE KEYS */;
/*!40000 ALTER TABLE `task_scheduling_configs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `task_statistics`
--

DROP TABLE IF EXISTS `task_statistics`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `task_statistics` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `task_type` tinyint NOT NULL COMMENT '任务类型: 1-文生文, 2-文生图, 3-文生视频, 4-文加图生视频',
  `stat_date` date DEFAULT NULL COMMENT '统计日期',
  `total_count` int NOT NULL DEFAULT '0' COMMENT '总任务数',
  `success_count` int NOT NULL DEFAULT '0' COMMENT '成功数',
  `fail_count` int NOT NULL DEFAULT '0' COMMENT '失败数',
  `avg_duration_seconds` int DEFAULT NULL COMMENT '平均耗时',
  `peak_hour` int DEFAULT NULL COMMENT '峰值时段',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除标识',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_type_date` (`task_type`,`stat_date`),
  KEY `idx_stat_date` (`stat_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='任务统计表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `task_statistics`
--

LOCK TABLES `task_statistics` WRITE;
/*!40000 ALTER TABLE `task_statistics` DISABLE KEYS */;
/*!40000 ALTER TABLE `task_statistics` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `transcoding_configs`
--

DROP TABLE IF EXISTS `transcoding_configs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `transcoding_configs` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `config_name` varchar(100) COLLATE utf8mb4_general_ci NOT NULL COMMENT '配置名称',
  `video_codec` varchar(50) COLLATE utf8mb4_general_ci NOT NULL COMMENT '视频编码',
  `audio_codec` varchar(50) COLLATE utf8mb4_general_ci NOT NULL COMMENT '音频编码',
  `video_bitrate_kbps` int NOT NULL COMMENT '视频码率',
  `audio_bitrate_kbps` int NOT NULL COMMENT '音频码率',
  `frame_rate` int NOT NULL COMMENT '帧率',
  `resolution_preset` json DEFAULT NULL COMMENT '分辨率预设',
  `quality_preset` tinyint NOT NULL DEFAULT '2' COMMENT '质量: 1-低, 2-中, 3-高, 4-自定义',
  `is_default` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否默认',
  `description` text COLLATE utf8mb4_general_ci COMMENT '描述',
  `created_by` varchar(32) COLLATE utf8mb4_general_ci NOT NULL COMMENT '创建人ID',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除标识',
  PRIMARY KEY (`id`),
  KEY `idx_created_by` (`created_by`),
  KEY `idx_is_default` (`is_default`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='转码配置表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transcoding_configs`
--

LOCK TABLES `transcoding_configs` WRITE;
/*!40000 ALTER TABLE `transcoding_configs` DISABLE KEYS */;
/*!40000 ALTER TABLE `transcoding_configs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tts_configs`
--

DROP TABLE IF EXISTS `tts_configs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tts_configs` (
  `id` varchar(32) COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键ID',
  `model_name` varchar(100) COLLATE utf8mb4_general_ci NOT NULL COMMENT '模型名称',
  `voice_type` varchar(20) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '声音类型',
  `language_type` varchar(50) COLLATE utf8mb4_general_ci NOT NULL COMMENT '语言类型',
  `speed_words_per_min` int DEFAULT NULL COMMENT '语速',
  `sample_rate` varchar(20) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '采样率',
  `model_file_path` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '模型路径',
  `config_json` json DEFAULT NULL COMMENT '配置详情',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态: 1-启用, 2-禁用',
  `creator_id` varchar(32) COLLATE utf8mb4_general_ci NOT NULL COMMENT '创建者ID',
  `description` text COLLATE utf8mb4_general_ci COMMENT '描述',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除标识',
  PRIMARY KEY (`id`),
  KEY `idx_creator_id` (`creator_id`),
  KEY `idx_voice_type` (`voice_type`),
  KEY `idx_language_type` (`language_type`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='TTS 配置表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tts_configs`
--

LOCK TABLES `tts_configs` WRITE;
/*!40000 ALTER TABLE `tts_configs` DISABLE KEYS */;
/*!40000 ALTER TABLE `tts_configs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_activity_logs`
--

DROP TABLE IF EXISTS `user_activity_logs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_activity_logs` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` varchar(32) COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户ID',
  `activity_type` varchar(50) COLLATE utf8mb4_general_ci NOT NULL COMMENT '活动类型',
  `description` text COLLATE utf8mb4_general_ci COMMENT '描述',
  `activity_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '活动时间',
  `ip_address` varchar(45) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'IP地址',
  `user_agent` text COLLATE utf8mb4_general_ci COMMENT 'UA信息',
  `duration_seconds` int DEFAULT NULL COMMENT '时长',
  `resource_id` varchar(32) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '资源ID',
  `additional_info` json DEFAULT NULL COMMENT '额外信息',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除标识',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_activity_time` (`activity_time`),
  KEY `idx_activity_type` (`activity_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户活跃度表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_activity_logs`
--

LOCK TABLES `user_activity_logs` WRITE;
/*!40000 ALTER TABLE `user_activity_logs` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_activity_logs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_generated_content`
--

DROP TABLE IF EXISTS `user_generated_content`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_generated_content` (
  `id` varchar(32) COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键ID(业务内容ID)',
  `creator_id` varchar(32) COLLATE utf8mb4_general_ci NOT NULL COMMENT '创作者ID(creators表ID)',
  `content_name` varchar(200) COLLATE utf8mb4_general_ci NOT NULL COMMENT '内容名称',
  `task_id` varchar(32) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '关联任务ID',
  `format` varchar(20) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '格式',
  `resolution` varchar(20) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '分辨率',
  `file_path` varchar(500) COLLATE utf8mb4_general_ci NOT NULL COMMENT '文件路径',
  `file_size` bigint DEFAULT '0' COMMENT '文件大小',
  `thumbnail_path` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '缩略图',
  `description` text COLLATE utf8mb4_general_ci COMMENT '描述',
  `tags` text COLLATE utf8mb4_general_ci COMMENT '标签',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除标识',
  PRIMARY KEY (`id`),
  KEY `idx_creator_id` (`creator_id`),
  KEY `idx_task_id` (`task_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户生成内容表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_generated_content`
--

LOCK TABLES `user_generated_content` WRITE;
/*!40000 ALTER TABLE `user_generated_content` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_generated_content` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_profiles`
--

DROP TABLE IF EXISTS `user_profiles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_profiles` (
  `id` varchar(32) COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键ID',
  `user_id` varchar(32) COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户ID',
  `real_name` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '真实姓名',
  `gender` tinyint DEFAULT '0' COMMENT '性别: 0-未知, 1-男, 2-女',
  `birthday` date DEFAULT NULL COMMENT '出生日期',
  `country` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '国家',
  `city` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '城市',
  `bio` text COLLATE utf8mb4_general_ci COMMENT '个人简介',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除标识',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`),
  KEY `idx_country_city` (`country`,`city`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户详情表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_profiles`
--

LOCK TABLES `user_profiles` WRITE;
/*!40000 ALTER TABLE `user_profiles` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_profiles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_wallets`
--

DROP TABLE IF EXISTS `user_wallets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_wallets` (
  `id` varchar(32) COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键ID',
  `user_id` varchar(32) COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户ID',
  `balance` decimal(15,4) NOT NULL DEFAULT '0.0000' COMMENT '余额',
  `total_recharged` decimal(15,4) DEFAULT '0.0000' COMMENT '累计充值',
  `total_consumed` decimal(15,4) NOT NULL DEFAULT '0.0000' COMMENT '累计消费',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除标识',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户钱包表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_wallets`
--

LOCK TABLES `user_wallets` WRITE;
/*!40000 ALTER TABLE `user_wallets` DISABLE KEYS */;
INSERT INTO `user_wallets` VALUES ('wallet-test-001','test-user-001',957.7310,1000.0000,42.2690,'2026-01-27 15:16:21','2026-01-27 20:32:53',0);
/*!40000 ALTER TABLE `user_wallets` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` varchar(32) COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键ID(业务用户ID)',
  `username` varchar(50) COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户名',
  `email` varchar(100) COLLATE utf8mb4_general_ci NOT NULL COMMENT '邮箱',
  `password_hash` varchar(255) COLLATE utf8mb4_general_ci NOT NULL COMMENT '密码哈希',
  `avatar_url` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '头像URL',
  `phone` varchar(20) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '手机号',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态: 1-正常, 2-封禁, 3-注销',
  `role_id` bigint unsigned NOT NULL COMMENT '角色ID',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_email` (`email`),
  KEY `idx_role_id` (`role_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES ('test-user-001','test_user','test@example.com','$2a$10$xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx',NULL,NULL,NULL,1,1,'2026-01-27 15:16:21','2026-01-27 15:16:21');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `video_edit_projects`
--

DROP TABLE IF EXISTS `video_edit_projects`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `video_edit_projects` (
  `id` varchar(32) COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键ID(业务工程ID)',
  `user_id` varchar(32) COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户ID',
  `project_name` varchar(100) COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'untitled' COMMENT '工程名称',
  `timeline_data` json DEFAULT NULL COMMENT '时间轴数据',
  `cover_image` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '封面图',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态: 1-草稿, 2-合成中, 3-完成',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除标识',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_updated_at` (`updated_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='剪辑工程表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `video_edit_projects`
--

LOCK TABLES `video_edit_projects` WRITE;
/*!40000 ALTER TABLE `video_edit_projects` DISABLE KEYS */;
/*!40000 ALTER TABLE `video_edit_projects` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `video_materials`
--

DROP TABLE IF EXISTS `video_materials`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `video_materials` (
  `id` varchar(32) COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键ID(业务视频ID)',
  `video_name` varchar(200) COLLATE utf8mb4_general_ci NOT NULL COMMENT '视频名称',
  `type` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '视频类型',
  `resolution` varchar(50) COLLATE utf8mb4_general_ci NOT NULL COMMENT '分辨率',
  `format` varchar(20) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '格式',
  `duration_seconds` decimal(10,2) DEFAULT '0.00' COMMENT '时长(秒)',
  `file_path` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '文件路径',
  `file_size` bigint DEFAULT '0' COMMENT '文件大小',
  `uploader_id` varchar(32) COLLATE utf8mb4_general_ci NOT NULL COMMENT '上传者ID',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态: 1-正常, 2-封禁, 3-禁用',
  `copyright_status` tinyint NOT NULL DEFAULT '1' COMMENT '版权: 1-免费商用, 2-付费授权, 3-个人使用',
  `tags` text COLLATE utf8mb4_general_ci COMMENT '标签',
  `description` text COLLATE utf8mb4_general_ci COMMENT '描述',
  `source_type` tinyint NOT NULL DEFAULT '2' COMMENT '来源: 1-官方, 2-用户上传, 3-AI生成',
  `is_public` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否公开',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除标识',
  PRIMARY KEY (`id`),
  KEY `idx_uploader_id` (`uploader_id`),
  KEY `idx_type` (`type`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='视频素材表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `video_materials`
--

LOCK TABLES `video_materials` WRITE;
/*!40000 ALTER TABLE `video_materials` DISABLE KEYS */;
/*!40000 ALTER TABLE `video_materials` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `video_templates`
--

DROP TABLE IF EXISTS `video_templates`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `video_templates` (
  `id` varchar(32) COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键ID(业务模板ID)',
  `template_name` varchar(200) COLLATE utf8mb4_general_ci NOT NULL COMMENT '模板名称',
  `type` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '类型',
  `aspect_ratio` varchar(20) COLLATE utf8mb4_general_ci NOT NULL COMMENT '宽高比',
  `preview_image_path` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '预览图路径',
  `template_file_path` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '模板文件路径',
  `creator_id` varchar(32) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建者ID',
  `usage_count` int DEFAULT '0' COMMENT '使用次数',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态: 1-启用, 2-禁用, 3-草稿',
  `tags` text COLLATE utf8mb4_general_ci COMMENT '标签',
  `description` text COLLATE utf8mb4_general_ci COMMENT '描述',
  `config_json` json DEFAULT NULL COMMENT '模板配置',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除标识',
  PRIMARY KEY (`id`),
  KEY `idx_creator_id` (`creator_id`),
  KEY `idx_type` (`type`),
  KEY `idx_status` (`status`),
  KEY `idx_usage_count` (`usage_count`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='视频模板表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `video_templates`
--

LOCK TABLES `video_templates` WRITE;
/*!40000 ALTER TABLE `video_templates` DISABLE KEYS */;
/*!40000 ALTER TABLE `video_templates` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `wallet_transactions`
--

DROP TABLE IF EXISTS `wallet_transactions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `wallet_transactions` (
  `id` varchar(32) COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键ID(业务流水ID)',
  `wallet_id` varchar(32) COLLATE utf8mb4_general_ci NOT NULL COMMENT '钱包ID',
  `type` tinyint NOT NULL COMMENT '类型: 1-充值, 2-消费, 3-退款',
  `amount` decimal(15,4) NOT NULL COMMENT '金额',
  `related_task_id` varchar(32) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '关联任务ID',
  `description` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '描述',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除标识',
  PRIMARY KEY (`id`),
  KEY `idx_wallet_id` (`wallet_id`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='交易流水表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `wallet_transactions`
--

LOCK TABLES `wallet_transactions` WRITE;
/*!40000 ALTER TABLE `wallet_transactions` DISABLE KEYS */;
/*!40000 ALTER TABLE `wallet_transactions` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-01-29  9:07:02
