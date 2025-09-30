<template>
  <div class="page-container">
    <!-- 背景上内容 -->
    <div class="top-banner"
         :style="{
          backgroundImage: `url('${backgroundImage}')`,
          backgroundRepeat: 'no-repeat',
          backgroundPosition: 'center center',
          backgroundSize: 'cover'
        }">
      <div class="content-center">
        <span class="pageFont">童话</span>
        <div class="background-mask">
          <span> {{ currentNotice }} </span>
        </div>
      </div>
    </div>

    <!-- 移动端导航按钮 -->
    <button class="mobile-nav-toggle" @click="toggleMobileNav" v-if="isMobile">
      <el-icon><Menu /></el-icon>
    </button>

    <!-- 内容主体 -->
    <div class="content-wrapper" :class="{ 'mobile-nav-open': mobileNavOpen }">
      <!-- 左侧 -->
      <div class="left-content">
        <!-- 个人面板 -->
        <el-card class="mt-30 person-card">
          <el-avatar :src="avatarName" :size="100" />
          <div class="firstFont">{{ webName }}</div>
          <!-- 记录 -->
          <div class="record-content">
            <!-- 记录 -->
            <div class="record-item">
              <span class="record-label">记录</span>
              <div class="record-value">{{ contentRecords }}</div>
            </div>
            <!-- 分类 -->
            <div class="record-item">
              <span class="record-label">分类</span>
              <div class="record-value">{{ contentClasses }}</div>
            </div>
            <!-- 访问量 -->
            <div class="record-item">
              <span class="record-label">访问</span>
              <div class="record-value">{{ contentVisits }}</div>
            </div>
          </div>
          <!-- 朋友圈 -->
          <el-button @click="handleAddress('/front/circle')" color="orange" round plain
                     class="circle-button">
            <font-awesome-icon icon="circle-notch" pulse/>
            <span style="margin-left: 10px">朋友圈</span>
          </el-button>
        </el-card>
        <!-- 小车 -->
        <el-card class="mt-30 car-card">
          <div class="car-container">
            <!-- 文字部分 -->
            <span class="station-text">站位</span>
            <!-- 小车动画 -->
            <CarAnimation />
          </div>
        </el-card>
        <!-- 速览 -->
        <div class="scan-container mt-30">
          <div v-for="(item, index) in navigationScan" :key="index" class="scan-item" >
            <el-card class="mt-10 scanning-blog " :style="{ background: item.bg }"
                     @click="scrollToSection(item.title)" >
              <div class="first-content ml-10">
                <span>速览</span>
              </div>
              <div class="second-content ml-10">
                <span>{{ item.title }}</span>
              </div>
              <div class="third-content ml-10" >
                <span>{{ item.desc }}</span>
              </div>
            </el-card>
          </div>
        </div>
      </div>
      <!-- 右侧 -->
      <div class="right-content">
        <!-- 头部介绍notice -->
        <div class="mt-30 dashed-border">
          <font-awesome-icon icon="bullhorn" class="animated-icon"/>
          <div class="text-content" >
            <span style="margin-left: 10px">{{ information }}</span>
          </div>
        </div>
        <!-- BLOG -->
        <div class="mt-10 content-section">
          <div class="left-justify">
            <div class="left-title">
              <el-icon color="orange" :size="32"><Grid /></el-icon>
              <span class="firstFont" id="BLOG">BLOG</span>
            </div>
            <!-- 右侧更多按钮 -->
            <div class="right-justify" @click="router.push('/front/blog')">
              <el-icon color="orange"><DArrowRight /></el-icon>
              <span style="margin-left: 10px">MORE</span>
            </div>
          </div>
          <div class="blog-container">
            <div v-for="(item, index) in blogContent" :key="index" class="mainBlog-box">
              <el-card class="mt-10 mainBlog-card" @click="navigateToDetail(item.id, 'blog')"
                       style="cursor: pointer; transition: transform 0.2s;">
                <!-- 图片 -->
                <div class="mainBlog-image">
                  <el-image :src="item.picture" class="card-img" lazy :alt="item.title"/>
                </div>
                <!-- 发布时间 -->
                <div class="mainBlog-time">
                  <el-icon size="15" color="orange"><Timer /></el-icon>
                  <span style="margin-left: 5px;">发布于 {{ item.publishTime }}</span>
                </div>
                <!-- 主题 -->
                <el-popover trigger="hover" placement="top-start" effect="dark"
                            :content="item.title" :width="200">
                  <template #reference>
                    <span class="secondFont">{{ item.title }}</span>
                  </template>
                </el-popover>
                <!-- 标签 -->
                <div class="mainBlog-badge">
                  <el-button type="danger" size="small">{{ item.author }}</el-button>
                  <el-button type="info" size="small" plain>{{ item.badge }}</el-button>
                </div>
              </el-card>
            </div>
          </div>
        </div>
        <!-- 生活倒影 -->
        <div class="mt-10 content-section">
          <div class="left-justify">
            <div class="left-title">
              <el-icon color="orange" :size="32"><Grid /></el-icon>
              <span class="firstFont" id="生活倒影">生活倒影</span>
            </div>
            <div class="right-justify" @click="router.push('/front/travel')">
              <el-icon color="orange"><DArrowRight /></el-icon>
              <span style="margin-left: 10px">MORE</span>
            </div>
          </div>
          <div class="blog-container">
            <div v-for="(item, index) in invertedContent" :key="index" class="mainBlog-box">
              <el-card class="mt-10 mainBlog-card" @click="router.push('/front/travel')"
                       style="cursor: pointer; transition: transform 0.2s;">
                <!-- 图片 -->
                <div class="mainBlog-image">
                  <el-image :src="item.coverImage" class="card-img" :alt="item.title" lazy/>
                </div>
                <!-- 发布时间 -->
                <div class="mainBlog-time">
                  <el-icon size="15" color="orange"><Timer /></el-icon>
                  <span style="margin-left: 5px;">发布于 {{ item.publishTime || '未知时间' }}</span>
                </div>
                <!-- 主题 -->
                <el-popover trigger="hover" placement="top-start" effect="dark"
                            :content="item.title" :width="200">
                  <template #reference>
                    <span class="secondFont">{{ item.title }}</span>
                  </template>
                </el-popover>

                <!-- 标签 -->
                <div class="mainBlog-badge">
                  <el-button
                      :type="item.styleType || 'danger'"
                      size="small"
                      :style="{ backgroundColor: item.astyle }">
                    {{ item.description || '生活' }}
                  </el-button>
                </div>
              </el-card>
            </div>
          </div>
        </div>
        <!-- 个人日记 -->
        <div class="mt-10 content-section">
          <div class="left-justify">
            <div class="left-title">
              <el-icon color="orange" :size="32"><Grid /></el-icon>
              <span class="firstFont" id="个人日记">个人日记</span>
            </div>
            <div class="right-justify" @click="router.push('/front/diary')">
              <el-icon color="orange"><DArrowRight /></el-icon>
              <span style="margin-left: 10px">MORE</span>
            </div>
          </div>
          <div class="blog-container">
            <div v-for="(item, index) in personalDiary" :key="index" class="mainBlog-box">
              <el-card class="mt-10 mainBlog-card" @click="navigateToDetail(item.id, 'diary')"
                       style="cursor: pointer; transition: transform 0.2s;">
                <!-- 图片 -->
                <div class="mainBlog-image">
                  <el-image :src="item.picture" class="card-img" :alt="item.title" lazy/>
                </div>
                <!-- 发布时间 -->
                <div class="mainBlog-time">
                  <el-icon size="15" color="orange"><Timer /></el-icon>
                  <span style="margin-left: 5px;">发布于 {{ item.publishTime }}</span>
                </div>
                <!-- 主题 -->
                <el-popover trigger="hover" placement="top-start" effect="dark"
                            :content="item.title" :width="200">
                  <template #reference>
                    <span class="secondFont">{{ item.title }}</span>
                  </template>
                </el-popover>
                <!-- 描述 -->
                <div class="mainBlog-badge">
                  <el-button type="danger" size="small">{{ item.author }}</el-button>
                  <el-button type="info" size="small" plain>{{ item.badge }}</el-button>

                </div>
              </el-card>
            </div>
          </div>
        </div>
        <!-- 学习笔记 -->
        <div class="mt-10 content-section">
          <div class="left-justify">
            <div class="left-title">
              <el-icon color="orange" :size="32"><Grid /></el-icon>
              <span class="firstFont" id="学习笔记">学习笔记</span>
            </div>
            <div class="right-justify" @click="router.push('/front/note')">
              <el-icon color="orange"><DArrowRight /></el-icon>
              <span style="margin-left: 10px">MORE</span>
            </div>
          </div>
          <div class="blog-container">
            <div v-for="(item, index) in learningNote" :key="index" class="mainBlog-box">
              <el-card class="mt-10 mainBlog-card" @click="navigateToDetail(item.id, 'note')"
                       style="cursor: pointer; transition: transform 0.2s;">
                <!-- 图片 -->
                <div class="mainBlog-image">
                  <el-image :src="item.picture" class="card-img" :alt="item.title" lazy/>
                </div>
                <!-- 发布时间 -->
                <div class="mainBlog-time">
                  <el-icon size="15" color="orange"><Timer /></el-icon>
                  <span style="margin-left: 5px;">发布于 {{ item.publishTime }}</span>
                </div>
                <!-- 主题 -->
                <el-popover trigger="hover" placement="top-start" effect="dark"
                            :content="item.title" :width="200">
                  <template #reference>
                    <span class="secondFont">{{ item.title }}</span>
                  </template>
                </el-popover>

                <!-- 描述 -->
                <div class="mainBlog-badge">
                  <el-button type="danger" size="small">{{ item.author }}</el-button>
                  <el-button type="info" size="small" plain>{{ item.badge }}</el-button>
                </div>
              </el-card>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import {onMounted, onUnmounted, reactive, ref, watch} from "vue";
