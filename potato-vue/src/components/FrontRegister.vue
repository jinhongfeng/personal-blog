<template>
  <!-- 液态玻璃效果背景 -->
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

    <!-- 注册卡片 -->
    <div class="glass-component login-card" ref="tiltCard"
         @mousemove="handleMouseMove"
         @mouseleave="handleMouseLeave"
         @click="handleClick"
    >
      <!-- 玻璃态效果元素 -->
      <div class="glass-effect"></div>
      <div class="glass-tint"></div>
      <div class="glass-shine"></div>

      <div class="glass-content">
        <h2 class="login-title">用户注册</h2>
        <form class="login-form" @submit.prevent="handleSubmit">
          <!-- 1. 用户名 -->
          <div class="form-group">
            <input
                type="text"
                placeholder="请设置用户名"
                class="glass-input"
                v-model="username"
                :class="{ 'input-error': usernameError }"
                @input="clearError('username')"
                :disabled="registerLoading"
            >
            <span v-if="usernameError" class="error-message">{{ usernameError }}</span>
          </div>

          <!-- 2. 密码 -->
          <div class="form-group">
            <input
                type="password"
                placeholder="请设置密码"
                class="glass-input"
                v-model="password"
                :class="{ 'input-error': passwordError }"
                @input="clearError('password')"
                :disabled="registerLoading"
            >
            <span v-if="passwordError" class="error-message">{{ passwordError }}</span>
          </div>

          <!-- 3. 邮箱 + 发送验证码按钮 -->
          <div class="form-group code-group">
            <input
                type="email"
                placeholder="请输入您的邮箱"
                class="glass-input code-input"
                v-model="email"
                :class="{ 'input-error': emailError }"
                @input="clearError('email')"
                :disabled="registerLoading || codeLoading"
            >
            <button
                type="button"
                class=" code-button"
                @click="sendCode"
                :disabled="registerLoading || codeLoading || !isEmailValid || countdown > 0"
            >
              <span v-if="!codeLoading && countdown === 0">发送验证码</span>
              <span v-if="codeLoading" class="loading-spinner">
                <i class="icon-loading"></i> 发送中...
              </span>
              <span v-if="countdown > 0" class="countdown">{{ countdown }}s后重新获取</span>
            </button>
            <span v-if="emailError" class="error-message">{{ emailError }}</span>
          </div>

          <!-- 4. 验证码 -->
          <div class="form-group">
            <input
                type="text"
                placeholder="请输入验证码"
                class="glass-input"
                v-model="code"
                :class="{ 'input-error': codeError }"
                @input="clearError('code')"
                :disabled="registerLoading"
                maxlength="6"
            >
            <span v-if="codeError" class="error-message">{{ codeError }}</span>
          </div>

          <!-- 5. 注册按钮 -->
          <button
              type="submit"
              class="glass-button"
              :disabled="registerLoading || !canRegister"
          >
            <span v-if="!registerLoading">注册</span>
            <span v-else class="loading-spinner">
              <i class="icon-loading"></i> 注册中...
            </span>
          </button>
        </form>
      </div>
      <div ref="clickEffect" class="click-gradient"></div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import request from '@/utils/request';
import { ElMessage } from "element-plus";

// --- 1. 状态管理 ---
// 表单数据
const username = ref('');
const password = ref('');
const email = ref('');
const code = ref('');
const backgroundImage = require("@/assets/loginBackground.jpeg");

// 状态和错误信息
const registerLoading = ref(false);
const codeLoading = ref(false);
const usernameError = ref('');
const passwordError = ref('');
const emailError = ref('');
const codeError = ref('');
const countdown = ref(0);

// DOM引用和路由
const tiltCard = ref(null);
const clickEffect = ref(null);
const router = useRouter();

// --- 2. 计算属性 ---
// 判断邮箱格式是否有效，用于控制“发送验证码”按钮的可点击性
const isEmailValid = computed(() => {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  return emailRegex.test(email.value.trim());
});

// 判断注册按钮是否可点击
const canRegister = computed(() => {
  // 只检查所有输入框是否都已填写
  return username.value.trim() !== ''
      && password.value.trim() !== ''
      && email.value.trim() !== ''
      && code.value.trim() !== '';
});

// --- 3. 生命周期钩子 ---
onMounted(() => {

});

// --- 4. 事件处理函数 ---
const handleMouseMove = (e) => {
  if (!tiltCard.value || registerLoading.value) return;
  const card = tiltCard.value;
  const rect = card.getBoundingClientRect();
  const x = e.clientX - rect.left;
  const y = e.clientY - rect.top;
  const centerX = rect.width / 2;
  const centerY = rect.height / 2;
  const maxTilt = 18;
  const rotateY = ((x - centerX) / centerX) * maxTilt;
  const rotateX = -((y - centerY) / centerY) * maxTilt;
  card.style.transform = `perspective(600px) rotateX(${rotateX}deg) rotateY(${rotateY}deg) scale(1.03)`;
};

