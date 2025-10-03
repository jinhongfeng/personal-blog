<template>
  <el-container class="full-container">
    <!-- 全屏背景图 -->
    <div class="concern-background"
         :style="{
          backgroundImage: `url('${backgroundImage}')`,
          backgroundRepeat: 'no-repeat',
          backgroundPosition: 'center center',
          backgroundSize: 'cover'
        }"></div>
    <!-- 顶部导航 -->
    <el-header>
      <FrontHeader />
    </el-header>

    <el-main>
      <div class="concern-container">
        <!-- 外层容器控制卡片宽度 -->
        <div class="card-wrapper">
          <!-- 加载状态 -->
          <div v-if="loading" class="state-container loading-container">
            <el-loading :fullscreen="false" text="加载中..."></el-loading>
          </div>

          <!-- 错误状态 -->
          <div v-else-if="showError" class="state-container error-container">
            <el-icon class="error-icon"><Warning /></el-icon>
            <p class="error-text">{{ errorMessage }}</p>
            <el-button type="primary" @click="loadData">重试</el-button>
          </div>

          <!-- 空数据状态 -->
          <div v-else-if="isEmptyState" class="state-container empty-container">
            <el-empty description="暂无个人信息数据"></el-empty>
            <el-button type="primary" @click="loadData" style="margin-top: 40px;">重新加载</el-button>
          </div>

          <!-- 正常内容 -->
          <el-card v-else class="concern-card">
            <!-- 内部flex容器实现布局 -->
            <div class="card-content-wrapper">
              <!-- 左侧 - 个人头像及基本信息 -->
              <div class="concern-left-card">
                <!-- 头像 -->
                <div class="avatar-container">
                  <el-avatar :size="100" :src="avatarUrl" class="personal-avatar" />
                </div>
                <!-- 简介 -->
                <el-form :model="personInfo" label-width="60px"
                         class="form-label-white custom-form-spacing" size="small">
                  <el-form-item label="姓名" class="tight-form-item">
                    <span>{{ personInfo.nickname }}</span>
                  </el-form-item>
                  <el-form-item label="年龄" class="tight-form-item">
                    <span>{{ personInfo.age }}</span>
                  </el-form-item>
                  <el-form-item label="爱好" class="tight-form-item hobby-item">
                    <el-tag v-for="(item, index) in personInfo.hobby" :key="index"
                            type="danger" class="hobby-tag">
                      {{ item }}
                    </el-tag>
                  </el-form-item>
                  <el-form-item label="邮箱" class="tight-form-item">
                    <span>{{ personInfo.email }}</span>
                  </el-form-item>
                </el-form>
                <!-- 导航 -->
                <div class="navigation-container">
                  <div class="concern-navigation"
                       v-for="(item, index) in navigation" :key="index"
                       @click="activeNavIndex = index"
                       :class="{ 'active-nav': activeNavIndex === index }">
                    <div>
                      <span>{{ item.title }}</span>
                    </div>
                  </div>
                </div>
              </div>
              <!-- 右侧 -->
              <div class="concern-right-card">
                <!-- 右侧上方图片 -->
                <div class="right-image-container">
                  <img :src="currentNavImage" alt="导航相关图片" class="nav-related-image">
                </div>
                <!-- 右侧下方内容区域 -->
                <div class="right-content-container">
                  <div v-if="activeNavIndex === 0" class="nav-content">
                    <h3>首页内容</h3>
                    <p v-for="(homepage, index) in homepageContent" :key="index">{{ homepage }}</p>
                  </div>
                  <div v-if="activeNavIndex === 1" class="nav-content">
                    <h3>个人简介</h3>
                    <p v-for="(paragraph, index) in personalProfile" :key="index" class="left-content">{{ paragraph }}</p>
                  </div>
                  <div v-if="activeNavIndex === 2" class="nav-content">
                    <h3>我的散文</h3>
                    <p v-for="(prose, index) in myProse" :key="index"  class="left-content">{{ prose }}</p>
                  </div>
                  <div v-if="activeNavIndex === 3" class="nav-content">
                    <h3>我的技能</h3>
                    <p v-for="(skill, index) in mySkill" :key="index">{{ skill }}</p>
                  </div>
                  <div v-if="activeNavIndex === 4" class="nav-content">
                    <h3>我读的书</h3>
                    <div class="media-scroll-container">
                      <div class="media-slider">
                        <div class="media-item" v-for="(item, index) in readBook" :key="item.id || index"
                             @click="handleLinkClick(item.url)">
                          <el-image :src="item.image" class="media-image" fit="cover"
                                    lazy :alt="item.title"></el-image>
                          <div class="media-title">{{ item.title }}</div>
                        </div>
                      </div>
                    </div>
                  </div>
                  <div v-if="activeNavIndex === 5" class="nav-content">
                    <h3>看的电影</h3>
                    <div class="media-scroll-container">
                      <div class="media-slider">
                        <div class="media-item" v-for="(item, index) in readFilm" :key="item.id || index"
                             @click="handleLinkClick(item.url)">
                          <el-image :src="item.image" class="media-image"
                                    fit="cover" lazy :alt="item.title"></el-image>
                          <div class="media-title">{{ item.title }}</div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </el-card>
        </div>
      </div>
    </el-main>
  </el-container>
