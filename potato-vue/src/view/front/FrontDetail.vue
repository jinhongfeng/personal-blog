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
            <el-icon><Eye /></el-icon>
            <span>浏览量: {{ contentDetail.pageview }}</span>
          </span>
        </div>

      </div>

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

      <!-- 正文（Markdown解析） -->
      <div v-if="markdownError" class="markdown-error">
        <el-icon class="error-icon"><Warning /></el-icon>
        <p>{{ markdownError }}</p>
      </div>
      <div v-else class="blog-body markdown-body" v-html="parsedMarkdown"></div>
    </div>
  </div>
</template>

<script setup>
import {computed, onMounted, ref, watch} from "vue";
import {useRoute, useRouter} from 'vue-router';
import {ArrowLeft, Eye, Timer, User, Warning} from "@element-plus/icons-vue";
import request from "@/utils/request";
import {ElIcon, ElLoading, ElNotification} from "element-plus";
import hljs from 'highlight.js/lib/core';
import 'highlight.js/styles/github.css';
import {marked} from "marked";
import DOMPurify from 'dompurify';
// 导入所需语言模块
import javascript from 'highlight.js/lib/languages/javascript';
import python from 'highlight.js/lib/languages/python';
import java from 'highlight.js/lib/languages/java';
import xml from 'highlight.js/lib/languages/xml';
import css from 'highlight.js/lib/languages/css';
import json from 'highlight.js/lib/languages/json';
import '@kangc/v-md-editor/lib/theme/style/github.css';

// 注册代码高亮语言
hljs.registerLanguage('javascript', javascript);
hljs.registerLanguage('python', python);
hljs.registerLanguage('java', java);
hljs.registerLanguage('html', xml);
hljs.registerLanguage('css', css);
hljs.registerLanguage('json', json);

// 状态管理
const contentDetail = ref({});
const loading = ref(true);
const showError = ref(false);
const errorMessage = ref("");
const parsedMarkdown = ref("");
const markdownError = ref(null);

// 路由相关
const route = useRoute();
const router = useRouter();

// 初始化Markdown解析器
const initMarked = () => {
  try {
    if (!marked || typeof marked.setOptions !== 'function') {
      throw new Error('Marked库未正确加载');
    }

    marked.setOptions({
      renderer: new marked.Renderer(),
      highlight: function (code, lang) {
        if (lang && hljs.getLanguage(lang)) {
          return hljs.highlight(code, { language: lang }).value;
        }
        return hljs.highlightAuto(code).value;
      },
      breaks: true,
      gfm: true,
      sanitize: false,
      smartLists: true,
      smartypants: true
    });
    return true;
  } catch (error) {
    console.error('初始化Markdown解析器失败:', error);
    markdownError.value = '内容解析器初始化失败，请刷新页面重试';
    return false;
  }
};

// 初始化Markdown解析器
const isMarkedInitialized = initMarked();

// 从路由参数获取内容ID
const contentId = computed(() => route.params.id);

// 从路由路径获取内容类型（关键修改点）
// 路由配置应为: /front/detail/:type/:id
const contentType = computed(() => {
  const type = route.params.type;
  // 验证类型是否合法
  if (['blog', 'diary', 'note'].includes(type)) {
    return type;
  }
  // 默认类型，可根据实际需求调整
  return 'blog';
});

// 内容类型中文名称，用于错误提示
const contentTypeName = computed(() => {
  const names = {
    blog: '博客',
    diary: '日记',
    note: '笔记'
  };
  return names[contentType.value] || '内容';
});

// 确定API前缀
const apiPrefix = computed(() => `/${contentType.value}`);

// 确定列表页路由名称
const listRouteName = computed(() => {
  const routes = {
    blog: 'FrontBlog',
    diary: 'FrontDiary',
    note: 'FrontNote'
  };
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

// Markdown解析函数
const parseMarkdownContent = (content) => {
  if (!isMarkedInitialized) {
    return '';
  }

  try {
    markdownError.value = null;

    if (typeof content !== 'string') {
      throw new Error('无效的内容格式，期望字符串');
    }

    // 处理空内容
    if (!content.trim()) {
      return `<div class="empty-content">该${contentTypeName.value}暂无详细信息</div>`;
    }

    // 处理换行和特殊字符
    const processedContent = content
        .replace(/\r\n/g, '\n')
        .replace(/\n/g, '\n\n');

    // 先解析为HTML，再进行XSS过滤
    const rawHtml = marked.parse(processedContent);
    return DOMPurify.sanitize(rawHtml);
  } catch (err) {
    markdownError.value = `内容解析失败: ${err.message}`;
    return '';
  }
};

// 监听内容变化，重新解析Markdown
watch(() => contentDetail.value.information, (newVal) => {
  parsedMarkdown.value = parseMarkdownContent(newVal);
}, { immediate: true });

// 更新浏览量
const updatePageview = async () => {
  if (!contentId.value) return;

  try {
    await request.put(`${apiPrefix.value}/${contentId.value}/pageview`);
    // 成功后本地更新浏览量显示
    if (contentDetail.value.pageview) {
      contentDetail.value.pageview++;
    }
  } catch (error) {
    console.error('更新浏览量失败:', error);
    // 浏览量更新失败不影响主要功能
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
  markdownError.value = null;

  try {
    // 根据内容类型调用对应的接口
    const detailRes = await request.get(`${apiPrefix.value}/${contentId.value}`);
    if (detailRes.code === '200') {
      contentDetail.value = detailRes.data || {};
      // 解析Markdown内容
      parsedMarkdown.value = parseMarkdownContent(contentDetail.value.information);
      // 调用更新浏览量的接口
      await updatePageview();
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
  loadContentDetail();
});
</script>

<style scoped>
/* 样式保持不变，与之前的详情页样式一致 */
.detail-container {
  max-width: 1000px;
  margin: 0 auto;
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

/* 加载和错误状态样式 */
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

/* 内容样式 */
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

/* Markdown内容样式 */
.blog-body {
  margin-bottom: 40px;
  animation: fadeIn 0.5s ease-in-out;
}

.empty-content {
  text-align: center;
  padding: 50px 0;
  color: #999;
  font-style: italic;
}

.markdown-error {
  color: #ff4d4f;
  padding: 15px;
  background-color: #fff1f0;
  border-radius: 4px;
  margin: 10px 0 40px;
  border: 1px solid #ffe3e3;
  display: flex;
  align-items: center;
  gap: 10px;
}

/* Markdown解析后的样式 */
.markdown-body {
  font-size: 16px;
  line-height: 1.8;
  color: #333;
  padding: 0 10px;
  text-align: left;
}

.markdown-body img {
  max-width: 100%;
  margin: 20px auto;
  display: block;
  border-radius: 4px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: all 0.3s;
  object-fit: cover;
  min-height: 50px;
}

.markdown-body img:hover {
  transform: scale(1.01);
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

  .markdown-body {
    font-size: 15px;
    line-height: 1.7;
  }
}
</style>
