<template>
  <div class="page-container">
    <!-- 顶部背景 -->
    <div class="top-banner"
         :style="{
            backgroundImage: `url('${backgroundImage}')`,
            backgroundRepeat: 'no-repeat',
            backgroundPosition: 'center center',
            backgroundSize: 'cover'
          }">
      <div class="content-center">
        <div class="noticeBox">
          <h2>{{ noticeList }}</h2>
        </div>
      </div>
    </div>

    <!-- 主要内容 -->
    <div style="margin-top: 50vh">
      <!-- 加载状态 -->
      <div v-if="loading" class="loading-container">
        <el-loading :fullscreen="false" text="加载中..."></el-loading>
      </div>

      <!-- 错误状态 -->
      <div v-else-if="showError" class="error-container">
        <el-icon class="error-icon"><Warning /></el-icon>
        <p class="error-text">{{ errorMessage }}</p>
        <el-button type="primary" @click="loadData">重试</el-button>
      </div>

      <!-- 数据加载完成但为空 -->
      <div v-else-if="blogList.length === 0" class="empty-state">
        <el-empty description="暂无博客数据"></el-empty>
      </div>

      <!-- 正常数据展示 -->
      <div v-else>
        <!-- 为整个卡片添加点击事件 -->
        <div
            class="mainContent-box"
            v-for="(item, index) in blogList"
            :key="index"
            :class="{ 'slide-up': loaded }"
            @click="navigateToDetail(item)"
            :style="{ cursor: 'pointer' }"
        >
          <div class="mainContent-card">
            <!-- 左侧主要内容 -->
            <div class="left-mainContent">
              <template v-if="getMediaType(item) === 'image'">
                <el-image :src="item.picture" class="card-image"></el-image>
              </template>
              <template v-else-if="getMediaType(item) === 'video'">
                <video controls class="card-video">
                  <source :src="item.picture" type="video/mp4">
                </video>
              </template>
              <template v-else>
                <el-image :src="defaultImage">
                  <template #placeholder>
                    <div class="image-slot">Loading<span class="dot">...</span></div>
                  </template>
                </el-image>
              </template>
            </div>

            <!-- 右侧主要内容 -->
            <div class="right-mainContent">
              <div class="mainContent-wrapper">
                <!-- 标题 -->
                <h2 class="card-title">{{ item.title }}</h2>
                <!-- 描述 -->
                <div class="card-description">
                  <span>{{ item.badge }}</span>
                </div>
                <!-- 时间 -->
                <div class="card-publishTime">
                  <el-icon><Timer /></el-icon>
                  <span style="margin-left: 5px">发布时间： </span>
                  <span style="margin-left: 5px">{{ item.publishTime }}</span>
                </div>
                <!-- 数据 -->
                <div class="card-dataContent">
                  <font-awesome-icon icon="eye" />
                  <span style="margin-left: 5px">浏览量：</span>
                  <span>{{ item.pageview }}</span>
                </div>
                <!-- 标签 -->
                <div class="card-badge">
                  <el-button type="danger">{{ item.author }}</el-button>
                  <el-button type="info" plain>{{ item.badge }}</el-button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from "vue";
import { useRouter } from 'vue-router'; // 引入路由
import { Timer, Warning } from "@element-plus/icons-vue";
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome";
import request from "@/utils/request";
import { ElNotification, ElLoading, ElEmpty, ElIcon } from "element-plus";
import {defaultImage} from "@/utils/defaultConfig";

// 初始化路由
const router = useRouter();
const backgroundImage = require("@/assets/top-image/image_2.jpg");
const noticeList = ref("美丽的东西存在于世上，这一点本身就值得感谢了。");
const blogList = ref([]);
const loading = ref(true);         // 加载状态
const showError = ref(false);      // 错误状态标识
const errorMessage = ref("");      // 错误信息
const loaded = ref(false);         // 用于动画触发

