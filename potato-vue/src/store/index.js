// src/store/index.js
import { createStore } from 'vuex';

// 创建一个新的 store 实例
const store = createStore({
    // State: 存放应用的状态（数据）
    state() {
        return {
            user: null // 这里存储用户信息
        };
    },

    // Mutations: 唯一可以修改 state 的地方（同步操作）
    mutations: {
        // 设置用户信息
        setUser(state, userData) {
            state.user = userData;
        },

        // 重置用户信息（用于登出）
        resetUser(state) {
            state.user = null;
        }
    },

    // Actions: 处理异步操作，可以通过 commit mutations 来改变 state
    actions: {
        // 模拟一个异步登录 action
        login({ commit }, userData) {
            // 这里可以是 axios 请求
            return new Promise((resolve) => {
                setTimeout(() => {
                    commit('setUser', userData);
                    resolve();
                }, 1000);
            });
        },

        // 登出 action
        logout({ commit }) {
            commit('resetUser');
        }
    },

    // Getters: 类似于计算属性，用于派生 state
    getters: {
        isLoggedIn(state) {
            return !!state.user; // 如果 user 存在，则 isLoggedIn 为 true
        }
    }
});

export default store;