</template>

<script setup>
import FrontHeader from "@/components/FrontHeader.vue";
import {ref, watch, onMounted, computed} from "vue";
import { Warning } from "@element-plus/icons-vue";
import request from '@/utils/request';
import {ElLoading, ElMessage, ElEmpty, ElIcon} from 'element-plus';

// 状态管理
const loading = ref(true);         // 加载状态
const showError = ref(false);      // 错误状态标识
const errorMessage = ref("");      // 错误信息

// 头像URL
const avatarUrl = ref(require("@/assets/personAvatar.jpg"));
const backgroundImage = require("@/assets/blueSkies.png")
// 个人信息
const personInfo = ref({
  id: null,
  nickname: '',
  age: '',
  hobby: [],
  email: ''
})
// 首页内容
const homepageContent = ref([])
// 个人简介
const personalProfile = ref([])
// 我的散文
const myProse = ref([])
// 我的技能
const mySkill = ref([])
// 读的书籍
const readBook = ref([])
// 读的电影
const readFilm = ref([])

// 导航数据
const navigation = ref([
  {title: '首页', image: require('@/assets/concern/personalPage.jpg')},
  {title: '个人简介', image: require('@/assets/concern/personalPage_2.png')},
  {
    title: '我的散文',
    image: 'https://cdn.yuanshikong.net/NewTab/wallpaper/itab/6486aed810f2b458c8669130.jpe?imageMogr2/thumbnail/450x300'
  },
  {title: '我的技能', image: require('@/assets/concern/personalPage_3.webp')},
  {
    title: '我读的书',
    image: 'https://cdn.yuanshikong.net/NewTab/wallpaper/itab/6453248f62269728166a721a.jpe?imageMogr2/thumbnail/450x300'
  },
  {
    title: '看的电影',
    image: 'https://cdn.yuanshikong.net/NewTab/wallpaper/itab/645324c962269728166a7246.jpe?imageMogr2/thumbnail/450x300'
  },
])

// 活跃的导航索引
const activeNavIndex = ref(0);

// 当前导航对应的图片
const currentNavImage = ref(navigation.value[0].image);

// 判断是否为空数据状态
const isEmptyState = computed(() => {
  // 检查主要数据是否都为空
  return !personInfo.value.id &&
      !homepageContent.value.length &&
      !personalProfile.value.length &&
      !myProse.value.length &&
      !mySkill.value.length &&
      !readBook.value.length &&
      !readFilm.value.length;
});

