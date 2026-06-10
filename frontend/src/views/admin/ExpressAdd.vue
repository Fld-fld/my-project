<template>
  <div class="express-add">
    <h1>录入快递</h1>
    
    <div class="form-container">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="快递单号" prop="trackingNumber">
          <el-input v-model="form.trackingNumber" placeholder="请输入快递单号" />
        </el-form-item>
        
        <el-form-item label="快递公司" prop="company">
          <el-select v-model="form.company" placeholder="请选择快递公司" style="width: 100%">
            <el-option label="顺丰速运" value="顺丰速运" />
            <el-option label="圆通速递" value="圆通速递" />
            <el-option label="中通快递" value="中通快递" />
            <el-option label="韵达快递" value="韵达快递" />
            <el-option label="申通快递" value="申通快递" />
            <el-option label="京东物流" value="京东物流" />
            <el-option label="邮政快递" value="邮政快递" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="收件人姓名" prop="recipientName">
          <el-input v-model="form.recipientName" placeholder="请输入收件人姓名" />
        </el-form-item>
        
        <el-form-item label="收件人手机" prop="recipientPhone">
          <el-input v-model="form.recipientPhone" placeholder="请输入收件人手机号" />
        </el-form-item>
        
        <el-form-item label="存放位置" prop="location">
          <el-input v-model="form.location" placeholder="请输入快递存放位置，如：A区-0123" />
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" :loading="submitting" @click="handleSubmit" style="width: 100%">
            录入
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { expressAPI } from '../../api'

const router = useRouter()
const formRef = ref(null)
const submitting = ref(false)

const form = reactive({
  trackingNumber: '',
  company: '',
  recipientName: '',
  recipientPhone: '',
  location: ''
})

const rules = {
  trackingNumber: [
    { required: true, message: '请输入快递单号', trigger: 'blur' }
  ],
  company: [
    { required: true, message: '请选择快递公司', trigger: 'change' }
  ],
  recipientName: [
    { required: true, message: '请输入收件人姓名', trigger: 'blur' }
  ],
  recipientPhone: [
    { required: true, message: '请输入收件人手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  location: [
    { required: true, message: '请输入存放位置', trigger: 'blur' }
  ]
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    submitting.value = true
    
    await expressAPI.add(form)
    
    ElMessage.success('录入成功')
    formRef.value.resetFields()
  } catch (error) {
    console.error('录入失败:', error)
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.express-add h1 {
  margin-bottom: 20px;
  color: #333;
}

.form-container {
  background: white;
  padding: 30px;
  border-radius: 10px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  max-width: 600px;
}
</style>
