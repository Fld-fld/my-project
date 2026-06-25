<template>
  <div class="notice-manage">
    <div class="page-header">
      <h1>公告管理</h1>
      <el-button type="primary" @click="showAddDialog" class="add-btn">
        <el-icon><Plus /></el-icon>
        发布公告
      </el-button>
    </div>
    
    <!-- 搜索筛选栏 -->
    <div class="filter-bar">
      <el-input
        v-model="searchKeyword"
        placeholder="搜索公告标题"
        clearable
        @clear="handleSearch"
        class="search-input"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>
      
      <el-date-picker
        v-model="filterDate"
        type="date"
        placeholder="筛选日期"
        clearable
        class="date-filter"
        value-format="yyyy-MM-dd"
      />
      
      <el-button type="primary" @click="handleSearch" class="search-btn">
        <el-icon><Search /></el-icon>
        搜索
      </el-button>
    </div>
    
    <!-- 表格 -->
    <div class="table-container" v-loading="loading">
      <el-table
        :data="filteredList"
        stripe
        style="width: 100%"
        :header-cell-style="{ background: '#f8f9fa', color: '#333', fontWeight: '600' }"
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="标题" min-width="200" />
        <el-table-column prop="content" label="内容" min-width="300" show-overflow-tooltip />
        <el-table-column prop="priority" label="优先级" width="100">
          <template #default="{ row }">
            <span :class="['priority-tag', row.priority]">
              {{ getPriorityText(row.priority) }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="发布时间" width="160">
          <template #default="{ row }">
            {{ formatTime(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button 
                type="primary" 
                size="small" 
                link
                @click="showEditDialog(row)"
              >
                编辑
              </el-button>
              <el-button 
                type="danger" 
                size="small" 
                link
                @click="handleDelete(row)"
              >
                删除
              </el-button>
            </div>
          </template>
        </el-table-column>
        
        <template #empty>
          <div class="empty-state">
            <div class="empty-illustration">📢</div>
            <div class="empty-text">暂无公告</div>
            <el-button type="primary" @click="showAddDialog" class="empty-btn">
              发布公告
            </el-button>
          </div>
        </template>
      </el-table>
    </div>
    
    <!-- 添加/编辑弹窗 -->
    <el-dialog 
      :title="dialogTitle" 
      v-model="dialogVisible" 
      width="600px"
      class="notice-dialog"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入公告标题" />
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input 
            v-model="form.content" 
            type="textarea" 
            :rows="5" 
            placeholder="请输入公告内容"
          />
        </el-form-item>
        <el-form-item label="优先级" prop="priority">
          <el-radio-group v-model="form.priority">
            <el-radio label="LOW">低</el-radio>
            <el-radio label="MEDIUM">中</el-radio>
            <el-radio label="HIGH">高</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { noticeAPI } from '@/api'
import { Search, Plus } from '@element-plus/icons-vue'

const loading = ref(false)
const list = ref([])
const searchKeyword = ref('')
const filterDate = ref('')

const dialogVisible = ref(false)
const dialogTitle = ref('发布公告')
const submitting = ref(false)
const isEdit = ref(false)
const editingId = ref(null)

const form = reactive({
  title: '',
  content: '',
  priority: 'MEDIUM'
})

const formRef = ref(null)

const rules = {
  title: [
    { required: true, message: '请输入公告标题', trigger: 'blur' }
  ],
  content: [
    { required: true, message: '请输入公告内容', trigger: 'blur' }
  ]
}

const filteredList = computed(() => {
  let result = list.value
  
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    result = result.filter(item => 
      item.title?.toLowerCase().includes(keyword)
    )
  }
  
  if (filterDate.value) {
    result = result.filter(item => {
      const date = new Date(item.createdAt).toISOString().split('T')[0]
      return date === filterDate.value
    })
  }
  
  return result
})

const getPriorityText = (priority) => {
  const map = {
    'LOW': '低',
    'MEDIUM': '中',
    'HIGH': '高'
  }
  return map[priority] || priority
}

const formatTime = (time) => {
  if (!time) return '-'
  const date = new Date(time)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

const loadList = async () => {
  loading.value = true
  try {
    const res = await noticeAPI.getList()
    list.value = res.data || []
  } catch (error) {
    console.error('加载公告列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {}

const showAddDialog = () => {
  isEdit.value = false
  dialogTitle.value = '发布公告'
  form.title = ''
  form.content = ''
  form.priority = 'MEDIUM'
  dialogVisible.value = true
}

const showEditDialog = (row) => {
  isEdit.value = true
  editingId.value = row.id
  dialogTitle.value = '编辑公告'
  form.title = row.title
  form.content = row.content
  form.priority = row.priority
  dialogVisible.value = true
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    submitting.value = true
    
    if (isEdit.value) {
      await noticeAPI.update(editingId.value, form)
      ElMessage.success('更新成功')
    } else {
      await noticeAPI.add(form)
      ElMessage.success('发布成功')
    }
    
    dialogVisible.value = false
    loadList()
  } catch (error) {
    console.error('操作失败:', error)
  } finally {
    submitting.value = false
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确认删除该公告？', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await noticeAPI.delete(row.id)
    ElMessage.success('删除成功')
    loadList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
    }
  }
}

onMounted(() => {
  loadList()
})
</script>

<style scoped>
.notice-manage {
  max-width: 1400px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h1 {
  font-size: 24px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.add-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%) !important;
  border: none !important;
  border-radius: 8px;
  display: flex;
  align-items: center;
  gap: 6px;
}

.filter-bar {
  background: white;
  border-radius: 12px;
  padding: 16px 20px;
  margin-bottom: 16px;
  display: flex;
  gap: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.search-input {
  width: 280px;
}

.date-filter {
  width: 160px;
}

.search-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%) !important;
  border: none !important;
  border-radius: 8px;
  display: flex;
  align-items: center;
  gap: 6px;
}

.table-container {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

:deep(.el-table th) {
  padding: 14px 12px;
  font-size: 13px;
}

:deep(.el-table td) {
  padding: 14px 12px;
  font-size: 14px;
}

.priority-tag {
  padding: 4px 10px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
}

.priority-tag.LOW {
  background: #e8f5e9;
  color: #4caf50;
}

.priority-tag.MEDIUM {
  background: #fff3e0;
  color: #ff9800;
}

.priority-tag.HIGH {
  background: #ffebee;
  color: #f44336;
}

.action-buttons {
  display: flex;
  gap: 8px;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
}

.empty-illustration {
  font-size: 64px;
  margin-bottom: 16px;
}

.empty-text {
  font-size: 16px;
  color: #666;
  margin-bottom: 20px;
}

.empty-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%) !important;
  border: none !important;
  border-radius: 20px;
}

.notice-dialog :deep(.el-dialog) {
  border-radius: 12px;
}
</style>
