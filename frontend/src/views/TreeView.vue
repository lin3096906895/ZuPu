<template>
  <div class="tree-page">
    <div class="toolbar">
      <el-select v-model="familyId" placeholder="选择家族" style="width: 180px" @change="loadData">
        <el-option v-for="f in families" :key="f.id" :label="f.name" :value="f.id" />
      </el-select>
      <el-radio-group v-model="viewMode" @change="loadData">
        <el-radio-button value="descendant">后代图</el-radio-button>
        <el-radio-button value="ancestor">祖先图</el-radio-button>
        <el-radio-button value="generation">吊线图</el-radio-button>
        <el-radio-button value="sushi">苏式图</el-radio-button>
      </el-radio-group>
      <el-select v-if="viewMode === 'generation' || viewMode === 'sushi'" v-model="genSegment" style="width: 150px" @change="render">
        <el-option v-for="(s, i) in genSegments" :key="i" :label="s.label" :value="i" />
      </el-select>
      <el-select v-if="viewMode === 'ancestor'" v-model="focusId" placeholder="选择人物" filterable style="width: 180px" @change="render">
        <el-option v-for="p in bloodline" :key="p.id" :label="p.name" :value="p.id" />
      </el-select>
      <el-button @click="fitView">重置视图</el-button>
      <span v-if="viewMode === 'descendant'" class="hint">点「+」按需加载支系</span>
    </div>
    <div ref="container" class="graph-container"></div>

    <el-drawer v-model="detailVisible" :title="currentPerson?.name || '人物详情'" size="380px">
      <div v-if="currentPerson" class="person-detail">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="姓名">{{ currentPerson.name }}</el-descriptions-item>
          <el-descriptions-item label="性别">{{ currentPerson.gender }}</el-descriptions-item>
          <el-descriptions-item label="世代">第 {{ currentPerson.generationNo }} 世</el-descriptions-item>
          <el-descriptions-item label="出生日期">{{ currentPerson.birthDate || '未知' }}</el-descriptions-item>
          <el-descriptions-item label="逝世日期">{{ currentPerson.deathDate || '—' }}</el-descriptions-item>
          <el-descriptions-item label="出生地">{{ currentPerson.birthPlace || '未知' }}</el-descriptions-item>
          <el-descriptions-item label="父亲">{{ currentPerson.fatherName || '未知' }}</el-descriptions-item>
          <el-descriptions-item label="母亲">{{ currentPerson.motherName || '未知' }}</el-descriptions-item>
          <el-descriptions-item label="配偶">{{ (currentPerson.spouseNames || []).join('、') || '无' }}</el-descriptions-item>
          <el-descriptions-item label="子女">{{ currentChildren.map((c) => c.name).join('、') || '无' }}</el-descriptions-item>
          <el-descriptions-item label="简介">{{ currentPerson.biography || '无' }}</el-descriptions-item>
        </el-descriptions>
        <div class="detail-actions">
          <el-button type="primary" @click="viewDetail">查看完整详情</el-button>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import G6 from '@antv/g6'
import { listFamilies, getTree, getTreeRoots, getTreeChildren, getPerson } from '@/api'

const families = ref([])
const persons = ref([])
const edges = ref([])
const roots = ref([])
const loadedChildren = ref(new Map())
const familyId = ref(null)
const viewMode = ref('descendant')
const focusId = ref(null)
const genSegment = ref(0)
const container = ref(null)

let graph = null
const router = useRouter()
const detailVisible = ref(false)
const currentPerson = ref(null)
const currentChildren = ref([])

// 血缘人物：排除「嫁入的妻子」（wife_id），她们只以「配 X」显示在丈夫卡片上，不作为独立节点
const bloodline = computed(() => {
  const wifeIds = new Set(edges.value.filter((e) => e.type === 'MARRIAGE').map((e) => e.target))
  return persons.value.filter((p) => !wifeIds.has(p.id))
})

// 五世一图分段：第 1-5 世、第 5-9 世、第 9-13 世 …（第 5/9 世重复衔接）
const genSegments = computed(() => {
  const gens = bloodline.value.map((p) => p.generationNo).filter((g) => g != null)
  if (!gens.length) return []
  const max = Math.max(...gens)
  const segs = []
  for (let start = 1; start <= max; start += 4) {
    const end = Math.min(start + 4, max)
    segs.push({ start, end, label: `第 ${start} - ${end} 世` })
  }
  return segs
})

const filteredBloodline = computed(() => {
  const seg = genSegments.value[genSegment.value]
  if (!seg) return bloodline.value
  return bloodline.value.filter((p) => p.generationNo >= seg.start && p.generationNo <= seg.end)
})

