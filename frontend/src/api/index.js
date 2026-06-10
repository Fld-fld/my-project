import request from '../utils/request'

// 用户认证
export const authAPI = {
  // 用户登录
  login(data) {
    return request.post('/auth/login', data)
  },
  // 用户注册
  register(data) {
    return request.post('/auth/register', data)
  },
  // 获取用户信息
  getUserInfo() {
    return request.get('/auth/userinfo')
  }
}

// 管理员认证
export const adminAuthAPI = {
  // 管理员登录
  login(data) {
    return request.post('/admin/auth/login', data)
  }
}

// 快递管理
export const expressAPI = {
  // 获取快递列表
  getList(params) {
    return request.get('/express/list', { params })
  },
  // 获取快递详情
  getDetail(id) {
    return request.get(`/express/${id}`)
  },
  // 获取所有快递（管理员）
  getAllList(params) {
    return request.get('/express/all', { params })
  },
  // 录入快递（管理员）
  add(data) {
    return request.post('/express', data)
  },
  // 更新快递状态
  updateStatus(id, status) {
    return request.put(`/express/${id}/status`, null, { params: { status } })
  }
}

// 预约管理
export const reservationAPI = {
  // 创建预约
  create(data) {
    return request.post('/reservation', data)
  },
  // 取消预约
  cancel(id) {
    return request.delete(`/reservation/${id}`)
  },
  // 获取我的预约
  getMyList() {
    return request.get('/reservation/my')
  },
  // 获取所有预约（管理员）
  getAllList(params) {
    return request.get('/reservation/all', { params })
  },
  // 完成预约（管理员）
  complete(id) {
    return request.put(`/reservation/${id}/complete`)
  }
}

// 通知管理
export const notificationAPI = {
  // 获取通知列表
  getList() {
    return request.get('/notification/list')
  },
  // 获取未读通知
  getUnreadList() {
    return request.get('/notification/unread')
  },
  // 获取未读数量
  getUnreadCount() {
    return request.get('/notification/unread/count')
  },
  // 标记已读
  markAsRead(id) {
    return request.put(`/notification/${id}/read`)
  },
  // 标记全部已读
  markAllAsRead() {
    return request.put('/notification/read/all')
  }
}

// 管理员统计
export const adminAPI = {
  // 获取统计数据
  getStatistics() {
    return request.get('/admin/statistics')
  }
}
