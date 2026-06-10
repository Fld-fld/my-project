<template>
  <div class="profile">
    <h1>个人中心</h1>
    
    <!-- 用户信息 -->
    <div class="user-card">
      <div class="avatar">
        {{ userInfo.name?.charAt(0) || 'U' }}
      </div>
      <div class="user-info">
        <div class="name">{{ userInfo.name }}</div>
        <div class="student-id">学号：{{ userInfo.studentId }}</div>
        <div class="phone">手机：{{ userInfo.phone }}</div>
      </div>
    </div>
    
    <!-- 菜单列表 -->
    <div class="menu-list">
      <div class="menu-item" @click="$router.push('/my-express')">
        <span class="icon">📦</span>
        <span class="text">我的快递</span>
        <span class="arrow">›</span>
      </div>
      <div class="menu-item" @click="$router.push('/reservation')">
        <span class="icon">📅</span>
        <span class="text">我的预约</span>
        <span class="arrow">›</span>
      </div>
      <div class="menu-item" @click="$router.push('/notifications')">
        <span class="icon">🔔</span>
        <span class="text">消息通知</span>
        <span class="arrow">›</span>
      </div>
    </div>
    
    <!-- 退出登录 -->
    <div class="logout-btn">
      <el-button type="danger" plain @click="handleLogout" style="width: 100%">
        退出登录
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../store/user'

const router = useRouter()
const userStore = useUserStore()

const userInfo = computed(() => userStore.userInfo || {})

const handleLogout = () => {
  userStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.profile {
  max-width: 800px;
  margin: 0 auto;
}

h1 {
  font-size: 24px;
  margin-bottom: 20px;
  color: #333;
}

.user-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 30px;
  border-radius: 10px;
  color: white;
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.avatar {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background: white;
  color: #667eea;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  font-weight: bold;
  margin-right: 20px;
}

.user-info {
  flex: 1;
}

.name {
  font-size: 20px;
  font-weight: bold;
  margin-bottom: 5px;
}

.student-id, .phone {
  font-size: 14px;
  opacity: 0.9;
  margin-bottom: 3px;
}

.menu-list {
  background: white;
  border-radius: 10px;
  overflow: hidden;
  margin-bottom: 20px;
}

.menu-item {
  display: flex;
  align-items: center;
  padding: 15px 20px;
  border-bottom: 1px solid #eee;
  cursor: pointer;
  transition: background 0.2s;
}

.menu-item:last-child {
  border-bottom: none;
}

.menu-item:hover {
  background: #f8f8ff;
}

.icon {
  font-size: 20px;
  margin-right: 15px;
}

.text {
  flex: 1;
  color: #333;
}

.arrow {
  color: #ccc;
  font-size: 20px;
}

.logout-btn {
  padding: 0 20px;
}
</style>
