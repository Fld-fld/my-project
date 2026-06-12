<template>
  <div class="reservation">
    <h1>取件预约</h1>
    
    <!-- 待预约快递 -->
    <div class="section" v-if="!selectedExpress">
      <h3>选择要预约的快递</h3>
      <div class="express-list" v-loading="loading">
        <div 
          v-for="item in pendingList" 
          :key="item.id" 
          class="express-item"
          @click="selectExpress(item)"
        >
          <div class="express-info">
            <div class="company">{{ item.company }}</div>
            <div class="tracking">{{ item.trackingNumber }}</div>
            <div class="location">位置：{{ item.location }}</div>
          </div>
          <div class="pickup-code">取件码：{{ item.pickupCode }}</div>
        </div>
        
        <div v-if="pendingList.length === 0" class="empty">
          <div class="empty-icon">📭</div>
          <div class="empty-text">暂无待预约快递</div>
        </div>
      </div>
    </div>
    
    <!-- 预约表单 -->
    <div class="section" v-if="selectedExpress">
      <div class="back-btn" @click="cancelSelect">
        ‹ 返回选择快递
      </div>
      
      <div class="selected-express">
        <h3>已选快递</h3>
        <div class="express-card">
          <div class="company">{{ selectedExpress.company }}</div>
          <div class="tracking">{{ selectedExpress.trackingNumber }}</div>
          <div class="location">位置：{{ selectedExpress.location }}</div>
          <div class="pickup-code">取件码：{{ selectedExpress.pickupCode }}</div>
        </div>
      </div>
      
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="预约日期" prop="reservationDate">
          <el-date-picker
            v-model="form.reservationDate"
            type="date"
            placeholder="选择日期"
            :picker-options="dateOptions"
            style="width: 100%"
          />
        </el-form-item>
        
        <el-form-item label="开始时间" prop="startTime">
          <el-select v-model="form.startTime" placeholder="选择开始时间" style="width: 100%">
            <el-option
              v-for="time in timeSlots"
              :key="time"
              :label="time"
              :value="time"
            />
          </el-select>
        </el-form-item>
        
        <el-form-item label="结束时间" prop="endTime">
          <el-select v-model="form.endTime" placeholder="选择结束时间" style="width: 100%">
            <el-option
              v-for="time in timeSlots"
              :key="time"
              :label="time"
              :value="time"
            />
          </el-select>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" :loading="submitting" @click="handleSubmit" style="width: 100%">
            确认预约
          </el-button>
        </el-form-item>
      </el-form>
    </div>
    
    <!-- 我的预约 -->
    <div class="section">
      <h3>我的预约</h3>
      <div class="reservation-list" v-loading="loading">
        <div 
          v-for="item in myReservations" 
          :key="item.id" 
          class="reservation-item"
        >
          <div class="res-header">
            <span class="date">{{ formatDate(item.reservationDate) }}</span>
            <span :class="['status', item.status]">{{ getStatusText(item.status) }}</span>
          </div>
          <div class="res-time">{{ item.startTime }} - {{ item.endTime }}</div>
          <div class="res-actions" v-if="item.status === 'PENDING'">
            <el-button size="small" @click="handleCancel(item.id)">取消预约</el-button>
          </div>
        </div>
        
        <div v-if="myReservations.length === 0" class="empty">
          <div class="empty-icon">📅</div>
          <div class="empty-text">暂无预约</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { expressAPI, reservationAPI } from '../api'

const route = useRoute()
const loading = ref(false)
const submitting = ref(false)
const pendingList = ref([])
const myReservations = ref([])
const selectedExpress = ref(null)
const formRef = ref(null)

// 生成时间段选项
const timeSlots = computed(() => {
  const slots = []
  for (let h = 8; h <= 20; h++) {
    for (let m = 0; m < 60; m += 30) {
      const hour = String(h).padStart(2, '0')
      const minute = String(m).padStart(2, '0')
      slots.push(`${hour}:${minute}:00`)
    }
  }
  return slots
})

const form = reactive({
  reservationDate: '',
  startTime: '',
  endTime: ''
})

const dateOptions = {
  disabledDate(time) {
    // 禁止选择今天之前的日期
    return time.getTime() < Date.now() - 86400000
  },
  // 限制只能选择未来 30 天内的日期
  maxDate: new Date(Date.now() + 30 * 86400000)
}

