<template>
  <!-- 模板部分 -->
  <div>
    <!-- 顶部背景 -->
    <div class="top-banner"
         :style="{
          backgroundImage: `url('${backgroundImage}')`,
          backgroundRepeat: 'no-repeat',
          backgroundPosition: 'center center',
          backgroundSize: 'cover'
        }">
      <div class="circle-box">
        <div class="circle-rightLower">
          <!-- 左侧内容 -->
          <div class="circle-leftContent">
            <div class="circle-leftTitle">
              <span>{{ personalTitle }}</span>
            </div>
            <div class="circle-leftDesc">
              <span>{{ personalDesc1 }}</span>
              <p>{{personalDesc2}}</p>
            </div>
          </div>
          <!-- 右侧头像 -->
          <div class="circle-rightAvatar">
            <el-avatar
                :src="defaultAvatar"
                shape="square"
                :size="isMobile ? 60 : 80"
                :fallback="personalTitle.charAt(0)"
            />
          </div>
        </div>
      </div>
    </div>

    <!-- 加载状态 -->
    <el-skeleton
        v-if="isLoading && circleList.length === 0"
        class="loading-skeleton"
        :count="3"
        style="margin-top: 50vh; padding: 0 16px;"
    />

    <!-- 错误提示 -->
    <div v-if="error && circleList.length === 0" class="error-message">
      <p>{{ error }}</p>
      <el-button type="primary" size="small" @click="loadData()">重新加载</el-button>
    </div>

    <!-- 空状态 -->
    <div v-if="!isLoading && !error && circleList.length === 0" class="empty-state">
      <el-empty description="暂无动态内容" />
    </div>

    <!-- 主要内容 -->
    <div :style="{ marginTop: isMobile ? '38vh' : '38vh' }">
      <div class="moment-card mt-10" v-for="(item, index) in circleList" :key="item.id">
        <div class="moment-wrapper">
          <div class="moment-header">
            <el-avatar
                class="moment-avatar"
                :src="defaultAvatar"
                alt="用户头像"
                :size="40"
                :fallback="personalTitle.charAt(0)"
            />
            <div class="moment-userInfo">
              <div class="moment-username">{{ personalTitle }}</div>
              <div class="moment-time">{{ formatTime(item.publishTime) }}</div>
            </div>
          </div>

          <!-- 动态内容 -->
          <div class="moment-content">{{ item.content }}</div>

          <!-- 九宫格图片 -->
          <div v-if="item.images && item.images.length > 0"
               class="moment-imageList"
               :class="`image-count-${item.images.length}`">
            <el-image
                v-for="(img, imgIndex) in item.images"
                :key="imgIndex"
                :alt="`动态图片 ${imgIndex + 1}`"
                :src="img.imageUrl"
                class="moment-image"
                show-progress
                :preview-src-list="getImagePreviewList(item.images)"
                :initial-index="imgIndex"
                :zoom-rate="1.2"
                :max-scale="3"
                :min-scale="0.5"
                :z-index="9999"
                @error="handleImageError(img)"
                :preview-teleported="true"
                :show-fullscreen-btn="true"
            >
              <template #placeholder>
                <div class="image-placeholder">
                  <el-skeleton width="100%" height="100%" />
                </div>
              </template>
              <template #error>
                <div class="image-error">
                  <el-icon><PictureFilled /></el-icon>
                </div>
              </template>
            </el-image>
          </div>

          <!-- 点赞数量 -->
          <div class="moment-likeInfo">
            <span
                class="moment-likeIcon"
                :class="{'liked': item.hasLiked}"
                @click="toggleLike(index)"
                :disabled="item.loading"
            >
              ❤
            </span>
            <span class="moment-likeCount">{{ item.likeCount || 0 }}</span>
          </div>
        </div>
      </div>

      <!-- 加载更多 -->
      <div v-if="hasMore && !isLoading" class="load-more">
        <el-button
            type="text"
            @click="loadMoreData"
            :loading="loadingMore"
        >
          加载更多
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref, computed, onUnmounted } from "vue";
import { ElMessage, ElEmpty, ElSkeleton, ElAvatar, ElImage, ElButton, ElIcon } from "element-plus";
import { PictureFilled } from "@element-plus/icons-vue";
import request from '@/utils/request'
import {defaultAvatar, defaultImage} from "@/utils/defaultConfig";

// 响应式判断设备类型
const isMobile = computed(() => {
  return window.innerWidth < 768;
});

