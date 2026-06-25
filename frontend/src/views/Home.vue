<template>
  <div class="home">
    <!-- 顶部轮播图 -->
    <div class="carousel-container">
      <el-carousel 
        :interval="4000"
        indicator-position="bottom"
        arrow="hover"
        class="custom-carousel"
      >
        <el-carousel-item v-for="(banner, index) in banners" :key="index">
          <div class="banner-content" :class="banner.type">
            <div class="banner-icon">{{ banner.icon }}</div>
            <div class="banner-text">
              <h3>{{ banner.title }}</h3>
              <p>{{ banner.subtitle }}</p>
            </div>
          </div>
        </el-carousel-item>
      </el-carousel>
    </div>
    
    <!-- 搜索框 -->
    <div class="search-section">
      <div class="search-box">
        <el-input
          v-model="searchKeyword"
          placeholder="输入取件码或快递单号查询"
          size="large"
          @keyup.enter="handleSearch"
          class="search-input"
        >
          <template #prefix>
            <el-icon class="search-icon"><Search /></el-icon>
          </template>
          <template #append>
            <el-button @click="handleSearch" class="search-btn">查询</el-button>
          </template>
        </el-input>
      </div>
    </div>
    
    <!-- 功能入口卡片 -->
    <div class="action-grid">
      <div class="action-card" @click="$router.push('/my-express')">
        <div class="action-icon blue">
          📦
        </div>
        <span class="action-text">我的快递</span>
      </div>
      <div class="action-card" @click="$router.push('/reservation')">
        <div class="action-icon green">
          <el-icon><Calendar /></el-icon>
        </div>
        <span class="action-text">取件预约</span>
      </div>
      <div class="action-card" @click="$router.push('/notifications')">
        <div class="action-icon orange">
          <el-icon><Bell /></el-icon>
        </div>
        <span class="action-text">消息通知</span>
      </div>
    </div>
    
    <!-- 待取快递快捷区 -->
    <div class="pending-section" v-if="pendingList.length > 0">
      <div class="section-header">
        <h3>待取快递</h3>
        <span class="more-btn" @click="$router.push('/my-express')">查看全部 ›</span>
      </div>
      <div class="pending-list">
        <div class="pending-card" v-for="item in pendingList.slice(0, 2)" :key="item.id">
          <div class="pending-header">
            <span class="company-tag">{{ item.company }}</span>
            <span class="pickup-code" @click.stop="copyCode(item.pickupCode)">
              {{ item.pickupCode }}
              <el-icon class="copy-icon"><DocumentCopy /></el-icon>
            </span>
          </div>
          <div class="pending-info">
            <span class="location">
              📍
              {{ item.location }}
            </span>
            <span class="time">{{ formatTime(item.createdAt) }}</span>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 最新通知 -->
    <div class="notification-section" v-if="latestNotification">
      <div class="section-header">
        <h3>最新通知</h3>
      </div>
      <div class="notification-card" @click="$router.push('/notifications')">
        <div class="notif-icon">📢</div>
        <div class="notif-content">
          <div class="notif-title">{{ latestNotification.title }}</div>
          <div class="notif-time">{{ formatTime(latestNotification.createdAt) }}</div>
        </div>
        <el-icon class="notif-arrow"><ArrowRight /></el-icon>
      </div>
    </div>
    
    <!-- 系统公告 -->
    <div class="notice-section" v-if="notices.length > 0">
      <div class="section-header">
        <h3>系统公告</h3>
        <span class="more-btn">共 {{ notices.length }} 条</span>
      </div>
      <div class="notice-list">
        <div class="notice-item" v-for="notice in notices.slice(0, 3)" :key="notice.id" @click="showNoticeDetail(notice)">
          <div class="notice-icon">📋</div>
          <div class="notice-content">
            <div class="notice-title">{{ notice.title }}</div>
            <div class="notice-desc">{{ notice.content ? notice.content.substring(0, 40) + (notice.content.length > 40 ? '...' : '') : '' }}</div>
            <div class="notice-time">{{ formatTime(notice.createTime) }}</div>
          </div>
          <el-icon class="notice-arrow"><ArrowRight /></el-icon>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { expressAPI, notificationAPI, noticeAPI } from '../api'
