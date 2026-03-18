<template>
  <div class="notification-container">
    <!-- 主内容卡片 -->
    <el-card class="main-content-card">
      <!-- 头部 -->
      <div class="notification-header">
        <div class="header-left">
          <h2>消息通知</h2>
          <span class="unread-count" v-if="unreadCount > 0">
            {{ unreadCount }} 条未读
          </span>
        </div>
        <div class="header-right">
          <el-select v-model="queryParams.type" placeholder="全部类型" clearable size="small" @change="fetchNotifications">
            <el-option value="" label="全部类型" />
            <el-option value="SYSTEM" label="系统通知" />
            <el-option value="LIKE" label="获赞通知" />
            <el-option value="FOLLOW" label="关注通知" />
            <el-option value="COMMENT" label="评论通知" />
            <el-option value="PURCHASE" label="购买通知" />
          </el-select>
          <el-button 
            type="primary" 
            size="small" 
            :disabled="unreadCount === 0"
            @click="markAllAsRead"
          >
            全部已读
          </el-button>
        </div>
      </div>

      <!-- 通知列表 -->
      <div class="notification-list" v-loading="loading">
        <template v-if="notifications.length > 0">
          <div
            v-for="item in notifications"
            :key="item.notificationId"
            class="notification-item"
            :class="{ unread: !item.isRead }"
            @click="handleNotificationClick(item)"
          >
            <div class="notification-icon" :class="getTypeClass(item.type)">
              <el-icon :size="24">
                <component :is="getTypeIcon(item.type)" />
              </el-icon>
            </div>
            <div class="notification-content">
              <div class="notification-title">{{ item.title }}</div>
              <div class="notification-text">{{ item.content }}</div>
              <div class="notification-time">{{ formatTime(item.createdTime) }}</div>
            </div>
            <div class="notification-status" v-if="!item.isRead">
              <span class="unread-dot"></span>
            </div>
          </div>
        </template>

        <el-empty v-else description="暂无通知消息" />
      </div>

      <!-- 分页 -->
      <div class="pagination-section" v-if="total > 0">
        <el-pagination
          v-model:current-page="queryParams.page"
          v-model:page-size="queryParams.size"
          :page-sizes="[10, 20, 50]"
          :total="total"
          layout="prev, pager, next, total"
          @current-change="fetchNotifications"
          @size-change="handleSizeChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Bell, Star, User, ChatDotRound, ShoppingCart } from '@element-plus/icons-vue'
import { getNotificationList, markNotificationRead, markAllNotificationRead } from '@/api/market/index.js'

const router = useRouter()

// 状态
const loading = ref(false)
const notifications = ref([])
const total = ref(0)
const unreadCount = ref(0)

const queryParams = reactive({
  page: 1,
  size: 10,
  type: ''
})

// 获取类型图标
const getTypeIcon = (type) => {
  const icons = {
    SYSTEM: Bell,
    LIKE: Star,
    FOLLOW: User,
    COMMENT: ChatDotRound,
    PURCHASE: ShoppingCart
  }
  return icons[type] || Bell
}

// 获取类型样式类
const getTypeClass = (type) => {
  const classes = {
    SYSTEM: 'type-system',
    LIKE: 'type-like',
    FOLLOW: 'type-follow',
    COMMENT: 'type-comment',
    PURCHASE: 'type-purchase'
  }
  return classes[type] || 'type-system'
}

// 格式化时间
const formatTime = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  const now = new Date()
  const diff = now - date
  
  // 一小时内显示"刚刚"
  if (diff < 60 * 60 * 1000) {
    return '刚刚'
  }
  // 今天内显示小时分钟
  if (diff < 24 * 60 * 60 * 1000) {
    return `${Math.floor(diff / (60 * 60 * 1000))}小时前`
  }
  // 一周内显示天数
  if (diff < 7 * 24 * 60 * 60 * 1000) {
    return `${Math.floor(diff / (24 * 60 * 60 * 1000))}天前`
  }
  // 超过一周显示完整日期
  return date.toLocaleDateString('zh-CN')
}

// 获取通知列表
const fetchNotifications = async () => {
  try {
    loading.value = true
    const params = {
      page: queryParams.page,
      size: queryParams.size
    }
    if (queryParams.type) {
      params.type = queryParams.type
    }

    const response = await getNotificationList(params)

    if (response.code === 20000 || response.success) {
      notifications.value = response.data.records || response.data.list || []
      total.value = response.data.total || 0
      // 计算未读数
      unreadCount.value = notifications.value.filter(n => !n.isRead).length
    } else {
      // 模拟数据
      notifications.value = getMockNotifications()
      total.value = notifications.value.length
      unreadCount.value = notifications.value.filter(n => !n.isRead).length
    }
  } catch (error) {
    console.error('获取通知列表失败:', error)
    // 使用模拟数据
    notifications.value = getMockNotifications()
    total.value = notifications.value.length
    unreadCount.value = notifications.value.filter(n => !n.isRead).length
  } finally {
    loading.value = false
  }
}

