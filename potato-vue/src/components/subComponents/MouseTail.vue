<template>
  <div class="mouse-tail-host">
    <div
        v-for="(trail, index) in trails"
        :key="index"
        ref="trailElements"
        class="trail"
        :style="{
        left: `${trail.x}px`,
        top: `${trail.y}px`,
        width: `${trail.size}px`,
        height: `${trail.size}px`,
        backgroundColor: trail.color,
        opacity: trail.opacity,
        transform: `translate(-50%, -50%) scale(${trail.scale})`
      }"
    />
  </div>
</template>

<script setup>
import {onMounted, onUnmounted, ref} from 'vue';

// 拖尾配置
const TRAIL_COUNT = 12; // 拖尾数量
// const TRAIL_DELAY = 5;  // 拖尾延迟（数值越大拖尾越长）
const TRAIL_SIZE = 10;  // 初始拖尾大小
const MIN_SIZE = 4;     // 最小拖尾大小

// 拖尾颜色（多彩渐变）
const COLORS = [
  '#ff6b6b', '#ff9e7d', '#ffd166', '#caffbf',
  '#9bf6ff', '#a0c4ff', '#bdb2ff', '#ffc6ff'
];

// 拖尾元素引用
const trailElements = ref([]);

// 拖尾数据
const trails = ref(Array(TRAIL_COUNT).fill().map((_, i) => ({
  x: 0,
  y: 0,
  size: TRAIL_SIZE - (i * (TRAIL_SIZE - MIN_SIZE) / TRAIL_COUNT),
  color: COLORS[i % COLORS.length],
  opacity: 1 - (i * 0.8 / TRAIL_COUNT),
  scale: 1
})));

// 鼠标位置
const mousePos = ref({x: 0, y: 0});
// 动画帧ID
let animationFrameId = null;

// 处理鼠标移动
const handleMouseMove = (e) => {
  mousePos.value = {x: e.clientX, y: e.clientY};
};

// 动画循环
const animate = () => {
  // 更新每个拖尾位置
  for (let i = TRAIL_COUNT - 1; i > 0; i--) {
    trails.value[i] = {
      ...trails.value[i],
      x: trails.value[i - 1].x,
      y: trails.value[i - 1].y
    };
  }

  // 第一个拖尾跟随鼠标
  trails.value[0] = {
    ...trails.value[0],
    x: mousePos.value.x,
    y: mousePos.value.y
  };

  // 添加缩放效果使拖尾更生动
  trails.value.forEach((trail, index) => {
    const nextTrail = trails.value[index + 1];
    if (nextTrail) {
      const dx = trail.x - nextTrail.x;
      const dy = trail.y - nextTrail.y;
      const distance = Math.sqrt(dx * dx + dy * dy);
      trail.scale = 1 + Math.min(0.5, distance / 50);
    }
  });

  animationFrameId = requestAnimationFrame(animate);
};

// 组件挂载
onMounted(() => {
  window.addEventListener('mousemove', handleMouseMove);
  animationFrameId = requestAnimationFrame(animate);
});

// 组件卸载
onUnmounted(() => {
  window.removeEventListener('mousemove', handleMouseMove);
  if (animationFrameId) cancelAnimationFrame(animationFrameId);
});
</script>

<style scoped>
.mouse-tail-host {
  position: fixed;
  top: 0;
  left: 0;
  width: 0;
  height: 0;
  overflow: visible;
  pointer-events: none; /* 不干扰鼠标事件 */
  z-index: 9999; /* 确保在最顶层 */
}

.trail {
  position: absolute;
  border-radius: 50%;
  transition: left 0.1s linear,
  top 0.1s linear,
  transform 0.2s ease-out;
  mix-blend-mode: screen; /* 混合模式使颜色更鲜艳 */
  box-shadow: 0 0 8px currentColor; /* 发光效果 */
}
</style>
