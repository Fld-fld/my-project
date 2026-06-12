<template>
  <div class="my-express">
    <h1>我的快递</h1>
    
    <!-- 标签页 -->
    <el-tabs v-model="activeTab" @tab-change="handleTabChange">
      <el-tab-pane name="PENDING">
        <template #label>
          待取 ({{ pendingList.length }})
        </template>
      </el-tab-pane>
      <el-tab-pane name="PICKED_UP">
        <template #label>
          已取 ({{ pickedList.length }})
        </template>
      </el-tab-pane>
    </el-tabs>
    
    <!-- 快递列表 -->
    <div class="express-list" v-loading="loading">
      <div v-if="currentList.length === 0" class="empty">
        <div class="empty-icon">📭</div>
        <div class="empty-text">暂无快递</div>
      </div>
      
      <div 
        v-for="item in currentList" 
        :key="item.id" 
        class="express-item"
        @click="showDetail(item)"
      >
        <div class="express-header">
          <span class="company">{{ item.company }}</span>
          <span :class="['status', item.status]">{{ getStatusText(item.status) }}</span>
        </div>
        
        <div class="express-info">
          <div class="info-row">
            <span class="label">单号：</span>
            <span class="value">{{ item.trackingNumber }}</span>
          </div>
          <div class="info-row" v-if="item.pickupCode">
            <span class="label">取件码：</span>
            <span class="value pickup-code">{{ item.pickupCode }}</span>
          </div>
          <div class="info-row">
            <span class="label">位置：</span>
            <span class="value">{{ item.location }}</span>
          </div>
        </div>
        
        <div class="express-time">
          {{ formatTime(item.createdAt) }}
        </div>
        
        <div class="express-actions" v-if="item.status === 'PENDING' || item.status === 'RESERVED'">
          <el-button type="primary" size="small" @click.stop="handleReserve(item)">
            预约取件
          </el-button>
        </div>
      </div>
      
      <!-- 分页器 -->
      <div class="pagination" v-if="totalList.length > pageSize">
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
    <el-dialog title="快递详情" :visible="detailVisible" @update:visible="detailVisible = $event" width="90%">
      <div v-if="currentExpress" class="detail-content">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="快递公司">{{ currentExpress.company }}</el-descriptions-item>
          <el-descriptions-item label="快递单号">{{ currentExpress.trackingNumber }}</el-descriptions-item>
          <el-descriptions-item label="收件人">{{ currentExpress.recipientName }}</el-descriptions-item>
          <el-descriptions-item label="手机号">{{ currentExpress.recipientPhone }}</el-descriptions-item>
          <el-descriptions-item label="存放位置">{{ currentExpress.location }}</el-descriptions-item>
          <el-descriptions-item label="取件码">
            <span class="pickup-code-large">{{ currentExpress.pickupCode }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="状态">{{ getStatusText(currentExpress.status) }}</el-descriptions-item>
          <el-descriptions-item label="到达时间">{{ formatTime(currentExpress.createdAt) }}</el-descriptions-item>
        </el-descriptions>
        
        <div class="detail-actions" v-if="currentExpress.status === 'PENDING' || currentExpress.status === 'RESERVED'">
          <el-button type="primary" @click="handleReserve(currentExpress)">预约取件</el-button>
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

const router = useRouter()
const loading = ref(false)
const activeTab = ref('PENDING')
const pendingList = ref([])
const pickedList = ref([])
const detailVisible = ref(false)
const currentExpress = ref(null)

// 分页相关
const currentPage = ref(1)
const pageSize = ref(10)

const currentList = computed(() => {
  const list = activeTab.value === 'PENDING' ? pendingList.value : pickedList.value
  // 计算分页
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return list.slice(start, end)
})

// 监听列表变化，重置页码
const resetPage = () => {
  currentPage.value = 1
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

const totalList = computed(() => {
  return activeTab.value === 'PENDING' ? pendingList.value : pickedList.value
})

const handlePageChange = (page) => {
  currentPage.value = page
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

const handleTabChange = () => {
  // 标签切换时重置页码
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
}

h1 {
  font-size: 24px;
  margin-bottom: 20px;
  color: #333;
}

.express-list {
  margin-top: 20px;
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

.express-item {
  background: white;
  padding: 15px;
  border-radius: 10px;
  margin-bottom: 15px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  cursor: pointer;
}

.express-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
}

.company {
  font-weight: bold;
  color: #333;
}

.status {
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
}

.status.PENDING {
  background: #fff3e0;
  color: #ff9800;
}

.status.RESERVED {
  background: #e3f2fd;
  color: #2196f3;
}

.status.PICKED_UP {
  background: #e8f5e9;
  color: #4caf50;
}

.express-info {
  margin-bottom: 10px;
}

.info-row {
  display: flex;
  margin-bottom: 5px;
  font-size: 14px;
}

.label {
  color: #999;
  width: 60px;
}

.value {
  color: #333;
}

.pickup-code {
  font-weight: bold;
  color: #667eea;
  font-size: 16px;
}

.express-time {
  font-size: 12px;
  color: #999;
  margin-bottom: 10px;
}

.express-actions {
  display: flex;
  justify-content: flex-end;
}

.pickup-code-large {
  font-size: 20px;
  font-weight: bold;
  color: #667eea;
}

.detail-actions {
  margin-top: 20px;
  text-align: center;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 20px;
  padding: 20px 0;
}
</style>
