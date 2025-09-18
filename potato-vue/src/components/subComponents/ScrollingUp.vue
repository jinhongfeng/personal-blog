<template>
  <div class="bottom-percent-btn"
       @mouseenter="isHover = true"
       @mouseleave="isHover = false"
       @click="scrollTop">
    <span v-if="!isHover">{{ getScrollPercentage() }}%</span>
    <span v-else><font-awesome-icon icon="arrow-up" /></span> <!-- 向上符号 -->
  </div>
</template>

<script setup>
import { onMounted, onUnmounted, ref} from "vue";


const scale = ref(1);
// 缩放比例
const calculateScale = () => {
  const viewportWidth = window.innerWidth;
  viewportHeight.value = window.innerHeight;
  const baseWidth = 1920;
  const baseHeight = 1080;
  const widthRatio = viewportWidth / baseWidth;
  const heightRatio = viewportHeight.value / baseHeight;
  scale.value = Math.min(widthRatio, heightRatio);
};

// 滚动距离
const scrollY = ref(0);
// 页面总高度
const documentHeight = ref(0);
// 视口高度
const viewportHeight = ref(0);

// 计算滚动百分比：被卷去高度 / (页面总高度 - 视口高度)
const getScrollPercentage = () => {
  // 确保所有计算值都存在
  if (!documentHeight.value || !viewportHeight.value) return 0;

  // 防止分母为零
  const scrollableHeight = documentHeight.value - viewportHeight.value;
  if (scrollableHeight <= 0) return 0;

  // 计算并限制在0-100%范围内
  const percentage = (scrollY.value / scrollableHeight) * 100;
  return Math.min(100, Math.max(0, percentage)).toFixed(0);
};

const handleScroll = () => {
  scrollY.value = window.scrollY;
  documentHeight.value = document.documentElement.scrollHeight;
  viewportHeight.value = window.innerHeight;
  calculateScale(); // 同步缩放比例
};
const scrollTop = () => {
  window.scrollTo({ top: 0, behavior: 'smooth' });
};

// 控制是否悬浮状态，右下角百分比按钮
const isHover = ref(false);
onMounted(() => {
  handleScroll();
  window.addEventListener('scroll', handleScroll);
  window.addEventListener('resize', handleScroll);
})
onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll);
  window.removeEventListener('resize', handleScroll);
})
</script>

<style scoped>
.bottom-percent-btn {
  position: fixed;
  width: 40px;
  height: 40px;
  padding: 10px 10px;

  bottom: 20vh;
  right: 20px;
  background: #6a737d;
  color: #fff;

  border-radius: 8px;
  font-size: 16px;
  font-family: inherit;

  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
  z-index: 999;
  transition: transform 0.3s ease; /* 适配缩放动画 */

  &:hover {
    background: #ffb347;
  }
}
</style>
