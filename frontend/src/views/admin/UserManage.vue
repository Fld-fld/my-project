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
            <el-switch 
              :value="scope.row.status === 1" 
              @change="handleStatusChange(scope.row.id, $event)"
              :disabled="scope.row.role === 'ADMIN'"
            />
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
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
    await adminAPI.updateUserStatus(id, value ? 1 : 0)
    loadUserList()
  } catch (error) {
    console.error('更新用户状态失败:', error)
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
</style>