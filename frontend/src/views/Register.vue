<template>
  <div class="login-container">
    <div class="login-box">
      <h2>校园快递取件优化系统</h2>
      <p class="subtitle">用户注册</p>
      
      <el-form :model="form" :rules="rules" ref="formRef" class="login-form">
        <el-form-item prop="studentId">
          <el-input 
            v-model="form.studentId" 
            placeholder="请输入学号"
            prefix-icon="School"
          />
        </el-form-item>
        
        <el-form-item prop="name">
          <el-input 
            v-model="form.name" 
            placeholder="请输入姓名"
            prefix-icon="User"
          />
        </el-form-item>
        
        <el-form-item prop="phone">
          <el-input 
            v-model="form.phone" 
            placeholder="请输入手机号"
            prefix-icon="Phone"
          />
        </el-form-item>
        
        <el-form-item prop="password">
          <el-input 
            v-model="form.password" 
            :type="showPassword ? 'text' : 'password'" 
            placeholder="请输入密码"
            prefix-icon="Lock"
            class="password-input"
          >
            <template #suffix>
              <span class="password-toggle" @click="showPassword = !showPassword">
                <el-icon :size="20" class="eye-icon" :class="{ 'active': showPassword }">
                  <component :is="showPassword ? 'Eye' : 'EyeClosed'" />
                </el-icon>
              </span>
            </template>
          </el-input>
        </el-form-item>
        
        <el-form-item prop="confirmPassword">
          <el-input 
            v-model="form.confirmPassword" 
            :type="showConfirmPassword ? 'text' : 'password'" 
            placeholder="请确认密码"
            prefix-icon="Lock"
            class="password-input"
          >
            <template #suffix>
              <span class="password-toggle" @click="showConfirmPassword = !showConfirmPassword">
                <el-icon :size="20" class="eye-icon" :class="{ 'active': showConfirmPassword }">
                  <component :is="showConfirmPassword ? 'Eye' : 'EyeClosed'" />
                </el-icon>
              </span>
            </template>
          </el-input>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="handleRegister" class="login-btn">
            注册
          </el-button>
        </el-form-item>
      </el-form>
      
      <div class="links">
        <router-link to="/login">已有账号？去登录</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { authAPI } from '../api'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)

const form = reactive({
  studentId: '',
  name: '',
  phone: '',
  password: '',
  confirmPassword: ''
})

const showPassword = ref(false)
const showConfirmPassword = ref(false)

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== form.password) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  studentId: [
    { required: true, message: '请输入学号', trigger: 'blur' }
  ],
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码至少6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

const handleRegister = async () => {
  try {
    await formRef.value.validate()
    loading.value = true
    
    await authAPI.register({
      studentId: form.studentId,
      name: form.name,
      phone: form.phone,
      password: form.password
    })
    
    ElMessage.success('注册成功，请登录')
    router.push('/login')
  } catch (error) {
    console.error('注册失败:', error)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-box {
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

.login-form {
  margin-top: 20px;
}

.login-btn {
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

.password-input {
  --el-input-suffix-icon-color: #999;
}

.password-toggle {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  cursor: pointer;
  border-radius: 50%;
  transition: all 0.2s ease;
  margin-right: 4px;
  background-color: #f5f5f5;
  border: 1px solid #ddd;
}

.password-toggle:hover {
  background-color: #e8e8e8;
  border-color: #667eea;
}

.password-toggle:active {
  background-color: #d8d8d8;
}

.eye-icon {
  color: #667eea;
  font-size: 22px;
  transition: color 0.2s ease;
}

.eye-icon.active {
  color: #764ba2;
}
</style>
