import axios from 'axios'

const s = axios.create({ baseURL: '/bigdata-api/api/offline', timeout: 30000 })
s.interceptors.response.use(r => r.data, e => { console.error('[Offline API]', e); return Promise.reject(e) })

export const fetchKpi = () => s.get('/kpi')
export const fetchUserTrend = () => s.get('/users/trend')
export const fetchRetention = () => s.get('/users/retention')
export const fetchTaskOverview = () => s.get('/tasks/overview')
export const fetchTaskTypes = () => s.get('/tasks/type_distribution')
export const fetchTaskTrend = () => s.get('/tasks/trend')
export const fetchModelPerf = () => s.get('/tasks/model_performance')
export const fetchRevenue = () => s.get('/revenue/overview')
export const fetchRevenueTrend = () => s.get('/revenue/trend')
export const fetchMaterials = () => s.get('/materials/overview')