import router from "@/router";
import {DArrowRight, Grid, Timer, Menu} from "@element-plus/icons-vue";
import CarAnimation from "@/view/front/subComponent/CarAnimation.vue";
import request from "@/utils/request";
import { ElNotification } from "element-plus";

let timer = null;
const noticeList = [
  "江南几度梅花发，人在天涯鬓已斑。",
  "朔风如解意，容易莫摧残。",
  "世界这么大，我想去看看。"
]
const currentNotice = ref(noticeList[0])

// 头像
const avatarName = ref(require('@/assets/personAvatar.jpg'))
const webName = ref('POTATO')
const contentRecords = ref(0)
const contentClasses = ref(0)
const contentVisits = ref(0)

// 右侧内容
const information = ref("我骄傲的是，从那些阴暗的人和事上吃过亏后，依旧不屑于成为那样的人。")
const backgroundImage = require("@/assets/top-image/image_3.jpg")
// 主题内容数据保存
const blogContent = ref([])
const personalDiary = ref([])
const learningNote = ref([])
const invertedContent = ref([])  // 生活倒影数据

// 速览
const navigationScan = ref([
  {title: 'BLOG', desc: '我的博客，记录所有', bg: 'linear-gradient(135deg, #23d5ab 0%, #67C23A 100%)'},
  {title: '生活倒影', desc: '记录美好生活', bg: 'linear-gradient(135deg, #e52e71 0%, #e81526 100%)' },
  {title: '个人日记', desc: '怕老年痴呆', bg: 'linear-gradient(135deg, #ff4b2b 0%, #ff9a00 100%)' },
  {title: '学习笔记', desc: '日常记录亿点点', bg: 'linear-gradient(135deg, #23a6d5 0%, #39c5bb 100%)' },
])

