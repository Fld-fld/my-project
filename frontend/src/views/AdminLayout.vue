<template>
  <div class="admin-layout">
    <!-- 侧边栏 -->
    <div class="sidebar">
      <div class="logo">
        <span class="logo-icon">📦</span>
        <span class="logo-text">快递管理</span>
      </div>
      <nav class="nav">
        <router-link to="/admin/home" class="nav-item">
          <span class="icon">📊</span>
          <span>首页</span>
        </router-link>
        <router-link to="/admin/express/add" class="nav-item">
          <span class="icon">➕</span>
          <span>录入快递</span>
        </router-link>
        <router-link to="/admin/express/manage" class="nav-item">
          <span class="icon">📦</span>
          <span>快递管理</span>
        </router-link>
        <router-link to="/admin/reservation/manage" class="nav-item">
          <span class="icon">📅</span>
          <span>预约管理</span>
        </router-link>
        <router-link to="/admin/user/manage" class="nav-item">
          <span class="icon">👥</span>
          <span>用户管理</span>
        </router-link>
        <router-link to="/admin/notice/manage" class="nav-item">
          <span class="icon">📢</span>
          <span>公告管理</span>
        </router-link>
      </nav>
      <div class="sidebar-footer">
        <div class="admin-info" @click="showSettings">
          <div class="admin-avatar">{{ adminName?.charAt(0) || 'A' }}</div>
          <div class="admin-detail">
            <span class="admin-name">{{ adminName || '管理员' }}</span>
            <span class="admin-role">管理员</span>
          </div>
        </div>
        <el-button type="danger" size="small" @click="handleLogout" class="logout-btn">
          退出
        </el-button>
      </div>
    </div>
    
    <!-- 主体内容 -->
    <div class="main">
      <router-view />
    </div>
    
    <!-- 个人设置弹窗 -->
    <el-dialog title="个人设置" v-model="settingsVisible" width="500px" class="settings-dialog">
      <div class="settings-content">
        <div class="settings-header">
          <div class="settings-avatar">{{ adminName?.charAt(0) || 'A' }}</div>
          <div class="settings-info">
            <span class="settings-name">{{ adminName }}</span>
            <span class="settings-role">管理员</span>
          </div>
        </div>
        <el-divider />
        <div class="settings-menu">
          <div class="settings-item" @click="showPasswordDialog">
            <span class="settings-icon">🔑</span>
            <span>修改密码</span>
            <span class="settings-arrow">›</span>
          </div>
        </div>
      </div>
    </el-dialog>
    
    <!-- 修改密码弹窗 -->
    <el-dialog title="修改密码" v-model="passwordDialogVisible" width="400px" class="password-dialog">
      <el-form :model="passwordForm" :rules="passwordRules" ref="passwordFormRef" label-width="80px">
        <el-form-item label="旧密码" prop="oldPassword">
          <el-input v-model="passwordForm.oldPassword" type="password" placeholder="请输入旧密码" show-password />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="passwordForm.newPassword" type="password" placeholder="请输入新密码" show-password />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="passwordForm.confirmPassword" type="password" placeholder="请确认新密码" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="passwordDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleChangePassword">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, reactive, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../store/user'
import { ElMessage } from 'element-plus'
import { adminAPI } from '@/api'

const router = useRouter()
const userStore = useUserStore()

const adminName = computed(() => userStore.userInfo?.name || userStore.userInfo?.username || '')

const settingsVisible = ref(false)
const passwordDialogVisible = ref(false)

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const passwordFormRef = ref(null)

const validateConfirm = (rule, value, callback) => {
  if (value !== passwordForm.newPassword) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入旧密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码至少6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirm, trigger: 'blur' }
  ]
}

watch(passwordForm, (val) => {
  passwordRules.confirmPassword[1] = {
    validator: validateConfirm,
    trigger: 'blur'
  }
})

const showSettings = () => {
  settingsVisible.value = true
}

