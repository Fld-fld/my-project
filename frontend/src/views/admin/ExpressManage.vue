<template>
  <div class="express-manage">
    <div class="page-header">
      <h1>快递管理</h1>
    </div>
    
    <!-- 搜索筛选栏 -->
    <div class="filter-bar">
      <el-input
        v-model="searchKeyword"
        placeholder="搜索快递单号/收件人"
        clearable
        @clear="handleSearch"
        class="search-input"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>
      
      <el-select v-model="filterStatus" placeholder="状态筛选" clearable class="status-filter">
        <el-option label="待取" value="PENDING" />
        <el-option label="已预约" value="RESERVED" />
        <el-option label="已取件" value="PICKED_UP" />
      </el-select>
      
      <el-select v-model="filterCompany" placeholder="快递公司" clearable class="company-filter">
        <el-option label="顺丰速运" value="顺丰速运" />
        <el-option label="圆通速递" value="圆通速递" />
        <el-option label="中通快递" value="中通快递" />
        <el-option label="韵达快递" value="韵达快递" />
        <el-option label="申通快递" value="申通快递" />
        <el-option label="京东物流" value="京东物流" />
        <el-option label="邮政快递" value="邮政快递" />
        <el-option label="其他" value="其他" />
      </el-select>
      
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
        <el-table-column prop="trackingNumber" label="快递单号" min-width="150" />
        <el-table-column prop="company" label="快递公司" width="120">
          <template #default="{ row }">
            <span class="company-tag">{{ row.company }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="recipientName" label="收件人" width="100" />
        <el-table-column prop="recipientPhone" label="手机号" width="130">
          <template #default="{ row }">
            <span class="phone-text">{{ row.recipientPhone }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="location" label="存放位置" width="120">
          <template #default="{ row }">
            <span class="location-tag">{{ row.location }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="pickupCode" label="取件码" width="100">
          <template #default="{ row }">
            <span class="pickup-code">{{ row.pickupCode || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="到件时间" width="160">
          <template #default="{ row }">
            {{ formatTime(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button 
                v-if="row.status !== 'PICKED_UP'"
                type="primary" 
                size="small" 
                round
                @click="handleUpdateStatus(row)"
                class="action-btn primary"
              >
                更新状态
              </el-button>
              <el-button 
                type="danger" 
                size="small" 
                round
                @click="handleDelete(row)"
                class="action-btn danger"
              >
                删除
              </el-button>
            </div>
          </template>
        </el-table-column>
        
        <template #empty>
          <div class="empty-state">
            <div class="empty-illustration">📭</div>
            <div class="empty-text">暂无快递数据</div>
            <el-button type="primary" @click="$router.push('/admin/express/add')" class="empty-btn">
              录入快递
            </el-button>
          </div>
        </template>
      </el-table>
      
      <!-- 分页 -->
      <div class="pagination-wrapper" v-if="total > pageSize">
        <el-pagination
          :current-page="currentPage"
          :page-size="pageSize"
          :total="total"
          layout="total, prev, pager, next"
          @current-change="handlePageChange"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { expressAPI } from '@/api'
import { Search } from '@element-plus/icons-vue'

const loading = ref(false)
const list = ref([])
const searchKeyword = ref('')
const filterStatus = ref('')
const filterCompany = ref('')
const currentPage = ref(1)
const pageSize = ref(10)

const filteredList = computed(() => {
  let result = list.value
  
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    result = result.filter(item => 
      item.trackingNumber?.toLowerCase().includes(keyword) ||
      item.recipientName?.toLowerCase().includes(keyword)
    )
  }
  
  if (filterStatus.value) {
    result = result.filter(item => item.status === filterStatus.value)
  }
  
  if (filterCompany.value) {
    result = result.filter(item => item.company === filterCompany.value)
  }
  
  return result
})

const total = computed(() => filteredList.value.length)

const rowClassName = ({ row }) => {
  if (row.status === 'PICKED_UP') return 'row-completed'
  return ''
}

const getStatusType = (status) => {
  const map = {
    'PENDING': 'warning',
    'RESERVED': 'primary',
    'PICKED_UP': 'success'
  }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = {
    'PENDING': '待取',
    'RESERVED': '已预约',
    'PICKED_UP': '已取件'
  }
  return map[status] || status
}

const formatTime = (time) => {
  if (!time) return '-'
  const date = new Date(time)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

const loadList = async () => {
  loading.value = true
  try {
    const res = await expressAPI.getAll()
    list.value = res.data || []
  } catch (error) {
    console.error('加载快递列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
}

const handlePageChange = (page) => {
  currentPage.value = page
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

const handleUpdateStatus = async (row) => {
  try {
    await ElMessageBox.confirm('确认更新快递状态为已取件？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await expressAPI.updateStatus(row.id, 'PICKED_UP')
    ElMessage.success('更新成功')
    loadList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('更新状态失败:', error)
    }
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确认删除该快递记录？', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await expressAPI.delete(row.id)
    ElMessage.success('删除成功')
    loadList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
    }
  }
}

onMounted(() => {
  loadList()
})
</script>

<style scoped>
.express-manage {
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
  width: 260px;
}

.status-filter {
  width: 150px;
}

.company-filter {
  width: 150px;
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

.company-tag {
  background: #f0f2f5;
  padding: 4px 10px;
  border-radius: 4px;
  font-size: 13px;
}

.phone-text {
  font-family: monospace;
  font-weight: 500;
}

.location-tag {
  color: #667eea;
  font-weight: 600;
}

.pickup-code {
  font-weight: 700;
  color: #333;
}

.action-buttons {
  display: flex;
  gap: 8px;
  justify-content: center;
}

.action-btn.primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%) !important;
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
  margin-bottom: 20px;
}

.empty-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%) !important;
  border: none !important;
  border-radius: 20px;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  padding: 20px 0;
  border-top: 1px solid #f0f0f0;
}
</style>
