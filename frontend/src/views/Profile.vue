<template>
  <div class="profile">
    <h1>个人中心</h1>
    
    <!-- 用户信息卡片 -->
    <div class="user-card">
      <div class="card-bg"></div>
      <div class="card-content">
        <div class="avatar">
          {{ userInfo.name?.charAt(0) || 'U' }}
        </div>
        <div class="user-info">
          <div class="name">{{ userInfo.name || '用户' }}</div>
          <div class="meta">
            <span class="meta-item">
              <span class="meta-icon">🎓</span>
              {{ userInfo.studentId || '未知学号' }}
            </span>
            <span class="meta-item">
              <span class="meta-icon">📱</span>
              {{ userInfo.phone || '未知手机' }}
            </span>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 菜单列表 -->
    <div class="menu-section">
      <div class="menu-header">
        <span class="header-title">我的服务</span>
      </div>
      <div class="menu-list">
        <div class="menu-item" @click="$router.push('/my-express')">
          <span class="menu-icon">📦</span>
          <span class="menu-text">我的快递</span>
          <span class="menu-arrow">›</span>
        </div>
        <div class="menu-item" @click="$router.push('/reservation')">
          <span class="menu-icon">📅</span>
          <span class="menu-text">我的预约</span>
          <span class="menu-arrow">›</span>
        </div>
        <div class="menu-item" @click="$router.push('/notifications')">
          <span class="menu-icon">🔔</span>
          <span class="menu-text">消息通知</span>
          <span class="menu-arrow">›</span>
        </div>
        <div class="menu-item" @click="showAddressDialog">
          <span class="menu-icon">📍</span>
          <span class="menu-text">地址管理</span>
          <span class="menu-arrow">›</span>
        </div>
        <div class="menu-item" @click="showFeedbackDialog">
          <span class="menu-icon">💬</span>
          <span class="menu-text">意见反馈</span>
          <span class="menu-arrow">›</span>
        </div>
        <div class="menu-item" @click="showHelpDialog">
          <span class="menu-icon">❓</span>
          <span class="menu-text">帮助中心</span>
          <span class="menu-arrow">›</span>
        </div>
      </div>
    </div>
    
    <!-- 退出登录 -->
    <div class="logout-section">
      <el-button type="danger" plain @click="handleLogout" class="logout-btn">
        <span class="btn-icon">🚪</span>
        退出登录
      </el-button>
    </div>
    
    <!-- 地址管理弹窗 -->
    <el-dialog title="地址管理" v-model="addressDialogVisible" width="90%" class="custom-dialog">
      <div class="dialog-content">
        <div class="no-address-info">
          <div class="no-address-icon">📦</div>
          <div class="no-address-title">校内快递无需地址</div>
          <div class="no-address-desc">快递直接送到学校快递点，凭取件码领取即可</div>
        </div>
      </div>
    </el-dialog>
    
    <!-- 意见反馈弹窗 -->
    <el-dialog title="意见反馈" v-model="feedbackDialogVisible" width="90%" class="custom-dialog">
      <div class="dialog-content">
        <el-form :model="feedbackForm" class="feedback-form">
          <el-form-item>
            <el-input
              v-model="feedbackForm.content"
              type="textarea"
              :rows="4"
              placeholder="请输入您的意见或建议..."
            />
          </el-form-item>
          <el-form-item>
            <el-input
              v-model="feedbackForm.contact"
              placeholder="联系方式（选填）"
            />
          </el-form-item>
        </el-form>
        <div class="dialog-footer">
          <el-button @click="feedbackDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitFeedback">提交</el-button>
        </div>
      </div>
    </el-dialog>
    
    <!-- 帮助中心弹窗 -->
    <el-dialog title="帮助中心" v-model="helpDialogVisible" width="90%" class="custom-dialog">
      <div class="dialog-content">
        <div class="help-item">
          <h4>如何取件？</h4>
          <p>凭取件码到指定快递柜或取件点取件</p>
        </div>
        <div class="help-item">
          <h4>如何预约取件？</h4>
          <p>在"取件预约"页面选择快递和时间段进行预约</p>
        </div>
        <div class="help-item">
          <h4>取件码忘记了怎么办？</h4>
          <p>可以在"我的快递"页面查看取件码</p>
        </div>
        <div class="help-item b站-link">
          <h4>其他问题？🔍</h4>
          <p>点击跳转到B站搜索快递取件攻略</p>
          <el-button type="primary" size="small" @click="openBilibili" class="b站-btn">
            去B站搜索
          </el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '../store/user'

