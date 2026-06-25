<template>
  <div class="my-express">
    <h1>我的快递</h1>
    
    <!-- 标签页 -->
    <div class="tabs-wrapper">
      <el-tabs v-model="activeTab" @tab-change="handleTabChange" class="custom-tabs">
        <el-tab-pane name="PENDING">
          <template #label>
            <span class="tab-label">
              <span class="tab-icon">📦</span>
              待取 ({{ pendingList.length }})
            </span>
          </template>
        </el-tab-pane>
        <el-tab-pane name="PICKED_UP">
          <template #label>
            <span class="tab-label">
              <span class="tab-icon">✅</span>
              已取 ({{ pickedList.length }})
            </span>
          </template>
        </el-tab-pane>
      </el-tabs>
    </div>
    
    <!-- 快递列表 -->
    <div class="express-list" v-loading="loading">
      <!-- 空状态 -->
      <div v-if="currentList.length === 0" class="empty-state">
        <div class="empty-illustration">📭</div>
        <div class="empty-text">暂无快递</div>
        <div class="empty-hint">点击下方按钮去预约取件</div>
        <el-button type="primary" @click="$router.push('/reservation')" class="empty-btn">
          去预约
        </el-button>
      </div>
      
      <div 
        v-for="item in currentList" 
        :key="item.id" 
        class="express-card"
        @click="showDetail(item)"
      >
        <div class="card-header">
          <div class="company-info">
            <span class="company-icon">{{ getCompanyIcon(item.company) }}</span>
            <span class="company-name">{{ item.company }}</span>
          </div>
          <span :class="['status-badge', item.status]">
            {{ getStatusText(item.status) }}
          </span>
        </div>
        
        <div class="card-body">
          <div class="pickup-code-box" v-if="item.pickupCode" @click.stop="copyCode(item.pickupCode)">
            <span class="code-label">取件码</span>
            <span class="code-value">{{ item.pickupCode }}</span>
            <span class="copy-hint">
              <el-icon><DocumentCopy /></el-icon>
              点击复制
            </span>
          </div>
          
          <div class="info-grid">
            <div class="info-item">
              <span class="info-icon">📝</span>
              <div class="info-content">
                <span class="info-label">快递单号</span>
                <span class="info-value">{{ item.trackingNumber }}</span>
              </div>
            </div>
            <div class="info-item">
              <span class="info-icon">📍</span>
              <div class="info-content">
                <span class="info-label">存放位置</span>
                <span class="info-value location">{{ item.location }}</span>
              </div>
            </div>
            <div class="info-item">
              <span class="info-icon">🕐</span>
              <div class="info-content">
                <span class="info-label">到达时间</span>
                <span class="info-value">{{ formatTime(item.createdAt) }}</span>
              </div>
            </div>
          </div>
        </div>
        
        <div class="card-footer" v-if="item.status === 'PENDING' || item.status === 'RESERVED'">
          <el-button type="primary" size="small" @click.stop="handleReserve(item)" class="reserve-btn">
            <el-icon><Calendar /></el-icon>
            预约取件
          </el-button>
        </div>
      </div>
      
      <!-- 分页器 -->
      <div class="pagination-wrapper" v-if="totalList.length > pageSize">
        <el-pagination
          :current-page="currentPage"
          :page-size="pageSize"
          :total="totalList.length"
          layout="prev, pager, next"
          @current-change="handlePageChange"
        />
      </div>
    </div>
    
    <!-- 详情弹窗 -->
    <el-dialog 
      title="快递详情" 
      :visible="detailVisible" 
      @update:visible="detailVisible = $event" 
      width="90%"
      class="detail-dialog"
    >
      <div v-if="currentExpress" class="detail-content">
        <div class="detail-header">
          <span class="detail-company-icon">{{ getCompanyIcon(currentExpress.company) }}</span>
          <div class="detail-company-info">
            <span class="detail-company-name">{{ currentExpress.company }}</span>
            <span :class="['detail-status', currentExpress.status]">
              {{ getStatusText(currentExpress.status) }}
            </span>
          </div>
        </div>
        
        <div class="detail-pickup-code" v-if="currentExpress.pickupCode" @click="copyCode(currentExpress.pickupCode)">
          <span class="detail-code-label">取件码</span>
          <span class="detail-code-value">{{ currentExpress.pickupCode }}</span>
          <span class="detail-copy-btn">
            <el-icon><DocumentCopy /></el-icon>
            复制
          </span>
        </div>
        
        <el-descriptions :column="1" border class="detail-descriptions">
          <el-descriptions-item label="快递单号">{{ currentExpress.trackingNumber }}</el-descriptions-item>
          <el-descriptions-item label="收件人">{{ currentExpress.recipientName }}</el-descriptions-item>
          <el-descriptions-item label="手机号">{{ currentExpress.recipientPhone }}</el-descriptions-item>
          <el-descriptions-item label="存放位置">{{ currentExpress.location }}</el-descriptions-item>
          <el-descriptions-item label="到达时间">{{ formatTime(currentExpress.createdAt) }}</el-descriptions-item>
        </el-descriptions>
        
        <div class="detail-actions" v-if="currentExpress.status === 'PENDING' || currentExpress.status === 'RESERVED'">
          <el-button type="primary" size="large" @click="handleReserve(currentExpress)" class="detail-reserve-btn">
            <el-icon><Calendar /></el-icon>
            预约取件
          </el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { expressAPI } from '../api'