// 分页参数
// eslint-disable-next-line no-unused-vars
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
});

// 移动端导航状态
const mobileNavOpen = ref(false);
const isMobile = ref(false);

const loadData = async () => {
  try {
    // 只请求必要的主表数据
    const [resDiary, resNote, resBlog, resTravelMain, resConcern] = await Promise.all([
      request.get('/diary'),
      request.get('/note'),
      request.get('/blog'),
      request.get('/travel'),  // 生活倒影主表
      request.get('/concern'),
    ]);

    // 检查所有请求是否成功
    const allSuccess = [resDiary, resNote, resBlog, resTravelMain, resConcern]
        .every(res => res.code === '200');

    if (allSuccess) {
      // 处理个人信息
      avatarName.value = resConcern.data[0]?.avatar || avatarName.value;
      webName.value = resConcern.data[0]?.nickname || webName.value;

      // 处理其他内容数据
      blogContent.value = resBlog.data.splice(0, 6);
      personalDiary.value = resDiary.data.splice(0, 6);
      learningNote.value = resNote.data.splice(0, 6);

      // 处理生活倒影数据（主表+副表关联）
      invertedContent.value = await processTravelData(resTravelMain.data);

      // 更新统计数据
      updateContentStatistics();
    }
  } catch (error) {
    ElNotification.error({
      title: '加载失败',
      message: '数据加载过程中出现错误，请稍后重试'
    });
  }
}

