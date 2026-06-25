<template>
  <div class="login-container">
    <div class="login-box">
      <h2>校园快递取件优化系统</h2>
      <p class="subtitle">用户登录</p>
      
      <el-form :model="form" :rules="rules" ref="formRef" class="login-form">
        <el-form-item prop="phone">
          <el-input 
            v-model="form.phone" 
            placeholder="请输入手机号"
            prefix-icon="User"
          />
        </el-form-item>
        
        <el-form-item prop="password">
          <el-input 
            v-model="form.password" 
            :type="showPassword ? 'text' : 'password'" 
            placeholder="请输入密码"
            prefix-icon="Lock"
            @keyup.enter="handleLogin"
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
        
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="handleLogin" class="login-btn">
            登录
          </el-button>
        </el-form-item>
      </el-form>
      
      <div class="links">
        <router-link to="/register">还没有账号？去注册</router-link>
        <span class="divider">|</span>
        <router-link to="/forget-password">忘记密码?</router-link>
        <span class="divider">|</span>
        <router-link to="/admin/login">管理员登录</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { authAPI } from '../api'
import { useUserStore } from '../store/user'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref(null)
const loading = ref(false)

const form = reactive({
  phone: '',
  password: ''
})

const showPassword = ref(false)

const rules = {
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  try {
    await formRef.value.validate()
    loading.value = true
    
    const res = await authAPI.login(form)
    userStore.login(res.data.token, res.data.user)
    
    ElMessage.success('登录成功')
    router.push('/home')
  } catch (error) {
    console.error('登录失败:', error)
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

.divider {
  margin: 0 10px;
  color: #ccc;
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

.eye-icon:hover {
  color: #667eea;
}
</style>
