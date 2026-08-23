<template>
  <div>
    <div class="toolbar">
      <h2>家族管理</h2>
      <el-button type="primary" @click="openCreate">新增家族</el-button>
    </div>

    <el-table :data="families" border v-loading="loading">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="name" label="家族名称" />
      <el-table-column prop="ancestorName" label="始祖" />
      <el-table-column prop="ownerName" label="创建人" />
      <el-table-column label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '启用' : '停用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="160">
        <template #default="{ row }">
          <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
          <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="editing ? '编辑家族' : '新增家族'" width="480px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="家族名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="始祖名称"><el-input v-model="form.ancestorName" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="form.description" type="textarea" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listFamilies, createFamily, updateFamily, deleteFamily } from '@/api'

const families = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const editing = ref(false)
const form = ref({})

async function load() {
  loading.value = true
  try {
    families.value = await listFamilies()
  } finally {
    loading.value = false
  }
}

function openCreate() {
  editing.value = false
  form.value = { name: '', ancestorName: '', description: '', status: 1 }
  dialogVisible.value = true
}

function openEdit(row) {
  editing.value = true
  form.value = { ...row }
  dialogVisible.value = true
}

async function handleSave() {
  if (editing.value) {
    await updateFamily(form.value.id, form.value)
    ElMessage.success('已更新')
  } else {
    await createFamily(form.value)
    ElMessage.success('已创建')
  }
  dialogVisible.value = false
  load()
}

async function handleDelete(row) {
  await ElMessageBox.confirm(`确认删除家族「${row.name}」？`, '提示', { type: 'warning' })
  await deleteFamily(row.id)
  ElMessage.success('已删除')
  load()
}

onMounted(load)
</script>

<style scoped>
.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}
</style>
