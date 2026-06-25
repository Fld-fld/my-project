<template>
  <div class="user-manage">
    <div class="page-header">
      <h1>用户管理</h1>
    </div>
    
    <!-- 搜索筛选栏 -->
    <div class="filter-bar">
      <el-input
        v-model="searchKeyword"
        placeholder="搜索姓名/学号/手机号"
        clearable
        @clear="handleSearch"
        class="search-input"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>
      
      <el-select v-model="filterStatus" placeholder="状态筛选" clearable class="status-filter">
        <el-option label="启用" :value="1" />
        <el-option label="禁用" :value="0" />
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
        <el-table-column prop="id" label="ID" width="60" align="center" />
        <el-table-column prop="studentId" label="学号" width="130">
          <template #default="{ row }">
            <span class="student-id">{{ row.studentId || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column prop="phone" label="手机号" width="130">
          <template #default="{ row }">
            <span class="phone-text">{{ row.phone || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="role" label="角色" width="100" align="center">
          <template #default="{ row }">
            <el-tag type="info" size="small">{{ row.role || '-' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="注册时间" width="160">
          <template #default="{ row }">
            {{ formatTime(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button 
                type="warning" 
                size="small" 
                round
                @click="handleToggleStatus(row)"
                class="action-btn warning"
              >
                {{ row.status === 1 ? '禁用' : '启用' }}
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
            <div class="empty-illustration">👥</div>
            <div class="empty-text">暂无用户数据</div>
          </div>
        </template>
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { userAPI } from '@/api'
import { Search } from '@element-plus/icons-vue'

const loading = ref(false)
const list = ref([])
const searchKeyword = ref('')
const filterStatus = ref('')

const filteredList = computed(() => {
  let result = list.value
  
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    result = result.filter(item => 
      item.name?.toLowerCase().includes(keyword) ||
      item.studentId?.toLowerCase().includes(keyword) ||
      item.phone?.includes(keyword)
    )
  }
  
  if (filterStatus.value !== '') {
    result = result.filter(item => item.status === filterStatus.value)
  }
  
  return result
})

const rowClassName = ({ row }) => {
  if (row.status === 0) return 'row-disabled'
  return ''
}

const getStatusType = (status) => {
  return status === 1 ? 'success' : 'info'
}

const getStatusText = (status) => {
  return status === 1 ? '启用' : '禁用'
}

const formatTime = (time) => {
  if (!time) return '-'
  const date = new Date(time)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

const loadList = async () => {
  loading.value = true
  try {
    const res = await userAPI.getList()
    list.value = res.data || []
  } catch (error) {
    console.error('加载用户列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {}

const handleToggleStatus = async (row) => {
  const action = row.status === 1 ? '禁用' : '启用'
  const newStatus = row.status === 1 ? 0 : 1
  try {
    await ElMessageBox.confirm(`确认${action}该用户？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await userAPI.updateStatus(row.id, newStatus)
    ElMessage.success(`${action}成功`)
    loadList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('操作失败:', error)
    }
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确认删除该用户？此操作不可恢复！', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await userAPI.delete(row.id)
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
.user-manage {
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

:deep(.el-table__body tr.row-disabled td) {
  opacity: 0.5;
  color: #999;
}

.student-id {
  font-family: monospace;
  font-weight: 500;
}

.phone-text {
  font-family: monospace;
  font-weight: 500;
}

.action-buttons {
  display: flex;
  gap: 8px;
  justify-content: center;
}

.action-btn.warning {
  background: linear-gradient(135deg, #f59e0b 0%, #fbbf24 100%) !important;
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