const showPasswordDialog = () => {
  settingsVisible.value = false
  passwordDialogVisible.value = true
}

const handleChangePassword = async () => {
  try {
    await passwordFormRef.value.validate()
    await adminAPI.changePassword(passwordForm.oldPassword, passwordForm.newPassword)
    ElMessage.success('密码修改成功')
    passwordDialogVisible.value = false
    passwordForm.oldPassword = ''
    passwordForm.newPassword = ''
    passwordForm.confirmPassword = ''
  } catch (error) {
    console.error('修改密码失败:', error)
  }
}

const handleLogout = () => {
  userStore.logout()
  router.push('/admin/login')
}
</script>

<style scoped>
.admin-layout {
  display: flex;
  min-height: 100vh;
}

.sidebar {
  width: 220px;
  background: linear-gradient(180deg, #2c3e50 0%, #34495e 100%);
  color: white;
  display: flex;
  flex-direction: column;
  position: fixed;
  height: 100vh;
}

.logo {
  padding: 24px 20px;
  display: flex;
  align-items: center;
  gap: 12px;
  border-bottom: 1px solid rgba(255,255,255,0.1);
}

.logo-icon {
  font-size: 28px;
}

.logo-text {
  font-size: 18px;
  font-weight: 600;
}

.nav {
  flex: 1;
  padding: 16px 0;
  overflow-y: auto;
}

.nav-item {
  display: flex;
  align-items: center;
  padding: 14px 20px;
  color: #b8c6db;
  text-decoration: none;
  transition: all 0.2s;
  border-left: 3px solid transparent;
}

.nav-item:hover {
  background: rgba(255,255,255,0.05);
  color: white;
}

.nav-item.router-link-active {
  background: rgba(102, 126, 234, 0.2);
  color: white;
  border-left-color: #667eea;
}

.icon {
  margin-right: 12px;
  font-size: 18px;
}

.sidebar-footer {
  padding: 16px;
  border-top: 1px solid rgba(255,255,255,0.1);
}

.admin-info {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: rgba(255,255,255,0.05);
  border-radius: 8px;
  cursor: pointer;
  margin-bottom: 12px;
  transition: background 0.2s;
}

.admin-info:hover {
  background: rgba(255,255,255,0.1);
}

.admin-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
}

.admin-detail {
  display: flex;
  flex-direction: column;
}

.admin-name {
  font-size: 14px;
  font-weight: 500;
  color: white;
}

.admin-role {
  font-size: 12px;
  color: rgba(255,255,255,0.6);
}

.logout-btn {
  width: 100%;
  background: rgba(255,255,255,0.1) !important;
  border: none !important;
  color: white !important;
}

.logout-btn:hover {
  background: rgba(255,255,255,0.2) !important;
}

.main {
  flex: 1;
  margin-left: 220px;
  background: #f5f7fa;
  padding: 24px;
  min-height: 100vh;
}

/* 弹窗样式 */
.settings-dialog :deep(.el-dialog) {
  border-radius: 12px;
}

.settings-content {
  padding: 10px 0;
}

.settings-header {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 0 20px;
}

.settings-avatar {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  font-weight: 600;
}

.settings-info {
  display: flex;
  flex-direction: column;
}

.settings-name {
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.settings-role {
  font-size: 13px;
  color: #999;
  margin-top: 4px;
}

.settings-menu {
  padding: 8px 0;
}

.settings-item {
  display: flex;
  align-items: center;
  padding: 14px 20px;
  cursor: pointer;
  transition: background 0.2s;
}

.settings-item:hover {
  background: #f8f9fa;
}

.settings-icon {
  font-size: 18px;
  margin-right: 12px;
}

.settings-item span:nth-child(2) {
  flex: 1;
  font-size: 14px;
  color: #333;
}

.settings-arrow {
  font-size: 18px;
  color: #ccc;
}

.password-dialog :deep(.el-dialog) {
  border-radius: 12px;
}
</style>
