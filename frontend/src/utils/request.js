import axios from 'axios'

const service = axios.create({
    baseURL: import.meta.env.VITE_APP_BASE_API,
    timeout: 50000
})

// Request Interceptor
service.interceptors.request.use(
    config => {
        // Inject Token (Satoken - Authorization)
        const token = localStorage.getItem('token')
        if (token) {
            config.headers['Authorization'] = token
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
                // Handle Not Login
                localStorage.removeItem('token')
                window.location.href = '/login'
            }
            return Promise.reject(new Error(res.message || 'Error'))
        } else {
            return res
        }
    },
    error => {
        return Promise.reject(error)
    }
)

export default service
