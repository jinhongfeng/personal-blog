<template>
  <div class="album-container">
    <!-- 装饰性背景元素 -->
    <div class="background-decoration"></div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <el-loading :fullscreen="false" text="加载中..."></el-loading>
    </div>

    <!-- 错误状态 -->
    <div v-else-if="showError" class="error-container">
      <el-icon class="error-icon"><Warning /></el-icon>
      <p class="error-text">{{ errorMessage }}</p>
      <el-button type="primary" @click="fetchAlbumData">重试</el-button>
    </div>

    <!-- 数据加载完成但为空 -->
    <div v-else-if="albumList.length === 0" class="empty-state">
      <el-empty description="暂无相册数据"></el-empty>
    </div>

    <!-- 数据加载完成且有数据 -->
    <div v-else>
      <!-- 相册索引 -->
      <div class="travel-album">
        <el-button
            v-for="(item, index) in albumList" :key="item.id"
            class="travel-btn"
            :style="getButtonStyle(item, index)"
            :class="{ active: currentAlbum === index}"
            @click="handleAlbumChange(index)">
          <span>{{ item.title }}</span>
          <span class="photo-count">{{ item.imageList.length }}</span>
        </el-button>
      </div>

      <!-- 内容 -->
      <div class="album-box" :key="currentAlbum">
        <!-- 标题 -->
        <div class="album-title">
          <h1>{{ albumList[currentAlbum]?.title || '未知相册' }}</h1>
          <div class="title-divider"></div>
          <p>共 {{ albumList[currentAlbum]?.imageList?.length || 0 }} 张照片</p>
        </div>

        <!-- 图片 -->
        <div class="album-picture" v-if="albumList[currentAlbum]?.imageList?.length > 0">
          <el-card class="album-card"
                   v-for="(img, indexImage) in albumList[currentAlbum].imageList"
                   :key="img.id">
            <div class="card-content">
              <!-- 图片容器 -->
              <div class="image-container">
                <el-image  alt="相册图片"
                           :src="img.imageUrl"
                           class="album-image"
                           show-progress
                           :preview-src-list="albumList[currentAlbum].imageList.map(img => img.imageUrl)"
                           :initial-index="indexImage"
                           :preview-teleported="true"
                           :show-fullscreen-btn="true"
                           :zoom-rate="1.2">
                  <template #error>
                    <div class="image-error">
                      <el-icon><Picture /></el-icon>
                    </div>
                  </template>
                </el-image>
              </div>
              <!-- 图片下方文字 -->
              <div class="album-descInfo">
                <div class="descInfo-title">
                  <span>{{ img.information || '无描述' }}</span>
                </div>
                <div class="descInfo-desc">
                  <span>拍摄日期: {{ formatDate(img.publishTime) }}</span>
                </div>
              </div>
            </div>
          </el-card>
        </div>

        <!-- 空状态 -->
        <div class="empty-state" v-else>
          <el-empty description="该相册暂无照片"></el-empty>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { Picture, Warning } from "@element-plus/icons-vue";
import request from '@/utils/request';
import { ElLoading, ElNotification } from "element-plus";

// 状态管理
const currentAlbum = ref(0);
const albumList = ref([]);
const loading = ref(true);
const showError = ref(false);      // 错误状态标识
const errorMessage = ref("");      // 错误信息

// 页面加载时获取数据
onMounted(() => {
  fetchAlbumData();
});

// 从API获取相册数据
const fetchAlbumData = async () => {
  try {
    // 重置状态
    loading.value = true;
    showError.value = false;
    errorMessage.value = "";

    // 获取所有相册列表
    const albumRes = await request.get('/travel');


    // 检查响应是否有效
    if (!albumRes.data || !Array.isArray(albumRes.data)) {
      if (albumRes.data?.records && Array.isArray(albumRes.data.records)) {
        albumRes.data = albumRes.data.records;
      } else {
        throw new Error('无效的相册数据格式，期望数组');
      }
    }

    // 为每个相册获取对应的图片列表
    const albumsWithImages = await Promise.all(
        albumRes.data.map(async (album) => {
          if (!album || !album.id) {
            return null;
          }

          try {
            const imageRes = await request.get('/travelImage/listByTravelId', {
              params: { travelId: album.id }
            });

            const sortedImages = (imageRes.data || []).sort((a, b) => a.sort - b.sort);
            return { ...album, imageList: sortedImages };
          } catch (imgErr) {
            return { ...album, imageList: [] };
          }
        })
    );

    // 更新数据
    albumList.value = albumsWithImages.filter(album => album);

    // 确保currentAlbum在有效范围内
    currentAlbum.value = albumList.value.length > 0 ? 0 : -1;
  } catch (err) {
    showError.value = true;
    errorMessage.value = '加载相册失败，请稍后重试';
    ElNotification.error({
      title: '加载失败',
      message: errorMessage.value
    });
    albumList.value = [];
    currentAlbum.value = -1;
  } finally {
    loading.value = false;
  }
};

// 相册切换处理函数
const handleAlbumChange = (index) => {
  if (index >= 0 && index < albumList.value.length) {
    currentAlbum.value = index;
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }
};

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return '未知日期';

  try {
    const date = new Date(dateString);
    return `${date.getFullYear()}.${(date.getMonth() + 1).toString().padStart(2, '0')}.${date.getDate().toString().padStart(2, '0')}`;
  } catch (err) {
    return '日期格式错误';
  }
};

// 根据状态获取按钮样式
const getButtonStyle = (item, index) => {
  // 选中状态
  if (currentAlbum.value === index) {
    return {
      backgroundColor: '#e81526', // 红色
      color: 'white'
    };
  }
  // 未选中状态
  return {
    backgroundColor: item.astyle, // 保持原色
    color: 'white'
  };
};
</script>

