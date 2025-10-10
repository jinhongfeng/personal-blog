<template>
  <div id="app">
      <router-view />
  </div>
</template>

<script setup>
import {onMounted, onUnmounted} from "vue";

const handleContextmenu = (event) => {
  event.preventDefault(); // 阻止默认的右键菜单

  // alert('右键菜单已禁用');

};

// 禁止键盘事件处理函数
const handleKeydown = (event) => {
  // F12
  if (event.key === 'F12') {
    event.preventDefault();
  }
  // Ctrl+Shift+I
  if (event.ctrlKey && event.shiftKey && event.key === 'I') {
    event.preventDefault();
  }
  // Ctrl+Shift+C
  if (event.ctrlKey && event.shiftKey && event.key === 'C') {
    event.preventDefault();
  }
  // Ctrl+Shift+J
  if (event.ctrlKey && event.shiftKey && event.key === 'J') {
    event.preventDefault();
  }
  // // Ctrl+U (查看源代码)
  if (event.ctrlKey && event.key === 'u') {
    event.preventDefault();
  }
  // Ctrl+S (保存页面)
  if (event.ctrlKey && event.key === 's') {
    event.preventDefault();
  }
};

// 在组件挂载时添加事件监听器
onMounted(() => {
  // 监听整个文档的右键菜单事件
  document.addEventListener('contextmenu', handleContextmenu);
  // 监听整个文档的键盘按下事件
  document.addEventListener('keydown', handleKeydown);
});

// 在组件卸载时移除事件监听器
// 这可以防止在单页应用（SPA）中切换页面后，事件监听器仍然存在，导致内存泄漏或意外行为。
onUnmounted(() => {
  document.removeEventListener('contextmenu', handleContextmenu);
  document.removeEventListener('keydown', handleKeydown);
});
</script>

<style>
#app {
  font-family: var(--globalFont);

  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  //display: flex;
  //align-items: center;
  //justify-content: center;
  //margin-top: 60px;
}

:root {
  /* 默认鼠标 */
  --default-cursor: url(https://npm.elemecdn.com/eurkon-cdn/hexo/images/user/default.cur), auto;
  --pointer-cursor: url(https://npm.elemecdn.com/eurkon-cdn/hexo/images/user/pointer.cur), pointer;
}

body, html {
  cursor: var(--default-cursor);
}


</style>
