<template>
  <div class="layout">
    <!-- 主体内容 -->
    <div class="main">
      <router-view />
    </div>
    
    <!-- 底部Tab栏 -->
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
  background-color: #f5f7fa;
}

.main {
  flex: 1;
  padding: 0;
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
  padding: 8px 0;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.08);
  z-index: 100;
}

.tab-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-decoration: none;
  color: #999;
  font-size: 11px;
  padding: 4px 0;
  transition: all 0.2s;
  min-width: 50px;
}

.tab-item .icon {
  font-size: 22px;
  margin-bottom: 2px;
  transition: transform 0.2s;
}

.tab-item.router-link-active {
  color: #667eea;
}

.tab-item.router-link-active .icon {
  transform: scale(1.1);
}

.tab-item:active {
  transform: scale(0.95);
}
</style>
