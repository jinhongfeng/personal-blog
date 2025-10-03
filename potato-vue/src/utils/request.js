import axios from 'axios';
import {ElMessage} from "element-plus";
import router from "@/router";

// 根据环境切换baseURL
const baseURL = process.env.NODE_ENV === 'production'
    ? 'http://39.96.202.75:9092/'  // 生产环境：公网服务器的后端地址
    : 'http://localhost:9090/';     // 开发环境：本地后端地址（或保持代理配置）

const request = axios.create({
    baseURL: baseURL,
    timeout: 5000
});
// 请求拦截器
request.interceptors.request.use(
    config => {
        try {
            const userStr = localStorage.getItem('user');
            if (userStr) {
                const user = JSON.parse(userStr);
                // 向后端传递Token
                if (user.token) {
                    config.headers['token'] = user.token;
                }
            }
        } catch (e) {
            console.error('添加Token失败:', e);
        }
        return config;
    },
    error => Promise.reject(error)
);

// 响应拦截器：处理Token无效/过期
request.interceptors.response.use(
    response => response,
    error => {
        if (error.response && error.response.status === 401) {
            // Token无效，清除存储并跳回登录页
            localStorage.removeItem('user');
            router.push({
                path: '/login',
                query: { redirect: router.currentRoute.value.fullPath }
            });
            ElMessage.error('登录已过期，请重新登录');
        }
        return Promise.reject(error);
    }
);

// 响应拦截器
request.interceptors.response.use(
    response => {
        return response.data;
    },
    error => {
        return Promise.reject(error);
    }
);

export default request;