// 处理爱好字符串分割
const handleHobbySplit = (hobbyStr) => {
  if (!hobbyStr || typeof hobbyStr !== 'string' || hobbyStr.trim() === '') {
    return [];
  }
  return hobbyStr.split('，').filter(item => item.trim() !== '');
};

// 辅助函数：解析JSON数据
const parseJsonData = (data, defaultVal) => {
  if (!data) return [...defaultVal];
  if (typeof data === 'string') {
    try {
      const parsed = JSON.parse(data.replace(/\\/g, ''));
      return Array.isArray(parsed) ? parsed : [parsed];
    } catch (e) {
      console.warn('JSON解析失败:', e);
      return [data];
    }
  }
  if (Array.isArray(data)) {
    return [...data];
  }
  return [data];
}

// 加载数据库数据
const loadData = async () => {
  try {
    // 重置状态
    loading.value = true;
    showError.value = false;
    errorMessage.value = "";

    const loadingInstance = ElLoading.service({
      lock: false,
      text: '加载中...',
      background: 'rgba(0, 0, 0, 0.7)'
    });

    // 1. 查询sys_concern获取用户基本信息
    const sysRes = await request.get('/concern')
    if (sysRes.code !== '200') {
      throw new Error('获取用户信息失败: ' + (sysRes.msg || '未知错误'))
    }

    const sysData = Array.isArray(sysRes.data) ? sysRes.data[0] : sysRes.data;

    // 2. 从concern_info查询所有关联数据，通过theme区分类型
    const allConcernsRes = await request.get('/concern_info');
    if (allConcernsRes.code === '200') {
      const allConcerns = allConcernsRes.data || [];

      // 3. 按theme字段筛选出书籍和电影
      readBook.value = allConcerns.filter(item => item.theme === 'book');
      readFilm.value = allConcerns.filter(item => item.theme === 'film');
    }

    // 4. 加载其他基本信息
    personInfo.value.id = sysData.id;
    personInfo.value.nickname = sysData.nickname || '未知用户';
    personInfo.value.age = sysData.age || '';
    personInfo.value.email = sysData.email || '未填写';
    personInfo.value.hobby = handleHobbySplit(sysData.hobby || '');
    avatarUrl.value = sysData.avatar || require("@/assets/personAvatar.jpg");

    // 5. 加载其他内容数据
    homepageContent.value = parseJsonData(sysData.homedesc, [
      '欢迎访问我的个人主页，这里展示了我的个人信息和兴趣爱好。',
      '通过导航可以浏览不同分类的内容'
    ]);
    personalProfile.value = parseJsonData(sysData.profile, [
      "'small potato' 意为小人物。通信工程小破硕。",
      "性格介于开朗和沉默之间，热爱生活",
      "如果自己还不够强大，就得把自己藏起来不断学习。学历、阅历、能力都是铺垫，在无人问津的地方打磨，在万众瞩目的地方出现。不必急着向世界证明什么，时间会筛选出真正有价值的东西。等风来不如追风去，不必焦虑路远，反正步步都算数。"
    ]);
    myProse.value = parseJsonData(sysData.prose, [
      '写什么呢？写我不知上进，写我贪玩勿学，写我缺乏志向，写我蹉跎二十余载，也未学一技之长傍身。终日违心陪笑，苟且于市井之间；写我多年八方奔跑，跌跌撞撞一事无成，还是写花有重开日，人无再少年？写春去秋来，鬓边未觉添霜，却已不敢再提"少年"二字；写曾以为山海可平，后来才懂，世间多的是"求不得"与"已失去"。',
    ]);
    mySkill.value = parseJsonData(sysData.skill, [
      '编程语言：JavaScript、Vue、Python',
      '设计工具：PS、PR'
    ]);

    loadingInstance.close();
    loading.value = false;
  } catch (error) {
    console.error('加载失败：', error);
    loading.value = false;
    showError.value = true;
    errorMessage.value = error.message || '数据加载异常，请稍后重试';

    // 关闭加载动画
    ElLoading.service().close();

    // 显示错误消息
    ElMessage.error(errorMessage.value);

    // 清空数据
    clearData();
  }
};

