<template>
  <div class="express-add">
    <div class="page-header">
      <h1>录入快递</h1>
    </div>
    
    <!-- 切换单条/批量录入 -->
    <el-tabs v-model="activeTab" class="custom-tabs">
      <el-tab-pane label="单条录入" name="single">
        <div class="form-container">
          <el-form :model="form" :rules="rules" ref="formRef" label-width="100px" class="two-column-form">
            <el-form-item label="快递单号" prop="trackingNumber" class="form-item">
              <el-input v-model="form.trackingNumber" placeholder="请输入快递单号" />
            </el-form-item>
            
            <el-form-item label="快递公司" prop="company" class="form-item">
              <el-select v-model="form.company" placeholder="请选择快递公司" class="full-width">
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
            
            <el-form-item label="收件人姓名" prop="recipientName" class="form-item">
              <el-input v-model="form.recipientName" placeholder="请输入收件人姓名" />
            </el-form-item>
            
            <el-form-item label="收件人手机" prop="recipientPhone" class="form-item">
              <el-input v-model="form.recipientPhone" placeholder="请输入收件人手机号" />
            </el-form-item>
            
            <el-form-item label="存放位置" prop="location" class="form-item full-width">
              <el-input v-model="form.location" placeholder="请输入快递存放位置，如：A-0123" />
            </el-form-item>
            
            <el-form-item class="form-item full-width submit-item">
              <el-button type="primary" :loading="submitting" @click="handleSubmit" class="submit-btn">
                <el-icon v-if="!submitting"><Check /></el-icon>
                录入快递
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
            class="batch-tip"
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
            class="batch-input"
          />
          
          <div class="batch-footer">
            <span class="batch-count">共 {{ batchCount }} 条数据</span>
            <el-button 
              type="primary" 
              :loading="batchSubmitting" 
              @click="handleBatchSubmit"
              :disabled="batchCount === 0"
              class="batch-btn"
            >
              <el-icon v-if="!batchSubmitting"><Upload /></el-icon>
              批量录入
            </el-button>
          </div>
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { expressAPI } from '@/api'
import { Check, Upload } from '@element-plus/icons-vue'

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
    { required: true, message: '请输入存放位置', trigger: 'blur' },
    { max: 50, message: '存放位置不能超过50个字符', trigger: 'blur' }
  ]
}

const parseBatchLine = (line) => {
  const parts = line.trim().split(/[\s\t]+/)
  if (parts.length < 5) return null
  
  return {
    trackingNumber: parts[0],
    company: parts[1],
    recipientName: parts[2],
    recipientPhone: parts[3],
    location: parts.slice(4).join(' ')
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
    } else if (item.location.length > 50) {
      errors.push(`第${index + 1}行：存放位置不能超过50个字符`)
    }
    if (item.trackingNumber && item.trackingNumber.length > 50) {
      errors.push(`第${index + 1}行：快递单号不能超过50个字符`)
    }
    if (item.company && item.company.length > 30) {
      errors.push(`第${index + 1}行：快递公司不能超过30个字符`)
    }
    if (item.recipientName && item.recipientName.length > 30) {
      errors.push(`第${index + 1}行：收件人姓名不能超过30个字符`)
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
.express-add {
  max-width: 900px;
}

.page-header {
  margin-bottom: 24px;
}

.page-header h1 {
  font-size: 24px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

/* 标签页 */
.custom-tabs :deep(.el-tabs__header) {
  margin-bottom: 20px;
}

.custom-tabs :deep(.el-tabs__item) {
  font-size: 15px;
  padding: 0 20px;
  height: 40px;
  line-height: 40px;
}

.custom-tabs :deep(.el-tabs__item.is-active) {
  color: #667eea;
  font-weight: 600;
}

.custom-tabs :deep(.el-tabs__active-bar) {
  height: 3px;
  border-radius: 3px;
}

/* 表单容器 */
.form-container {
  background: white;
  border-radius: 12px;
  padding: 30px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.two-column-form {
  display: flex;
  flex-wrap: wrap;
  gap: 0;
}

.form-item {
  width: 50%;
  padding-right: 20px;
  box-sizing: border-box;
}

.form-item:nth-child(even) {
  padding-right: 0;
  padding-left: 20px;
}

.form-item.full-width {
  width: 100%;
  padding: 0;
}

.input-icon {
  color: #999;
}

.full-width {
  width: 100%;
}

:deep(.el-select) {
  width: 100%;
}

.submit-item {
  padding: 0 !important;
  margin-top: 20px;
}

.submit-btn {
  width: 200px;
  height: 42px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%) !important;
  border: none !important;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  font-size: 15px;
}

/* 批量录入 */
.batch-container {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.batch-tip {
  margin-bottom: 20px;
  border-radius: 8px;
}

.batch-tip :deep(.el-alert__content) {
  font-size: 13px;
}

.batch-tip p {
  margin: 6px 0;
  line-height: 1.6;
}

.batch-input :deep(.el-textarea__inner) {
  border-radius: 8px;
  font-family: monospace;
  font-size: 13px;
}

.batch-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 16px;
}

.batch-count {
  font-size: 14px;
  color: #999;
}

.batch-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%) !important;
  border: none !important;
  border-radius: 8px;
  display: flex;
  align-items: center;
  gap: 6px;
}
</style>
