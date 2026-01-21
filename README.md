# 自动化短视频创作平台 - 前端项目

## 项目简介

本项目是自动化短视频创作平台的前端部分，采用 Vue 3 + Vite + Pinia 技术栈实现。

## 技术栈

- **框架**: Vue 3 (Composition API)
- **构建工具**: Vite 5
- **状态管理**: Pinia
- **路由**: Vue Router 4
- **样式**: CSS + CSS Variables
- **HTTP客户端**: Axios

## 功能模块

### 1. 用户认证
- [x] 用户注册 (支持手机号/邮箱)
- [x] 用户登录 (支持用户名/邮箱/手机号)
- [x] 忘记密码
- [x] 验证码发送 (60秒倒计时)
- [x] JWT Token认证

### 2. UI特性
- [x] 响应式设计
- [x] 深色主题
- [x] 密码显示/隐藏切换
- [x] 密码强度指示器

## 安装与运行

```bash
# 安装依赖
npm install

# 开发模式运行
npm run dev

# 构建生产版本
npm run build
```

## 环境配置

复制环境变量模板并配置：
```bash
cp .env.example .env
```

配置项说明：
- `VITE_API_URL`: 后端API地址

## API接口文档

详见 [docs/API_AUTH.md](docs/API_AUTH.md)

## 目录结构

```
src/
├── assets/
│   └── styles/          # 全局样式
├── components/          # 公共组件
│   ├── Toast.vue       # 提示组件
│   └── VerificationCode.vue  # 验证码组件
├── router/             # 路由配置
├── services/           # API服务
│   ├── api.js         # 模拟API
│   └── real-api.js    # 真实API
├── stores/             # Pinia状态管理
│   └── auth.js        # 认证状态
├── types/              # TypeScript类型定义
├── views/              # 页面组件
│   ├── Login.vue      # 登录页
│   ├── Register.vue   # 注册页
│   ├── ForgotPassword.vue  # 忘记密码页
│   └── Home.vue       # 首页
├── App.vue
└── main.js
```

## 与后端对接

1. 确保后端API已启动并配置正确的地址
2. 前端默认连接: `http://localhost:3001/api`
3. API响应格式遵循RESTful规范

### API端点
- `POST /api/auth/register` - 用户注册
- `POST /api/auth/login` - 用户登录
- `POST /api/auth/send-code` - 发送验证码
- `POST /api/auth/reset-password` - 重置密码
- `POST /api/auth/logout` - 注销登录

## 团队协作

1. 从主分支创建功能分支
2. 开发完成后提交Pull Request
3. 代码Review后合并到主分支

## 许可证

MIT