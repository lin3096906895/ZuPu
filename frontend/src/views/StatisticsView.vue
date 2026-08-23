<template>
  <div>
    <div class="toolbar">
      <el-select v-model="familyId" placeholder="选择家族" style="width: 180px" @change="load">
        <el-option v-for="f in families" :key="f.id" :label="f.name" :value="f.id" />
      </el-select>
      <span class="total" v-if="stats">共 {{ stats.total }} 人</span>
    </div>
    <el-row :gutter="16">
      <el-col :span="12"><div ref="barRef" class="chart"></div></el-col>
      <el-col :span="12"><div ref="pieRef" class="chart"></div></el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import * as echarts from 'echarts'
import { listFamilies, getStats } from '@/api'

const families = ref([])
const familyId = ref(null)
const stats = ref(null)
const barRef = ref(null)
const pieRef = ref(null)
let barChart = null
let pieChart = null

async function load() {
  if (!familyId.value) return
  stats.value = await getStats(familyId.value)
  renderBar()
  renderPie()
}

function renderBar() {
  if (!barChart) barChart = echarts.init(barRef.value)
  barChart.setOption({
    title: { text: '世代人口分布', left: 'center' },
    tooltip: {},
    grid: { left: 40, right: 20, top: 50, bottom: 30 },
    xAxis: { type: 'category', data: stats.value.generations.map((g) => '第' + g.generationNo + '世') },
    yAxis: { type: 'value', minInterval: 1 },
    series: [{ type: 'bar', data: stats.value.generations.map((g) => g.count), itemStyle: { color: '#409eff' }, barWidth: 40 }]
  })
}

function renderPie() {
  if (!pieChart) pieChart = echarts.init(pieRef.value)
  pieChart.setOption({
    title: { text: '性别比例', left: 'center' },
    tooltip: { trigger: 'item' },
    legend: { bottom: 0 },
    series: [
      {
        type: 'pie',
        radius: ['40%', '65%'],
        data: [
          { name: '男', value: stats.value.male, itemStyle: { color: '#409eff' } },
          { name: '女', value: stats.value.female, itemStyle: { color: '#ff7875' } }
        ],
        label: { formatter: '{b}: {c} 人 ({d}%)' }
      }
    ]
  })
}

function resize() {
  barChart?.resize()
  pieChart?.resize()
}

onMounted(async () => {
  families.value = await listFamilies()
  if (families.value.length) {
    familyId.value = families.value[0].id
  }
  load()
  window.addEventListener('resize', resize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', resize)
  barChart?.dispose()
  pieChart?.dispose()
})
</script>

<style scoped>
.toolbar {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 16px;
}
.total {
  color: #666;
}
.chart {
  height: 400px;
}
</style>
