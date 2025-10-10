<template>
  <!-- 液态玻璃效果登录页面 -->
  <div class="login-container animated-background"
       :style="{
            backgroundImage: `url('${backgroundImage}')`,
            backgroundRepeat: 'no-repeat',
            backgroundPosition: 'center center',
            backgroundSize: 'cover'
          }">
    <!-- SVG滤镜库 -->
    <svg style="display: none">
      <filter id="glass-distortion" x="0%" y="0%" width="100%" height="100%" filterUnits="objectBoundingBox">
        <feTurbulence type="fractalNoise" baseFrequency="0.001 0.005" numOctaves="1" seed="17" result="turbulence" />
        <feComponentTransfer in="turbulence" result="mapped">
          <feFuncR type="gamma" amplitude="1" exponent="10" offset="0.5" />
          <feFuncG type="gamma" amplitude="0" exponent="1" offset="0" />
          <feFuncB type="gamma" amplitude="0" exponent="1" offset="0.5" />
        </feComponentTransfer>
        <feGaussianBlur in="turbulence" stdDeviation="3" result="softMap" />
        <feSpecularLighting in="softMap" surfaceScale="5" specularConstant="1" specularExponent="100" lighting-color="white" result="specLight">
          <fePointLight x="-200" y="-200" z="300" />
        </feSpecularLighting>
        <feComposite in="specLight" operator="arithmetic" k1="0" k2="1" k3="1" k4="0" result="litImage"  in2=""/>
        <feDisplacementMap in="SourceGraphic" in2="softMap" scale="200" xChannelSelector="R" yChannelSelector="G" />
      </filter>
    </svg>

    <!-- 登录卡片 -->
    <div class="glass-component login-card" ref="tiltCard"
        @mousemove="handleMouseMove"
        @mouseleave="handleMouseLeave"
        @click="handleClick"
    >
      <div class="glass-effect"></div>
      <div class="glass-tint"></div>
      <div class="glass-shine"></div>
      <div class="glass-content">
        <h2 class="login-title">欢迎登录</h2>
        <form class="login-form" @submit.prevent="handleSubmit">
          <div class="form-group">
            <input
                type="text"
                placeholder="用户名"
                class="glass-input"
                v-model="username"
                :class="{ 'input-error': usernameError }"
                @input="clearError('username')"
                :disabled="loginLoading"
            >
            <span v-if="usernameError" class="error-message">{{ usernameError }}</span>
          </div>
          <div class="form-group">
            <input
                type="password"
                placeholder="密码"
                class="glass-input"
                v-model="password"
                :class="{ 'input-error': passwordError }"
                @input="clearError('password')"
                :disabled="loginLoading"
            >
            <span v-if="passwordError" class="error-message">{{ passwordError }}</span>
          </div>
          <div class="form-group ml-10">
<!--            <el-link @click="router.push('/register')" >-->
<!--              <span style="color: var(&#45;&#45;orangeRed)">没有账号？ 点击注册</span>-->
<!--            </el-link>-->
          </div>
          <button
              type="submit"
              class="glass-button"
              :disabled="loginLoading"
          >
            <span v-if="!loginLoading">登录</span>
            <span v-else class="loading-spinner">
              <i class="icon-loading"></i> 登录中...
            </span>
          </button>
        </form>
      </div>
      <div ref="clickEffect" class="click-gradient"></div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import request from '@/utils/request';
import { ElMessage } from "element-plus";
import {setUserInfo} from "@/utils/auth";

// 表单数据
const username = ref('');
const password = ref('');
const role = ref('');
const backgroundImage = require("@/assets/loginBackground.jpeg");

// 状态管理
const loginLoading = ref(false);
const usernameError = ref('');
const passwordError = ref('');

// DOM引用
const tiltCard = ref(null);
const clickEffect = ref(null);

// 路由相关
const router = useRouter();
const route = useRoute();

// 监听路由变化，清除表单
watch(
    () => route.path,
    () => {
      resetForm();
    }
);

// 组件挂载时检查是否已登录
onMounted(() => {
  const user = localStorage.getItem('user');
  if (user) {
    try {
      const userData = JSON.parse(user);
      if (userData.token) {
        // 如果已登录，跳转到后台首页
        router.push('/back/home').catch(() => {
        });
      }
    } catch (e) {
      localStorage.removeItem('user');
    }
  }
});

