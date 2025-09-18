<template>
  <div>
    <div class="scrollTop-box" v-if="showRopeEffect">
      <div class="rope" :style="{ height: ropeHeight + 'px' }"></div>
      <el-image :src="ropeImage" alt="回到顶部"
                class="effect-image" @click="scrollTop" />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from "vue";

const ropeImage = require('@/assets/pikaqiu.png');

// 滚动相关状态
const scrollY = ref(0);
const viewportHeight = ref(window.innerHeight);
const documentHeight = ref(document.documentElement.scrollHeight);
// 图片高度
const imageHeight = ref(80)
// 计算当前滚动百分比
const scrollPercent = computed(() => {
  const scrollableHeight = documentHeight.value - viewportHeight.value;
  return scrollableHeight > 0
      ? Math.min(100, Math.max(0, (scrollY.value / scrollableHeight) * 100))
      : 0;
});

// 计算绳子高度（占视口的百分比）
const ropeHeight = computed(() => {
  const maxHeight = viewportHeight.value - imageHeight.value; // 减去图片高度
  let ropeTemp = (scrollPercent.value / 100) * maxHeight;
  if (ropeTemp >= maxHeight * 0.7) {
    return maxHeight * 0.7
  }
  // 根据滚动百分比计算绳子高度
  return ropeTemp;
});

// 是否显示绳子效果（滚动超过10%时显示）
const showRopeEffect = computed(() => {
  return scrollPercent.value >= 2;
});

// 监听滚动事件
const handleScroll = () => {
  scrollY.value = window.scrollY;
  viewportHeight.value = window.innerHeight;
  documentHeight.value = document.documentElement.scrollHeight;
};

// 点击回顶
const scrollTop = () => {
  window.scrollTo({ top: 0, behavior: 'smooth' });
};

onMounted(() => {
  handleScroll();
  window.addEventListener('scroll', handleScroll);
  window.addEventListener('resize', handleScroll);
});

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll);
  window.removeEventListener('resize', handleScroll);
});
</script>

<style scoped>
.scrollTop-box {
  position: fixed;
  right: 50px;
  top: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  z-index: 999;
  height: 100vh;
  pointer-events: none; /* 防止遮挡下方内容 */
}

.rope {
  width: 5px;
  background-color: rgba(0, 0, 0, 0.5);
  border-radius: 2px;
  transition: height 0.2s ease-out; /* 平滑过渡效果 */
  background-image: -webkit-linear-gradient(45deg, rgba(255, 255, 255, 0.1) 25%,
                    transparent 25%, transparent 50%,
                    rgba(255, 255, 255, 0.1) 50%, rgba(255, 255, 255, 0.1) 75%,
                    transparent 75%, transparent);
}

.effect-image {
  width: 80px;
  height: 80px;
  cursor: pointer;
  z-index: 999;
  margin-top: -15px; /* 与绳子无缝衔接 */
  pointer-events: auto; /* 恢复点击事件 */
  transition: transform 0.3s ease;
  opacity: 0.8;
}

.effect-image:hover {
  transform: scale(1.1); /* 悬停效果 */
  opacity: 1;
}
</style>
