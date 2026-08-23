<template>
  <div>
    <h2>仪表盘</h2>
    <el-row :gutter="16" class="cards">
      <el-col :span="6" v-for="item in cards" :key="item.label">
        <el-card>
          <div class="card-value">{{ item.value }}</div>
          <div class="card-label">{{ item.label }}</div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getDashboard } from '@/api'

const summary = ref({ familyCount: 0, generationCount: 0, personCount: 0, userCount: 0 })

const cards = computed(() => [
  { label: '家族数量', value: summary.value.familyCount },
  { label: '世代数量', value: summary.value.generationCount },
  { label: '人物数量', value: summary.value.personCount },
  { label: '用户数量', value: summary.value.userCount }
])

onMounted(async () => {
  summary.value = await getDashboard()
})
</script>

<style scoped>
.cards {
  margin-top: 16px;
}
.card-value {
  font-size: 32px;
  font-weight: bold;
}
.card-label {
  color: #999;
  margin-top: 8px;
}
</style>