import { DocumentCopy, Calendar } from '@element-plus/icons-vue'

const router = useRouter()
const loading = ref(false)
const activeTab = ref('PENDING')
const pendingList = ref([])
const pickedList = ref([])
const detailVisible = ref(false)
const currentExpress = ref(null)

const currentPage = ref(1)
const pageSize = ref(10)

const currentList = computed(() => {
  const list = activeTab.value === 'PENDING' ? pendingList.value : pickedList.value
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return list.slice(start, end)
})

const totalList = computed(() => {
  return activeTab.value === 'PENDING' ? pendingList.value : pickedList.value
})

const resetPage = () => {
  currentPage.value = 1
}

const getCompanyIcon = (company) => {
  const iconMap = {
    '顺丰速运': '🚚',
    '圆通速递': '📦',
    '中通快递': '📦',
    '韵达快递': '📦',
    '申通快递': '📦',
    '京东物流': '🚚',
    '邮政快递': '📮',
    '其他': '📦'
  }
  return iconMap[company] || '📦'
}

const getStatusText = (status) => {
  const map = {
    'PENDING': '待取',
    'RESERVED': '已预约',
    'PICKED_UP': '已取'
  }
  return map[status] || status
}

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

const copyCode = async (code) => {
  try {
    await navigator.clipboard.writeText(code)
    ElMessage.success('取件码已复制')
  } catch {
    ElMessage.error('复制失败')
  }
}

