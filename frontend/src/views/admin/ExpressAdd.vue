<template>
  <div class="express-add">
    <h1>录入快递</h1>
    
    <!-- 切换单条/批量录入 -->
    <el-tabs v-model="activeTab">
      <el-tab-pane label="单条录入" name="single">
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
              <el-input v-model="form.location" placeholder="请输入快递存放位置，如：A-0123" />
            </el-form-item>
            
            <el-form-item>
              <el-button type="primary" :loading="submitting" @click="handleSubmit" style="width: 100%">
                录入
              </el-button>
            </el-form-item>
          </el-form>
        </div>
      </el-tab-pane>
      
      <el-tab-pane label="批量录入" name="batch">
        <div class="batch-container">
          <el-alert
            title="批量录入说明"
            type="info"
            :closable="false"
            show-icon
            style="margin-bottom: 20px"
          >
            <p>请按以下格式输入，每行一条数据，字段之间用空格或制表符分隔：</p>
            <p><strong>快递单号 快递公司 收件人姓名 收件人手机 存放位置</strong></p>
            <p>示例：SF123456789 顺丰速运 张三 13800138001 A-0123</p>
          </el-alert>
          
          <el-input
            v-model="batchText"
            type="textarea"
            :rows="10"
            placeholder="请粘贴批量数据..."
            style="margin-bottom: 20px"
          />
          
          <el-button 
            type="primary" 
            :loading="batchSubmitting" 
            @click="handleBatchSubmit"
            style="width: 100%"
          >
            批量录入（{{ batchCount }}条）
          </el-button>
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { expressAPI } from '../../api'

const router = useRouter()
const formRef = ref(null)
const submitting = ref(false)
const activeTab = ref('single')
const batchText = ref('')
const batchSubmitting = ref(false)

const form = reactive({
  trackingNumber: '',
  company: '',
  recipientName: '',
  recipientPhone: '',
  location: ''
})

const batchCount = computed(() => {
  if (!batchText.value.trim()) return 0
  return batchText.value.trim().split('\n').filter(line => line.trim()).length
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

const parseBatchLine = (line) => {
  // 支持空格或制表符分隔
  const parts = line.trim().split(/[\s\t]+/)
  if (parts.length < 5) return null
  
  return {
    trackingNumber: parts[0],
    company: parts[1],
    recipientName: parts[2],
    recipientPhone: parts[3],
    location: parts.slice(4).join(' ') // 位置可能包含空格
  }
}

const validateBatchData = (data) => {
  const errors = []
  const phoneRegex = /^1[3-9]\d{9}$/
  
  data.forEach((item, index) => {
    if (!item.trackingNumber) {
      errors.push(`第${index + 1}行：缺少快递单号`)
    }
    if (!item.company) {
      errors.push(`第${index + 1}行：缺少快递公司`)
    }
    if (!item.recipientName) {
      errors.push(`第${index + 1}行：缺少收件人姓名`)
    }
    if (!item.recipientPhone) {
      errors.push(`第${index + 1}行：缺少收件人手机`)
    } else if (!phoneRegex.test(item.recipientPhone)) {
      errors.push(`第${index + 1}行：手机号格式不正确`)
    }
    if (!item.location) {
      errors.push(`第${index + 1}行：缺少存放位置`)
    }
  })
  
  return errors
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

const handleBatchSubmit = async () => {
  if (!batchText.value.trim()) {
    ElMessage.warning('请输入批量数据')
    return
  }
  
  const lines = batchText.value.trim().split('\n').filter(line => line.trim())
  const batchData = lines.map(parseBatchLine).filter(item => item !== null)
  
  if (batchData.length === 0) {
    ElMessage.error('没有有效的数据行')
    return
  }
  
  // 校验数据
  const errors = validateBatchData(batchData)
  if (errors.length > 0) {
    ElMessage.error({
      message: `数据校验失败：\n${errors.join('\n')}`,
      duration: 5000
    })
    return
  }
  
  try {
    batchSubmitting.value = true
    
    // 逐条提交
    let successCount = 0
    let failCount = 0
    const failMessages = []
    
    for (const item of batchData) {
      try {
        await expressAPI.add(item)
        successCount++
      } catch (error) {
        failCount++
        failMessages.push(`${item.trackingNumber}: ${error.message || '录入失败'}`)
      }
    }
    
    if (failCount === 0) {
      ElMessage.success(`批量录入成功！共录入 ${successCount} 条`)
    } else {
      ElMessage.warning({
        message: `录入完成：成功${successCount}条，失败${failCount}条\n${failMessages.join('\n')}`,
        duration: 8000
      })
    }
    
    batchText.value = ''
  } catch (error) {
    console.error('批量录入失败:', error)
  } finally {
    batchSubmitting.value = false
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

.batch-container {
  background: white;
  padding: 30px;
  border-radius: 10px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

:deep(.el-alert__content) {
  font-size: 13px;
}

:deep(.el-alert__content p) {
  margin: 5px 0;
}
</style>