// 清空数据
const clearData = () => {
  personInfo.value = {
    id: null,
    nickname: '',
    age: '',
    hobby: [],
    email: ''
  };
  homepageContent.value = [];
  personalProfile.value = [];
  myProse.value = [];
  mySkill.value = [];
  readBook.value = [];
  readFilm.value = [];
};

// 设置默认数据
// eslint-disable-next-line no-unused-vars
const setDefaultData = () => {
  avatarUrl.value = require("@/assets/personAvatar.jpg");
  personInfo.value = {
    id: 1,
    name: 'POTATO',
    age: 24,
    hobby: ['篮球', '健身', '看书', '敲代码'],
    email: '2474921543@qq.com'
  };
  homepageContent.value = [
    '欢迎访问我的个人主页，这里展示了我的个人信息和兴趣爱好。',
    '通过导航栏可以浏览不同分类的内容，包括我的散文、技能、读书和观影记录等。'
  ];
  personalProfile.value = [
    "'small potato' 意为小人物。今年24岁，喜欢篮球、健身、看书、敲代码。通信工程小破硕。",
    "性格介于开朗和沉默之间，热爱生活，对新鲜事物充满好奇心，始终保持学习的热情。偶尔高兴，偶尔沉闷。",
    "闲的时候，简简单单写个代码。"
  ];
  myProse.value = [
    '写什么呢？写我不知上进，写我贪玩勿学，写我缺乏志向，写我蹉跎二十余载，也未学一技之长傍身，终日违心陪笑，苟且于市井之间，' +
    '写我多年八方奔跑，跌跌撞撞一事无成，还是写花有重开日，人无再少年？',
    '如果自己还不够强大，就得把自己藏起来不断学习。学历阅历能力都是铺垫，在无人问津的地方练、在万众瞩目的地方出现，时间就是最好的证明。'
  ];
  mySkill.value = [
    '我掌握的技能包括编程、设计等，不断学习新的知识和技术。',
    '编程语言：JavaScript、Vue、Python、C++、java、Springboot、matlab',
    '设计工具：PS、PR'
  ];
  readBook.value = [];
  readFilm.value = [];
};

// 监听导航切换，更新图片
watch(activeNavIndex, (newIndex) => {
  currentNavImage.value = navigation.value[newIndex].image;
});

// 处理链接点击
const handleLinkClick = (url) => {
  if (url && typeof window !== 'undefined') {
    window.open(url, '_blank');
  }
};

// 页面加载时获取数据
onMounted(() => {
  loadData();
});
</script>

<style scoped>

html, body {
  height: 100%;
  margin: 0;
  padding: 0;

}

.full-container {
  display: flex;
  flex-direction: column;
}

.concern-background {
  background-size: cover;
  position: fixed;
  width: 100%;
  height: 100%;
  top: 0;
  left: 0;
  z-index: -2;
}

.el-main {
  padding: 15px !important;
  flex-grow: 1;
}

.concern-container {
  width: 100%;
  display: flex;
  justify-content: center;
  box-sizing: border-box;
  overflow-y: hidden;
}

.card-wrapper {
  width: 90%;
  max-width: 1400px;
  display: flex;
  flex-direction: column;
  margin-top: 10vh;
}