// 处理鼠标移动实现卡片倾斜效果
const handleMouseMove = (e) => {
  if (!tiltCard.value || loginLoading.value) return;

  const card = tiltCard.value;
  const rect = card.getBoundingClientRect();
  const x = e.clientX - rect.left;
  const y = e.clientY - rect.top;
  const centerX = rect.width / 2;
  const centerY = rect.height / 2;

  // 最大旋转角度
  const maxTilt = 18;
  const rotateY = ((x - centerX) / centerX) * maxTilt;
  const rotateX = -((y - centerY) / centerY) * maxTilt;

  card.style.transform = `perspective(600px) rotateX(${rotateX}deg) rotateY(${rotateY}deg) scale(1.03)`;
};

// 鼠标离开时恢复卡片状态
const handleMouseLeave = () => {
  if (tiltCard.value && !loginLoading.value) {
    tiltCard.value.style.transform = 'perspective(600px) rotateX(0deg) rotateY(0deg) scale(1)';
  }
};

// 处理卡片点击波纹效果
const handleClick = (e) => {
  if (!tiltCard.value || !clickEffect.value || loginLoading.value) return;

  const card = tiltCard.value;
  const rect = card.getBoundingClientRect();
  const x = e.clientX - rect.left;
  const y = e.clientY - rect.top;

  // 设置波纹位置
  const effect = clickEffect.value;
  effect.style.left = `${x}px`;
  effect.style.top = `${y}px`;

  // 触发动画
  card.classList.add('clicked');
  setTimeout(() => {
    card.classList.remove('clicked');
  }, 600);
};

// 验证表单
const validateForm = () => {
  let isValid = true;

  // 验证用户名
  if (!username.value.trim()) {
    usernameError.value = '请输入用户名';
    isValid = false;
  } else if (username.value.length < 2 || username.value.length > 10) {
    usernameError.value = '用户名长度在2-10个字符之间';
    isValid = false;
  }

  // 验证密码
  if (!password.value.trim()) {
    passwordError.value = '请输入密码';
    isValid = false;
  } else if (password.value.length < 6 || password.value.length > 20) {
    passwordError.value = '密码长度在6-20个字符之间';
    isValid = false;
  }

  return isValid;
};

// 清除错误信息
const clearError = (field) => {
  if (field === 'username') {
    usernameError.value = '';
  } else if (field === 'password') {
    passwordError.value = '';
  }
};

// 重置表单
const resetForm = () => {
  username.value = '';
  password.value = '';
  usernameError.value = '';
  passwordError.value = '';
  loginLoading.value = false;
};

// 处理表单提交
const handleSubmit = async () => {
  // 清除之前的错误
  clearError('username');
  clearError('password');

  // 验证表单
  if (!validateForm()) {
    return;
  }

  loginLoading.value = true; // 开始加载

  try {

    const res = await request.post('/user/login', {
      username: username.value,
      password: password.value,
      role: role.value
    });

    // 检查响应格式
    if (!res || !res.data) {
      throw new Error('服务器响应格式错误');
    }

    // 检查登录状态
    if (res.code === "200" && res.data && res.data.token) {
      // 保存用户信息
      setUserInfo(res.data);

      ElMessage.success('登录成功');

      // 使用路由实例的currentRoute获取redirect参数
      const redirectPath = router.currentRoute.value.query.redirect || '/back/home';

      // 强制刷新路由缓存
      router.push(redirectPath).then(() => {
        // location.reload(); // 如仍有问题可尝试启用此句
      }).catch(err => {
        // 跳转失败时的降级处理
        if (err.name === 'NavigationDuplicated') {
          // 处理重复导航错误
          router.replace(redirectPath);
        } else {
          // 未知错误，跳转到默认首页
          router.push('/back/home');
        }
      });
    }

  } catch (error) {
    // 更友好的错误提示
    if (error.response) {
      ElMessage.error(`登录失败: ${error.response.data?.msg || '服务器错误'}`);
    } else if (error.message) {
      ElMessage.error(`登录失败: ${error.message}`);
    } else {
      ElMessage.error('网络异常，请稍后重试');
    }
  } finally {
    loginLoading.value = false;
  }
};
</script>

<style lang="scss" scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
  padding: 20px;
}

.animated-background {
  width: 100%;
  height: 100%;
  animation: backgroundShift 30s ease-in-out infinite alternate;
}

@keyframes backgroundShift {
  0% {
    background-position: 0% 50%;
  }
  100% {
    background-position: 100% 50%;
  }
}