const rules = {
  reservationDate: [
    { required: true, message: '请选择预约日期', trigger: 'change' }
  ],
  startTime: [
    { required: true, message: '请选择开始时间', trigger: 'change' }
  ],
  endTime: [
    { required: true, message: '请选择结束时间', trigger: 'change' }
  ]
}

const getStatusText = (status) => {
  const map = {
    'PENDING': '待取件',
    'COMPLETED': '已完成',
    'CANCELLED': '已取消'
  }
  return map[status] || status
}

const formatDate = (date) => {
  if (!date) return ''
  const d = new Date(date)
  return `${d.getMonth() + 1}月${d.getDate()}日`
}

const loadData = async () => {
  loading.value = true
  try {
    // 加载快递列表
    const expressRes = await expressAPI.getList()
    const allList = expressRes.data || []
    pendingList.value = allList.filter(e => e.status === 'PENDING')
    
    // 检查URL是否有快递ID
    if (route.query.expressId) {
      const express = pendingList.value.find(e => e.id == route.query.expressId)
      if (express) {
        selectedExpress.value = express
      }
    }
    
    // 加载我的预约
    const resRes = await reservationAPI.getMyList()
    myReservations.value = resRes.data || []
  } catch (error) {
    console.error('加载数据失败:', error)
  } finally {
    loading.value = false
  }
}

const selectExpress = (item) => {
  selectedExpress.value = item
}

const cancelSelect = () => {
  selectedExpress.value = null
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    submitting.value = true
    
    await reservationAPI.create({
      expressId: selectedExpress.value.id,
      reservationDate: formatDateForSubmit(form.reservationDate),
      startTime: form.startTime,
      endTime: form.endTime
    })
    
    ElMessage.success('预约成功')
    selectedExpress.value = null
    formRef.value.resetFields()
    loadData()
  } catch (error) {
    console.error('预约失败:', error)
  } finally {
    submitting.value = false
  }
}

const formatDateForSubmit = (date) => {
  if (!date) return ''
  const d = new Date(date)
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
}

const handleCancel = async (id) => {
  try {
    await ElMessageBox.confirm('确定要取消这个预约吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await reservationAPI.cancel(id)
    ElMessage.success('取消成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消失败:', error)
    }
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.reservation {
  max-width: 800px;
  margin: 0 auto;
}

h1 {
  font-size: 24px;
  margin-bottom: 20px;
  color: #333;
}

.section {
  background: white;
  padding: 20px;
  border-radius: 10px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

h3 {
  font-size: 16px;
  margin-bottom: 15px;
  color: #333;
}

.back-btn {
  color: #667eea;
  cursor: pointer;
  margin-bottom: 15px;
  font-size: 14px;
}

.express-item {
  border: 1px solid #eee;
  padding: 15px;
  border-radius: 8px;
  margin-bottom: 10px;
  cursor: pointer;
  transition: all 0.2s;
}

.express-item:hover {
  border-color: #667eea;
  background: #f8f8ff;
}

.express-card {
  background: #f8f8ff;
  padding: 15px;
  border-radius: 8px;
  margin-bottom: 20px;
}

.company {
  font-weight: bold;
  margin-bottom: 5px;
}

.tracking {
  font-size: 14px;
  color: #666;
  margin-bottom: 5px;
}

.location {
  font-size: 14px;
  color: #666;
  margin-bottom: 5px;
}

.pickup-code {
  font-weight: bold;
  color: #667eea;
  font-size: 16px;
}

.empty {
  text-align: center;
  padding: 40px 0;
}

.empty-icon {
  font-size: 40px;
  margin-bottom: 10px;
}

.empty-text {
  color: #999;
  font-size: 14px;
}

.reservation-item {
  border: 1px solid #eee;
  padding: 15px;
  border-radius: 8px;
  margin-bottom: 10px;
}

.res-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 5px;
}

.date {
  font-weight: bold;
}

.status {
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
}

.status.PENDING {
  background: #e3f2fd;
  color: #2196f3;
}

.status.COMPLETED {
  background: #e8f5e9;
  color: #4caf50;
}

.status.CANCELLED {
  background: #f5f5f5;
  color: #999;
}

.res-time {
  font-size: 14px;
  color: #666;
  margin-bottom: 10px;
}

.res-actions {
  text-align: right;
}
</style>
