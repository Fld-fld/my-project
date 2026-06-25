<template>
  <div class="reservation-manage">
    <div class="page-header">
      <h1>预约管理</h1>
    </div>
    
    <!-- 搜索筛选栏 -->
    <div class="filter-bar">
      <el-input
        v-model="searchKeyword"
        placeholder="搜索用户ID/快递ID"
        clearable
        @clear="handleSearch"
        class="search-input"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>
      
      <el-select v-model="filterStatus" placeholder="状态筛选" clearable class="status-filter">
        <el-option label="待取件" value="PENDING" />
        <el-option label="已完成" value="COMPLETED" />
        <el-option label="已取消" value="CANCELLED" />
      </el-select>
      
      <el-date-picker
        v-model="filterDate"
        type="date"
        placeholder="筛选日期"
        clearable
        class="date-filter"
        value-format="yyyy-MM-dd"
      />
      
      <el-button type="primary" @click="handleSearch" class="search-btn">
        <el-icon><Search /></el-icon>
        搜索
      </el-button>
    </div>
    
    <!-- 表格 -->
    <div class="table-container" v-loading="loading">
      <el-table
        :data="filteredList"
        border
        style="width: 100%"
        :header-cell-style="{ background: '#f8f9fa', color: '#495057', fontWeight: '600', borderBottom: '2px solid #667eea' }"
        :row-class-name="rowClassName"
      >
        <el-table-column prop="id" label="预约ID" width="80" align="center" />
        <el-table-column prop="userId" label="用户ID" width="100" align="center">
          <template #default="{ row }">
            <span class="user-id">{{ row.userId || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="expressId" label="快递ID" width="100" align="center">
          <template #default="{ row }">
            <span class="express-id">{{ row.expressId || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="reservationDate" label="预约日期" width="120">
          <template #default="{ row }">
            {{ row.reservationDate || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="预约时段" width="160">
          <template #default="{ row }">
            {{ formatTimeSlot(row.startTime, row.endTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="160">
          <template #default="{ row }">
            {{ formatTime(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button 
                v-if="row.status === 'PENDING'"
                type="success" 
                size="small" 
                round
                @click="handleComplete(row)"
                class="action-btn primary"
              >
                ✓ 完成
              </el-button>
              <el-button 
                v-if="row.status === 'PENDING'"
                type="danger" 
                size="small" 
                round
                @click="handleCancel(row)"
                class="action-btn danger"
              >
                ✕ 取消
              </el-button>
            </div>
          </template>
        </el-table-column>
        
        <template #empty>
          <div class="empty-state">
            <div class="empty-illustration">📅</div>
            <div class="empty-text">暂无预约数据</div>
          </div>
        </template>
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { reservationAPI } from '@/api'
import { Search } from '@element-plus/icons-vue'

const loading = ref(false)
const list = ref([])
const searchKeyword = ref('')
const filterStatus = ref('')
const filterDate = ref('')

const filteredList = computed(() => {
  let result = list.value
  
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    result = result.filter(item => 
      String(item.userId)?.includes(keyword) ||
      String(item.expressId)?.includes(keyword)
    )
  }
  
  if (filterStatus.value) {
    result = result.filter(item => item.status === filterStatus.value)
  }
  
  if (filterDate.value) {
    result = result.filter(item => item.reservationDate === filterDate.value)
  }
  
  return result
})

const rowClassName = ({ row }) => {
  if (row.status === 'COMPLETED') return 'row-completed'
  if (row.status === 'CANCELLED') return 'row-cancelled'
  return ''
}

const getStatusType = (status) => {
  const map = {
    'PENDING': 'warning',
    'COMPLETED': 'success',
    'CANCELLED': 'info'
  }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = {
    'PENDING': '待取件',
    'COMPLETED': '已完成',
    'CANCELLED': '已取消'
  }
  return map[status] || status
}

const formatTime = (time) => {
  if (!time) return '-'
  const date = new Date(time)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

const formatTimeSlot = (startTime, endTime) => {
  if (!startTime) return '-'
  const start = startTime.replace('T', '').substring(0, 5)
  const end = endTime ? endTime.replace('T', '').substring(0, 5) : ''
  return end ? `${start} - ${end}` : start
}

const loadList = async () => {
  loading.value = true
  try {
    const res = await reservationAPI.getAll()
    list.value = res.data || []
  } catch (error) {
    console.error('加载预约列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {}

const handleComplete = async (row) => {
  try {
    await ElMessageBox.confirm('确认完成该预约？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await reservationAPI.complete(row.id)
    ElMessage.success('操作成功')
    loadList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('操作失败:', error)
    }
  }
}

const handleCancel = async (row) => {
  try {
    await ElMessageBox.confirm('确认取消该预约？', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await reservationAPI.cancel(row.id)
    ElMessage.success('已取消')
    loadList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('操作失败:', error)
    }
  }
}

onMounted(() => {
  loadList()
})
</script>

<style scoped>
.reservation-manage {
  max-width: 1400px;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h1 {
  font-size: 24px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.filter-bar {
  background: white;
  border-radius: 12px;
  padding: 16px 20px;
  margin-bottom: 16px;
  display: flex;
  gap: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.search-input {
  width: 240px;
}

.status-filter {
  width: 150px;
}

.date-filter {
  width: 160px;
}

.search-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%) !important;
  border: none !important;
  border-radius: 8px;
  display: flex;
  align-items: center;
  gap: 6px;
}

.table-container {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

:deep(.el-table) {
  border-radius: 12px;
}

:deep(.el-table th) {
  padding: 16px 12px;
  font-size: 13px;
}

:deep(.el-table td) {
  padding: 16px 12px;
  font-size: 14px;
  color: #495057;
}

:deep(.el-table--border th.gutter) {
  background-color: #f8f9fa;
}

:deep(.el-table--border td.gutter) {
  background-color: white;
}

:deep(.el-table__row:hover > td) {
  background-color: #f0f4ff !important;
}

:deep(.el-table__body tr.row-completed td) {
  opacity: 0.6;
}

:deep(.el-table__body tr.row-cancelled td) {
  opacity: 0.5;
  color: #999;
}

.user-id, .express-id {
  font-family: monospace;
  font-weight: 600;
  color: #667eea;
}

.action-buttons {
  display: flex;
  gap: 8px;
  justify-content: center;
}

.action-btn.primary {
  background: linear-gradient(135deg, #10b981 0%, #34d399 100%) !important;
  border: none !important;
  padding: 4px 14px;
}

.action-btn.danger {
  background: linear-gradient(135deg, #ef4444 0%, #f87171 100%) !important;
  border: none !important;
  padding: 4px 14px;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
}

.empty-illustration {
  font-size: 64px;
  margin-bottom: 16px;
}

.empty-text {
  font-size: 16px;
  color: #666;
}
</style>