const loadExpressList = async () => {
  loading.value = true
  try {
    const res = await expressAPI.getList()
    const allList = res.data || []
    pendingList.value = allList.filter(e => e.status === 'PENDING' || e.status === 'RESERVED')
    pickedList.value = allList.filter(e => e.status === 'PICKED_UP')
  } catch (error) {
    console.error('加载快递列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handlePageChange = (page) => {
  currentPage.value = page
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

const handleTabChange = () => {
  resetPage()
}

const showDetail = (item) => {
  currentExpress.value = item
  detailVisible.value = true
}

const handleReserve = (item) => {
  detailVisible.value = false
  router.push(`/reservation?expressId=${item.id}`)
}

onMounted(() => {
  loadExpressList()
})
</script>

<style scoped>
.my-express {
  max-width: 800px;
  margin: 0 auto;
  padding: 0 16px 80px;
}

h1 {
  font-size: 22px;
  font-weight: 600;
  color: #333;
  padding: 16px 0;
}

/* 标签页 */
.tabs-wrapper {
  margin-bottom: 16px;
}

.custom-tabs :deep(.el-tabs__header) {
  margin-bottom: 0;
}

.custom-tabs :deep(.el-tabs__nav-wrap::after) {
  display: none;
}

.custom-tabs :deep(.el-tabs__item) {
  font-size: 15px;
  color: #999;
  height: 44px;
  line-height: 44px;
  padding: 0 16px;
}

.custom-tabs :deep(.el-tabs__item.is-active) {
  color: #667eea;
  font-weight: 600;
}

.custom-tabs :deep(.el-tabs__active-bar) {
  height: 3px;
  border-radius: 3px;
}

.tab-label {
  display: flex;
  align-items: center;
  gap: 6px;
}

.tab-icon {
  font-size: 16px;
}

/* 快递卡片 */
.express-list {
  margin-top: 8px;
}

.express-card {
  background: white;
  border-radius: 12px;
  margin-bottom: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  cursor: pointer;
  transition: all 0.3s;
}

.express-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 14px 16px;
  border-bottom: 1px solid #f0f0f0;
}

.company-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.company-icon {
  font-size: 20px;
}

.company-name {
  font-weight: 600;
  color: #333;
  font-size: 15px;
}

.status-badge {
  padding: 4px 10px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
}

.status-badge.PENDING {
  background: #fff3e0;
  color: #ff9800;
}

.status-badge.RESERVED {
  background: #e3f2fd;
  color: #2196f3;
}

.status-badge.PICKED_UP {
  background: #e8f5e9;
  color: #4caf50;
}

/* 取件码盒子 */
.pickup-code-box {
  background: linear-gradient(135deg, #667eea15 0%, #764ba215 100%);
  margin: 12px 16px;
  padding: 16px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  cursor: pointer;
  transition: all 0.2s;
}

.pickup-code-box:hover {
  background: linear-gradient(135deg, #667eea25 0%, #764ba225 100%);
}

.code-label {
  font-size: 12px;
  color: #666;
}

.code-value {
  font-size: 28px;
  font-weight: 700;
  color: #667eea;
  letter-spacing: 2px;
}

.copy-hint {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #999;
}

/* 信息网格 */
.info-grid {
  padding: 0 16px 14px;
}

.info-item {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  margin-bottom: 10px;
}

.info-item:last-child {
  margin-bottom: 0;
}

.info-icon {
  font-size: 14px;
  margin-top: 2px;
}

.info-content {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.info-label {
  font-size: 12px;
  color: #999;
}

.info-value {
  font-size: 14px;
  color: #333;
}

.info-value.location {
  color: #667eea;
  font-weight: 500;
}

/* 卡片底部 */
.card-footer {
  padding: 12px 16px;
  border-top: 1px solid #f0f0f0;
}

.reserve-btn {
  width: 100%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%) !important;
  border: none !important;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 60px 20px;
  background: white;
  border-radius: 12px;
}

.empty-illustration {
  font-size: 64px;
  margin-bottom: 16px;
}

.empty-text {
  font-size: 16px;
  color: #666;
  margin-bottom: 8px;
}

.empty-hint {
  font-size: 13px;
  color: #999;
  margin-bottom: 20px;
}

.empty-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%) !important;
  border: none !important;
  border-radius: 20px;
  padding: 10px 30px;
}

/* 分页 */
.pagination-wrapper {
  display: flex;
  justify-content: center;
  padding: 20px 0;
}

/* 详情弹窗 */
.detail-dialog :deep(.el-dialog) {
  border-radius: 16px;
}

.detail-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.detail-company-icon {
  font-size: 36px;
}

.detail-company-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.detail-company-name {
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.detail-status {
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 10px;
  width: fit-content;
}

.detail-status.PENDING {
  background: #fff3e0;
  color: #ff9800;
}

.detail-status.RESERVED {
  background: #e3f2fd;
  color: #2196f3;
}

.detail-pickup-code {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
  cursor: pointer;
}

.detail-code-label {
  color: rgba(255,255,255,0.8);
  font-size: 14px;
}

.detail-code-value {
  font-size: 36px;
  font-weight: 700;
  color: white;
  letter-spacing: 4px;
}

.detail-copy-btn {
  color: white;
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
}

.detail-descriptions :deep(.el-descriptions__label) {
  background: #f8f9fa;
  color: #666;
}

.detail-reserve-btn {
  width: 100%;
  height: 48px;
  font-size: 16px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%) !important;
  border: none !important;
  border-radius: 24px;
  margin-top: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}
</style>
