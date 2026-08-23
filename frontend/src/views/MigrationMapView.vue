<template>
  <div>
    <div class="toolbar">
      <el-select v-model="familyId" placeholder="选择家族" style="width: 180px" @change="load">
        <el-option v-for="f in families" :key="f.id" :label="f.name" :value="f.id" />
      </el-select>
      <span class="hint">地图支持滚轮缩放，任意出生地自动定位</span>
    </div>
    <el-row :gutter="16">
      <el-col :span="16">
        <div ref="mapRef" class="map"></div>
      </el-col>
      <el-col :span="8">
        <el-card class="timeline-card">
          <h3>迁徙脉络</h3>
          <el-timeline>
            <el-timeline-item v-for="g in generationGroups" :key="g.gen" :timestamp="'第 ' + g.gen + ' 世'" placement="top">
              <div v-for="(item, i) in g.items" :key="i" class="person-line">
                {{ item.name }}<span class="place">{{ item.place || '未知' }}</span>
              </div>
            </el-timeline-item>
          </el-timeline>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { listFamilies, listFamilyPersons } from '@/api'

const families = ref([])
const familyId = ref(null)
const generationGroups = ref([])
const mapRef = ref(null)
let map = null
let geocoder = null
let markers = []

async function load() {
  if (!familyId.value) return
  const persons = await listFamilyPersons(familyId.value)
  const withPlace = persons.filter((p) => p.birthPlace)

  const grouped = new Map()
  withPlace.forEach((p) => {
    const g = p.generationNo || 1
    if (!grouped.has(g)) grouped.set(g, [])
    grouped.get(g).push({ name: p.name, place: p.birthPlace })
  })
  generationGroups.value = [...grouped.entries()].sort((a, b) => a[0] - b[0]).map(([gen, items]) => ({ gen, items }))

  renderMap(withPlace)
}

function renderMap(persons) {
  if (!window.BMap) {
    ElMessage.error('百度地图加载失败，请检查网络或 AK 是否有效')
    return
  }
  if (!map) {
    map = new window.BMap.Map(mapRef.value)
    map.centerAndZoom(new window.BMap.Point(104.07, 35.86), 5)
    map.enableScrollWheelZoom(true)
    geocoder = new window.BMap.Geocoder()
  }
  markers.forEach((m) => map.removeOverlay(m))
  markers = []

  const byPlace = new Map()
  persons.forEach((p) => {
    if (!byPlace.has(p.birthPlace)) byPlace.set(p.birthPlace, [])
    byPlace.get(p.birthPlace).push(p.name)
  })

  byPlace.forEach((names, place) => {
    geocoder.getPoint(place, (point) => {
      if (!point) return
      const marker = new window.BMap.Marker(point)
      map.addOverlay(marker)
      markers.push(marker)
      const info = new window.BMap.InfoWindow(`<b>${place}</b><br/>${names.join('、')}`, { width: 220 })
      marker.addEventListener('click', () => {
        marker.openInfoWindow(info)
      })
    })
  })
}

onMounted(async () => {
  families.value = await listFamilies()
  if (families.value.length) {
    familyId.value = families.value[0].id
    await nextTick()
    load()
  }
})

onBeforeUnmount(() => {
  markers = []
  map = null
  geocoder = null
})
</script>

<style scoped>
.toolbar {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}
.hint {
  color: #999;
  font-size: 13px;
}
.map {
  height: 520px;
  border-radius: 4px;
}
.timeline-card {
  max-height: 520px;
  overflow-y: auto;
}
.person-line {
  line-height: 1.8;
}
.place {
  color: #409eff;
  margin-left: 8px;
}
</style>
