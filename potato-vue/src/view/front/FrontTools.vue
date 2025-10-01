<template>
  <div class="tools-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1 class="page-title">我的工具集合</h1>
      <p class="page-desc">一站式管理和使用各种实用小工具</p>
    </div>

    <!-- 工具分类列表 -->
    <div class="tools-container">
      <div v-for="(category, index) in subTitleList" :key="index" class="category-section">
        <!-- 分类标题与自定义分割线 -->
        <div class="category-header">
          <h2 class="category-title">{{ category.title }}</h2>
          <div class="custom-divider"></div>
        </div>

        <!-- 工具卡片网格 -->
        <div class="tools-grid">
          <div
              v-for="(tool, toolIndex) in category.dataList"
              :key="toolIndex"
              class="tool-card"
              @click="navigateTo(tool.address)"
          >
            <!-- 工具图标 -->
            <div class="tool-icon">
              <el-image
                  :src="tool.img || defaultToolImg"
                  :alt="tool.title"
                  fit="cover"
                  :preview-teleported="true"
              />
            </div>
            <!-- 工具名称 -->
            <div class="tool-name">{{ tool.title }}</div>
          </div>

          <!-- 空状态提示 -->
          <div v-if="!category.dataList || category.dataList.length === 0" class="empty-state">
            <p>暂无工具</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import {defaultToolImg} from "@/utils/defaultConfig";

// 工具数据
const toolsList = [
  { title: '随机选餐', img: require('@/assets/tool/randomMeals.png'), address: '/front/randomMeals' },
  { title: '英语学习', img: require('@/assets/tool/studyEnglish.svg'), address: '/front/studyEnglish' },
  { title: '计时器', img: '', address: '' },
  { title: '待办清单', img: '', address: '' },
  { title: '天气查询', img: '', address: '' },
  { title: '计算器', img: '', address: '' },
  { title: '二维码生成', img: '', address: '' },
];

const gamesList = [
  { title: '贪吃蛇', img: '', address: '' },
  { title: '2048', img: '', address: '' },
  { title: '扫雷', img: '', address: '' },
];

const appToolsList = [
  { title: '文件转换', img: '', address: '' },
  { title: '截图工具', img: '', address: '' },
  { title: '笔记应用', img: '', address: '' },
];

// 分类数据
const subTitleList = ref([
  { title: '小工具', dataList: toolsList },
  { title: '小游戏', dataList: gamesList },
  { title: '应用工具', dataList: appToolsList },
]);

// 路由跳转
const router = useRouter();
const navigateTo = (address) => {
  if (address) {
    router.push(address);
  }
};
</script>

<style scoped>
/* 基础样式 */
.tools-page {
  min-height: 100vh;
  padding: 2rem;
  background-color: #f5f7fa;
  font-family: 'Inter', system-ui, sans-serif;
}

/* 页面标题 */
.page-header {
  text-align: center;
  margin-bottom: 3rem;
}

.page-title {
  font-size: 2.5rem;
  font-weight: 700;
  color: #2c3e50;
  margin-bottom: 0.5rem;
}

.page-desc {
  font-size: 1.1rem;
  color: #6c7a89;
}

/* 工具容器 */
.tools-container {
  max-width: 1200px;
  margin: 0 auto;
}

/* 分类标题与分割线 */
.category-section {
  margin-bottom: 3rem;
}

.category-header {
  display: flex;
  align-items: center;
  margin-bottom: 1.5rem;
}

.category-title {
  font-size: 1.5rem;
  font-weight: 600;
  color: #34495e;
  margin-right: 1rem;
  white-space: nowrap;
}

.custom-divider {
  flex-grow: 1;
  height: 2px;
  background: linear-gradient(to right, #3498db, #2ecc71);
  border-radius: 1px;
}

/* 工具网格布局 */
.tools-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  gap: 1.5rem;
}

/* 工具卡片 */
.tool-card {
  background-color: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.05);
  padding: 1.5rem 1rem;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  transition: all 0.3s ease;
  cursor: pointer;
}

.tool-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(52, 152, 219, 0.15);
}

/* 工具图标 */
.tool-icon {
  width: 80px;
  height: 80px;
  border-radius: 12px;
  overflow: hidden;
  margin-bottom: 1rem;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.08);
}

/* 工具名称 */
.tool-name {
  font-size: 1rem;
  font-weight: 500;
  color: #2c3e50;
}

/* 空状态 */
.empty-state {
  grid-column: 1 / -1;
  text-align: center;
  padding: 2rem;
  color: #95a5a6;
  background-color: rgba(255, 255, 255, 0.7);
  border-radius: 12px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .page-title {
    font-size: 2rem;
  }

  .tools-grid {
    grid-template-columns: repeat(auto-fill, minmax(140px, 1fr));
  }

  .tool-icon {
    width: 60px;
    height: 60px;
  }
}
</style>