/* 状态容器通用样式 */
.state-container {
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(10px);
  border-radius: 15px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  padding: 20px;
  min-height: 400px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

/* 加载状态样式 */
.loading-container {
  min-height: 500px;
}

/* 错误状态样式 */
.error-container {
  text-align: center;
  padding: 80px 20px;
  color: #F56C6C;
  min-height: 50vh;
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
.empty-container {
  min-height: 50vh;
  padding: 80px 20px;
}

.concern-card {

  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(10px);
  border-radius: 15px;
  overflow-y: auto;
  padding: 0 !important;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
}

.card-content-wrapper {
  display: flex;
  flex-direction: row;
  width: 100%;
  flex-grow: 1;
}

.avatar-container {
  margin-bottom: 10px;
  position: relative;
  display: flex;
  justify-content: center;
}

.concern-left-card {
  min-width: 260px;
  width: 30%;
  border-right: 2px solid #F7F9FE;
  user-select: none;
  padding: 15px 10px;
  display: flex;
  flex-direction: column;
  align-items: center;
  box-sizing: border-box;

  .concern-navigation {
    width: 95%;
    min-width: 250px;

    margin: 10px 2.5%;
    height: 36px;
    display: flex;
    justify-content: center;
    align-items: center;
    background-color: white;
    cursor: pointer;
    border-radius: 8px;
    transition: all 0.3s ease;
    font-size: 15px; /* 略微增大字体 */
    padding: 0 10px; /* 增加内边距，给文字更多空间 */

    &:hover {
      background-color: #F4E1C0;
      border: 1px solid #666666;
    }
  }

  .active-nav {
    background-color: #E6F4EA;
    border: 1px solid #4CAF50;
    font-weight: bold;
  }
}

.custom-form-spacing {
  width: 90%;

  ::v-deep .el-form-item {
    margin-bottom: 1px !important;
    padding: 0 !important;
    line-height: 1.0;
  }

  ::v-deep .el-form-item__label {
    padding-right: 8px !important;
  }

  ::v-deep .el-form-item__content {
    line-height: 1.0;
  }
}

.form-label-white {
  ::v-deep .el-form-item__label {
    color: #333 !important;
    font-weight: 500;
    font-size: 13px;
  }

  ::v-deep .el-form-item__content {
    color: #555;
    font-size: 13px;
  }
}

.concern-right-card {
  width: 70%;
  padding: 15px;
  display: flex;
  flex-direction: column;
  gap: 15px;
  box-sizing: border-box;
}



.right-image-container {
  width: 100%;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  flex-shrink: 0;
}

.nav-related-image {
  width: 100%;
  height: 220px;
  object-fit: cover;
  transition: transform 0.3s ease;

  &:hover {
    transform: scale(1.02);
  }
}



.right-content-container {
  background-color: white;
  padding: 15px;
  border-radius: 8px;
  flex-grow: 1;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow-y: auto; /* 内容区自身可滚动 */

}

.nav-content {
  color: #333;

  h3 {
    color: #2c3e50;
    margin-bottom: 10px;
    padding-bottom: 8px;
    border-bottom: 1px solid #eee;
    font-size: 16px;
  }

  p {
    line-height: 1.4;
    margin-bottom: 8px;
    font-size: 14px;

    display: flex;
  }
}

.tight-form-item {
  margin-bottom: 3px !important;
  padding: 0 !important;
  height: 24px;
  display: flex;
  align-items: center;
}

::v-deep .tight-form-item .el-form-item__label {
  padding-right: 6px !important;
  font-size: 12px !important;
  width: 50px !important;
}

::v-deep .tight-form-item .el-form-item__content {
  line-height: 1.0 !important;
  font-size: 12px !important;
  padding: 0 !important;
  margin: 0 !important;
}

.hobby-item {
  height: auto !important;
  min-height: 24px;
  align-items: flex-start;
  padding-top: 2px !important;
}

.hobby-tag {
  margin: 0 3px 2px 0 !important;
  height: 20px !important;
  line-height: 20px !important;
  font-size: 11px !important;
  padding: 0 6px !important;
}

/* 媒体滑动容器样式 */
.media-scroll-container {
  overflow-x: auto; /* 允许横向滚动 */
  overflow-y: hidden; /* 隐藏纵向滚动 */
  padding: 10px 0;
  margin: 10px 0;
  -webkit-overflow-scrolling: touch; /* 移动端平滑滚动 */
  cursor: pointer;
}

/* 滑动轨道样式 */
.media-scroll-container::-webkit-scrollbar {
  height: 6px; /* 滚动条高度 */
}

.media-scroll-container::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 10px;
}

.media-scroll-container::-webkit-scrollbar-thumb {
  background: #ccc;
  border-radius: 10px;
}

.media-scroll-container::-webkit-scrollbar-thumb:hover {
  background: #aaa;
}

/* 滑动内容容器 */
.media-slider {
  display: flex; /* 横向排列 */
  gap: 10px; /* 项目间距 */
  padding: 5px 0;
  min-height: 150px; /* 确保容器高度足够 */
}

/* 媒体项样式 */
.media-item {
  flex: 0 0 150px;
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
  transition: transform 0.2s ease;
}

.media-item:hover {
  transform: translateY(-3px);
}

/* 图片样式 */
.media-image {
  width: 80%;
  height: 120px;
  border-radius: 6px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  margin-bottom: 10px;
}
.left-content {
  display: flex;
  justify-content: left;
  align-items: center;
}
/* 标题样式 */
.media-title {
  font-size: 14px;
  color: #333;
  text-align: center;
  line-height: 1.4;
  max-width: 100%;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  padding: 0 5px;
}
@media (max-width: 1300px) {
  .card-wrapper{
    padding: 0;
    margin-top: 0;
  }

}

/* 小屏幕右侧区域占满宽度 */
@media (max-width: 992px) {
  .concern-right-card {
    width: 100% !important;
    padding: 20px;
  }
  .nav-related-image {
    height: 180px;
  }
  .card-content-wrapper {
    flex-direction: column;
  }
  .concern-left-card {
    width: 100% !important;
    border-right: none;
    border-bottom: 2px solid #F7F9FE; /* 小屏幕底部加边框区分 */
  }

  /* 小屏幕导航横向排列 - 增加宽度 */
  .concern-left-card .concern-navigation {
    width: 45%; /* 从30%增加到45%，每行显示2个更宽的导航项 */
    margin: 5px 2.5%; /* 调整间距 */
    display: inline-flex;
    height: 40px;
    font-size: 15px;
  }

  .navigation-container {
    width: 100%;
    text-align: center; /* 导航项居中 */
  }
}

/* 响应式调整 */
@media (max-width: 768px) {
  .concern-right-card {
    width: 100% !important;

  }
  .nav-related-image {
    height: 180px;
  }
  .card-content-wrapper {
    flex-direction: column;
  }
  .concern-left-card {
    width: 100% !important;
    border-right: none;
    border-bottom: 2px solid #F7F9FE; /* 小屏幕底部加边框区分 */
  }

  /* 小屏幕导航横向排列 - 增加宽度 */
  .concern-left-card .concern-navigation {
    width: 45%; /* 从30%增加到45%，每行显示2个更宽的导航项 */
    margin: 5px 2.5%; /* 调整间距 */
    display: inline-flex;
    height: 40px;
    font-size: 15px;
  }

  .navigation-container {
    width: 100%;
    text-align: center; /* 导航项居中 */
  }
}

/* 超小屏幕导航调整 */
@media (max-width: 576px) {
  .concern-right-card {
    width: 100% !important;
    padding: 20px;
  }
  .nav-related-image {
    height: 180px;
  }
  .card-content-wrapper {
    flex-direction: column;
  }
  .concern-left-card {
    width: 100% !important;
    border-right: none;
    border-bottom: 2px solid #F7F9FE; /* 小屏幕底部加边框区分 */
  }

  /* 小屏幕导航横向排列 - 增加宽度 */
  .concern-left-card .concern-navigation {
    width: 45%; /* 从30%增加到45%，每行显示2个更宽的导航项 */
    margin: 5px 2.5%; /* 调整间距 */
    display: inline-flex;
    height: 40px;
    font-size: 15px;
  }

  .navigation-container {
    width: 100%;
    text-align: center; /* 导航项居中 */
  }
}

</style>
