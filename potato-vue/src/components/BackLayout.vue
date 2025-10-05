<template>
  <div class="app-container">
    <el-container class="full-height">
      <!-- 头部 -->
      <el-header class="header-style">
        <!-- 左侧网站名称 -->
        <div class="top-box" @click="router.push('/front/home')">
          <h2>{{ defaultWebName }}</h2>
        </div>

        <!-- 右侧用户头像区域 -->
        <div class="user-avatar-container">
          <el-dropdown placement="bottom-end">
            <!-- 使用el-image包裹，提供更好的错误处理和占位符 -->
            <el-image
                class="user-avatar"
                :src="avatarData"
                :alt="defaultWebName || '用户头像'"
                fit="cover"
                @click.stop
            >
              <!-- 加载中占位符 -->
              <template #loading>
                <div class="image-loading">
                  <el-spinner size="20" />
                </div>
              </template>
              <!-- 加载失败占位符 -->
              <template #error>
                <div class="image-error">
                  <el-icon class="avatar-fallback-icon"><User /></el-icon>
                </div>
              </template>
            </el-image>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="router.push('/front/home')">
                  <span>前台</span>
                </el-dropdown-item>
                <el-dropdown-item @click="router.push('/back/concern')">
                  <span>个人信息</span>
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- 内容部分 -->
      <el-container class="content-container">
        <!-- 侧面栏 -->
        <el-aside class="aside-style">
          <BackAside />
        </el-aside>

        <!-- 主体内容 -->
        <el-main class="main-style">
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import {onMounted, ref, watch} from "vue";
import BackAside from "@/components/BackAside.vue";
import router from "@/router";
import {User} from "@element-plus/icons-vue";
import {ElLoading, ElMessage} from "element-plus";
import request from "@/utils/request";
import {defaultAvatar, defaultWebName} from "@/utils/defaultConfig";


const avatarData = ref('');
// 处理头像URL的标准化
const normalizeAvatarUrl = (url) => {
  // 检查是否为空
  if (!url || url.trim() === '') return '';

  // 检查是否是有效的HTTP/HTTPS链接
  if (url.startsWith('http://') || url.startsWith('https://')) {
    return url;
  }

  // 检查是否是base64格式
  if (url.startsWith('data:image/')) {
    return url;
  }

  // 否则视为无效URL
  return '';
};

const loadData = async () => {
  const loading = ElLoading.service({
    lock: false,
    text: '加载用户信息中...',
    target: '.user-avatar-container'
  });

  try {
    const res = await request.get('/concern');

    // 验证响应结构
    if (res.code === '200') {
      // 处理网站名称
      if (res.data?.nickname) {
        defaultWebName.value = res.data.nickname;
      }

      // 处理头像URL
      const rawAvatarUrl = res.data?.avatar;
      const normalizedUrl = normalizeAvatarUrl(rawAvatarUrl);

      // 如果有有效的头像URL则使用，否则使用默认头像
      avatarData.value = normalizedUrl || defaultAvatar;
    } else {
      // 接口返回成功但状态码不正确
      avatarData.value = defaultAvatar;
    }
  } catch (error) {
    ElMessage.error('加载用户信息失败，使用默认头像');
    avatarData.value = defaultAvatar;
  } finally {
    loading.close();
  }
};

// 监听头像数据变化，确保异常时使用默认头像
watch(avatarData, (newVal) => {
  if (!newVal || newVal.trim() === '') {
    avatarData.value = defaultAvatar;
  }
});

// 页面加载时获取数据
onMounted(() => {
  loadData();
});
</script>

<style scoped>

.full-height {
  height: 100vh;
  display: flex;
  flex-direction: column;
  z-index: 1;
}

/* 头部样式优化 */
.header-style {
  height: 60px !important;
  padding: 0 20px;
  background-color: #abd1e1;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  border-bottom: 1px solid #eee;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

/* 左侧网站名称样式 */
.top-box {
  height: 100%;
  display: flex;
  align-items: center;
  cursor: pointer;
  transition: transform 0.3s ease;
}

.top-box h2 {
  margin: 0;
  color: #303133;
  font-weight: 600;
}

.top-box:hover {
  transform: scale(1.01);
}

/* 右侧头像容器样式 */
.user-avatar-container {
  display: flex;
  align-items: center;
  padding: 0 10px;
  margin-right: 20px;
  position: relative;
}

/* 头像样式优化 */
.user-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  cursor: pointer;
  transition: transform 0.2s ease;
}

.user-avatar:hover {
  transform: scale(1.05);
}

/* 头像加载状态样式 */
.image-loading {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background-color: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 头像加载失败样式 */
.image-error {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background-color: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #909399;
}

.avatar-fallback-icon {
  width: 20px;
  height: 20px;
}

/* 内容区域容器 */
.content-container {
  flex: 1;
  display: flex;
  overflow: hidden;
}

/* 侧面栏样式 */
.aside-style {
  width: 60px;
  transition: width 0.3s ease;
  padding: 0 !important;
  margin: 0 !important;
}

/* 主体内容样式 */
.main-style {
  margin-left: 10vw;
  flex: 1;
}

@media (max-width: 767px) {
  .main-style {
    margin-left: 10px;
    flex: 1;
  }
  .aside-style {
    width: 30px;
    transition: width 0.3s ease;
    padding: 0 !important;
    margin: 0 !important;
  }
}

@media (min-width: 768px) and (max-width: 1023px) {
  .main-style {
    margin-left: 10px;
    flex: 1;
  }
}
</style>
