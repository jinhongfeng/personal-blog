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
              @click="navigateTo(tool.link)"
          >
            <!-- 工具图标 -->
            <div class="tool-icon">
              <el-image
                  :src="tool.icon || defaultToolImg"
                  :alt="tool.title"
                  fit="cover"
                  :preview-teleported="true"
                  :style="{ background: `${tool.astyle}`|| 'none' }"
                  class="tool-image"
              />
            </div>
            <!-- 工具名称信息 -->
            <div class="tool-information">
              <div class="name-wrapper">
                <h2 class="tool-name">{{ tool.title }}</h2>
              </div>

              <div class="desc-wrapper">
                <p class="tool-desc">{{ tool.description }}</p>
              </div>

            </div>
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
import {onMounted, ref} from 'vue';
import {defaultToolImg} from "@/utils/defaultConfig";
import {ElNotification} from "element-plus";
import request from "@/utils/request";

const errorMessage = ref("");
// 工具数据
const toolsList = ref([
  { title: '计时器', img: '', link: '', description: '' },
  { title: '待办清单', img: '', link: '' },
  { title: '天气查询', img: '', link: '' },
  { title: '计算器', img: '', link: '' },
  { title: '二维码生成', img: '', link: '' },
]);

const websiteList = ref([]);

const blogList = ref([]);

// 分类数据 更改时后台上传字段也要修改
const subTitleList = ref([
  { title: '小工具', dataList: toolsList },
  { title: '一些网站', dataList: websiteList },
  { title: '博客', dataList: blogList },
]);

// 路由跳转
const navigateTo = (link) => {
  window.open(link)
};
const loadData = async () => {
  try {
    const res = await request.get('/apps');
    if (res.code === '200') {
      toolsList.value = res.data.filter(it => it.category === 'toolsList') || []
      websiteList.value = res.data.filter(it => it.category === 'websiteList') || []
      blogList.value = res.data.filter(it => it.category === 'blogList') || []
    }
  } catch (error) {
    errorMessage.value = '无法连接到服务器，请检查网络';
    ElNotification.error({
      title: '网络错误',
      message: errorMessage.value
    });
  }
}
onMounted(() => {
  loadData();
})
</script>

<style scoped>
/* 基础样式 */
.tools-page {
  min-height: 100vh;
  padding: 1.5rem;
  background-color: #f5f7fa;
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
  margin-bottom: 1.5rem;
}

.category-header {
  display: flex;
  align-items: center;
  margin-bottom: 1rem;
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
  grid-template-columns: repeat(4, 1fr);
  margin: 0 4vw 0 8vw;
  gap: 1rem;
}

/* 工具卡片 */
.tool-card {
  background-color: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.05);
  padding: 0.8rem 0.8rem;
  display: flex;
  align-items: center;
  transition: all 0.3s ease;
  cursor: pointer;
  overflow: hidden;
}

.tool-card:hover {
  box-shadow: 0 8px 25px rgba(52, 152, 219, 0.15);
  background: #5B6FF1;
}

/* 工具图标 */
.tool-icon {
  width: 80px;
  height: 80px;
  border-radius: 100%;
  overflow: hidden;
  padding: 0.2rem;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.08);
  flex-shrink: 0;
  transition: all 0.3s ease;

  background-color: #f0f2f5;
  .tool-image {
    display: flex;
    justify-content: center;
    align-content: center;
  }
}

.tool-card:hover .tool-icon {
  width: 0;
  opacity: 0;

}

/* 工具信息 */
.tool-information {
  text-align: left;
  margin: 0 6px;
  flex-grow: 1;
  transition: all 0.3s ease;
  position: relative;
}

.tool-card:hover .tool-information {
  margin-left: 0;
  text-align: left;
}

.name-wrapper {
  position: relative;
  overflow: hidden;
  height: 24px;
}
/* 工具名称 */
.tool-name {
  font-size: 1rem;
  color: rgb(0, 0, 0);
  font-family: "黑体",serif;
  margin: 0;
  /* --- 单行文本截断 --- */
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;

  /* --- 核心动画样式 --- */
  position: absolute;
  left: 0;
  max-width: 100%;
  transition: max-width 0.3s ease, color 0.3s ease;

}

.tool-card:hover .tool-name {
  color: #fff;

  /* --- 移除截断限制，显示全部文本 --- */
  display: block;
  -webkit-line-clamp: none;
  overflow: visible;
}

.desc-wrapper {
  height: 40px;
  position: relative;
  overflow: hidden;
}
/* 工具描述 */
.tool-desc {
  font-size: 0.8rem;
  color: #7f7c7c;
  font-family: "宋体", serif;
  margin: 0;
  padding-right: 2px;

  /* --- 核心动画样式 --- */
  position: absolute;
  width: 100%;
  transition: max-height 0.6s ease, color 0.3s ease;



  /* --- 多行文本截断 --- */
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2; /* 显示的行数 */

  overflow: hidden;
  text-overflow: ellipsis;
  height: auto;
}

.tool-card:hover .tool-desc {
  color: rgba(255, 255, 255, 0.8);

  /* --- 移除截断限制，显示全部文本 --- */
  display: block;
  -webkit-line-clamp: none;
  overflow: visible;
  height: auto;
  max-height: 500px;
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
@media (max-width: 1300px) {
  .page-title {
    font-size: 2rem;
  }

  .tools-grid {
    grid-template-columns: repeat(3, 1fr);
  }

  .tool-icon {
    width: 60px;
    height: 60px;
  }

  .tool-card:hover .tool-icon {
    margin-right: -60px;
  }
}
@media (max-width: 768px) {
  .page-title {
    font-size: 2rem;
  }

  .tools-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .tool-icon {
    width: 60px;
    height: 60px;
  }

  .tool-card:hover .tool-icon {
    margin-right: -60px;
  }
}

@media (max-width: 600px) {
  .page-title {
    font-size: 2rem;
  }

  .tools-grid {
    grid-template-columns: repeat(1, 1fr);
  }

  .tool-icon {
    width: 60px;
    height: 60px;
  }

  .tool-card:hover .tool-icon {
    margin-right: -60px;
  }
}
</style>
