<template>
  <div class="home">
    <h1>欢迎使用校园快递取件系统</h1>
    
    <!-- 搜索框 -->
    <div class="search-box">
      <el-input
        v-model="searchKeyword"
        placeholder="输入取件码或快递单号查询"
        @keyup.enter="handleSearch"
      >
        <template #append>
          <el-button @click="handleSearch">查询</el-button>
        </template>
      </el-input>
    </div>
    
    <!-- 快捷操作 -->
    <div class="quick-actions">
      <div class="action-item" @click="$router.push('/my-express')">
        <div class="action-icon">📦</div>
        <div class="action-text">我的快递</div>
      </div>
      <div class="action-item" @click="$router.push('/reservation')">
        <div class="action-icon">📅</div>
        <div class="action-text">取件预约</div>
      </div>
      <div class="action-item" @click="$router.push('/notifications')">
        <div class="action-icon">🔔</div>
        <div class="action-text">消息通知</div>
      </div>
    </div>
    
    <!-- 待取快递提示 -->
    <div class="pending-express" v-if="pendingCount > 0" @click="$router.push('/my-express')">
      <div class="pending-icon">📬</div>
      <div class="pending-info">
        <div class="pending-title">您有 {{ pendingCount }} 件待取快递</div>
        <div class="pending-desc">点击查看详情</div>
      </div>
      <div class="arrow">›</div>
    </div>
    
    <!-- 最新通知 -->
    <div class="latest-notification" v-if="latestNotification">
      <h3>最新通知</h3>
      <div class="notification-item" @click="$router.push('/notifications')">
        <div class="notification-title">{{ latestNotification.title }}</div>
        <div class="notification-time">{{ formatTime(latestNotification.createdAt) }}</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { expressAPI, notificationAPI } from '../api'

const searchKeyword = ref('')
const pendingCount = ref(0)
const latestNotification = ref(null)

const handleSearch = () => {
  if (searchKeyword.value) {
    // 根据关键词跳转到我的快递页面
    window.location.href = `/my-express?keyword=${searchKeyword.value}`
  }
}

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  return `${date.getMonth() + 1}-${date.getDate()} ${date.getHours()}:${String(date.getMinutes()).padStart(2, '0')}`
}

const loadData = async () => {
  try {
    // 加载快递列表
    const expressRes = await expressAPI.getList()
    const expressList = expressRes.data || []
    pendingCount.value = expressList.filter(e => e.status === 'PENDING' || e.status === 'RESERVED').length
    
    // 加载通知
    const notifRes = await notificationAPI.getUnreadList()
    const notifications = notifRes.data || []
    if (notifications.length > 0) {
      latestNotification.value = notifications[0]
    }
  } catch (error) {
    console.error('加载数据失败:', error)
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.home {
  max-width: 800px;
  margin: 0 auto;
}

h1 {
  font-size: 24px;
  margin-bottom: 20px;
  color: #333;
}

.search-box {
  margin-bottom: 30px;
}

.quick-actions {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 15px;
  margin-bottom: 30px;
}

.action-item {
  background: white;
  padding: 20px;
  border-radius: 10px;
  text-align: center;
  cursor: pointer;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: transform 0.2s;
}

.action-item:hover {
  transform: translateY(-2px);
}

.action-icon {
  font-size: 32px;
  margin-bottom: 10px;
}

.action-text {
  font-size: 14px;
  color: #666;
}

.pending-express {
  background: #fff3e0;
  padding: 15px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  cursor: pointer;
}

.pending-icon {
  font-size: 30px;
  margin-right: 15px;
}

.pending-info {
  flex: 1;
}

.pending-title {
  font-weight: bold;
  color: #ff9800;
  margin-bottom: 5px;
}

.pending-desc {
  font-size: 12px;
  color: #999;
}

.arrow {
  font-size: 24px;
  color: #999;
}

.latest-notification h3 {
  font-size: 16px;
  margin-bottom: 10px;
  color: #333;
}

.notification-item {
  background: white;
  padding: 15px;
  border-radius: 10px;
  cursor: pointer;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.notification-title {
  font-size: 14px;
  color: #333;
  margin-bottom: 5px;
}

.notification-time {
  font-size: 12px;
  color: #999;
}
</style>