// 头部信息栏
const personalTitle = ref('江鸟有山夆');
const personalDesc1 = ref('君子择而后交，故寡过；');
const personalDesc2 = ref('小人交而后择，故多怨；');
const backgroundImage = require("@/assets/top-image/image_4.jpg")
// 朋友圈内容
const circleList = ref([]);
const error = ref('');
const isLoading = ref(false);
const loadingMore = ref(false);
const hasMore = ref(true);
const currentPage = ref(1);
const pageSize = ref(isMobile.value ? 6 : 10);

// 格式化时间
const formatTime = (timeStr) => {
  if (!timeStr) return '';

  const date = new Date(timeStr);
  const now = new Date();
  const diffMs = now - date;
  const diffDays = Math.floor(diffMs / (1000 * 60 * 60 * 24));

  // 最近3天内的显示相对时间
  if (diffDays < 3) {
    if (diffDays === 0) {
      const diffHours = Math.floor(diffMs / (1000 * 60 * 60));
      if (diffHours === 0) {
        const diffMinutes = Math.floor(diffMs / (1000 * 60));
        return diffMinutes < 1 ? '刚刚' : `${diffMinutes}分钟前`;
      }
      return `${diffHours}小时前`;
    }
    return `${diffDays}天前`;
  }

  // 超过3天的显示具体日期
  return new Intl.DateTimeFormat('zh-CN', {
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  }).format(date);
};

// 处理点赞
const toggleLike = async (index) => {
  const item = circleList.value[index];
  if (item.loading) return; // 防止重复点击

  item.loading = true;
  try {
    // 调用点赞API
    const response = await request.post(`/circle/${item.id}/hasCount`, {
      isLike: !item.hasLiked
    });

    if (response.code === '200') {
      if (item.hasLiked) {
        // 取消点赞
        circleList.value[index].likeCount = Math.max(0, (item.likeCount || 0) - 1);
      } else {
        // 点赞
        circleList.value[index].likeCount = (item.likeCount || 0) + 1;
      }
      item.hasLiked = !item.hasLiked;
      ElMessage.success(item.hasLiked ? '点赞成功' : '取消点赞');

      // 点赞后更新浏览量
      await updatePageview(item.id);
    } else {
      ElMessage.error(response.msg || '操作失败，请稍后重试');
    }
  } catch (err) {
    console.error('点赞操作失败:', err);
    ElMessage.error('网络错误，操作失败');
  } finally {
    item.loading = false;
  }
};

// 更新浏览量
const updatePageview = async (circleId) => {
  try {
    await request.post(`/circle/${circleId}/hasCount`);

    // 更新本地数据
    const circleItem = circleList.value.find(item => item.id === circleId);
    if (circleItem) {
      circleItem.viewCount = (circleItem.viewCount || 0) + 1;
    }
  } catch (err) {
    console.error(`更新圈子${circleId}浏览量失败:`, err);
  }
};

// 获取图片预览列表
const getImagePreviewList = (images) => {
  return images.map(img => img.imageUrl);
};

// 处理图片加载错误
const handleImageError = (img) => {
  img.imageUrl = defaultImage; // 设置默认图片
};

// 加载朋友圈数据
const loadData = async (append = false) => {
  if (append) {
    loadingMore.value = true;
  } else {
    isLoading.value = true;
    error.value = '';
  }

  try {
    // 获取主表数据（circle表）
    const resCircle = await request.get('/circle', {
      params: {
        page: currentPage.value,
        size: pageSize.value
      }
    });
    // 首先检查响应是否存在
    if (!resCircle) {
      error.value = '获取朋友圈数据失败：无响应';
      ElMessage.error(error.value);
      return;
    }

    // 检查响应代码
    if (resCircle.code !== '200') {
      error.value = resCircle.msg || `获取朋友圈数据失败：错误码 ${resCircle.code}`;
      ElMessage.error(error.value);
      return;
    }

    // 确保数据存在，并且是对象类型
    if (!resCircle.data || typeof resCircle.data !== 'object') {
      error.value = '获取朋友圈数据失败：数据格式不正确';
      ElMessage.error(error.value);
      return;
    }

    // 处理主表数据，确保records是数组
    const records = Array.isArray(resCircle.data) ? resCircle.data : [];
    const current = resCircle.data.current || 1;
    const pages = resCircle.data.pages || 1;

    const newData = records.map(item => ({
      ...item,
      loading: false,
      hasLiked: item.hasLiked || false,
      likeCount: item.likeCount || 0,
      images: [] // 初始化图片数组
    }));

    // 获取这些动态对应的图片
    if (newData.length > 0) {
      await fetchImagesForCircles(newData);
    }

    // 更新列表数据
    if (append) {
      circleList.value = [...circleList.value, ...newData];
    } else {
      circleList.value = newData;
    }

    // 检查是否还有更多数据
    hasMore.value = current < pages;
    currentPage.value = current + 1;

  } catch (err) {
    // 捕获所有可能的错误，包括网络错误
    error.value = '网络错误，无法获取朋友圈数据';
    console.error('获取朋友圈数据失败:', err);
    ElMessage.error(error.value);
  } finally {
    isLoading.value = false;
    loadingMore.value = false;
  }
};