const handleMouseLeave = () => {
  if (tiltCard.value && !registerLoading.value) {
    tiltCard.value.style.transform = 'perspective(600px) rotateX(0deg) rotateY(0deg) scale(1)';
  }
};

const handleClick = (e) => {
  if (!tiltCard.value || !clickEffect.value || registerLoading.value) return;
  const card = tiltCard.value;
  const rect = card.getBoundingClientRect();
  const x = e.clientX - rect.left;
  const y = e.clientY - rect.top;
  const effect = clickEffect.value;
  effect.style.left = `${x}px`;
  effect.style.top = `${y}px`;
  card.classList.add('clicked');
  setTimeout(() => {
    card.classList.remove('clicked');
  }, 600);
};

// --- 5. 核心业务逻辑 ---
// 发送验证码
const sendCode = async () => {
  clearError('email');

  if (!isEmailValid.value) {
    emailError.value = '请输入有效的邮箱地址';
    return;
  }

  codeLoading.value = true;
  try {
    const res = await request.post('/user/send-code', { email: email.value });

    if (res.code === "200") {
      ElMessage.success(res.msg || '验证码已发送，请注意查收');
      startCountdown();
    } else {
      ElMessage.error(res.msg || '发送验证码失败');
    }
  } catch (error) {
    ElMessage.error('请求失败，请检查网络');
    console.error("发送验证码失败:", error);
  } finally {
    codeLoading.value = false;
  }
};

// 验证码倒计时
const startCountdown = () => {
  countdown.value = 60; // 60秒倒计时
  const timer = setInterval(() => {
    countdown.value--;
    if (countdown.value <= 0) {
      clearInterval(timer);
    }
  }, 1000);
};

// 验证表单
const validateForm = () => {
  let isValid = true;
  clearError('username');
  clearError('password');
  clearError('email');
  clearError('code');

  // 用户名验证
  if (!username.value.trim()) {
    usernameError.value = '请输入用户名';
    isValid = false;
  } else if (username.value.length < 2 || username.value.length > 10) {
    usernameError.value = '用户名长度在2-10个字符之间';
    isValid = false;
  }

  // 密码验证
  if (!password.value.trim()) {
    passwordError.value = '请输入密码';
    isValid = false;
  } else if (password.value.length < 6 || password.value.length > 20) {
    passwordError.value = '密码长度在6-20个字符之间';
    isValid = false;
  }

  // 邮箱验证
  if (!email.value.trim()) {
    emailError.value = '请输入邮箱';
    isValid = false;
  } else if (!isEmailValid.value) {
    emailError.value = '请输入有效的邮箱地址';
    isValid = false;
  }

  // 验证码验证
  if (!code.value.trim()) {
    codeError.value = '请输入验证码';
    isValid = false;
  } else if (code.value.length !== 6) {
    codeError.value = '验证码长度为6位';
    isValid = false;
  }

  return isValid;
};

// 清除错误信息
const clearError = (field) => {
  if (field === 'username') usernameError.value = '';
  else if (field === 'password') passwordError.value = '';
  else if (field === 'email') emailError.value = '';
  else if (field === 'code') codeError.value = '';
};

// 处理表单提交
const handleSubmit = async () => {
  if (!validateForm()) return;

  registerLoading.value = true;
  try {
    // 1. 先验证邮箱和验证码是否正确
    // 调用登录用的验证码验证接口，这是一个巧妙的复用
    const codeValidationRes = await request.post('/user/login-email', {
      email: email.value,
      code: code.value
    });

    // 2. 如果验证码验证成功，再执行注册逻辑
    if (codeValidationRes.code === "200") {
      // 3. 准备注册所需的数据
      const userData = {
        username: username.value,
        password: password.value, // 注意：实际项目中密码应在前端加密
        email: email.value,
        // 根据后端save方法的逻辑，如果role为空，可能需要给一个默认值
        role: "user"
      };

      // 4. 调用通用的 save 接口进行注册
      const registerRes = await request.post('/user', userData);

      if (registerRes.code === "200") {
        ElMessage.success('注册成功！即将跳转到登录页...');
        setTimeout(() => {
          router.push('/login'); // 假设你的登录页路由是 /login
        }, 1500);
      } else {
        // 如果用户名已存在等原因导致注册失败
        ElMessage.error(registerRes.msg || '注册失败，请检查信息');
      }
    } else {
      // 如果验证码验证失败
      ElMessage.error(codeValidationRes.msg || '验证码错误或已过期');
    }
  } catch (error) {
    if (error.response) {
      ElMessage.error(`操作失败: ${error.response.data?.msg || '服务器错误'}`);
    } else {
      ElMessage.error('网络异常，请稍后重试');
    }
  } finally {
    registerLoading.value = false;
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
