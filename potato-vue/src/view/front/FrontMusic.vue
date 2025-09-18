<template>
  <el-container>
    <!-- 小屏幕设备提示 -->
    <div class="mobile-notification">
      <div class="notification-content">
        <font-awesome-icon icon="laptop" class="notification-icon" />
        <p>建议使用电脑端查看以获得最佳体验</p>
      </div>
    </div>

    <!-- 全屏背景图 -->
    <div class="page-background" :style="{ backgroundImage: `url(${currentBgUrl})` }"></div>
    <!-- 头部 -->
    <el-header>
      <FrontHeader />
    </el-header>
    <el-main class="main-content">
      <div class="desktop-container">
        <!-- 背景切换按钮 -->
        <div class="bg-switcher" @click="toggleBgSelector">
          <font-awesome-icon icon="globe" />
        </div>

        <!-- 背景选择面板 -->
        <div class="bg-selector" v-if="showBgSelector">
          <div class="bg-selector-header">
            <h3>选择背景</h3>
            <el-icon class="close-btn" @click="toggleBgSelector"><Close /></el-icon>
          </div>
          <div class="bg-thumbs">
            <div v-for="(bg, index) in backgroundImages" :key="index"
                 class="bg-thumb" :class="{ active: currentBgIndex === index }"
                 @click="setBackground(index)">
              <img :src="bg.thumbnail" :alt="bg.name" class="bg-thumb-img">
              <span class="bg-name">{{ bg.name }}</span>
            </div>
          </div>
        </div>
        <MusicItem />
      </div>
    </el-main>
  </el-container>
</template>

<script setup>
// 当前背景图URL
import {computed, onMounted, ref} from "vue";
import FrontHeader from "@/components/FrontHeader.vue";
import MusicItem from "@/view/front/subComponent/MusicItem.vue";
import {ElIcon, ElNotification} from "element-plus";
import {Close} from "@element-plus/icons-vue";
import request from "@/utils/request";

import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome';

const currentBgIndex = ref(0)
const backgroundImages = ref([
  {
    name: '水漫窗户',
    url: 'https://cdn.yuanshikong.net/NewTab/wallpaper/video/62552700452009fb7a5febf2.jpg',
    thumbnail: 'https://cdn.yuanshikong.net/NewTab/wallpaper/video/62552700452009fb7a5febf2.jpg?imageMogr2/thumbnail/450x300',
  },
  {
    name: '山川湖泊',
    url: 'https://picsum.photos/id/10/1920/1080',
    thumbnail: 'https://picsum.photos/id/10/200/120'
  },
])
const currentBgUrl = computed(() => {
  return backgroundImages.value[currentBgIndex.value].url
})
// 加载数据相关
const loading = ref(true);         // 加载状态
const showError = ref(false);      // 错误状态标识
const errorMessage = ref("");      // 错误信息
const loaded = ref(false);         // 用于动画触发
const showBgSelector = ref(false)  // 是否展示背景切换面板

const toggleBgSelector = () => {
  showBgSelector.value = !showBgSelector.value
}

const setBackground = (index) => {
  currentBgIndex.value = index
  showBgSelector.value = false
  localStorage.setItem('selectedBgIndex', index)
}

// 加载数据
const loadData = async () => {
  // 重置状态
  loading.value = true;
  showError.value = false;
  errorMessage.value = "";
  loaded.value = false;

  try {
    const res = await request.get('/bgImage');
    if (res.code === '200') {
      backgroundImages.value = res.data || [];
      // 数据加载完成后触发动画
      setTimeout(() => {
        loaded.value = true;
      }, 100);
    } else {
      showError.value = true;
      errorMessage.value = res.msg || '获取背景图片列表失败';
      ElNotification.error({
        title: '加载失败',
        message: errorMessage.value
      });
    }
  } catch (error) {
    showError.value = true;
    errorMessage.value = '无法连接到服务器，请检查网络';
    ElNotification.error({
      title: '网络错误',
      message: errorMessage.value
    });
  } finally {
    loading.value = false;
  }
};

// 页面挂载时加载数据
onMounted(() => {
  loadData();
});
</script>

<style scoped>

.main-content {
  position: relative;
  z-index: 10;
  padding: 0;
  margin: 0;
  height: calc(100vh - var(--el-header-height, 60px));
}

/* 小屏幕设备提示 */
.mobile-notification {
  display: none;
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.85);
  z-index: 9999;
  justify-content: center;
  align-items: center;
  color: white;
  text-align: center;
}

.notification-content {
  max-width: 80%;
  padding: 20px;
}

.notification-icon {
  font-size: 48px;
  margin-bottom: 20px;
  color: #409eff;
}

.notification-content p {
  font-size: 18px;
  line-height: 1.5;
  margin: 0;
}

/* 背景切换按钮 */
.bg-switcher {
  position: fixed;
  top: 80px;
  left: 40px;
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(10px);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  cursor: pointer;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
  transition: all 0.3s ease;
  z-index: 100;
}

.bg-switcher:hover {
  background: rgba(255, 255, 255, 0.25);
  transform: scale(1.1);
}

/* 背景选择面板 */
.bg-selector {
  position: fixed;
  top: 80px;
  left: 20px;
  width: 300px;
  background: rgba(15, 15, 15, 0.85);
  backdrop-filter: blur(15px);
  border-radius: 12px;
  padding: 15px;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.3);
  z-index: 100;
  animation: slideUp 0.3s ease-out forwards;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.bg-selector-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.bg-selector-header h3 {
  color: white;
  font-size: 16px;
  margin: 0;
}
.bg-thumbs {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 10px;
  max-height: 300px;
  overflow-y: auto;
  padding-right: 5px;
}

.bg-thumbs::-webkit-scrollbar {
  width: 5px;
}

.bg-thumbs::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.2);
  border-radius: 5px;
}

.bg-thumb {
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  position: relative;
  transition: all 0.3s ease;
  border: 2px solid transparent;
}

.bg-thumb.active {
  border-color: #f5f5f5;
}

.bg-thumb:hover {
  transform: translateY(-3px);
}

.bg-thumb-img {
  width: 100%;
  height: 80px;
  object-fit: cover;
  display: block;
}
.bg-name {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: linear-gradient(transparent, rgba(0,0,0,0.7));
  color: white;
  font-size: 12px;
  padding: 5px 8px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 响应式适配 */
@media screen and (max-width: 768px) {
  /* 小屏幕设备显示提示 */
  .mobile-notification {
    display: flex;
  }

  /* 移动设备样式 */
  .bg-switcher {
    top: auto;
    bottom: 30px;
    left: 50%;
    transform: translateX(-50%);
    width: 56px;
    height: 56px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
  }

  .bg-selector {
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    width: 90%;
    max-width: 300px;
    max-height: 70vh;
  }

  .bg-thumbs {
    grid-template-columns: repeat(3, 1fr);
    max-height: calc(70vh - 80px);
    gap: 8px;
  }

  .bg-thumb-img {
    height: 70px;
  }
}

@media screen and (min-width: 769px) and (max-width: 1024px) {
  /* 平板设备样式 */
  .bg-switcher {
    top: 80px;
    left: 40px;
  }

  .bg-selector {
    top: 40px;
    left: 20px;
    width: 280px;
  }
}
</style>
