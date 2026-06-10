<template>
  <div class="layout">
    <!-- 顶部导航 -->
    <div class="header">
      <div class="logo">校园快递</div>
      <div class="nav">
        <router-link to="/home" class="nav-item">首页</router-link>
        <router-link to="/my-express" class="nav-item">我的快递</router-link>
        <router-link to="/reservation" class="nav-item">取件预约</router-link>
        <router-link to="/notifications" class="nav-item">消息</router-link>
        <router-link to="/profile" class="nav-item">我的</router-link>
      </div>
    </div>
    
    <!-- 主体内容 -->
    <div class="main">
      <router-view />
    </div>
    
    <!-- 底部导航 -->
    <div class="footer">
      <router-link to="/home" class="tab-item">
        <span class="icon">🏠</span>
        <span>首页</span>
      </router-link>
      <router-link to="/my-express" class="tab-item">
        <span class="icon">📦</span>
        <span>快递</span>
      </router-link>
      <router-link to="/reservation" class="tab-item">
        <span class="icon">📅</span>
        <span>预约</span>
      </router-link>
      <router-link to="/profile" class="tab-item">
        <span class="icon">👤</span>
        <span>我的</span>
      </router-link>
    </div>
  </div>
</template>

<script setup>
</script>

<style scoped>
.layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.header {
  background: white;
  padding: 15px 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 100;
}

.logo {
  font-size: 20px;
  font-weight: bold;
  color: #667eea;
}

.nav {
  display: flex;
  gap: 20px;
}

.nav-item {
  color: #666;
  text-decoration: none;
  font-size: 14px;
}

.nav-item.router-link-active {
  color: #667eea;
  font-weight: bold;
}

.main {
  flex: 1;
  padding: 20px;
  padding-bottom: 70px;
}

.footer {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: white;
  display: flex;
  justify-content: space-around;
  padding: 10px 0;
  box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.1);
}

.tab-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-decoration: none;
  color: #666;
  font-size: 12px;
}

.tab-item.router-link-active {
  color: #667eea;
}

.icon {
  font-size: 20px;
  margin-bottom: 4px;
}
</style>
