<template>
  <div class="admin-layout">
    <!-- 侧边栏 -->
    <div class="sidebar">
      <div class="logo">后台管理</div>
      <nav class="nav">
        <router-link to="/admin/home" class="nav-item">
          <span class="icon">📊</span>
          <span>首页</span>
        </router-link>
        <router-link to="/admin/express/add" class="nav-item">
          <span class="icon">➕</span>
          <span>录入快递</span>
        </router-link>
        <router-link to="/admin/express/manage" class="nav-item">
          <span class="icon">📦</span>
          <span>快递管理</span>
        </router-link>
        <router-link to="/admin/reservation/manage" class="nav-item">
          <span class="icon">📅</span>
          <span>预约管理</span>
        </router-link>
        <router-link to="/admin/user/manage" class="nav-item">
          <span class="icon">👥</span>
          <span>用户管理</span>
        </router-link>
        <router-link to="/admin/notice/manage" class="nav-item">
          <span class="icon">📢</span>
          <span>公告管理</span>
        </router-link>
      </nav>
      <div class="logout-section">
        <el-button type="danger" size="small" @click="handleLogout">退出登录</el-button>
      </div>
    </div>
    
    <!-- 主体内容 -->
    <div class="main">
      <router-view />
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useUserStore } from '../store/user'

const router = useRouter()
const userStore = useUserStore()

const handleLogout = () => {
  userStore.logout()
  router.push('/admin/login')
}
</script>

<style scoped>
.admin-layout {
  display: flex;
  min-height: 100vh;
}

.sidebar {
  width: 200px;
  background: #2c3e50;
  color: white;
  display: flex;
  flex-direction: column;
}

.logo {
  padding: 20px;
  font-size: 18px;
  font-weight: bold;
  text-align: center;
  border-bottom: 1px solid #3d566e;
}

.nav {
  flex: 1;
  padding: 10px 0;
}

.nav-item {
  display: flex;
  align-items: center;
  padding: 12px 20px;
  color: #b8c6db;
  text-decoration: none;
  transition: all 0.2s;
}

.nav-item:hover {
  background: #34495e;
  color: white;
}

.nav-item.router-link-active {
  background: #667eea;
  color: white;
}

.icon {
  margin-right: 10px;
  font-size: 18px;
}

.logout-section {
  padding: 20px;
  border-top: 1px solid #3d566e;
  text-align: center;
}

.main {
  flex: 1;
  background: #f5f5f5;
  padding: 20px;
  overflow-y: auto;
}
</style>