const router = useRouter()
const userStore = useUserStore()

const userInfo = computed(() => userStore.userInfo || {})

const addressDialogVisible = ref(false)
const feedbackDialogVisible = ref(false)
const helpDialogVisible = ref(false)

const feedbackForm = reactive({
  content: '',
  contact: ''
})

const showAddressDialog = () => {
  addressDialogVisible.value = true
}

const showFeedbackDialog = () => {
  feedbackDialogVisible.value = true
}

const showHelpDialog = () => {
  helpDialogVisible.value = true
}

const submitFeedback = () => {
  if (!feedbackForm.content) {
    ElMessage.warning('请输入反馈内容')
    return
  }
  ElMessage.success('感谢您的反馈')
  feedbackForm.content = ''
  feedbackForm.contact = ''
  feedbackDialogVisible.value = false
}

const openBilibili = () => {
  window.open('https://search.bilibili.com/all?keyword=校园快递取件攻略', '_blank')
}

const handleLogout = () => {
  userStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.profile {
  max-width: 800px;
  margin: 0 auto;
  padding: 0 16px 100px;
}

h1 {
  font-size: 22px;
  font-weight: 600;
  color: #333;
  padding: 16px 0;
}

/* 用户卡片 */
.user-card {
  position: relative;
  border-radius: 16px;
  overflow: hidden;
  margin-bottom: 20px;
}

.card-bg {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 120px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.card-content {
  position: relative;
  padding: 30px 20px 20px;
  display: flex;
  align-items: center;
  gap: 16px;
}

.avatar {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  background: white;
  color: #667eea;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  font-weight: 700;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
  flex-shrink: 0;
}

.user-info {
  flex: 1;
  min-width: 0;
}

.name {
  font-size: 22px;
  font-weight: 700;
  color: white;
  margin-bottom: 8px;
}

.meta {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: rgba(255, 255, 255, 0.9);
}

.meta-icon {
  font-size: 14px;
}

/* 菜单区块 */
.menu-section {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.menu-header {
  padding: 12px 16px;
  border-bottom: 1px solid #f0f0f0;
}

.header-title {
  font-size: 13px;
  color: #999;
  font-weight: 500;
}

.menu-list {
  padding: 0;
}

.menu-item {
  display: flex;
  align-items: center;
  padding: 14px 16px;
  border-bottom: 1px solid #f5f5f5;
  cursor: pointer;
  transition: background 0.2s;
}

.menu-item:last-child {
  border-bottom: none;
}

.menu-item:hover {
  background: #f8f9fa;
}

.menu-item:active {
  background: #f0f0f0;
}

.menu-icon {
  font-size: 20px;
  margin-right: 14px;
}

.menu-text {
  flex: 1;
  font-size: 15px;
  color: #333;
}

.menu-arrow {
  font-size: 20px;
  color: #ccc;
}

/* 退出登录 */
.logout-section {
  padding: 0 4px;
}

.logout-btn {
  width: 100%;
  height: 48px;
  border-radius: 12px;
  font-size: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  background: white !important;
  border: 1px solid #f0f0f0 !important;
}

.btn-icon {
  font-size: 18px;
}

/* 弹窗样式 */
.custom-dialog :deep(.el-dialog) {
  border-radius: 16px;
}

.dialog-content {
  padding: 10px 0;
}

.feedback-form {
  margin-bottom: 16px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding-top: 10px;
  border-top: 1px solid #f0f0f0;
}

/* 帮助中心 */
.help-item {
  margin-bottom: 20px;
}

.help-item:last-child {
  margin-bottom: 0;
}

.help-item h4 {
  font-size: 15px;
  color: #333;
  margin-bottom: 6px;
  font-weight: 600;
}

.help-item p {
  font-size: 14px;
  color: #666;
  line-height: 1.5;
}

/* 地址管理提示 */
.no-address-info {
  text-align: center;
  padding: 40px 20px;
}

.no-address-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.no-address-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
}

.no-address-desc {
  font-size: 14px;
  color: #999;
  line-height: 1.5;
}
</style>
