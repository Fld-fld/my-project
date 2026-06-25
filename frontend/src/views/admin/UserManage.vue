<template>
  <div class="user-manage">
    <el-card title="用户管理">
      <el-table :data="userList" border stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="studentId" label="学号" />
        <el-table-column prop="name" label="姓名" />
        <el-table-column prop="phone" label="手机号" />
        <el-table-column prop="role" label="角色">
          <template #default="scope">
            <span :class="scope.row.role === 'STUDENT' ? 'role-student' : 'role-admin'">
              {{ scope.row.role === 'STUDENT' ? '学生' : '管理员' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态">
          <template #default="scope">
            <div class="status-cell">
              <el-switch 
                v-model="scope.row.status" 
                :active-value="1" 
                :inactive-value="0"
                @change="handleStatusChange(scope.row.id, $event)"
                :disabled="scope.row.role === 'ADMIN'"
                active-color="#67c23a"
                inactive-color="#f56c6c"
              />
              <span :class="scope.row.status === 1 ? 'status-active' : 'status-inactive'">
                {{ scope.row.status === 1 ? '启用' : '禁用' }}
              </span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="180">
          <template #default="scope">
            <span>{{ formatDateTime(scope.row.createdAt) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120">
          <template #default="scope">
            <el-button 
              v-if="scope.row.role !== 'ADMIN'" 
              type="danger" 
              size="small" 
              @click="handleDelete(scope.row.id)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { adminAPI } from '../../api'
import { ElMessage } from 'element-plus'

const userList = ref([])

const loadUserList = async () => {
  try {
    const res = await adminAPI.getUserList()
    userList.value = res.data || []
  } catch (error) {
    console.error('加载用户列表失败:', error)
  }
}

const handleStatusChange = async (id, value) => {
  try {
    await adminAPI.updateUserStatus(id, value)
    ElMessage.success(value === 1 ? '用户已启用' : '用户已禁用')
  } catch (error) {
    ElMessage.error('更新用户状态失败')
    console.error('更新用户状态失败:', error)
    loadUserList()
  }
}

const handleDelete = async (id) => {
  if (confirm('确定要删除该用户吗？')) {
    try {
      await adminAPI.deleteUser(id)
      loadUserList()
    } catch (error) {
      console.error('删除用户失败:', error)
    }
  }
}

const formatDateTime = (dateTime) => {
  if (!dateTime) return ''
  const date = new Date(dateTime)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  const seconds = String(date.getSeconds()).padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
}

onMounted(() => {
  loadUserList()
})
</script>

<style scoped>
.user-manage {
  padding: 20px;
}

.role-student {
  color: #67c23a;
  background: #e8f5e9;
  padding: 2px 8px;
  border-radius: 4px;
}

.role-admin {
  color: #409eff;
  background: #e3f2fd;
  padding: 2px 8px;
  border-radius: 4px;
}

.status-cell {
  display: flex;
  align-items: center;
  gap: 10px;
}

.status-active {
  color: #67c23a;
  font-weight: 500;
}

.status-inactive {
  color: #f56c6c;
  font-weight: 500;
}
</style>