// 加载更多数据
const loadMoreData = () => {
  if (!hasMore.value || isLoading.value || loadingMore.value) return;
  loadData(true);
};

// 获取图片数据 - 适配后端接口，每次查询单个circleId
const fetchImagesForCircles = async (circles) => {
  try {
    // 为每个圈子单独请求图片，适配后端接口要求
    for (const circle of circles) {
      // 确保circleId存在
      if (circle.id) {
        const response = await request.get('/circleImage/listByCircleId', {
          params: {
            circleId: circle.id  // 传递单个circleId，符合后端要求
          }
        });
        if (response.code === '200' && Array.isArray(response.data)) {
          // 按sort字段排序
          circle.images = response.data.sort((a, b) => (a.sort || 0) - (b.sort || 0));
        }
      }
    }
  } catch (err) {
    console.error('获取图片数据失败:', err);
    ElMessage.warning('部分图片加载失败');
  }
};

// 监听滚动加载更多
let scrollTimeout = null;
const handleScroll = () => {
  if (isLoading.value || loadingMore.value || !hasMore.value) return;

  // 防抖动处理
  if (scrollTimeout) clearTimeout(scrollTimeout);

  scrollTimeout = setTimeout(() => {
    const scrollTop = document.documentElement.scrollTop || document.body.scrollTop;
    const clientHeight = document.documentElement.clientHeight || window.innerHeight;
    const scrollHeight = document.documentElement.scrollHeight || document.body.scrollHeight;

    // 滚动到接近底部时加载更多
    if (scrollTop + clientHeight >= scrollHeight - 300) {
      loadMoreData();
    }
  }, 200);
};

// 页面加载时获取数据
onMounted(() => {
  loadData();
  window.addEventListener('scroll', handleScroll);

  // 监听窗口大小变化
  window.addEventListener('resize', () => {
    pageSize.value = isMobile.value ? 6 : 10;
  });
});

// 组件卸载时清理
onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll);
  if (scrollTimeout) clearTimeout(scrollTimeout);
});
</script>

<style scoped>

.top-banner {
  height: 40vh;
  animation: slideDown 0.8s ease-out forwards;
}

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

.circle-box {
  width: 30vw;
  height: 20vh;
  z-index: 0;
  margin: auto 20px 10px auto;
}

.circle-rightLower {
  display: flex;
  justify-content: flex-end;
}

.circle-rightAvatar {
  width: auto;
  height: auto;
  margin: 0 10px;
  display: flex;
  align-items: center;
  text-align: right;
}

.circle-leftContent {
  width: auto;
  max-width: calc(100% - 100px);
  height: 20vh;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: flex-end;

  .circle-leftTitle {
    font-size: 22px;
    color: white;
    margin-bottom: 10px;
    text-align: right;
    text-shadow: 0 1px 3px rgba(0, 0, 0, 0.3);
  }

  .circle-leftDesc {
    font-size: 14px;
    color: white;
    text-align: right;
    max-width: 100%;
    margin-top: 10px;
    word-break: break-word;
    text-shadow: 0 1px 3px rgba(0, 0, 0, 0.3);
  }
}

.loading-skeleton {
  width: 80%;
  margin: 30vh auto 0;
}

.error-message {
  text-align: center;
  margin-top: 50vh;
  color: #f56c6c;
  padding: 20px;
}

.empty-state {
  text-align: center;
  margin-top: 60vh;
  padding: 20px;
}

