<template>
  <div>
    <div class="toolbar">
      <div class="left">
        <el-select v-model="familyId" placeholder="选择家族" style="width: 200px" @change="reload">
          <el-option v-for="f in families" :key="f.id" :label="f.name" :value="f.id" />
        </el-select>
        <el-input v-model="keyword" placeholder="搜索姓名" style="width: 200px" clearable @keyup.enter="reload" @clear="reload" />
        <el-button type="primary" @click="reload">查询</el-button>
      </div>
      <el-button type="primary" @click="openCreate">新增人物</el-button>
    </div>

    <el-table :data="persons" border v-loading="loading">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column label="姓名">
        <template #default="{ row }">
          <el-link type="primary" @click="router.push(`/persons/${row.id}`)">{{ row.name }}</el-link>
        </template>
      </el-table-column>
      <el-table-column prop="gender" label="性别" width="70" />
      <el-table-column prop="generationNo" label="世代" width="70" />
      <el-table-column prop="fatherName" label="父亲" />
      <el-table-column prop="birthDate" label="出生日期" />
      <el-table-column label="操作" width="160">
        <template #default="{ row }">
          <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
          <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-model:current-page="pageNum"
      :page-size="pageSize"
      :total="total"
      :page-sizes="[10, 20, 50]"
      layout="total, sizes, prev, pager, next"
      class="pagination"
      @current-change="load"
      @size-change="handleSizeChange"
    />

    <el-dialog v-model="dialogVisible" :title="editing ? '编辑人物' : '新增人物'" width="560px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="姓名"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="form.gender">
            <el-radio label="男">男</el-radio>
            <el-radio label="女">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="父亲">
          <el-select v-model="form.fatherId" clearable filterable style="width: 100%">
            <el-option v-for="p in parentOpts" :key="p.id" :label="p.label" :value="p.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="出生日期">
          <el-date-picker v-model="form.birthDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        <el-form-item label="出生地"><el-input v-model="form.birthPlace" /></el-form-item>
        <el-form-item label="简介"><el-input v-model="form.biography" type="textarea" /></el-form-item>
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
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listFamilies, listPersons, createPerson, updatePerson, deletePerson, parentOptions } from '@/api'

const families = ref([])
const persons = ref([])
const parentOpts = ref([])
const familyId = ref(null)
const keyword = ref('')
const loading = ref(false)
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const dialogVisible = ref(false)
const editing = ref(false)
const form = ref({})
const router = useRouter()

async function loadFamilies() {
  families.value = await listFamilies()
  if (families.value.length && !familyId.value) {
    familyId.value = families.value[0].id
  }
}

async function load() {
  if (!familyId.value) return
  loading.value = true
  try {
    const res = await listPersons({
      familyId: familyId.value,
      keyword: keyword.value || undefined,
      pageNum: pageNum.value,
      pageSize: pageSize.value
    })
    persons.value = res.list
    total.value = res.total
  } finally {
    loading.value = false
  }
}

function reload() {
  pageNum.value = 1
  load()
}

function handleSizeChange(size) {
  pageSize.value = size
  pageNum.value = 1
  load()
}

async function loadParentOpts() {
  parentOpts.value = await parentOptions(familyId.value, form.value.id || undefined)
}

async function openCreate() {
  editing.value = false
  form.value = { familyId: familyId.value, name: '', gender: '男', fatherId: null, birthDate: null, birthPlace: '', biography: '' }
  await loadParentOpts()
  dialogVisible.value = true
}

async function openEdit(row) {
  editing.value = true
  form.value = { ...row }
  await loadParentOpts()
  dialogVisible.value = true
}

async function handleSave() {
  form.value.familyId = familyId.value
  if (editing.value) {
    await updatePerson(form.value.id, form.value)
    ElMessage.success('已更新')
  } else {
    await createPerson(form.value)
    ElMessage.success('已创建')
  }
  dialogVisible.value = false
  load()
}

async function handleDelete(row) {
  await ElMessageBox.confirm(`确认删除人物「${row.name}」？`, '提示', { type: 'warning' })
  await deletePerson(row.id)
  ElMessage.success('已删除')
  load()
}

onMounted(async () => {
  await loadFamilies()
  load()
})
</script>

<style scoped>
.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}
.left {
  display: flex;
  gap: 12px;
}
.pagination {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>
