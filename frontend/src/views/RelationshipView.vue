<template>
  <div>
    <div class="toolbar">
      <el-select v-model="familyId" placeholder="选择家族" style="width: 160px" @change="onFamilyChange">
        <el-option v-for="f in families" :key="f.id" :label="f.name" :value="f.id" />
      </el-select>
      <el-select v-model="aId" filterable placeholder="选择人物 A" style="width: 180px">
        <el-option v-for="p in persons" :key="p.id" :label="p.name" :value="p.id" />
      </el-select>
      <el-select v-model="bId" filterable placeholder="选择人物 B" style="width: 180px">
        <el-option v-for="p in persons" :key="p.id" :label="p.name" :value="p.id" />
      </el-select>
      <el-button type="primary" :disabled="!aId || !bId" @click="query">查询关系</el-button>
    </div>

    <el-card v-if="result" class="result">
      <div class="term">{{ result.term }}</div>
      <div class="desc">{{ result.description }}</div>
      <div class="path">
        <template v-for="(n, i) in result.path" :key="i">
          <el-tag :type="i === 0 ? 'primary' : i === result.path.length - 1 ? 'success' : 'info'">{{ n }}</el-tag>
          <span v-if="i < result.path.length - 1" class="arrow">→</span>
        </template>
      </div>
    </el-card>

    <el-empty v-else description="选择两个人，查看他们的亲缘关系" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { listFamilies, listFamilyPersons, getRelationship } from '@/api'

const families = ref([])
const persons = ref([])
const familyId = ref(null)
const aId = ref(null)
const bId = ref(null)
const result = ref(null)

async function loadFamilies() {
  families.value = await listFamilies()
  if (families.value.length) {
    familyId.value = families.value[0].id
    await loadPersons()
  }
}

async function loadPersons() {
  if (!familyId.value) return
  persons.value = await listFamilyPersons(familyId.value)
}

async function onFamilyChange() {
  aId.value = null
  bId.value = null
  result.value = null
  await loadPersons()
}

async function query() {
  if (!aId.value || !bId.value) return
  if (aId.value === bId.value) {
    result.value = { term: '本人', description: '同一个人', path: [persons.value.find((p) => p.id === aId.value)?.name || ''] }
    return
  }
  result.value = await getRelationship(aId.value, bId.value, familyId.value)
}

onMounted(loadFamilies)
</script>

<style scoped>
.toolbar {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
  flex-wrap: wrap;
}
.result {
  text-align: center;
}
.term {
  font-size: 40px;
  font-weight: bold;
  color: #409eff;
  margin-bottom: 8px;
}
.desc {
  color: #666;
  margin-bottom: 20px;
}
.path {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-wrap: wrap;
  gap: 4px;
}
.arrow {
  color: #999;
  margin: 0 4px;
}
</style>
