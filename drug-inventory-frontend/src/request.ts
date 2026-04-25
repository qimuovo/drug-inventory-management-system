import axios from 'axios'
import {ElMessage} from 'element-plus'
import { useUserStore } from '@/stores/user'

const isDev = import.meta.env.DEV

// 创建 Axios 实例
const myAxios = axios.create({
    baseURL: isDev ? 'http://localhost:8080' : '/',
    timeout: 60000,
    withCredentials: true,
})

// 全局请求拦截器
myAxios.interceptors.request.use(
    function (config) {
        const userStore = useUserStore()
        if (userStore.token) {
            config.headers = config.headers ?? {}
            config.headers.Authorization = `Bearer ${userStore.token}`
        }
        return config
    },
    function (error) {
        // Do something with request error
        return Promise.reject(error)
    },
)

// 全局响应拦截器
myAxios.interceptors.response.use(
    function (response) {
        const {data} = response
        // 未登录
        if (data.code === 40100) {
            if (
                !response.request.responseURL.includes('user/get/login') &&
                !window.location.pathname.includes('/login')
            ) {
                ElMessage.warning('请先登录')
                window.location.href = `/login?redirect=${encodeURIComponent(window.location.href)}`
            }
        }
        return response
    },
    function (error) {
        // Any status codes that falls outside the range of 2xx cause this function to trigger
        // Do something with response error
        return Promise.reject(error)
    },
)

export default myAxios
