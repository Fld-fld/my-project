<template>
  <div class="forget-password-container">
    <div class="forget-password-box">
      <h2>校园快递取件优化系统</h2>
      <p class="subtitle">忘记密码</p>
      
      <el-form :model="form" :rules="rules" ref="formRef" class="forget-form">
        <el-form-item prop="phone">
          <el-input 
            v-model="form.phone" 
            placeholder="请输入手机号"
            prefix-icon="User"
          />
        </el-form-item>
        
        <el-form-item v-if="step === 2" prop="code">
          <el-input 
            v-model="form.code" 
            placeholder="请输入验证码"
            prefix-icon="Message"
          />
          <el-button 
            type="text" 
            @click="sendCode"
            :disabled="countdown > 0"
            class="send-code-btn"
          >
            {{ countdown > 0 ? `${countdown}s` : '发送验证码' }}
          </el-button>
        </el-form-item>
        
        <el-form-item v-if="step === 2" prop="newPassword">
          <el-input 
            v-model="form.newPassword" 
            type="password"
            placeholder="请输入新密码"
            prefix-icon="Lock"
          />
        </el-form-item>
        
        <el-form-item v-if="step === 2" prop="confirmPassword">
          <el-input 
            v-model="form.confirmPassword" 
            type="password"
            placeholder="请确认新密码"
            prefix-icon="Lock"
          />
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="handleSubmit" class="submit-btn">
            {{ step === 1 ? '获取验证码' : '确认重置' }}
          </el-button>
        </el-form-item>
      </el-form>
      
      <div class="links">
        <router-link to="/login">返回登录</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { authAPI } from '../api'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)
const step = ref(1)
const countdown = ref(0)

const form = reactive({
  phone: '',
  code: '',
  newPassword: '',
  confirmPassword: ''
})

const rules = {
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  code: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    { pattern: /^\d{6}$/, message: '验证码格式不正确', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' }
  ]
}

watch(form, (val) => {
  rules.confirmPassword[1] = {
    validator: (rule, value, callback) => {
      if (value !== val.newPassword) {
        callback(new Error('两次输入的密码不一致'))
      } else {
        callback()
      }
    },
    trigger: 'blur'
  }
}, { deep: true })

const sendCode = async () => {
  try {
    await formRef.value.validateField('phone')
    
    loading.value = true
    await authAPI.sendCode(form.phone)
    ElMessage.success('验证码已发送，请注意查收')
    
    countdown.value = 60
    const timer = setInterval(() => {
      countdown.value--
      if (countdown.value <= 0) {
        clearInterval(timer)
      }
    }, 1000)
  } catch (error) {
    console.error('发送验证码失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSubmit = async () => {
  try {
    if (step.value === 1) {
      await formRef.value.validateField('phone')
      step.value = 2
      await sendCode()
    } else {
      await formRef.value.validate()
      loading.value = true
      
      await authAPI.resetPassword({
        phone: form.phone,
        code: form.code,
        newPassword: form.newPassword
      })
      
      ElMessage.success('密码重置成功')
      router.push('/login')
    }
  } catch (error) {
    console.error('操作失败:', error)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.forget-password-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.forget-password-box {
  background: white;
  padding: 40px;
  border-radius: 10px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
  width: 400px;
}

h2 {
  text-align: center;
  color: #333;
  margin-bottom: 10px;
}

.subtitle {
  text-align: center;
  color: #666;
  margin-bottom: 30px;
}

.forget-form {
  margin-top: 20px;
}

.submit-btn {
  width: 100%;
  height: 40px;
}

.links {
  text-align: center;
  margin-top: 20px;
}

.links a {
  color: #667eea;
  text-decoration: none;
}

.send-code-btn {
  position: absolute;
  right: 10px;
  top: 50%;
  transform: translateY(-50%);
  padding: 0;
  font-size: 14px;
}
</style>