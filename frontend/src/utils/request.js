import axios from 'axios'

const service = axios.create({
    baseURL: import.meta.env.VITE_APP_BASE_API,
    timeout: 50000
})

let isRefreshing = false
let refreshSubscribers = []

// 添加请求到等待队列
function addRefreshSubscriber(callback) {
    refreshSubscribers.push(callback)
}

// 执行等待队列中的请求
function onRefreshed(token) {
    refreshSubscribers.forEach(callback => callback(token))
    refreshSubscribers = []
}

// 刷新Token函数
async function refreshToken() {
    try {
        const refreshToken = localStorage.getItem('refreshToken')
        if (!refreshToken) {
            throw new Error('No refresh token')
        }
        
        const response = await axios.post(`${import.meta.env.VITE_APP_BASE_API}/auth/refresh`, {
            refreshToken
        })
        
        const { token: newToken, refreshToken: newRefreshToken } = response.data.data
        localStorage.setItem('token', newToken)
        localStorage.setItem('refreshToken', newRefreshToken)
        localStorage.setItem('tokenExpiry', Date.now() + 2 * 60 * 60 * 1000) // 2小时
        
        return newToken
    } catch (error) {
        // 刷新失败，清除所有token并跳转登录
        localStorage.removeItem('token')
        localStorage.removeItem('refreshToken')
        localStorage.removeItem('tokenExpiry')
        localStorage.removeItem('rememberedLogin')
        window.location.href = '/login'
        throw error
    }
}

// Request Interceptor
service.interceptors.request.use(
    config => {
        const token = localStorage.getItem('token')
        if (token) {
            // 检查token是否即将过期（剩余30分钟内）
            const expiryTime = parseInt(localStorage.getItem('tokenExpiry') || '0')
            const currentTime = Date.now()
            const shouldRefresh = expiryTime - currentTime < 30 * 60 * 1000
            
            if (shouldRefresh && !isRefreshing) {
                isRefreshing = true
                refreshToken().then(newToken => {
                    isRefreshing = false
                    config.headers['Authorization'] = newToken
                    onRefreshed(newToken)
                }).catch(() => {
                    isRefreshing = false
                })
            } else {
                config.headers['Authorization'] = token
            }
        }
        return config
    },
    error => {
        return Promise.reject(error)
    }
)

// Response Interceptor
service.interceptors.response.use(
    response => {
        const res = response.data
        // Custom Code Check
        if (res.code && res.code !== 200) {
            if (res.code === 401) {
                // Token过期，尝试刷新
                const originalRequest = response.config
                
                if (!isRefreshing) {
                    isRefreshing = true
                    return refreshToken().then(newToken => {
                        isRefreshing = false
                        originalRequest.headers['Authorization'] = newToken
                        onRefreshed(newToken)
                        return service(originalRequest)
                    }).catch(error => {
                        isRefreshing = false
                        // 刷新失败，清除所有token并跳转登录
                        localStorage.removeItem('token')
                        localStorage.removeItem('refreshToken')
                        localStorage.removeItem('tokenExpiry')
                        localStorage.removeItem('rememberedLogin')
                        window.location.href = '/login'
                        return Promise.reject(error)
                    })
                } else {
                    // 正在刷新，将请求加入等待队列
                    return new Promise((resolve) => {
                        addRefreshSubscriber((newToken) => {
                            originalRequest.headers['Authorization'] = newToken
                            resolve(service(originalRequest))
                        })
                    })
                }
            }
            return Promise.reject(new Error(res.message || 'Error'))
        } else {
            return res
        }
    },
    error => {
        if (error.response?.status === 401) {
            // Token过期处理
            localStorage.removeItem('token')
            localStorage.removeItem('refreshToken')
            localStorage.removeItem('tokenExpiry')
            window.location.href = '/login'
        }
        return Promise.reject(error)
    }
)

export default service
