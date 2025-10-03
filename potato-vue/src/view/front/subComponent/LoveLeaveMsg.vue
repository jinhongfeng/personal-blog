<template>
  <div class="leaveMsg-container">
    <!-- 标题区域 -->
    <div class="leaveMsg-header">
      <font-awesome-icon icon="pencil-square" class="header-icon" />
      <span class="header-title">留言</span>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="state-container loading-container">
      <el-loading :fullscreen="false" text="加载中..."></el-loading>
    </div>

    <!-- 错误状态 -->
    <div v-else-if="showError" class="state-container error-container">
      <el-icon class="error-icon"><Warning /></el-icon>
      <p class="error-text">{{ errorMessage }}</p>
      <el-button type="primary" @click="loadData" :loading="isRetryLoading">重试</el-button>
    </div>

    <!-- 正常内容区域 -->
    <div v-else>
      <!-- 输入区域 -->
      <div class="input-wrapper">
        <textarea
            ref="msgTextarea"
            class="leaveMsg-textarea"
            v-model="inputContent"
            placeholder="写下点什么..."
            rows="6"
            @input="adjustHeight"
            @keydown.enter.exact="handleSubmit"
            :disabled="isSubmitting"></textarea>
        <!-- 卡通插画装饰 -->
        <div class="cartoon-decoration">
          <img :src="illustrationImage" alt="卡通装饰图" />
        </div>
      </div>

      <!-- 操作栏 -->
      <div class="action-bar">
        <div class="icon-group">
          <font-awesome-icon icon="face-smile" class="action-icon" @click="openEmoji" />
          <font-awesome-icon icon="image" class="action-icon" @click="openImageUpload" />
        </div>
        <button class="submit-btn" @click="handleSubmit" :disabled="!inputContent.trim() || isSubmitting">
          <span v-if="!isSubmitting">提交</span>
          <span v-else>提交中...</span>
        </button>
      </div>

      <!-- 空数据状态 -->
      <div v-if="comments.length === 0" class="state-container empty-container">
        <el-empty description="暂无留言，快来抢沙发吧～"></el-empty>
      </div>

      <!-- 留言列表 -->
      <div class="comments-section" v-else>
        <div class="comments-header">
          <span>评论： | {{ comments.length }} 条留言</span>
        </div>
        <div
            class="comment-item"
            v-for="(comment) in sortedComments"
            :key="comment.id"
            :data-id="comment.id">
          <div class="comment-meta">
            <img :src="defaultList.avatar"
                 alt="用户头像"
                 class="comment-avatar">
            <div class="user-info">
              <span class="username">{{ comment.author || defaultList.author }}</span>
              <span class="comment-time">{{ formatTime(comment.publishTime) }}</span>
            </div>
          </div>
          <div class="comment-content">
            <div class="comment-wrapper">
              {{ comment.content }}
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import {ref, onMounted, nextTick, watch, computed} from 'vue';
import dayjs from 'dayjs';
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome';
import { Warning } from "@element-plus/icons-vue";
import {ElNotification, ElLoading, ElEmpty, ElIcon} from "element-plus";
import request from '@/utils/request';
import {formatTime} from "../../../utils/defaultConfig";

// 资源引用
const illustrationImage = require('@/assets/love/chahua.jpg');

// 响应式数据
const inputContent = ref('');
const comments = ref([
  {
    id: 1,
    publish: '2025-06-19',
    content: '长长久久，百年好合(｡･ω･｡)',
    author: '陌生人'
  }
]);

// 默认头像和用户名
const defaultList = ref({
  avatar: require('@/assets/love/love_default.jpg'),
  author: '陌生人'
});

// 状态管理
const loading = ref(true);         // 初始加载状态
const showError = ref(false);      // 错误状态标识
const errorMessage = ref("");      // 错误信息
const isSubmitting = ref(false);   // 提交状态
const isRetryLoading = ref(false); // 重试加载状态

// 文本域引用
const msgTextarea = ref(null);

// 计算属性：按发布时间倒序排序，确保最新的在最上面
const sortedComments = computed(() => {
  return [...comments.value].sort((a, b) => {
    return new Date(b.publish) - new Date(a.publish);
  });
});

