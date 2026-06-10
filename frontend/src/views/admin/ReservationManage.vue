<template>
  <div class="reservation-manage">
    <h1>预约管理</h1>
    
    <!-- 筛选 -->
    <div class="filter-bar">
      <el-date-picker
        v-model="dateFilter"
        type="date"
        placeholder="筛选日期"
        @change="loadData"
        style="margin-right: 10px"
      />
      <el-button @click="clearFilter">清除筛选</el-button>
    </div>
    
    <!-- 预约列表 -->
    <div class="reservation-table" v-loading="loading">
      <el-table :data="reservationList" border style="width: 100%">
        <el-table-column prop="id" label="预约ID" width="80" />
        <el-table-column label="预约日期" width="120">
          <template #default="{ row }">
            {{ formatDate(row.reservationDate) }}
          </template>
        </el-table-column>
        <el-table-column label="预约时段" width="150">
          <template #default="{ row }">
            {{ row.startTime }} - {{ row.endTime }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="160">
          <template #default="{ row }">
            {{ formatTime(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="120">
          <template #default="{ row }">
            <el-button 
              v-if="row.status === 'PENDING'" 
              size="small" 
              type="success"
              @click="handleComplete(row)"
            >
              确认取件
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { reservationAPI } from '../../api'

const loading = ref(false)
const dateFilter = ref('')
const reservationList = ref([])

const getStatusText = (status) => {
  const map = {
    'PENDING': '待取件',
    'COMPLETED': '已完成',
    'CANCELLED': '已取消'
  }
  return map[status] || status
}

const getStatusType = (status) => {
  const map = {
    'PENDING': 'warning',
    'COMPLETED': 'success',
    'CANCELLED': 'info'
  }
  return map[status] || 'info'
}

const formatDate = (date) => {
  if (!date) return ''
  const d = new Date(date)
  return `${d.getMonth() + 1}月${d.getDate()}日`
}

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

const loadData = async () => {
  loading.value = true
  try {
    const params = {}
    if (dateFilter.value) {
      const d = new Date(dateFilter.value)
      params.date = `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
    }
    
    const res = await reservationAPI.getAllList(params)
    reservationList.value = res.data || []
  } catch (error) {
    console.error('加载预约列表失败:', error)
  } finally {
    loading.value = false
  }
}

const clearFilter = () => {
  dateFilter.value = ''
  loadData()
}

const handleComplete = async (row) => {
  try {
    await reservationAPI.complete(row.id)
    ElMessage.success('确认取件成功')
    loadData()
  } catch (error) {
    console.error('操作失败:', error)
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.reservation-manage h1 {
  margin-bottom: 20px;
  color: #333;
}

.filter-bar {
  margin-bottom: 20px;
}

.reservation-table {
  background: white;
  padding: 20px;
  border-radius: 10px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}
</style>
