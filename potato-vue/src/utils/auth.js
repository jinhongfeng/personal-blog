import request from './request'

const TOKEN_KEY = 'user';

/**
 * 从 localStorage 获取用户信息
 */
export function getUserInfo() {
    try {
        const user = localStorage.getItem(TOKEN_KEY);
        if (user) {
            return JSON.parse(user);
        }
    } catch (e) {
        console.error('Failed to parse user info from localStorage', e);
    }
    return null;
}

/**
 * 保存用户信息到 localStorage，并设置 axios 请求头
 * @param {object} userData - 包含 token 的用户信息对象
 */
export function setUserInfo(userData) {
    if (userData && userData.token) {
        localStorage.setItem(TOKEN_KEY, JSON.stringify(userData));
        request.defaults.headers.common['token'] = userData.token;
    }
}

/**
 * 清除 localStorage 的用户信息，并删除 axios 请求头
 */
export function removeUserInfo() {
    localStorage.removeItem(TOKEN_KEY);
    delete request.defaults.headers.common['token'];
}

/**
 * 检查用户是否已登录
 */
export function isLoggedIn() {
    const user = getUserInfo();
    return !!user?.token;
}
