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
  },
  // 发送验证码
  sendCode(phone) {
    return request.post('/auth/send-code', null, { params: { phone } })
  },
  // 重置密码
  resetPassword(data) {
    return request.post('/auth/reset-password', data)
  }
}

// 管理员认证
export const adminAuthAPI = {
  // 管理员登录
  login(data) {
    return request.post('/admin/auth/login', data)
  },
  // 发送验证码
  sendCode(username) {
    return request.post('/admin/auth/send-code', null, { params: { username } })
  },
  // 重置密码
  resetPassword(data) {
    return request.post('/admin/auth/reset-password', data)
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
  getAll(params) {
    return request.get('/express/all', { params })
  },
  // 录入快递（管理员）
  add(data) {
    return request.post('/express', data)
  },
  // 更新快递状态
  updateStatus(id, status) {
    return request.put(`/express/${id}/status`, null, { params: { status } })
  },
  // 删除快递
  delete(id) {
    return request.delete(`/express/${id}`)
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
  getAll(params) {
    return request.get('/reservation/all', { params })
  },
  // 完成预约（管理员）
  complete(id) {
    return request.put(`/reservation/${id}/complete`)
  }
}

// 用户管理（管理员侧）
export const userAPI = {
  getList() {
    return request.get('/admin/user/list')
  },
  getDetail(id) {
    return request.get(`/admin/user/${id}`)
  },
  updateStatus(id, status) {
    return request.put(`/admin/user/${id}/status`, null, { params: { status } })
  },
  delete(id) {
    return request.delete(`/admin/user/${id}`)
  }
}

// 公告管理（管理员侧）
export const noticeAPI = {
  getList() {
    return request.get('/admin/notice/list')
  },
  getDetail(id) {
    return request.get(`/admin/notice/${id}`)
  },
  add(data) {
    return request.post('/admin/notice', data)
  },
  update(id, data) {
    return request.put(`/admin/notice/${id}`, data)
  },
  delete(id) {
    return request.delete(`/admin/notice/${id}`)
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
  },
  // 修改密码
  changePassword(oldPassword, newPassword) {
    return request.put('/admin/password', null, { params: { oldPassword, newPassword } })
  },
  // 获取用户列表
  getUserList() {
    return request.get('/admin/user/list')
  },
  // 更新用户状态
  updateUserStatus(id, status) {
    return request.put(`/admin/user/${id}/status`, null, { params: { status } })
  },
  // 删除用户
  deleteUser(id) {
    return request.delete(`/admin/user/${id}`)
  },
  // 获取公告列表
  getNoticeList() {
    return request.get('/admin/notice/list')
  },
  // 创建公告
  createNotice(data) {
    return request.post('/admin/notice', data)
  },
  // 更新公告
  updateNotice(id, data) {
    return request.put(`/admin/notice/${id}`, data)
  },
  // 删除公告
  deleteNotice(id) {
    return request.delete(`/admin/notice/${id}`)
  }
}
