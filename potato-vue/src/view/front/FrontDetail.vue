<template>
  <div class="detail-container">
    <!-- 顶部导航栏 -->
    <div class="detail-header">
      <el-button
          type="primary"
          :icon="ArrowLeft"
          @click="goBack"
          class="back-button"
          size="default"
      >
        返回列表
      </el-button>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <el-loading :fullscreen="false" text="加载内容中..."></el-loading>
    </div>

    <!-- 错误状态 -->
    <div v-else-if="showError" class="error-container">
      <el-icon class="error-icon"><Warning /></el-icon>
      <p class="error-text">{{ errorMessage }}</p>
      <el-button type="primary" @click="loadContentDetail">重试</el-button>
    </div>

    <!-- 内容展示 -->
    <div v-else class="blog-content">
      <!-- 标题区域 -->
      <div class="blog-header">
        <h1 class="blog-title">{{ contentDetail.title }}</h1>

        <div class="blog-meta">
          <span class="meta-item">
            <el-icon><User /></el-icon>
            <span>{{ contentDetail.author }}</span>
          </span>
          <span class="meta-item">
            <el-icon><Timer /></el-icon>
            <span>{{ formatDate(contentDetail.publishTime) }}</span>
          </span>
          <span class="meta-item">
            <font-awesome-icon icon="eye" />
            <span>浏览量: {{ contentDetail.pageview }}</span>
          </span>
        </div>
      </div>

      <!-- 正文 -->
      <div class="blog-body">
        <MdCatalog :editorId="id" :scrollElement="scrollElement" />
        <!-- 媒体内容（图片或视频） -->
        <div class="blog-media">
          <template v-if="getMediaType() === 'image' && contentDetail.picture">
            <el-image
                :src="contentDetail.picture"
                fit="contain"
                class="media-image"
                lazy
            >
              <template #placeholder>
                <div class="image-placeholder">加载中...</div>
              </template>
              <template #error>
                <div class="image-error">图片加载失败</div>
              </template>
            </el-image>
          </template>
          <template v-else-if="getMediaType() === 'video' && contentDetail.picture">
            <video controls class="media-video" preload="metadata">
              <source :src="contentDetail.picture" type="video/mp4">
              您的浏览器不支持视频播放
            </video>
          </template>
        </div>
        <MdPreview :id="id" :modelValue="contentDetail.information" />
      </div>
    </div>
  </div>
</template>

<script setup>
import {computed, onMounted, ref } from "vue";
import {useRoute, useRouter} from 'vue-router';
import {ArrowLeft, Timer, User, Warning} from "@element-plus/icons-vue";
import request from "@/utils/request";
import {ElNotification} from "element-plus";

import { MdPreview, MdCatalog } from 'md-editor-v3';
import 'md-editor-v3/lib/preview.css';
const id = 'preview-only';

const scrollElement = document.documentElement;

// 状态管理
const contentDetail = ref({});
const loading = ref(true);
const showError = ref(false);
const errorMessage = ref("");
// 用于动态设置预览高度
const height = ref('auto');

// 路由相关
const route = useRoute();
const router = useRouter();

// 从路由参数获取内容ID和类型
const contentId = computed(() => route.params.id);
const contentType = computed(() => {
  const type = route.params.type;
  if (['blog', 'diary', 'note'].includes(type)) {
    return type;
  }
  return 'blog';
});
const contentTypeName = computed(() => {
  const names = { blog: '博客', diary: '日记', note: '笔记' };
  return names[contentType.value] || '内容';
});
const apiPrefix = computed(() => `/${contentType.value}`);
const listRouteName = computed(() => {
  const routes = { blog: 'FrontBlog', diary: 'FrontDiary', note: 'FrontNote' };
  return routes[contentType.value] || 'FrontBlog';
});

