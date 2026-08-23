import request from '@/utils/request'

// 认证
export const login = (data) => request.post('/auth/login', data)
export const register = (data) => request.post('/auth/register', data)

// 仪表盘
export const getDashboard = () => request.get('/dashboard')

// 家族
export const listFamilies = () => request.get('/families')
export const createFamily = (data) => request.post('/families', data)
export const updateFamily = (id, data) => request.put(`/families/${id}`, data)
export const deleteFamily = (id) => request.delete(`/families/${id}`)

// 人物
export const listPersons = (params) => request.get('/persons', { params })
export const listFamilyPersons = (familyId) => request.get('/persons/family', { params: { familyId } })
export const getPerson = (id) => request.get(`/persons/${id}`)
export const createPerson = (data) => request.post('/persons', data)
export const updatePerson = (id, data) => request.put(`/persons/${id}`, data)
export const deletePerson = (id) => request.delete(`/persons/${id}`)
export const parentOptions = (familyId, excludeId) =>
  request.get('/persons/parent-options', { params: { familyId, excludeId } })

// 世代
export const listGenerations = (familyId) => request.get('/generations', { params: { familyId } })
export const createGeneration = (data) => request.post('/generations', data)
export const deleteGeneration = (id) => request.delete(`/generations/${id}`)

// 婚姻
export const listMarriages = (familyId) => request.get('/marriages', { params: { familyId } })
export const createMarriage = (data) => request.post('/marriages', data)
export const deleteMarriage = (id) => request.delete(`/marriages/${id}`)

// 事件
export const listEvents = (familyId) => request.get('/events', { params: { familyId } })

// 家族树
export const getTree = (familyId) => request.get('/tree', { params: { familyId } })
export const getTreeRoots = (familyId) => request.get('/tree/roots', { params: { familyId } })
export const getTreeChildren = (personId) => request.get('/tree/children', { params: { personId } })

// 统计
export const getStats = (familyId) => request.get('/stats', { params: { familyId } })

// 关系查询
export const getRelationship = (aId, bId, familyId) =>
  request.get('/relationship', { params: { aId, bId, familyId } })
