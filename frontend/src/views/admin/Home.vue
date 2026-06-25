<template>
  <div class="admin-home">
    <div class="page-header">
      <h1>后台首页</h1>
      <span class="greeting">欢迎使用快递管理系统</span>
    </div>
    
    <!-- 统计卡片 -->
    <div class="stats-grid">
      <div class="stat-card primary">
        <div class="stat-icon">
          <span>📦</span>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.totalExpress || 0 }}</div>
          <div class="stat-label">总快递数</div>
        </div>
      </div>
      
      <div class="stat-card warning">
        <div class="stat-icon">
          <span>⏳</span>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.pendingExpress || 0 }}</div>
          <div class="stat-label">待取件</div>
        </div>
      </div>
      
      <div class="stat-card success">
        <div class="stat-icon">
          <span>✅</span>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.pickedUpExpress || 0 }}</div>
          <div class="stat-label">已取件</div>
        </div>
      </div>
      
      <div class="stat-card info">
        <div class="stat-icon">
          <span>📈</span>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.pickupRate || 0 }}%</div>
          <div class="stat-label">取件率</div>
        </div>
      </div>
      
      <div class="stat-card danger">
        <div class="stat-icon">
          <span>📬</span>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.todayArrivals || 0 }}</div>
          <div class="stat-label">今日到件</div>
        </div>
      </div>
      
      <div class="stat-card purple">
        <div class="stat-icon">
          <span>📅</span>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.todayReservations || 0 }}</div>
          <div class="stat-label">今日预约</div>
        </div>
      </div>
    </div>
    
    <!-- 快捷操作 -->
    <div class="quick-actions">
      <div class="section-header">
        <h2>快捷操作</h2>
      </div>
      <div class="actions-grid">
        <div class="action-card" @click="$router.push('/admin/express/add')">
          <div class="action-icon blue">
            <span>➕</span>
          </div>
          <span class="action-text">录入快递</span>
        </div>
        <div class="action-card" @click="$router.push('/admin/express/manage')">
          <div class="action-icon green">
            <span>📦</span>
          </div>
          <span class="action-text">快递管理</span>
        </div>
        <div class="action-card" @click="$router.push('/admin/reservation/manage')">
          <div class="action-icon orange">
            <span>📅</span>
          </div>
          <span class="action-text">预约管理</span>
        </div>
        <div class="action-card" @click="$router.push('/admin/user/manage')">
          <div class="action-icon purple">
            <span>👥</span>
          </div>
          <span class="action-text">用户管理</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { adminAPI } from '@/api'

const stats = ref({})

const loadStats = async () => {
  try {
    const res = await adminAPI.getStatistics()
    stats.value = res.data || {}
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

onMounted(() => {
  loadStats()
})
</script>

<style scoped>
.admin-home {
  max-width: 1200px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.page-header h1 {
  font-size: 24px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.greeting {
  font-size: 14px;
  color: #999;
}

/* 统计卡片网格 */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  gap: 16px;
  margin-bottom: 24px;
}

.stat-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  transition: all 0.3s;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
}

.stat-card.primary .stat-icon {
  background: linear-gradient(135deg, #667eea20 0%, #764ba220 100%);
}

.stat-card.warning .stat-icon {
  background: linear-gradient(135deg, #f59e0b20 0%, #fbbf2420 100%);
}

.stat-card.success .stat-icon {
  background: linear-gradient(135deg, #10b98120 0%, #34d39920 100%);
}

.stat-card.info .stat-icon {
  background: linear-gradient(135deg, #3b82f620 0%, #60a5fa20 100%);
}

.stat-card.danger .stat-icon {
  background: linear-gradient(135deg, #ef444420 0%, #f8717120 100%);
}

.stat-card.purple .stat-icon {
  background: linear-gradient(135deg, #8b5cf620 0%, #a78bfa20 100%);
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #333;
  line-height: 1.2;
}

.stat-label {
  font-size: 13px;
  color: #999;
  margin-top: 4px;
}

/* 快捷操作 */
.quick-actions {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.section-header {
  margin-bottom: 16px;
}

.section-header h2 {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.actions-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
}

.action-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px 16px;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.2s;
  border: 1px solid #f0f0f0;
}

.action-card:hover {
  border-color: #667eea;
  background: #f8f9ff;
}

.action-icon {
  width: 48px;
  height: 48px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  margin-bottom: 10px;
}

.action-icon.blue {
  background: linear-gradient(135deg, #667eea20 0%, #764ba220 100%);
}

.action-icon.green {
  background: linear-gradient(135deg, #10b98120 0%, #34d39920 100%);
}

.action-icon.orange {
  background: linear-gradient(135deg, #f59e0b20 0%, #fbbf2420 100%);
}

.action-icon.purple {
  background: linear-gradient(135deg, #8b5cf620 0%, #a78bfa20 100%);
}

.action-text {
  font-size: 13px;
  color: #333;
  font-weight: 500;
}
</style>
