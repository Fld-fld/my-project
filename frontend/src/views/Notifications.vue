<template>
  <div class="notifications">
    <h1>消息通知</h1>
    
    <!-- 操作栏 -->
    <div class="action-bar" v-if="notifications.length > 0">
      <el-button size="small" @click="handleMarkAll">全部标为已读</el-button>
    </div>
    
    <!-- 通知列表 -->
    <div class="notification-list" v-loading="loading">
      <div v-if="notifications.length === 0" class="empty">
        <div class="empty-icon">🔔</div>
        <div class="empty-text">暂无消息通知</div>
      </div>
      
      <div 
        v-for="item in notifications" 
        :key="item.id" 
        :class="['notification-item', { unread: !item.isRead }]"
        @click="handleRead(item)"
      >
        <div class="notification-icon">{{ getTypeIcon(item.type) }}</div>
        <div class="notification-content">
          <div class="notification-title">{{ item.title }}</div>
          <div class="notification-text">{{ item.content }}</div>
          <div class="notification-time">{{ formatTime(item.createdAt) }}</div>
        </div>
        <div class="notification-badge" v-if="!item.isRead">未读</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { notificationAPI } from '../api'

const loading = ref(false)
const notifications = ref([])

const getTypeIcon = (type) => {
  const map = {
    'ARRIVAL': '📬',
    'RESERVATION': '📅',
    'REMINDER': '⏰',
    'SYSTEM': '📢'
  }
  return map[type] || '📢'
}

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const diff = now - date
  
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return `${Math.floor(diff / 60000)}分钟前`
  if (diff < 86400000) return `${Math.floor(diff / 3600000)}小时前`
  
  return `${date.getMonth() + 1}-${date.getDate()} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

const loadNotifications = async () => {
  loading.value = true
  try {
    const res = await notificationAPI.getList()
    notifications.value = res.data || []
  } catch (error) {
    console.error('加载通知失败:', error)
  } finally {
    loading.value = false
  }
}

const handleRead = async (item) => {
  if (!item.isRead) {
    try {
      await notificationAPI.markAsRead(item.id)
      item.isRead = 1
    } catch (error) {
      console.error('标记已读失败:', error)
    }
  }
}

const handleMarkAll = async () => {
  try {
    await notificationAPI.markAllAsRead()
    notifications.value.forEach(n => n.isRead = 1)
    ElMessage.success('全部标为已读')
  } catch (error) {
    console.error('标记失败:', error)
  }
}

onMounted(() => {
  loadNotifications()
})
</script>

<style scoped>
.notifications {
  max-width: 800px;
  margin: 0 auto;
}

h1 {
  font-size: 24px;
  margin-bottom: 20px;
  color: #333;
}

.action-bar {
  margin-bottom: 15px;
}

.notification-list {
  background: white;
  border-radius: 10px;
  overflow: hidden;
}

.empty {
  text-align: center;
  padding: 60px 0;
}

.empty-icon {
  font-size: 60px;
  margin-bottom: 20px;
}

.empty-text {
  color: #999;
  font-size: 14px;
}

.notification-item {
  display: flex;
  padding: 15px;
  border-bottom: 1px solid #eee;
  cursor: pointer;
  transition: background 0.2s;
  position: relative;
}

.notification-item:last-child {
  border-bottom: none;
}

.notification-item:hover {
  background: #f8f8ff;
}

.notification-item.unread {
  background: #f0f7ff;
}

.notification-icon {
  font-size: 30px;
  margin-right: 15px;
}

.notification-content {
  flex: 1;
}

.notification-title {
  font-weight: bold;
  margin-bottom: 5px;
  color: #333;
}

.notification-text {
  font-size: 14px;
  color: #666;
  margin-bottom: 5px;
  white-space: pre-line;
}

.notification-time {
  font-size: 12px;
  color: #999;
}

.notification-badge {
  position: absolute;
  top: 15px;
  right: 15px;
  background: #ff4d4f;
  color: white;
  padding: 2px 6px;
  border-radius: 10px;
  font-size: 10px;
}
</style>