// 自动调整文本域高度
const adjustHeight = (e) => {
  const textarea = e?.target || msgTextarea.value;
  if (!textarea) return;

  // 重置高度以获取正确的scrollHeight
  textarea.style.height = 'auto';
  // 设置新高度，限制最大高度
  const newHeight = Math.min(textarea.scrollHeight, 300);
  textarea.style.height = `${newHeight}px`;
};


// 提交留言逻辑
const handleSubmit = async () => {
  const content = inputContent.value.trim();
  if (!content || isSubmitting.value) return;

  try {
    isSubmitting.value = true;

    // 显示加载状态
    const loadingInstance = ElLoading.service({
      lock: false,
      text: '提交中...',
      background: 'rgba(255, 255, 255, 0.7)'
    });

    const newComment = {
      content: content,
      author: defaultList.value.author
    };

    // 提交到后端
    const res = await request.post('/lovemessage', newComment);

    if (res.code === '200') {
      // 重新加载数据确保与服务器同步
      await loadData();

      // 清空输入框
      inputContent.value = '';

      // 重置文本域高度
      await nextTick(() => adjustHeight({target: msgTextarea.value}));

      // 显示成功通知
      ElNotification.success({
        title: '提交成功',
        message: '留言已发布'
      });
    } else {
      throw new Error(res.msg || '提交失败，请稍后重试');
    }

    loadingInstance.close();
  } catch (error) {
    ElNotification.error({
      title: '提交失败',
      message: error.message || '留言发布失败，请稍后重试'
    });
  } finally {
    isSubmitting.value = false;
    ElLoading.service().close();
  }
};

// 表情功能
const openEmoji = () => {
  ElNotification.info({
    title: '提示',
    message: '表情功能待实现～'
  });
};

// 图片上传功能
const openImageUpload = () => {
  ElNotification.info({
    title: '提示',
    message: '图片上传功能待实现～'
  });
};

// 加载数据
const loadData = async () => {
  // 重置状态
  if (!loading.value) {
    isRetryLoading.value = true;
  }
  showError.value = false;
  errorMessage.value = "";

  try {
    const loadingInstance = ElLoading.service({
      lock: false,
      text: '加载中...',
      background: 'rgba(255, 255, 255, 0.7)'
    });

    const resLoveMessage = await request.get('/lovemessage');

    if (resLoveMessage.code === '200') {
      comments.value = resLoveMessage.data || [];
    } else {
      throw new Error(resLoveMessage.msg || '获取数据失败！');
    }

    loadingInstance.close();
  } catch (err) {
    errorMessage.value = err.message || '获取留言列表失败';
    showError.value = true;

    ElNotification.error({
      title: '加载失败',
      message: errorMessage.value
    });
  } finally {
    loading.value = false;
    isRetryLoading.value = false;
    ElLoading.service().close();
  }
};

// 监听输入内容变化，限制最大长度
watch(inputContent, (newVal) => {
  const maxLength = 500;
  if (newVal.length > maxLength) {
    inputContent.value = newVal.substring(0, maxLength);
    ElNotification.warning({
      title: '提示',
      message: `留言不能超过${maxLength}个字符`
    });
  }
});

// 页面加载时初始化
onMounted(() => {
  // 初始化文本域高度
  nextTick(() => adjustHeight({target: msgTextarea.value}));
  // 加载留言数据
  loadData();
});
</script>

<style scoped>
/* 样式保持不变 */
.leaveMsg-container {
  width: 60%;
  margin: 40px auto;
  font-family: "Microsoft YaHei", sans-serif;
  color: #333;
  padding: 0 15px;
}

