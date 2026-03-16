# 缺失接口分析报告 (Missing APIs Analysis)

基于 `@接口文档草稿-1.2.md` 与 `backend` 目录下的 Controller 实现情况比对，以下是各模块尚未实现（或完全缺失）的接口列表：

## 模块一：用户管理与权限模块 (User Management & Authority)
- **用户信息服务**
  - `POST /api/v1/users/me/vip-subscription` (开通/续费会员)
  - `DELETE /api/v1/users/me` (注销账户)
- **钱包资产服务**
  - `POST /api/v1/wallets/me/recharge` (发起充值)
  - `POST /api/v1/wallets/me/withdraw` (发起提现)
- **管理员用户管理** (整体缺失)
  - `GET /api/v1/admin/users` (获取用户列表)
  - `PUT /api/v1/admin/users/{userId}/status` (封禁/解封用户)
  - `GET /api/v1/admin/users/{userId}` (获取用户详情)

## 模块二：素材资源管理模块
*(该模块的 管理员素材管理、标签与分类服务、用户素材服务 均已基本实现)*
- **无明显缺失**

## 模块三：创作与任务处理模块 (Creation & Task Processing)
- **AI 辅助创作服务**
  - `GET /api/v1/creation/hotspots` (获取全网热点)
  - `POST /api/v1/creation/scripts/generate` (AI 生成/优化脚本)
- **模版管理服务** (整体缺失)
  - `POST /api/v1/templates` (创建/更新模版草稿)
  - `GET /api/v1/templates/me` (我的模版列表)
## 模块四：内容审核与安全模块 (Content Review & Security)
*(整体缺失)*
- `GET /api/v1/admin/reviews` (获取审核列表)
- `GET /api/v1/admin/reviews/{reviewId}` (获取审核详情)
- `PUT /api/v1/admin/reviews/{reviewId}/decision` (提交审核决定)
- `GET /api/v1/admin/review-rules` (获取规则列表)
- `POST /api/v1/admin/review-rules` (新增/修改规则)
- `PUT /api/v1/admin/review-rules/{ruleId}/status` (启用/禁用规则)
- `POST /api/v1/reports` (提交举报)

## 模块五：系统配置与模型管理模块 (System Config & Model Management)
*(整体缺失)*
- `GET /api/v1/admin/models` (获取模型列表)
- `GET /api/v1/admin/ai-models/{modelId}` (获取单个模型配置)
- `POST /api/v1/admin/models` (新增/修改模型配置)
- `PUT /api/v1/admin/models/{modelId}/status` (启用/停用模型)
- `GET /api/v1/admin/tts-configs` (获取 TTS 语音列表)
- `POST /api/v1/admin/tts-configs` (配置 TTS 参数)
- `GET /api/v1/admin/configs/{category}` (获取系统参数集合)
- `PUT /api/v1/admin/configs/storage` (更新存储配置)
- `PUT /api/v1/admin/configs/transcoding` (更新转码配置)
- `PUT /api/v1/admin/configs/scheduling` (更新任务调度配置)

## 模块六：数据中心与统计分析模块 (Data & Statistics)
*(整体缺失)*
- `GET /api/v1/admin/stats/overview` (获取核心指标卡片)
- `GET /api/v1/admin/stats/trend` (获取趋势图表数据)
- `GET /api/v1/admin/stats/tasks/distribution` (任务类型分布统计)
- `GET /api/v1/admin/stats/tasks/failure-analysis` (任务失败原因分析)
- `GET /api/v1/admin/stats/finance` (获取财务报表)
- `GET /api/v1/stats/creator/me` (获取个人核心数据)
- `GET /api/v1/stats/creator/templates/analysis` (模版表现分析)

## 模块七：模版市场与社区模块 (Template Marketplace & Community)
*(整体缺失)*
- `GET /api/v1/market/templates` (获取模版列表)
- `GET /api/v1/market/templates/{templateId}` (获取模版详情)
- `POST /api/v1/market/templates/{templateId}/purchase` (购买/获取模版)
- `POST /api/v1/interactions/like` (点赞/取消点赞)
- `POST /api/v1/interactions/follow` (关注/取关作者)
- `GET /api/v1/comments` (获取模版评价)
- `POST /api/v1/comments` (发表/回复评论)
- `GET /api/v1/notifications` (获取消息列表)

## 模块八：帮助与反馈模块 (Help & Feedback)
*(整体缺失)*
- `POST /api/v1/support/feedback` (提交意见反馈)

## 模块九：支付与订单中心 (Payment & Order Center)
*(整体缺失)*
- `POST /api/v1/orders` (创建支付订单)
- `GET /api/v1/orders/{orderId}` (查询订单状态)
- `POST /api/v1/callbacks/payment/{channel}` (支付回调通知)
- `POST /api/v1/orders/{orderId}/cancel` (取消订单)
