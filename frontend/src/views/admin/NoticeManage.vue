<template>
  <div class="notice-manage">
    <el-card>
      <div class="toolbar">
        <el-button type="primary" @click="showAddDialog = true">发布公告</el-button>
      </div>
      
      <el-table :data="noticeList" border stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="content" label="内容" :show-overflow-tooltip="true" min-width="200" />
        <el-table-column prop="status" label="状态">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'warning'">
              {{ scope.row.status === 1 ? '发布中' : '已下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="180">
          <template #default="scope">
            <el-button type="text" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button 
              :type="scope.row.status === 1 ? 'warning' : 'success'" 
              size="small"
              @click="handleToggleStatus(scope.row.id, scope.row.status)"
            >
              {{ scope.row.status === 1 ? '下架' : '发布' }}
            </el-button>
            <el-button type="danger" size="small" @click="handleDelete(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    
    <!-- 添加/编辑弹窗 -->
    <el-dialog :title="editForm.id ? '编辑公告' : '发布公告'" :visible.sync="showAddDialog">
      <el-form :model="editForm" label-width="80px">
        <el-form-item label="标题">
          <el-input v-model="editForm.title" placeholder="请输入公告标题" />
        </el-form-item>
        <el-form-item label="内容">
          <el-textarea v-model="editForm.content" rows="5" placeholder="请输入公告内容" />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { adminAPI } from '../../api'
import { ElMessage } from 'element-plus'

const noticeList = ref([])
const showAddDialog = ref(false)
const editForm = reactive({
  id: null,
  title: '',
  content: ''
})

const loadNoticeList = async () => {
  try {
    const res = await adminAPI.getNoticeList()
    noticeList.value = res.data || []
  } catch (error) {
    console.error('加载公告列表失败:', error)
  }
}

const handleEdit = (row) => {
  editForm.id = row.id
  editForm.title = row.title
  editForm.content = row.content
  showAddDialog.value = true
}

const handleSave = async () => {
  try {
    if (editForm.id) {
      await adminAPI.updateNotice(editForm.id, {
        title: editForm.title,
        content: editForm.content
      })
    } else {
      await adminAPI.createNotice({
        title: editForm.title,
        content: editForm.content
      })
    }
    showAddDialog.value = false
    editForm.id = null
    editForm.title = ''
    editForm.content = ''
    loadNoticeList()
  } catch (error) {
    console.error('保存公告失败:', error)
  }
}

const handleToggleStatus = async (id, status) => {
  try {
    const newStatus = status === 1 ? 0 : 1
    await adminAPI.updateNotice(id, { status: newStatus })
    ElMessage.success(newStatus === 1 ? '公告已发布' : '公告已下架')
    loadNoticeList()
  } catch (error) {
    ElMessage.error('更新公告状态失败')
    console.error('更新公告状态失败:', error)
  }
}

const handleDelete = async (id) => {
  if (confirm('确定要删除该公告吗？')) {
    try {
      await adminAPI.deleteNotice(id)
      loadNoticeList()
    } catch (error) {
      console.error('删除公告失败:', error)
    }
  }
}

onMounted(() => {
  loadNoticeList()
})
</script>

<style scoped>
.notice-manage {
  padding: 20px;
}

.toolbar {
  margin-bottom: 20px;
}
</style>
