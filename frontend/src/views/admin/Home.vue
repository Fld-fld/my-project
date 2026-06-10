<template>
  <div class="admin-home">
    <h1>后台首页</h1>
    
    <!-- 统计卡片 -->
    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-icon">📦</div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.totalExpress || 0 }}</div>
          <div class="stat-label">总快递数</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon">⏳</div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.pendingExpress || 0 }}</div>
          <div class="stat-label">待取件</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon">✅</div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.pickedUpExpress || 0 }}</div>
          <div class="stat-label">已取件</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon">📈</div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.pickupRate || 0 }}%</div>
          <div class="stat-label">取件率</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon">📬</div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.todayArrivals || 0 }}</div>
          <div class="stat-label">今日到件</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon">📅</div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.todayReservations || 0 }}</div>
          <div class="stat-label">今日预约</div>
        </div>
      </div>
    </div>
    
    <!-- 快捷操作 -->
    <div class="quick-actions">
      <h2>快捷操作</h2>
      <div class="actions">
        <el-button type="primary" @click="$router.push('/admin/express/add')">
          录入快递
        </el-button>
        <el-button type="success" @click="$router.push('/admin/express/manage')">
          快递管理
        </el-button>
        <el-button type="warning" @click="$router.push('/admin/reservation/manage')">
          预约管理
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { adminAPI } from '../../api'

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
.admin-home h1 {
  margin-bottom: 20px;
  color: #333;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.stat-card {
  background: white;
  padding: 20px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.stat-icon {
  font-size: 40px;
  margin-right: 15px;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #333;
}

.stat-label {
  font-size: 14px;
  color: #666;
}

.quick-actions {
  background: white;
  padding: 20px;
  border-radius: 10px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.quick-actions h2 {
  margin-bottom: 15px;
  font-size: 18px;
  color: #333;
}

.actions {
  display: flex;
  gap: 10px;
}
</style>