import { Search, Calendar, Bell, DocumentCopy, ArrowRight } from '@element-plus/icons-vue'

const searchKeyword = ref('')
const pendingList = ref([])
const latestNotification = ref(null)
const notices = ref([])

const banners = ref([
  {
    type: 'announcement',
    icon: '📢',
    title: '取件提醒',
    subtitle: '请及时取走您的快递'
  },
  {
    type: 'info',
    icon: '📦',
    title: '新到快递',
    subtitle: '今日已到件12件'
  },
  {
    type: 'notice',
    icon: '⏰',
    title: '预约取件',
    subtitle: '预约时段可减少等待'
  }
])

const copyCode = async (code) => {
  try {
    await navigator.clipboard.writeText(code)
    ElMessage.success('取件码已复制')
  } catch {
    ElMessage.error('复制失败')
  }
}

const handleSearch = () => {
  if (searchKeyword.value) {
    window.location.href = `/my-express?keyword=${searchKeyword.value}`
  }
}

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  return `${date.getMonth() + 1}-${date.getDate()} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

const loadData = async () => {
  try {
    const expressRes = await expressAPI.getList()
    const expressList = expressRes.data || []
    pendingList.value = expressList.filter(e => e.status === 'PENDING' || e.status === 'RESERVED')
    
    const notifRes = await notificationAPI.getUnreadList()
    const notifications = notifRes.data || []
    if (notifications.length > 0) {
      latestNotification.value = notifications[0]
    }
    
    // 加载公告
    const noticeRes = await noticeAPI.getList()
    notices.value = noticeRes.data || []
  } catch (error) {
    console.error('加载数据失败:', error)
  }
}

const showNoticeDetail = (notice) => {
  ElMessageBox.alert(notice.content, notice.title, {
    confirmButtonText: '我知道了'
  })
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.home {
  max-width: 800px;
  margin: 0 auto;
  padding-bottom: 80px;
}

/* 轮播图 */
.carousel-container {
  margin: -20px -20px 0;
  overflow: hidden;
}

.custom-carousel :deep(.el-carousel__container) {
  height: 180px;
}

.custom-carousel :deep(.el-carousel__item) {
  padding: 0 20px;
}

.custom-carousel :deep(.el-carousel__indicator--horizontal) {
  bottom: 12px;
}

.custom-carousel :deep(.el-carousel__indicator.is-active button) {
  background: white;
  width: 20px;
  border-radius: 4px;
}

.custom-carousel :deep(.el-carousel__indicator button) {
  background: rgba(255,255,255,0.5);
  width: 8px;
  height: 8px;
  border-radius: 50%;
}

.custom-carousel :deep(.el-carousel__arrow) {
  background: rgba(255,255,255,0.9);
  border-radius: 50%;
  width: 32px;
  height: 32px;
}

.custom-carousel :deep(.el-carousel__arrow i) {
  color: #333;
}

.banner-content {
  height: 100%;
  border-radius: 12px;
  padding: 20px 24px;
  display: flex;
  align-items: center;
  gap: 16px;
}

.banner-content.announcement {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.banner-content.info {
  background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
}

.banner-content.notice {
  background: linear-gradient(135deg, #fc4a1a 0%, #f7b733 100%);
}

.banner-icon {
  font-size: 48px;
}

.banner-text h3 {
  color: white;
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 4px;
}

.banner-text p {
  color: rgba(255,255,255,0.9);
  font-size: 14px;
}

.carousel-btn {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: rgba(255,255,255,0.9);
  border: none;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s;
  z-index: 10;
}

.carousel:hover .carousel-btn {
  opacity: 1;
}

.carousel-btn.prev {
  left: 28px;
}

.carousel-btn.next {
  right: 28px;
}

.carousel-indicators {
  position: absolute;
  bottom: 12px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 8px;
}

.indicator {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: rgba(255,255,255,0.5);
  cursor: pointer;
  transition: all 0.3s;
}

.indicator.active {
  width: 20px;
  border-radius: 4px;
  background: white;
}

/* 搜索框 */
.search-section {
  padding: 20px;
}

.search-box {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.08);
  overflow: hidden;
}

.search-input :deep(.el-input__wrapper) {
  border-radius: 12px 0 0 12px;
  box-shadow: none;
  padding-left: 16px;
}

.search-input :deep(.el-input__inner) {
  font-size: 15px;
}

.search-icon {
  color: #999;
}

.search-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%) !important;
  border: none !important;
  color: white !important;
  font-weight: 500;
  border-radius: 0 12px 12px 0;
}

/* 功能入口 */
.action-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
  padding: 0 20px;
  margin-bottom: 24px;
}

.action-card {
  background: white;
  border-radius: 12px;
  padding: 20px 12px;
  text-align: center;
  cursor: pointer;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
  transition: all 0.3s;
}

.action-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0,0,0,0.1);
}

.action-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 10px;
  font-size: 24px;
}

.action-icon.blue {
  background: linear-gradient(135deg, #667eea20 0%, #764ba220 100%);
  color: #667eea;
}

.action-icon.green {
  background: linear-gradient(135deg, #11998e20 0%, #38ef7d20 100%);
  color: #11998e;
}

.action-icon.orange {
  background: linear-gradient(135deg, #fc4a1a20 0%, #f7b73320 100%);
  color: #fc4a1a;
}

.action-text {
  font-size: 14px;
  color: #333;
  font-weight: 500;
}

/* 待取快递 */
.pending-section {
  padding: 0 20px;
  margin-bottom: 20px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.section-header h3 {
  font-size: 16px;
  color: #333;
  font-weight: 600;
}

.more-btn {
  font-size: 13px;
  color: #667eea;
  cursor: pointer;
}

.pending-list {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.pending-card {
  background: white;
  border-radius: 12px;
  padding: 16px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
  transition: all 0.3s;
}

.pending-card:hover {
  box-shadow: 0 4px 16px rgba(0,0,0,0.1);
}

.pending-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.company-tag {
  font-size: 12px;
  color: #667eea;
  background: #667eea15;
  padding: 2px 8px;
  border-radius: 4px;
  font-weight: 500;
}

.pickup-code {
  font-size: 20px;
  font-weight: 700;
  color: #333;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 4px;
  transition: color 0.2s;
}

.pickup-code:hover {
  color: #667eea;
}

.copy-icon {
  font-size: 14px;
  color: #999;
}

.pending-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
  color: #999;
}

.location {
  display: flex;
  align-items: center;
  gap: 4px;
}

/* 最新通知 */
.notification-section {
  padding: 0 20px;
}

.notification-card {
  background: white;
  border-radius: 12px;
  padding: 16px;
  display: flex;
  align-items: center;
  gap: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
  cursor: pointer;
  transition: all 0.3s;
}

.notification-card:hover {
  box-shadow: 0 4px 16px rgba(0,0,0,0.1);
}

.notif-icon {
  font-size: 32px;
}

.notif-content {
  flex: 1;
}

.notif-title {
  font-size: 14px;
  color: #333;
  margin-bottom: 4px;
}

.notif-time {
  font-size: 12px;
  color: #999;
}

.notif-arrow {
  color: #ccc;
  font-size: 20px;
}

/* 系统公告 */
.notice-section {
  padding: 20px 20px 0;
}

.notice-list {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}

.notice-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 16px;
  border-bottom: 1px solid #f5f5f5;
  cursor: pointer;
  transition: background 0.2s;
}

.notice-item:last-child {
  border-bottom: none;
}

.notice-item:hover {
  background: #f9faff;
}

.notice-icon {
  font-size: 24px;
  flex-shrink: 0;
}

.notice-content {
  flex: 1;
  min-width: 0;
}

.notice-title {
  font-size: 14px;
  color: #333;
  font-weight: 600;
  margin-bottom: 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.notice-desc {
  font-size: 12px;
  color: #666;
  line-height: 1.4;
  margin-bottom: 4px;
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.notice-time {
  font-size: 11px;
  color: #999;
}

.notice-arrow {
  color: #ccc;
  font-size: 18px;
  flex-shrink: 0;
}
</style>
