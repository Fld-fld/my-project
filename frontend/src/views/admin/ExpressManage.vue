<template>
  <div class="express-manage">
    <h1>快递管理</h1>
    
    <!-- 筛选 -->
    <div class="filter-bar">
      <el-select v-model="statusFilter" placeholder="筛选状态" clearable @change="loadData">
        <el-option label="全部" value="" />
        <el-option label="待取" value="PENDING" />
        <el-option label="已预约" value="RESERVED" />
        <el-option label="已取" value="PICKED_UP" />
      </el-select>
      <el-button type="primary" @click="$router.push('/admin/express/add')">
        录入快递
      </el-button>
    </div>
    
    <!-- 快递列表 -->
    <div class="express-table" v-loading="loading">
      <el-table :data="expressList" border style="width: 100%">
        <el-table-column prop="trackingNumber" label="快递单号" width="180" />
        <el-table-column prop="company" label="快递公司" width="120" />
        <el-table-column prop="recipientName" label="收件人" width="100" />
        <el-table-column prop="recipientPhone" label="手机号" width="130" />
        <el-table-column prop="location" label="存放位置" width="120" />
        <el-table-column prop="pickupCode" label="取件码" width="100">
          <template #default="{ row }">
            <span class="pickup-code">{{ row.pickupCode }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="到达时间" width="160">
          <template #default="{ row }">
            {{ formatTime(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="150">
          <template #default="{ row }">
            <el-button 
              v-if="row.status !== 'PICKED_UP'" 
              size="small" 
              type="success"
              @click="handlePickup(row)"
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
import { expressAPI } from '../../api'

const loading = ref(false)
const statusFilter = ref('')
const expressList = ref([])

const getStatusText = (status) => {
  const map = {
    'PENDING': '待取',
    'RESERVED': '已预约',
    'PICKED_UP': '已取'
  }
  return map[status] || status
}

const getStatusType = (status) => {
  const map = {
    'PENDING': 'warning',
    'RESERVED': 'primary',
    'PICKED_UP': 'success'
  }
  return map[status] || 'info'
}

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await expressAPI.getAllList({ status: statusFilter.value })
    expressList.value = res.data || []
  } catch (error) {
    console.error('加载快递列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handlePickup = async (row) => {
  try {
    await expressAPI.updateStatus(row.id, 'PICKED_UP')
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
.express-manage h1 {
  margin-bottom: 20px;
  color: #333;
}

.filter-bar {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
}

.express-table {
  background: white;
  padding: 20px;
  border-radius: 10px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.pickup-code {
  font-weight: bold;
  color: #667eea;
}
</style>