function registerNode() {
  if (window.__zupuNodeRegistered) return
  window.__zupuNodeRegistered = true
  G6.registerNode(
    'family-node',
    {
      draw(cfg, group) {
        const isFemale = cfg.gender === '女'
        const w = 150
        const h = 60
        const key = group.addShape('rect', {
          attrs: {
            x: -w / 2,
            y: -h / 2,
            width: w,
            height: h,
            radius: 6,
            fill: isFemale ? '#fff0f6' : '#f0f7ff',
            stroke: isFemale ? '#ffadd2' : '#91caff',
            lineWidth: 2,
            cursor: 'pointer'
          },
          name: 'node-key'
        })
        group.addShape('text', {
          attrs: {
            text: cfg.name,
            x: 0,
            y: -10,
            fontSize: 15,
            fontWeight: 'bold',
            fill: '#333',
            textAlign: 'center',
            textBaseline: 'middle'
          },
          name: 'node-name'
        })
        const subParts = []
        if (cfg.birthDate || cfg.deathDate) {
          subParts.push([cfg.birthDate, cfg.deathDate].filter(Boolean).join(' ~ '))
        }
        if (cfg.spouseNames && cfg.spouseNames.length) {
          subParts.push('配 ' + cfg.spouseNames.join('、'))
        }
        group.addShape('text', {
          attrs: {
            text: subParts.join('  ·  '),
            x: 0,
            y: 12,
            fontSize: 11,
            fill: '#666',
            textAlign: 'center',
            textBaseline: 'middle'
          },
          name: 'node-sub'
        })
        if (cfg.hasChildren) {
          const bx = w / 2 - 10
          const by = 0
          group.addShape('circle', {
            attrs: { x: bx, y: by, r: 9, fill: cfg.expanded ? '#fff' : '#409eff', stroke: '#409eff', lineWidth: 1.5, cursor: 'pointer' },
            name: 'expand-btn'
          })
          group.addShape('text', {
            attrs: { text: cfg.expanded ? '−' : '+', x: bx, y: by, fontSize: 15, fill: cfg.expanded ? '#409eff' : '#fff', textAlign: 'center', textBaseline: 'middle' },
            name: 'expand-btn'
          })
        }
        return key
      },
      getAnchorPoints() {
        return [
          [0.5, 0],
          [0.5, 1],
          [0, 0.5],
          [1, 0.5]
        ]
      }
    },
    'single-node'
  )
}

async function loadFamilies() {
  families.value = await listFamilies()
  if (families.value.length && !familyId.value) {
    familyId.value = families.value[0].id
  }
}

async function loadData() {
  if (!familyId.value) return
  focusId.value = null
  genSegment.value = 0
  if (viewMode.value === 'descendant') {
    roots.value = await getTreeRoots(familyId.value)
    loadedChildren.value = new Map()
  } else {
    const data = await getTree(familyId.value)
    persons.value = data.nodes
    edges.value = data.edges
  }
  render()
}

function buildDescendantData() {
  const build = (node) => {
    const children = loadedChildren.value.get(node.id)
    const expanded = children !== undefined
    return {
      id: String(node.id),
      name: node.name,
      gender: node.gender,
      generationNo: node.generationNo,
      birthDate: node.birthDate,
      deathDate: node.deathDate,
      spouseNames: node.spouseNames,
      hasChildren: node.hasChildren,
      expanded,
      children: expanded ? children.map(build) : []
    }
  }
  const tree = roots.value.map(build)
  if (tree.length === 1) return tree[0]
  if (tree.length === 0) return null
  return { id: 'virtual-root', name: '家族', hasChildren: false, children: tree }
}

function buildAncestorData() {
  if (!focusId.value) return null
  const map = new Map(persons.value.map((p) => [p.id, p]))
  const visited = new Set()
  function walk(id) {
    const node = map.get(id)
    if (!node || visited.has(id)) return null
    visited.add(id)
    const parents = []
    if (node.fatherId && map.has(node.fatherId)) parents.push(walk(node.fatherId))
    if (node.motherId && map.has(node.motherId)) parents.push(walk(node.motherId))
    return { ...node, id: String(node.id), hasChildren: false, children: parents.filter(Boolean) }
  }
  return walk(focusId.value)
}

function buildGenerationData() {
  const nodes = filteredBloodline.value.map((p) => ({
    id: String(p.id),
    name: p.name,
    gender: p.gender,
    birthDate: p.birthDate,
    deathDate: p.deathDate,
    spouseNames: p.spouseNames,
    generationNo: p.generationNo,
    size: [150, 60]
  }))
  const map = new Map(filteredBloodline.value.map((p) => [p.id, p]))
  const edgesData = []
  filteredBloodline.value.forEach((p) => {
    if (p.fatherId && map.has(p.fatherId)) {
      edgesData.push({ source: String(p.fatherId), target: String(p.id) })
    }
  })
  return { nodes, edges: edgesData }
}

function buildSushiData() {
  const map = new Map(filteredBloodline.value.map((p) => [p.id, p]))
  const roots = filteredBloodline.value.filter((p) => p.fatherId == null || !map.has(p.fatherId))
  const build = (id) => {
    const node = map.get(id)
    if (!node) return null
    const children = filteredBloodline.value
      .filter((p) => p.fatherId === id)
      .map((p) => build(p.id))
      .filter(Boolean)
    return { ...node, id: String(node.id), hasChildren: false, children }
  }
  const trees = roots.map((r) => build(r.id)).filter(Boolean)
  if (trees.length === 1) return trees[0]
  if (trees.length === 0) return null
  return { id: 'virtual-root', name: '家族', hasChildren: false, children: trees }
}

