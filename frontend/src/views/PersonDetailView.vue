<template>
  <div>
    <el-page-header content="人物详情" @back="router.back()" />
    <el-card v-if="person" class="detail-card">
      <div class="header">
        <h2 class="name">{{ person.name }}</h2>
        <el-tag :type="person.gender === '女' ? 'danger' : 'primary'">{{ person.gender }}</el-tag>
        <el-tag type="info">第 {{ person.generationNo }} 世</el-tag>
      </div>

      <h3 class="section">基本信息</h3>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="出生日期">{{ person.birthDate || '未知' }}</el-descriptions-item>
        <el-descriptions-item label="逝世日期">{{ person.deathDate || '—' }}</el-descriptions-item>
        <el-descriptions-item label="出生地">{{ person.birthPlace || '未知' }}</el-descriptions-item>
        <el-descriptions-item label="性别">{{ person.gender }}</el-descriptions-item>
      </el-descriptions>

      <h3 class="section">家族关系</h3>
      <el-descriptions :column="1" border>
        <el-descriptions-item label="父亲">
          <el-link v-if="person.fatherId" type="primary" @click="go(person.fatherId)">{{ person.fatherName }}</el-link>
          <span v-else>未知</span>
        </el-descriptions-item>
        <el-descriptions-item label="母亲">
          <el-link v-if="person.motherId" type="primary" @click="go(person.motherId)">{{ person.motherName }}</el-link>
          <span v-else>未知</span>
        </el-descriptions-item>
        <el-descriptions-item label="配偶">
          <template v-if="spouses.length">
            <el-link v-for="s in spouses" :key="s.id" type="danger" @click="go(s.id)">{{ s.name }}</el-link>
          </template>
          <span v-else>无</span>
        </el-descriptions-item>
        <el-descriptions-item label="子女">
          <template v-if="children.length">
            <el-link v-for="c in children" :key="c.id" type="primary" @click="go(c.id)">{{ c.name }}</el-link>
          </template>
          <span v-else>无</span>
        </el-descriptions-item>
      </el-descriptions>

      <h3 class="section">生平简介</h3>
      <p class="biography">{{ person.biography || '暂无简介' }}</p>

      <div class="actions">
        <el-button type="primary" @click="router.push('/persons')">去编辑</el-button>
        <el-button @click="router.back()">返回</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getPerson, listFamilyPersons, listMarriages } from '@/api'

const route = useRoute()
const router = useRouter()
const person = ref(null)
const spouses = ref([])
const children = ref([])

async function load() {
  const id = Number(route.params.id)
  if (!id) return
  person.value = await getPerson(id)
  const [allPersons, marriages] = await Promise.all([
    listFamilyPersons(person.value.familyId),
    listMarriages(person.value.familyId)
  ])
  children.value = allPersons.filter((p) => p.fatherId === id || p.motherId === id)
  const sp = []
  marriages.forEach((m) => {
    if (m.husbandId === id) sp.push({ id: m.wifeId, name: m.wifeName })
    else if (m.wifeId === id) sp.push({ id: m.husbandId, name: m.husbandName })
  })
  spouses.value = sp
}

function go(pid) {
  router.push(`/persons/${pid}`)
}

watch(() => route.params.id, load, { immediate: true })
</script>

<style scoped>
.detail-card {
  margin-top: 16px;
}
.header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}
.name {
  margin: 0;
}
.section {
  margin: 20px 0 12px;
}
.biography {
  line-height: 1.8;
  color: #444;
}
.actions {
  margin-top: 24px;
  display: flex;
  gap: 12px;
}
</style>