// 确定媒体类型（图片或视频）
const getMediaType = computed(() => {
  return (item) => {
    if (!item.picture) return 'image'; // 处理空地址情况
    const ext = item.picture.toLowerCase().split('.').pop();
    return ['mp4', 'webm', 'ogg'].includes(ext) ? 'video' : 'image';
  };
});

// 跳转到详情页
const navigateToDetail = (item) => {
  if (item.id) {
    router.push({
      name: 'FrontDetail',
      params: { type: 'blog', id: item.id }
    })
  } else {
    ElNotification.warning({
      title: '提示',
      message: '该博客缺少必要的标识信息，无法查看详情'
    });
  }
};

// 加载数据
const loadData = async () => {
  // 重置状态
  loading.value = true;
  showError.value = false;
  errorMessage.value = "";
  loaded.value = false;

  try {
    const res = await request.get('/blog');
    if (res.code === '200') {
      blogList.value = res.data || [];
      // 数据加载完成后触发动画
      setTimeout(() => {
        loaded.value = true;
      }, 100);
    } else {
      showError.value = true;
      errorMessage.value = res.msg || '获取博客列表失败';
      ElNotification.error({
        title: '加载失败',
        message: errorMessage.value
      });
    }
  } catch (error) {
    console.error('数据加载失败:', error);
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
@keyframes slideDown {
  from {
    opacity: 0;
    transform: translateY(-100%);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(100%);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes dot-flash {
  0%, 20% { opacity: 0; }
  50% { opacity: 1; }
  100% { opacity: 0; }
}

.top-banner {
  animation: slideDown 0.8s ease-out forwards;
}

.noticeBox {
  color: #e6e6e6;
  font-family: "华文新魏", serif;
  font-size: 25px;
}

/* 加载状态样式 */
.loading-container {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 50px 0;
}

/* 错误状态样式 */
.error-container {
  text-align: center;
  padding: 50px 20px;
  color: #F56C6C;
}

.error-icon {
  font-size: 48px;
  margin-bottom: 20px;
}

.error-text {
  font-size: 16px;
  margin-bottom: 20px;
}

/* 空状态样式 */
.empty-state {
  padding: 50px 0;
  display: flex;
  justify-content: center;
}

/* 主内容样式 */
.mainContent-box {
  width: 80%;
  margin: 0 auto 30px;
  opacity: 0;
  transition: opacity 0.5s ease-out;
}

.mainContent-box.slide-up {
  animation: slideUp 0.5s ease-out forwards;
}

/* 为每个卡片添加不同的动画延迟，创造层次感 */
.mainContent-box:nth-child(1) { animation-delay: 0.1s; }
.mainContent-box:nth-child(2) { animation-delay: 0.2s; }
.mainContent-box:nth-child(3) { animation-delay: 0.3s; }
.mainContent-box:nth-child(4) { animation-delay: 0.4s; }

.mainContent-card {
  display: flex;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  overflow: hidden;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

/* 添加悬停效果，提升用户体验 */
.mainContent-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.15);
}

.left-mainContent {
  flex: 1;
  min-width: 300px;
  padding: 15px;
}

.right-mainContent {
  flex: 2;

  padding: 20px;
}

.card-image, .card-video {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 4px;
}

.card-title {
  margin-bottom: 15px;
  color: #333;
  font-size: 20px;
}

.card-description {
  margin-bottom: 15px;
  color: #666;
}

.card-publishTime, .card-dataContent {
  margin-bottom: 10px;
  color: #999;
  font-size: 14px;
}

.card-badge {
  margin-top: 20px;
  display: flex;
  gap: 10px;
}

.image-slot {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  background-color: #f5f7fa;
  color: #c0c4cc;
}

.dot {
  animation: dot-flash 1.5s infinite;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .mainContent-box {
    width: 90%;
  }

  .mainContent-card {
    flex-direction: column;
    height: auto;
  }

  .left-mainContent, .right-mainContent {
    width: 100%;
    min-width: 100%;
    padding: 10px;
  }

  .left-mainContent .el-image {
    height: 200px;
  }

  .card-image, .card-video {
    height: 150px;
  }

  .card-title {
    font-size: 18px;
  }
}
</style>