// 确定媒体类型
const getMediaType = computed(() => {
  return () => {
    if (!contentDetail.value.picture) return 'none';
    const ext = contentDetail.value.picture.toLowerCase().split('.').pop();
    return ['mp4', 'webm', 'ogg'].includes(ext) ? 'video' : 'image';
  };
});

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return '未知时间';
  try {
    const date = new Date(dateString);
    return date.toLocaleDateString('zh-CN', {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
  } catch (error) {
    return dateString;
  }
};

// 更新浏览量
const updatePageview = async () => {
  if (!contentId.value) return;
  try {
    await request.put(`${apiPrefix.value}/${contentId.value}/pageview`);
    if (contentDetail.value.pageview) {
      contentDetail.value.pageview++;
    }
  } catch (error) {
    console.error('更新浏览量失败:', error);
  }
};

// 加载内容详情
const loadContentDetail = async () => {
  if (!contentId.value) {
    showError.value = true;
    errorMessage.value = "缺少内容ID参数";
    loading.value = false;
    return;
  }

  loading.value = true;
  showError.value = false;
  errorMessage.value = "";

  try {
    const detailRes = await request.get(`${apiPrefix.value}/${contentId.value}`);
    if (detailRes.code === '200') {
      contentDetail.value = detailRes.data || {};
      // 如果内容为空，提供一个友好的提示
      if (!contentDetail.value.information) {
        contentDetail.value.information = `好的${contentTypeName.value}，但它似乎没有任何内容。`;
      }
      await updatePageview();

      // 在内容加载后，可以根据需要调整预览高度
      height.value = 'calc(100vh - 300px)';
    } else {
      showError.value = true;
      errorMessage.value = detailRes.msg || `获取${contentTypeName.value}详情失败`;
      ElNotification.error({ title: '加载失败', message: errorMessage.value });
    }
  } catch (error) {
    console.error(`加载${contentTypeName.value}详情失败:`, error);
    showError.value = true;
    errorMessage.value = '无法连接到服务器，请检查网络';
    ElNotification.error({ title: '网络错误', message: errorMessage.value });
  } finally {
    loading.value = false;
  }
};

// 返回对应的列表页
const goBack = () => {
  router.push({ name: listRouteName.value });
};

// 页面挂载时加载数据
onMounted(() => {
  setTimeout(() => {
    loadContentDetail();
  }, 500)
});
</script>

<style scoped>

.detail-container {
  max-width: 1000px;
  margin: 7vh auto;
  padding: 20px;
}

.detail-header {
  margin-bottom: 30px;
}

.back-button {
  display: flex;
  align-items: center;
  gap: 8px;
  transition: all 0.2s;
}

.back-button:hover {
  transform: translateX(-3px);
}

.loading-container, .error-container {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 100px 0;
}

.error-icon {
  font-size: 48px;
  color: #F56C6C;
  margin-bottom: 20px;
}

.error-text {
  font-size: 16px;
  color: #F56C6C;
  margin-bottom: 20px;
}

.blog-header {
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 1px solid #eee;
}

.blog-title {
  font-size: 28px;
  color: #333;
  margin-bottom: 20px;
  line-height: 1.5;
  transition: color 0.3s;
}

.blog-title:hover {
  color: #42b983;
}

.blog-meta {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 20px;
  margin-bottom: 15px;
  color: #666;
  font-size: 14px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 5px;
}

.blog-media {
  margin-bottom: 30px;
  text-align: center;
  border-radius: 8px;
  overflow: hidden;
  background-color: #f9f9f9;
  padding: 20px;
}

.media-image {
  max-width: 100%;
  max-height: 500px;
  border-radius: 8px;
  transition: all 0.3s;
}

.media-image:hover {
  transform: scale(1.02);
}

.media-video {
  width: 100%;
  max-height: 500px;
  border-radius: 8px;
  background-color: #000;
}

.image-placeholder, .image-error {
  width: 100%;
  height: 300px;
  background-color: #f5f5f5;
  display: flex;
  justify-content: center;
  align-items: center;
  color: #999;
}


.blog-body {
  margin-bottom: 40px;
  text-align: left;
  .blog-body-category {

  }
}

:deep(.v-md-editor) {
  border: none; /* 移除边框 */
  box-shadow: none; /* 移除阴影 */
}

:deep(.v-md-editor-content) {
  padding-top: 0; /* 调整内边距 */
}

/* 响应式调整 */
@media (max-width: 768px) {
  .detail-container {
    padding: 15px;
  }
  .blog-title {
    font-size: 22px;
  }
  .blog-meta {
    gap: 10px 20px;
  }
  .media-image, .media-video {
    max-height: 300px;
  }
}
</style>