.login-card {
  width: 400px;
  position: relative;
  border-radius: 24px;
  overflow: hidden;
  box-shadow: 0 4px 24px 0 rgba(0, 0, 0, 0.10),
  0 1.5px 6px 0 rgba(0, 0, 0, 0.08);
  transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.6);
  cursor: pointer;
  background: transparent;

  @media (max-width: 480px) {
    width: 90%;
    max-width: 350px;
  }
}

// 加载状态时禁用交互效果
.login-loading {
  pointer-events: none;
  opacity: 0.8;
}

.glass-effect {
  position: absolute;
  inset: 0;
  z-index: 0;
  backdrop-filter: blur(5px);
  filter: url(#glass-distortion);
  isolation: isolate;
  border-radius: 24px;
}

.glass-tint {
  position: absolute;
  inset: 0;
  z-index: 1;
  background: rgba(0, 0, 0, 0.15);
  border-radius: 24px;
}

.glass-shine {
  position: absolute;
  inset: 0;
  z-index: 2;
  border: 1px solid rgba(255, 255, 255, 0.13);
  border-radius: 24px;
  box-shadow: inset 1px 1px 8px 0 rgba(255, 255, 255, 0.18),
  inset -1px -1px 8px 0 rgba(255, 255, 255, 0.08);
  pointer-events: none;
}

.glass-content {
  position: relative;
  z-index: 3;
  padding: 2rem;
  color: white;
}

.login-title {
  text-align: center;
  color: #fff;
  margin-bottom: 2rem;
  font-size: 2rem;
  font-weight: 600;
  text-shadow: 0 1px 3px rgba(0, 0, 0, 0.2);
}

.form-group {
  margin-bottom: 1.5rem;
  position: relative;
}

.glass-input {
  width: 90%;
  padding: 12px 20px;
  border: none;
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.1);
  color: #fff;
  font-size: 1rem;
  backdrop-filter: blur(5px);
  transition: all 0.3s ease;

  &::placeholder {
    color: rgba(255, 255, 255, 0.7);
  }

  &:focus {
    outline: none;
    background: rgba(255, 255, 255, 0.2);
    box-shadow: 0 0 15px rgba(255, 255, 255, 0.1);
  }

  &:disabled {
    opacity: 0.7;
    cursor: not-allowed;
    background: rgba(255, 255, 255, 0.05);
  }
}

.input-error {
  border: 1px solid rgba(255, 80, 80, 0.7) !important;
  background: rgba(255, 100, 100, 0.15) !important;
}

.error-message {
  display: block;
  margin-top: 5px;
  font-size: 0.85rem;
  color: rgba(255, 180, 180, 0.9);
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.2);
  padding-left: 5px;
}

.glass-button {
  width: 100%;
  padding: 12px;
  border: none;
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.2);
  color: #fff;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  backdrop-filter: blur(5px);
  position: relative;
  overflow: hidden;

  &:hover {
    background: rgba(255, 255, 255, 0.3);
    transform: translateY(-2px);
    box-shadow: 0 8px 25px rgba(0, 0, 0, 0.2);
  }

  &:active {
    transform: translateY(0);
  }

  &:disabled {
    opacity: 0.7;
    cursor: not-allowed;
    background: rgba(255, 255, 255, 0.1);
    transform: none;
    box-shadow: none;
  }
}

// 加载指示器
.loading-spinner {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.icon-loading {
  width: 16px;
  height: 16px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-radius: 50%;
  border-top-color: white;
  animation: spin 1s ease-in-out infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.click-gradient {
  position: absolute;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(255, 255, 255, 0.4) 0%, rgba(180, 180, 255, 0.2) 40%, rgba(100, 100, 255, 0.1) 70%, rgba(50, 50, 255, 0) 100%);
  transform: translate(-50%, -50%) scale(0);
  opacity: 0;
  pointer-events: none;
  z-index: 4;
  width: 200px;
  height: 200px;
}

.glass-component.clicked .click-gradient {
  animation: gradient-ripple 0.6s ease-out;
}

@keyframes gradient-ripple {
  0% {
    transform: translate(-50%, -50%) scale(0);
    opacity: 1;
  }
  100% {
    transform: translate(-50%, -50%) scale(3);
    opacity: 0;
  }
}

.glass-component {
  transition: transform 0.25s cubic-bezier(0.22, 1, 0.36, 1);
  will-change: transform;
}
</style>