/* 标题样式 */
.leaveMsg-header {
  display: flex;
  align-items: center;
  font-size: 28px;
  color: #ff9800; /* 橙色主题色 */
  margin-bottom: 20px;
  animation: fadeIn 0.5s ease;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.header-icon {
  font-size: 32px;
  margin-right: 10px;
}

.header-title {
  font-family: "楷体", serif;
  letter-spacing: 2px;
}

/* 状态容器通用样式 */
.state-container {
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(10px);
  border-radius: 15px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  padding: 20px;
  width: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  box-sizing: border-box;
  margin-bottom: 20px;
}

/* 加载状态样式 */
.loading-container {
  min-height: 200px;
}

/* 错误状态样式 */
.error-container {
  text-align: center;
  padding: 60px 20px;
  min-height: 200px;
}

.error-icon {
  font-size: 64px;
  margin-bottom: 25px;
  color: #F56C6C;
  transition: transform 0.3s ease;
}

.error-container:hover .error-icon {
  transform: scale(1.1);
}

.error-text {
  font-size: 18px;
  margin-bottom: 30px;
  color: #333;
  line-height: 1.6;
}

.error-container .el-button {
  padding: 10px 24px;
  font-size: 16px;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.error-container .el-button:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 12px rgba(232, 21, 38, 0.2);
}

/* 空状态样式 */
.empty-container {
  min-height: 200px;
  padding: 40px 20px;
}

/* 输入区域 */
.input-wrapper {
  position: relative;
  background: #fff;
  border: 1px solid #eee;
  border-radius: 12px;
  overflow: hidden;
  transition: box-shadow 0.3s ease, border-color 0.3s ease;
}

.input-wrapper:focus-within {
  box-shadow: 0 0 0 2px rgba(110, 87, 224, 0.2);
  border-color: #6e57e0;
}

.leaveMsg-textarea {
  width: 100%;
  border: none;
  outline: none;
  resize: none;
  padding: 16px;
  font-size: 14px;
  line-height: 1.8;
  box-sizing: border-box;
  min-height: 120px;
  transition: background-color 0.3s ease;
}

.leaveMsg-textarea:disabled {
  background-color: #f9f9f9;
  cursor: not-allowed;
}

/* 卡通装饰 */
.cartoon-decoration {
  position: absolute;
  bottom: 10px;
  right: 10px;
  width: 120px;
  opacity: 0.9;
  pointer-events: none; /* 防止遮挡输入 */
  transition: opacity 0.3s ease;
}

.leaveMsg-textarea:focus ~ .cartoon-decoration {
  opacity: 0.6;
}

.cartoon-decoration img {
  width: 100%;
  height: auto;
}

/* 操作栏 */
.action-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 12px;
  margin-bottom: 30px;
}

.icon-group {
  display: flex;
  gap: 18px;
}

.action-icon {
  font-size: 22px;
  color: #999;
  cursor: pointer;
  transition: color 0.3s, transform 0.2s;
}

.action-icon:hover {
  color: #ff9800;
  transform: scale(1.1);
}

.submit-btn {
  background: #6e57e0; /* 紫色按钮 */
  color: #fff;
  border: none;
  padding: 10px 24px;
  border-radius: 8px;
  cursor: pointer;
  transition: background 0.3s, transform 0.2s, box-shadow 0.3s;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.submit-btn:hover:not(:disabled) {
  background: #5c48c2;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(110, 87, 224, 0.3);
}

.submit-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

/* 留言列表 */
.comments-section {
  margin-top: 40px;
  animation: fadeIn 0.5s ease;
}

.comments-header {
  font-size: 16px;
  color: #666;
  margin-bottom: 16px;
  padding-bottom: 8px;
  border-bottom: 1px solid #eee;
  display: flex;
  align-items: center;
}

.comment-item {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
  transition: transform 0.2s ease, box-shadow 0.2s ease;
  animation: slideUp 0.3s ease;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.comment-item:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.comment-meta {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
}

.comment-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  margin-right: 12px;
  object-fit: cover;
  border: 2px solid #fff;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.user-info {
  display: flex;
  flex-direction: column;
}

.username {
  font-weight: bold;
  font-size: 15px;
  margin-bottom: 2px;
}

.comment-time {
  font-size: 12px;
  color: #999;
  display: flex;
  align-items: center;
  gap: 4px;
}

.comment-content {
  line-height: 1.6;
  margin-bottom: 12px;
  color: #555;
  padding-left: 60px; /* 与头像对齐 */
  text-align: left;
}

.comment-wrapper {
  background-color: #fff;
  padding: 12px 15px;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

/* 响应式适配 */
@media (max-width: 768px) {
  .leaveMsg-container {
    width: 100%;
    margin: 20px auto;
  }

  .cartoon-decoration {
    width: 80px;
  }

  .state-container {
    width: 100%;
    min-width: auto;
  }

  .error-icon {
    font-size: 48px;
  }

  .error-text {
    font-size: 16px;
  }

  .comment-content {
    padding-left: 0;
    margin-top: 10px;
  }

  .leaveMsg-header {
    font-size: 24px;
  }
}
</style>