// 根据travelId获取图片列表
const getImagesByTravelId = async (travelId) => {
  try {
    // 通过params传递travelId参数
    const response = await request.get('/travelImage/listByTravelId', {
      params: {
        travelId: travelId  // 明确传递参数
      }
    });

    if (response.code === '200') {
      return response.data;
    } else {
      console.warn('获取图片失败:', response.msg);
      return [];
    }
  } catch (error) {
    console.error(`获取travelId=${travelId}的图片失败:`, error);
    return [];
  }
};
/**
 * 处理生活倒影数据，关联主表和副表
 * @param {Array} mainData 主表数据
 * @returns {Array} 处理后的生活倒影数据
 */
const processTravelData = async (mainData) => {
  if (!mainData || mainData.length === 0) {
    return [];
  }

  // 遍历主表数据，为每个项获取对应的图片
  const processedData = [];
  for (const item of mainData) {
    // 确保item.id存在才请求
    if (item.id) {
      const images = await getImagesByTravelId(item.id);
      const firstImage = images.sort((a, b) => a.sort - b.sort)[0] || {};

      processedData.push({
        id: item.id,
        title: item.title,
        astyle: item.astyle,
        coverImage: firstImage.imageUrl || require('@/assets/defaultImage.jpeg'),
        description: firstImage.information || '',
        publishTime: firstImage.publishTime || '未知时间'
      });
    }
  }

  // 只保留前6项
  return processedData.slice(0, 6);
}

// 更新内容统计数据
const updateContentStatistics = () => {
  // 计算总记录数
  const totalRecords = blogContent.value.length +
      personalDiary.value.length +
      learningNote.value.length +
      invertedContent.value.length;
  contentRecords.value = totalRecords;

  // 计算分类数（固定为4个分类）
  contentClasses.value = 4;

  // 模拟访问量增加
  contentVisits.value += Math.floor(Math.random() * 10);
};

// 速览调动页面
const pagePosition = ref(240)
const scrollToSection = (title) => {
  // 根据标题查找页面中的元素
  const target = document.getElementById(title);
  if (target) {
    const offsetTop = target.offsetTop;
    window.scrollTo({
      top: offsetTop + pagePosition.value,
      behavior: 'smooth'
    })
  }
}

// 地址跳转
const handleAddress = (url) => {
  router.push(url)
}

// 跳转到详情页
const navigateToDetail = (id, type) => {
  if (!id) {
    ElNotification.warning({
      title: '提示',
      message: '缺少内容ID，无法查看详情'
    });
    return;
  }

  // 根据路由配置跳转到详情页
  router.push({
    name: 'FrontDetail',
    params: {
      type: type,
      id: id
    }
  });
}

// 移动端导航切换
const toggleMobileNav = () => {
  mobileNavOpen.value = !mobileNavOpen.value;
}

// 检测屏幕尺寸
const checkScreenSize = () => {
  isMobile.value = window.innerWidth < 768;
  if (!isMobile.value) {
    mobileNavOpen.value = false;
  }
}

onMounted(() => {
  // 公告轮播
  timer = setInterval(() => {
    const currentIndex = noticeList.indexOf(currentNotice.value);
    const nextIndex = (currentIndex + 1) % noticeList.length;
    currentNotice.value = noticeList[nextIndex]
  }, 3000)

  // 加载数据
  loadData();

  // 初始化屏幕尺寸检测
  checkScreenSize();
  window.addEventListener('resize', checkScreenSize);
})