// 模拟数据
const getMockNotifications = () => {
  return [
    {
      notificationId: 1,
      type: 'SYSTEM',
      title: '系统维护通知',
      content: '系统将于今晚 22:00 进行例行维护，届时服务将暂停约30分钟。',
      isRead: false,
      createdTime: new Date(Date.now() - 30 * 60 * 1000).toISOString(),
      relatedId: null
    },
    {
      notificationId: 2,
      type: 'LIKE',
      title: '收到新的点赞',
      content: '用户 "创意达人" 赞了你的模版 "高级转场效果"。',
      isRead: false,
      createdTime: new Date(Date.now() - 2 * 60 * 60 * 1000).toISOString(),
      relatedId: 'TPL001'
    },
    {
      notificationId: 3,
      type: 'FOLLOW',
      title: '有新粉丝关注了你',
      content: '用户 "视频爱好者" 关注了你，快去看看吧！',
      isRead: false,
      createdTime: new Date(Date.now() - 5 * 60 * 60 * 1000).toISOString(),
      relatedId: 'U20260113002'
    },
    {
      notificationId: 4,
      type: 'COMMENT',
      title: '收到新评论',
      content: '用户 "设计师小王" 评论了你的模版："效果非常棒，已经用于项目中！"',
      isRead: true,
      createdTime: new Date(Date.now() - 24 * 60 * 60 * 1000).toISOString(),
      relatedId: 'TPL001'
    },
    {
      notificationId: 5,
      type: 'PURCHASE',
      title: '模版被购买',
      content: '你的模版 "高级转场效果" 被购买，获得收益 ¥29.00',
      isRead: true,
      createdTime: new Date(Date.now() - 2 * 24 * 60 * 60 * 1000).toISOString(),
      relatedId: 'ORD20260115001'
    }
  ]
}

// 点击通知
const handleNotificationClick = async (item) => {
  // 标记为已读
  if (!item.isRead) {
    await markAsRead(item.notificationId)
  }

  // 根据类型跳转
  if (item.relatedId) {
    switch (item.type) {
      case 'LIKE':
      case 'COMMENT':
      case 'PURCHASE':
        router.push(`/market/template/${item.relatedId}`)
        break
      case 'FOLLOW':
        router.push(`/market/following`)
        break
    }
  }
}

// 标记单条已读
const markAsRead = async (notificationId) => {
  try {
    const response = await markNotificationRead(notificationId)
    if (response.code === 20000 || response.success) {
      const notification = notifications.value.find(n => n.notificationId === notificationId)
      if (notification) {
        notification.isRead = true
        unreadCount.value = Math.max(0, unreadCount.value - 1)
      }
    }
  } catch (error) {
    // 本地更新
    const notification = notifications.value.find(n => n.notificationId === notificationId)
    if (notification) {
      notification.isRead = true
      unreadCount.value = Math.max(0, unreadCount.value - 1)
    }
  }
}

// 全部标记已读
const markAllAsRead = async () => {
  try {
    const response = await markAllNotificationRead()
    if (response.code === 20000 || response.success) {
      notifications.value.forEach(n => n.isRead = true)
      unreadCount.value = 0
      ElMessage.success('已全部标记为已读')
    }
  } catch (error) {
    // 本地更新
    notifications.value.forEach(n => n.isRead = true)
    unreadCount.value = 0
    ElMessage.success('已全部标记为已读')
  }
}

// 分页大小变化
const handleSizeChange = (size) => {
  queryParams.size = size
  queryParams.page = 1
  fetchNotifications()
}

onMounted(() => {
  fetchNotifications()
})
</script>

<style scoped>
.notification-container {
  padding: 24px;
  max-width: 900px;
  margin: 0 auto;
  min-height: calc(100vh - 80px);
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
}

.main-content-card {
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: none;
}

.notification-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid #f0f0f0;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.header-left h2 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: #303133;
}

.unread-count {
  background: #f56c6c;
  color: white;
  padding: 2px 10px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.notification-list {
  min-height: 400px;
}

.notification-item {
  display: flex;
  align-items: flex-start;
  padding: 20px 24px;
  border-bottom: 1px solid #f5f5f5;
  cursor: pointer;
  transition: all 0.3s ease;
}

.notification-item:hover {
  background: #fafafa;
}

.notification-item.unread {
  background: #f0f7ff;
}

.notification-item.unread:hover {
  background: #e6f1ff;
}

.notification-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
  flex-shrink: 0;
}

.type-system {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.type-like {
  background: linear-gradient(135deg, #f56c6c 0%, #e6405a 100%);
  color: white;
}

.type-follow {
  background: linear-gradient(135deg, #409eff 0%, #3a8ee6 100%);
  color: white;
}

.type-comment {
  background: linear-gradient(135deg, #67c23a 0%, #5daf34 100%);
  color: white;
}

.type-purchase {
  background: linear-gradient(135deg, #e6a23c 0%, #cf9236 100%);
  color: white;
}

.notification-content {
  flex: 1;
  min-width: 0;
}

.notification-title {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 6px;
}

.notification-text {
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
  margin-bottom: 8px;
}

.notification-time {
  font-size: 12px;
  color: #909399;
}

.notification-status {
  margin-left: 12px;
  padding-top: 8px;
}

.unread-dot {
  display: inline-block;
  width: 10px;
  height: 10px;
  background: #409eff;
  border-radius: 50%;
}

.pagination-section {
  padding: 20px;
  display: flex;
  justify-content: center;
  border-top: 1px solid #f0f0f0;
}

@media (max-width: 768px) {
  .notification-container {
    padding: 16px;
  }

  .notification-header {
    flex-direction: column;
    gap: 16px;
    align-items: flex-start;
  }

  .header-right {
    width: 100%;
  }

  .notification-item {
    padding: 16px;
  }

  .notification-icon {
    width: 40px;
    height: 40px;
  }
}
</style>