<style scoped>
:root {
  --white: #ffffff;
  --black: #333333;
  --articleFontColor: #666666;
  --transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  --shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  --hover-color: #ff69b4; /* 粉色 - 鼠标悬浮颜色 */
}

/* 相册索引按钮样式 */
.travel-btn {
  height: 45px;
  padding: 0 18px;
  border-radius: 25px;
  border: none;
  font-weight: 500;
  transition: var(--transition);
  display: inline-flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
}

/* 未选中状态的悬停效果 */
.travel-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
  background-color: #ff69b4 !important;
}

/* 照片数量样式 */
.photo-count {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 20px;
  height: 20px;
  background: rgba(255, 255, 255, 0.3);
  border-radius: 50%;
  margin-left: 8px;
  font-size: 12px;
  padding: 0;
}

/* 相册索引容器 */
.travel-album {
  margin: 20px 5%;
  padding: 20px;
  border-radius: 12px;
  box-shadow: var(--shadow);
  display: flex;
  justify-content: flex-start;
  flex-wrap: wrap;
  overflow-x: auto;
  scrollbar-width: thin;
}

/* 状态样式优化 */
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 50vh;
  padding: 20px;
}

/* 错误状态样式 */
.error-container {
  text-align: center;
  padding: 80px 20px;
  color: #F56C6C;
  min-height: 50vh;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

.error-icon {
  font-size: 64px;
  margin-bottom: 25px;
  transition: transform 0.3s ease;
}

.error-container:hover .error-icon {
  transform: scale(1.1);
}

.error-text {
  font-size: 18px;
  margin-bottom: 30px;
  max-width: 500px;
  color: var(--articleFontColor);
  line-height: 1.6;
}

.error-container .el-button {
  padding: 10px 24px;
  font-size: 16px;
  border-radius: 8px;
  transition: var(--transition);
}

.error-container .el-button:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 12px rgba(232, 21, 38, 0.2);
}

/* 空状态样式 */
.empty-state {
  padding: 80px 20px;
  text-align: center;
  min-height: 50vh;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

.album-container {
  position: relative;
  min-height: 100vh;
  padding-bottom: 30px;
}

.background-decoration {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-size: cover;
  z-index: -2;
  filter: brightness(0.2);
}

.album-box {
  width: 90%;
  margin: 0 auto;
  padding: 20px;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 12px;
  box-shadow: var(--shadow);
  animation: fadeIn 0.5s ease-out;
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

.album-title {
  text-align: center;
  margin-bottom: 30px;
  padding-bottom: 10px;
  position: relative;
}

.album-title h1 {
  font-family: "华文新魏", serif;
  color: var(--black);
  font-size: 2.2rem;
  margin-bottom: 10px;
  letter-spacing: 0.5px;
}

.title-divider {
  width: 80px;
  height: 3px;
  background: #e73c7e;
  margin: 0 auto 15px;
  border-radius: 2px;
}

.album-picture {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

.album-card {

  border-radius: 10px;
  overflow: hidden;
  transition: var(--transition);
  border: none;
  box-shadow: 0 3px 10px rgba(0, 0, 0, 0.08);
  display: flex;
  flex-direction: column;
}

.album-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.12);
  border-color: transparent !important;
}

.card-content {
  height: 100%;
  display: flex;
  flex-direction: column;
}

/* 新增图片容器样式 */
.image-container {
  position: relative;
  width: 100%;
  aspect-ratio: 16/9; /*  3/4、16/9  */
  overflow: hidden;
}

.album-image {
  width: 100%;
  height: 100%;
  cursor: pointer;
  /* 确保图片保持比例并填充容器，超出部分裁剪 */
  object-fit: cover;
  transition: var(--transition);
  border-radius: 8px 8px 0 0;
}

.album-card:hover .album-image {
  transform: scale(1.03);
}

/* 确保描述区域高度固定，不会被图片挤压 */
.album-descInfo {
  padding: 15px;
  background: var(--white);
  min-height: 80px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.descInfo-title {
  font-family: "华文新魏", serif;
  font-size: 18px;
  color: var(--black);
  margin-bottom: 8px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.descInfo-desc {
  font-size: 14px;
  color: var(--articleFontColor);
}

.image-error {
  width: 100%;
  height: 100%;
  background: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #ccc;
}

.image-error .el-icon {
  font-size: 36px;
}

/* 响应式调整 */
@media (min-width: 1600px) {
  .album-picture {
    grid-template-columns: repeat(4, 1fr);
  }
  .album-card {
    min-height: 260px;
  }
}
@media (max-width: 1300px) {
  .album-picture {
    grid-template-columns: repeat(3, 1fr);
  }
  .album-card {
    min-height: 280px;
  }
}
@media (max-width: 1024px) {
  .album-picture {
    grid-template-columns: repeat(2, 1fr);
  }
  .album-card {
    min-height: 300px;
  }
}

@media (max-width: 768px) {
  .album-picture {
    grid-template-columns: 1fr;
  }

  .album-title h1 {
    font-size: 1.8rem;
  }

  .travel-btn {
    padding: 0 14px;
    font-size: 14px;
  }

  .album-card {
    min-height: 300px;
  }

  .error-icon {
    font-size: 48px;
  }

  .error-text {
    font-size: 16px;
  }
}

@media (max-width: 480px) {
  .album-box, .travel-album {
    width: 100%;
    margin-left: 1%;
    margin-right: 1%;
    padding: 8px;
  }

  .album-title h1 {
    font-size: 1.5rem;
  }

  .album-card {
    min-height: 270px;
  }
}
</style>