onUnmounted(() => {
  clearInterval(timer);
  window.removeEventListener('resize', checkScreenSize);
})

// 监听屏幕尺寸变化
watch(isMobile, (newValue) => {
  if (!newValue) {
    mobileNavOpen.value = false;
  }
})
</script>

<style scoped>
/* 基础样式 */
.page-container {
  min-height: 100vh;
  position: relative;
  width: 100%;
  box-sizing: border-box;
}

/* 顶部横幅 */
.top-banner {
  position: absolute;
  top: 0;
  left: 0;
  height: 40vh; /* 更改时，也需要更改banner-box */
  width: 100%;
  align-items: center;
  display: flex;
  justify-content: center;
  z-index: -1;
  transition: height 0.3s ease;
}

.content-center {
  text-align: center;
  padding: 20px;
  width: 90%;
  max-width: 800px;
}

.pageFont {
  font-size: clamp(2rem, 5vw, 3rem);
  font-weight: bold;
  color: white;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

.background-mask {
  margin-top: 20px;
  padding: 10px 20px;
  background-color: rgba(0, 0, 0, 0.5);
  border-radius: 20px;
  color: white;
  font-size: clamp(1rem, 2vw, 1.25rem);
  display: inline-block;
}

/* 移动端导航按钮 */
.mobile-nav-toggle {
  display: none;
  position: fixed;
  bottom: 20px;
  right: 20px;
  width: 50px;
  height: 50px;
  border-radius: 50%;
  background-color: #ff7d00;
  color: white;
  border: none;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  z-index: 100;
  justify-content: center;
  align-items: center;
  cursor: pointer;
  transition: all 0.3s ease;
}

.mobile-nav-toggle:hover {
  background-color: #ff9a00;
  transform: scale(1.05);
}

/* 内容主体 */
.content-wrapper {
  display: flex;
  align-items: flex-start;
  padding: 0 2vw;
  margin-top: 40vh;
  width: 100%;
  box-sizing: border-box;
  gap: 2rem;
  transition: all 0.3s ease;
}

/* 左侧内容样式 */
.left-content {
  width: 25%;
  min-width: 250px;
  transition: all 0.3s ease;
}

/* 右侧内容样式 */
.right-content {
  width: 75%;
  min-width: 300px;
  transition: all 0.3s ease;
}

/* 个人面板 */
.person-card {
  position: relative;
  background: linear-gradient(135deg, #ff9a00 0%, #9733ee 100%);
  transition: all 0.3s ease;
  border-radius: 12px;
  overflow: hidden;
  text-align: center;
  padding: 20px 10px;

  .el-avatar, .firstFont, .record-content, .el-button {
    position: relative;

    z-index: 1;
  }

  &:hover {
    background: linear-gradient(135deg, #ffb347 0%, #aa33ff 100%);
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  }

  &::before {
    content: '';
    position: absolute;
    top: -50%;
    left: -50%;
    width: 200%;
    height: 200%;
    background: linear-gradient(
        30deg,
        rgba(255, 154, 0, 0.3) 0%,
        rgba(151, 51, 238, 0.3) 50%,
        rgba(255, 154, 0, 0.3) 100%
    );
    background-size: 200% 200%;
    animation: flow 12s linear infinite;
    z-index: 0;
  }

  &::after {
    content: '';
    position: absolute;
    top: -50%;
    left: -50%;
    width: 200%;
    height: 200%;
    background: linear-gradient(
        150deg,
        rgba(255, 154, 0, 0.2) 0%,
        rgba(151, 51, 238, 0.2) 50%,
        rgba(255, 154, 0, 0.2) 100%
    );
    background-size: 200% 200%;
    animation: flow 8s linear infinite reverse;
    z-index: 0;
  }
}

@keyframes flow {
  0% {
    transform: translateX(-25%) translateY(-25%);
  }
  50% {
    transform: translateX(25%) translateY(25%);
  }
  100% {
    transform: translateX(-25%) translateY(-25%);
  }
}

/* 朋友圈按钮 */
.circle-button {
  width: 60%;
  margin: 10px 0;
}

/* 小车样式 */
.car-card {
  background-color: #fff;
  border-radius: 12px;
  overflow: hidden;
}

.car-container {
  display: flex;
  align-items: center;
  background: #fff;
  border-radius: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 10px 20px;
  position: relative;
  overflow: hidden;
  width: 100%;
}

.station-text {
  font-size: 12px;
  color: #e94c6d;
  font-weight: bold;
  position: relative;
  z-index: 1;
}

.record-content {
  display: flex;
  justify-content: space-around;
  width: 100%;
  padding: 10px 0;
  margin: 0 auto;
}

.record-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  flex: 1;
}

.record-label {
  font-size: 16px;
  font-family: "华文新魏", serif;
  color: white;
  margin-bottom: 5px;
}

.record-value {
  font-size: 18px;
  font-family: "华文新魏", serif;
  color: white;
  font-weight: bold;
}
/* 头部介绍 */
.dashed-border {
  width: 100%;
  height: 50px;
  border: 2px dashed rgba(110, 110, 110, 0.4);
  border-radius: 8px;
  padding: 10px;

  display: flex;
  justify-content: center;
  align-items: center;

  &:hover {
    border: 2px dashed rgba(0, 0, 0, 0.5);
  }
}

/* 动画 */
.animated-icon {
  animation: pulse 2s infinite ease-in-out;
  display: inline-block;
  color: orange;
}

@keyframes pulse {
  0% {
    transform: scale(1.5)
  }
  50% {
    transform: scale(1.75)
  }
  100% {
    transform: scale(1.5)
  }
}

.text-content {
  flex: 1;
  min-width: 0;

  span {
    display: block;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }
}

/* 速览 */
.scan-container {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.scan-item {
  width: 100%;
  height: 80%;
  transition: transform 0.3s ease;

  &:hover {
    transform: translateX(5px);
  }
}

.scanning-blog {
  width: 100%;
  border-radius: 8px;
  user-select: none;
  cursor: pointer;
  transition: all 0.3s ease;
  overflow: hidden;
  &:hover {
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
    transform: translateY(-3px);
  }
}

.first-content {
  top: 0;
  left: 0;
  display: flex;
  justify-content: left;

  & span {
    font-family: "华文新魏", serif;
    font-size: 17px;
    color: #e6e6e6;
  }
}

.second-content {
  display: flex;
  justify-content: left;
  margin-top: 10px;

  & span {
    font-family: "黑体", serif;
    font-size: 30px;
    color: #e6e6e6;
    font-weight: bold;
    border-bottom: 2px solid #e6e6e6;
  }
}

.third-content {
  display: flex;
  justify-content: left;
  margin-top: 10px;

  & span {
    font-family: "黑体", serif;
    font-size: 17px;
    color: #e6e6e6;
  }
}

/* 内容区块 */
.content-section {
  background-color: #fff;
  border-radius: 8px;
  padding: 15px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  transition: box-shadow 0.3s ease;

  &:hover {
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  }
}

/* 标题栏 */
.left-justify {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 10px;
}

.left-title {
  display: flex;
  align-items: center;
}

.right-justify {
  display: flex;
  align-items: center;
  cursor: pointer;
  color: #666;
  transition: all 0.3s ease;

  &:hover {
    color: #ff7d00;
  }
}

.firstFont {
  font-size: clamp(1.25rem, 3vw, 1.75rem);
  font-weight: 600;
  color: #333;
  margin: 10px 0;
}

.secondFont {
  font-size: clamp(1rem, 2vw, 1.25rem);
  font-weight: 500;
  color: #333;
  transition: color 0.3s ease;
  display: block;
  margin: 10px 0;

  &:hover {
    color: #ff7d00;
  }
}

/* 博客容器 */
.blog-container {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

.mainBlog-box {
  transition: transform 0.3s ease;
  width: 100%;
  height: 350px;
  &:hover {
    transform: translateY(-5px);
  }
}

.mainBlog-card {
  height: 100%;
  display: flex;
  flex-direction: column;
  transition: all 0.3s ease;
  overflow: hidden;

  &:hover {
    transform: translateY(-3px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  }
}

.mainBlog-image {
  width: 100%;
  height: 180px;
  overflow: hidden;

  .card-img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: transform 0.5s ease;

    &:hover {
      transform: scale(1.05);
    }
  }
}

.mainBlog-time {
  font-size: 12px;
  color: #999;
  margin: 8px 0 5px;
  display: flex;
  align-items: center;
}

/* 标签样式 */
.mainBlog-badge {
  margin-top: auto;
  padding-top: 10px;
  display: flex;
  gap: 5px;
  flex-wrap: wrap;
}

/* 响应式适配 - 大屏设备 */
@media (min-width: 1600px) {
  .left-content {
    width: 30%;
  }

  .right-content {
    width: 70%;
  }
  .scan-container {
    display: flex;
    flex-wrap: wrap;
    gap: 10px;
    width: 100%;
  }
  .blog-container {
    grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  }
  .mainBlog-image {
    height: 220px;
  }
  .mainBlog-box {
    height: 370px;
  }
}

/* 响应式适配 - 大型设备 */
@media (max-width: 1300px) {
  .left-content {
    width: 30%;
  }

  .right-content {
    width: 70%;
  }

  .mainBlog-image {
    height: 160px;
  }
  .scan-item {
    width: 100%;
  }
  .blog-container {
    grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  }
  .mainBlog-box {
    width: 100%;
    height: 320px;
  }
}

/* 响应式适配 - 平板设备 */
@media (max-width: 992px) {
  .left-content {
    width: 30%;
  }

  .right-content {
    width: 70%;
  }

  .mainBlog-image {
    height: 160px;
  }

  .scan-item {
    width: 100%;
  }

  .scan-item:nth-child(2n) {
    margin-right: 0;
  }

  .mainBlog-box {
    width: 100%;
    height: 320px;
  }
}

/* 响应式适配 - 小型平板/大型手机 */
@media (max-width: 768px) {
  .mobile-nav-toggle {
    display: flex;
  }

  .content-wrapper {
    flex-direction: column;
    margin-top: 30vh;
    padding: 0 3vw;
    gap: 1rem;
  }

  .content-wrapper.mobile-nav-open {
    margin-top: 30vh;
  }

  .top-banner {
    height: 30vh;
  }

  .left-content, .right-content {
    width: 100%;
    min-width: auto;
    margin-left: 0;
  }

  .left-content {
    margin-bottom: 20px;
    position: fixed;
    top: 30vh;
    left: 0;
    background-color: white;
    z-index: 90;
    padding: 0 3vw;
    max-height: 0;
    overflow: hidden;
    transition: max-height 0.3s ease;
  }

  .content-wrapper.mobile-nav-open .left-content {
    max-height: calc(100vh - 30vh - 70px);
    overflow-y: auto;
    box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
  }

  .scan-item {
    width: 48%;
  }

  .mainBlog-image {
    height: 150px;
  }

  .mainBlog-box {
    width: 100%;
    height: 300px;
  }
}

/* 响应式适配 - 手机设备 */
@media (max-width: 576px) {
  .content-wrapper {
    margin-top: 25vh;
  }

  .content-wrapper.mobile-nav-open {
    margin-top: 25vh;
  }

  .top-banner {
    height: 25vh;
  }

  .content-wrapper.mobile-nav-open .left-content {
    max-height: calc(100vh - 25vh - 70px);
  }

  .scan-item {
    width: 100%;
    margin-right: 0;
  }

  .blog-container {
    grid-template-columns: 1fr;
  }

  .mainBlog-image {
    height: 140px;
  }

  .record-content {
    padding: 5px 0;
  }

  .record-label {
    font-size: 14px;
  }

  .record-value {
    font-size: 16px;
  }

  .second-content span {
    font-size: 24px;
  }

  .left-justify {
    flex-direction: column;
    align-items: flex-start;
  }

  .right-justify {
    align-self: flex-end;
  }

  .mainBlog-box {
    width: 100%;
    height: 280px;
  }
}

/* 极小屏幕适配 */
@media (max-width: 360px) {
  .circle-button {
    width: 80%;
  }

  .dashed-border {
    height: auto;
    min-height: 50px;
  }

  .mainBlog-box {
    width: 100%;
    height: 280px;
  }
}
</style>