function render() {
  if (!familyId.value || !container.value) return
  if (graph) {
    graph.destroy()
    graph = null
  }
  const width = container.value.clientWidth || 1000
  const height = container.value.clientHeight || 600

  try {
    if (viewMode.value === 'generation') {
      renderGeneration(width, height)
    } else if (viewMode.value === 'sushi') {
      renderSushi(width, height)
    } else {
      renderTree(width, height)
    }
  } catch (e) {
    console.error('家族树渲染失败:', e)
  }
}

function renderTree(width, height) {
  const data = viewMode.value === 'ancestor' ? buildAncestorData() : buildDescendantData()
  if (!data) return
  graph = new G6.TreeGraph({
    container: container.value,
    width,
    height,
    modes: { default: ['drag-canvas', 'zoom-canvas'] },
    defaultNode: { type: 'family-node' },
    defaultEdge: { type: 'cubic-horizontal', style: { stroke: '#b3b3b3', lineWidth: 1.5 } },
    layout: {
      type: 'compactBox',
      direction: 'LR',
      getId: (d) => String(d.id),
      getHeight: () => 70,
      getWidth: () => 170,
      getVGap: () => 24,
      getHGap: () => 60
    },
    fitView: true,
    fitViewPadding: [30, 50, 30, 50],
    minZoom: 0.05,
    maxZoom: 2
  })
  graph.data(data)
  graph.render()
  graph.on('node:click', onNodeClick)
}

function renderGeneration(width, height) {
  const { nodes, edges } = buildGenerationData()
  graph = new G6.Graph({
    container: container.value,
    width,
    height,
    modes: { default: ['drag-canvas', 'zoom-canvas'] },
    defaultNode: { type: 'family-node', size: [150, 60] },
    defaultEdge: { type: 'polyline', style: { stroke: '#b3b3b3', lineWidth: 1.5, radius: 8 } },
    layout: {
      type: 'dagre',
      rankdir: 'TB',
      nodesep: 20,
      ranksep: 70
    },
    fitView: true,
    fitViewPadding: 40,
    minZoom: 0.05,
    maxZoom: 2
  })
  graph.data({ nodes, edges })
  graph.render()
  graph.on('node:click', onNodeClick)
}

function renderSushi(width, height) {
  const data = buildSushiData()
  if (!data) return
  graph = new G6.TreeGraph({
    container: container.value,
    width,
    height,
    modes: { default: ['drag-canvas', 'zoom-canvas'] },
    defaultNode: { type: 'family-node' },
    defaultEdge: { type: 'polyline', style: { stroke: '#b3b3b3', lineWidth: 1.5, radius: 0 } },
    layout: {
      type: 'compactBox',
      direction: 'TB',
      getId: (d) => String(d.id),
      getHeight: () => 70,
      getWidth: () => 170,
      getVGap: () => 20,
      getHGap: () => 40
    },
    fitView: true,
    fitViewPadding: [30, 50, 30, 50],
    minZoom: 0.05,
    maxZoom: 2
  })
  graph.data(data)
  graph.render()
  graph.on('node:click', onNodeClick)
}

function onNodeClick(evt) {
  const model = evt.item && evt.item.getModel ? evt.item.getModel() : null
  const id = model && model.id
  if (!id || id === 'virtual-root') return
  const targetName = evt.target && evt.target.get ? evt.target.get('name') : null
  if (targetName === 'expand-btn' && viewMode.value === 'descendant') {
    toggleExpand(Number(id))
    return
  }
  showDetail(Number(id))
}

async function toggleExpand(id) {
  if (loadedChildren.value.has(id)) {
    loadedChildren.value.delete(id)
    render()
  } else {
    const children = await getTreeChildren(id)
    loadedChildren.value.set(id, children)
    render()
  }
}

async function showDetail(id) {
  const person = await getPerson(id)
  currentPerson.value = person
  currentChildren.value = persons.value.filter((p) => p.fatherId === id || p.motherId === id)
  detailVisible.value = true
}

function viewDetail() {
  const id = currentPerson.value?.id
  detailVisible.value = false
  if (id) {
    router.push(`/persons/${id}`)
  }
}

function fitView() {
  graph?.fitView()
}

onMounted(async () => {
  registerNode()
  await loadFamilies()
  await nextTick()
  loadData()
})

onBeforeUnmount(() => {
  graph?.destroy()
  graph = null
})
</script>

<style scoped>
.tree-page {
  height: 100%;
  display: flex;
  flex-direction: column;
}
.toolbar {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 12px;
  flex-wrap: wrap;
}
.hint {
  color: #999;
  font-size: 12px;
}
.graph-container {
  flex: 1;
  min-height: 500px;
  border: 1px solid #eee;
  border-radius: 4px;
  background: #fff;
}
.person-detail {
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.detail-actions {
  display: flex;
  justify-content: flex-end;
}
</style>
