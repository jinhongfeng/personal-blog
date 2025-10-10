<template>
  <el-button class="theme-toggle-btn" :class="{ 'dark-mode-active': isDarkMode }"
             @click="toggleDarkMode" aria-label="切换主题模式" >
    <span v-if="isDarkMode">☀️</span>
    <span v-else>🌙</span>
  </el-button>
</template>

<script setup>
import {onMounted, ref} from "vue";

const isDarkMode = ref(false); // false：白天模式
const toggleDarkMode = () => {
  isDarkMode.value = !isDarkMode.value;

  if (isDarkMode.value) {
    document.documentElement.classList.add('dark-mode');
  } else {
    document.documentElement.classList.remove('dark-mode')
  }

  localStorage.setItem('theme', isDarkMode.value ? 'dark' : 'light');
}
onMounted(() => {
  const saveTheme = localStorage.getItem('theme');
  if (saveTheme === 'dark') {
    toggleDarkMode();
  }
})
</script>

<style scoped>
.theme-toggle-btn {
  position: fixed;
  width: 40px;
  height: 40px;

  bottom: 26vh;
  right: 20px;
  background: #6a737d;
  color: #fff;

  padding: 10px 10px;
  border-radius: 8px;
  font-size: 16px;
  font-family: inherit;

  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
  z-index: 999;
  transition: transform 0.3s ease;
  border: none;
  &:hover {
    background: #ffb347;
    border: none;
  }
}

.theme-toggle-btn.dark-mode-active {
  color: #333333;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
  &:hover {
    background: #ffb347;
  }
}
</style>