.moment-card {
  padding: 16px;
  border-radius: 8px;
  background-color: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  margin-bottom: 16px;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
  animation: fadeIn 0.3s ease-out;
  margin-left: 10%;
  margin-right: 10%;
  .moment-wrapper {
    margin-right: 5vw;
  }
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.moment-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.moment-header {
  display: flex;
  align-items: center;
  margin-bottom: 12px;

  .moment-avatar {
    width: 48px;
    height: 48px;
    border-radius: 50%;
    object-fit: cover;
    margin-right: 12px;
  }

  .moment-userInfo {
    display: flex;
    flex-direction: column;
  }

  .moment-username {
    font-size: 16px;
    font-weight: 500;
    color: #333;
  }

  .moment-time {
    font-size: 12px;
    color: #999;
    margin-top: 2px;
  }
}

.moment-content {
  font-size: 20px;
  color: #333;
  margin-bottom: 12px;
  line-height: 1.6;
  display: flex;
  justify-content: left;
  align-items: center;
  margin-left: 4vw;
}

/* --- 九宫格图片样式 --- */

/* 基础容器样式 */
.moment-imageList {
  margin-bottom: 12px;
  margin-left: 4vw;
  display: grid;
  gap: 4px;
}

/* 所有图片的通用样式 */
.moment-image {
  cursor: pointer;
  border-radius: 4px;
  transition: transform 0.2s ease;
  padding: 2px;
}

.moment-image:hover {
  transform: scale(1.02);
}

/* --- 单张图片样式 --- */
.image-count-1 {
  height: auto;
  max-height: 2fr;
  max-width: 2fr;
}
.image-count-1 .moment-image {
  width: auto;
  height: auto;
  max-width: 30vw;
  max-height: 30vh;
  object-fit: contain;

}


/* --- 多张图片 (2-9张) 样式 --- */
.image-count-2, .image-count-3,
.image-count-4, .image-count-5, .image-count-6,
.image-count-7, .image-count-8, .image-count-9 {
  grid-template-columns: repeat(3, 1fr); /* 3列，每列等宽 */
  grid-auto-rows: 1fr;
}

.image-count-2 .moment-image,
.image-count-3 .moment-image,
.image-count-4 .moment-image,
.image-count-5 .moment-image,
.image-count-6 .moment-image,
.image-count-7 .moment-image,
.image-count-8 .moment-image,
.image-count-9 .moment-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  aspect-ratio: 1/1;
}


/* --- 图片占位符和错误状态 --- */
.image-placeholder {
  width: 100%;
  height: 100%;
  position: relative;
}

.image-error {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f5f5f5;
  color: #999;
}

.moment-likeInfo {
  display: flex;
  align-items: center;
  color: #666;
  background: #abd1e1;
  border-radius: 12px;
  font-size: 20px;
  margin-left: 4vw;
  width: 30vw;
  user-select: none;
  transition: background-color 0.2s ease;

  &:hover {
    background-color: #98c6d6;
  }

  .moment-likeIcon {
    color: #666;
    font-size: 20px;
    margin-right: 4px;
    margin-left: 20px;
    transition: all 0.2s ease;
    cursor: pointer;
  }

  .moment-likeIcon:active {
    transform: scale(1.2);
  }

  .moment-likeCount {
    margin-left: 20px;
  }

  .moment-likeIcon.liked {
    color: #f44336;
    animation: pulse 0.3s ease;
  }
}

@keyframes pulse {
  0% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.3);
  }
  100% {
    transform: scale(1);
  }
}

.load-more {
  text-align: center;
  padding: 20px 0;
}

@media (max-width: 1300px) {
  .circle-box {
    width: 80vw;
  }

  .circle-rightAvatar {
    width: 10vw;
  }

  .moment-card {
    width: 90%;
    margin: 0 auto 16px;

  }

  .moment-imageList {
    margin-left: auto; /* 在小屏幕上也居中 */
    margin-right: auto;
  }

  .moment-likeInfo {
    width: 80vw;
  }

  .moment-content {
    font-size: 16px;
  }
}

@media (max-width: 768px) {
  .circle-box {
    width: 90vw;
  }

  .circle-rightAvatar {
    width: 15vw;
  }

  .moment-card {
    width: 90%;
    margin: 0 auto 16px;

  }

  .moment-imageList {
    margin-left: auto; /* 在小屏幕上也居中 */
    margin-right: auto;
  }

  .moment-likeInfo {
    width: 80vw;
  }

  .moment-content {
    font-size: 16px;
  }
}
</style>
