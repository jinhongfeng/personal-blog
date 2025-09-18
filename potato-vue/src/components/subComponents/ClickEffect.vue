<template>
  <!-- 点击效果容器 -->
  <div ref="containerRef" class="click-effect-container">
    <span
        v-for="(effect, index) in effects"
        :key="index"
        class="click-text"
        :style="{
        left: effect.x + 'px',
        top: effect.y + 'px',
        color: effect.color,
        animationDelay: (index * 0.05) + 's'
      }"
    >
      {{ effect.text }}
    </span>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'

const containerRef = ref(null)
const effects = ref([])

const textArr = [
  "(＾∀＾)", "富强", "Σ(ﾟдﾟ；)", "民主", "(⊙o⊙)", "文明",
  "✈", "和谐", "☯", "爱国", "★", "敬业", "诚信", "友善",
  "自由", "☠", "平等", "〠", "公正", "❤", "法治"
]

// 生成随机颜色
const colorRandom = () => {
  const r = Math.floor(Math.random() * 156) + 50
  const g = Math.floor(Math.random() * 156) + 50
  const b = Math.floor(Math.random() * 156) + 50
  return `rgb(${r},${g},${b})`
}

// 处理点击事件
const handleClick = (e) => {
  // 添加点击效果
  effects.value.push({
    x: e.clientX,
    y: e.clientY,
    color: colorRandom(),
    text: textArr[Math.floor(Math.random() * textArr.length)]
  })

  // 5秒后移除效果
  setTimeout(() => {
    effects.value.shift()
  }, 5000)
}

// 添加全局点击监听
onMounted(() => {
  document.addEventListener('click', handleClick)
})

// 移除监听
onUnmounted(() => {
  document.removeEventListener('click', handleClick)
})
</script>

<style scoped>
.click-effect-container {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 9999;
  overflow: hidden;
}

.click-text {
  position: absolute;
  font-size: 24px;
  font-weight: bold;
  pointer-events: none;
  user-select: none;
  z-index: 10000;
  animation: floatUp 1.5s ease-out forwards;
  text-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
  transform: translate(-50%, -50%);
  will-change: transform, opacity;
}

@keyframes floatUp {
  0% {
    opacity: 1;
    transform: translate(-50%, -50%) scale(1);
    filter: blur(0);
  }
  70% {
    opacity: 1;
    filter: blur(0);
  }
  100% {
    opacity: 0;
    transform: translate(-50%, -150px) scale(1.8);
    filter: blur(2px);
  }
}
</style>
