<template>
  <el-container class="full-container">
    <!-- 全屏背景图 -->
    <div class="page-background" :style="{ backgroundImage: `url(${currentBgUrl || defaultBg})` }"></div>

    <!-- 导航栏 -->
    <el-header class="transparent-header">
      <FrontHeader />
    </el-header>

    <el-main class="main-content">
      <div class="desktop-container">

        <!-- 背景切换组件 -->
        <ToggleBackgroundSelector
            v-model:show="isBgSelectorVisible"
            @change="handleBgChange"
        />
        <!-- 动态布局容器 -->
        <div class="layout-wrapper" :class="{ full: isFullMode, compact: !isFullMode }">
          <!-- 时间区域 -->
          <div class="time-card" :class="{ compact: !isFullMode }" @click="toggleMode">
            <div class="time">
              <span :class="{ pulse: isFullMode }">{{ hours }}</span>
              <span class="colon">:</span>
              <span :class="{ pulse: isFullMode }">{{ minutes }}</span>
              <span class="colon">:</span>
              <span :class="{ pulse: isFullMode }">{{ seconds }}</span>
            </div>
            <div class="date">
              {{ month }}月{{ date }}日 星期{{ week }} 农历{{ lunarMonth }}月{{ lunarDay }}日
            </div>
            <div class="mode-indicator" :class="{ full: isFullMode }">
              <el-icon><ArrowDown /></el-icon>
              <span>{{ isFullMode ? '精简模式' : '展开模式' }}</span>
            </div>
          </div>

          <!-- 搜索框 -->
          <transition name="search-transition" class="search-box-wrapper">
            <div class="search-box" key="search">
              <!-- 搜索引擎选择器 -->
              <div class="engine-picker" :class="{ compact: !isFullMode, full: isFullMode }">
                <div class="engine-current" @click="toggleEngineDropdown">
                  <img :src="activeEngine.icon" class="engine-icon" :alt="activeEngine.name">
                  <span class="engine-name">{{ activeEngine.name }}</span>
                  <el-icon class="dropdown-arrow" :class="{ flipped: isFullMode }"><ArrowDown /></el-icon>
                </div>
                <!-- 下拉菜单 - 根据模式切换方向 -->
                <div class="engine-dropdown"
                     v-show="isEngineDropdownOpen"
                     @click.stop
                     :class="{ compact: !isFullMode, full: isFullMode }"
                >
                  <div v-for="(engine, index) in engines" :key="engine.name"
                       class="engine-item" @click="selectEngine(engine)"
                       :style="{ animationDelay: `${index * 0.05}s` }">

                    <img :src="engine.icon" class="engine-icon" :alt="engine.name">
                    <span>{{ engine.name }}</span>
                  </div>
                </div>
              </div>

              <div class="search-inner">
                <input
                    v-model="searchQuery"
                    class="search-input"
                    :placeholder="isFullMode ? '输入内容搜索' : '输入并搜索'"
                    @keyup.enter="handleSearch"
                    @focus="searchFocused = true"
                    @blur="searchFocused = false"
                >
                <div class="search-btn" @click="handleSearch" :class="{ active: searchFocused }">
                  <el-icon><Search /></el-icon>
                </div>
              </div>
            </div>
          </transition>

          <!-- 应用列表 -->
          <transition name="apps-transition" mode="out-in">
            <div class="apps-container" v-if="isFullMode" key="apps">
              <div class="apps">
                <div v-for="(app, idx) in apps" :key="idx"
                     class="app-card" @click="openApp(app.link)"
                     :style="{ animationDelay: `${idx * 0.05}s` }">
                  <div class="app-icon-container">
                    <img :src="app.icon" class="app-icon" :alt="app.name">
                  </div>
                  <div class="app-name">{{ app.title }}</div>
                </div>
              </div>
            </div>
          </transition>
        </div>
      </div>
    </el-main>
  </el-container>
</template>

<script setup>
import {ref, computed, onMounted } from 'vue'
import { ArrowDown, Search } from '@element-plus/icons-vue'
import {ElIcon, ElNotification} from 'element-plus'
import FrontHeader from "@/components/FrontHeader.vue";
import { Solar } from 'lunar-javascript';
import request from "@/utils/request";
import ToggleBackgroundSelector from "@/view/front/subComponent/ToggleBackgroundSelector.vue";
import {defaultBg} from "@/utils/defaultConfig";

// 响应式状态
const searchQuery = ref('')
const searchFocused = ref(false)

// 当前背景索引 & 面板显隐
const isBgSelectorVisible = ref(false);
const currentBgUrl = ref('');

const handleBgChange = (bgObject) => {
  if (bgObject && bgObject.url) {
    currentBgUrl.value = bgObject.url;
  }
};

// 搜索引擎列表
const engines = ref([
  {
    name: '必应',
    icon: 'https://www.bing.com/favicon.ico',
    url: 'https://www.bing.com/search?q='
  },
  {
    name: '百度',
    icon: 'https://www.baidu.com/favicon.ico',
    url: 'https://www.baidu.com/s?wd='
  },
  {
    name: '搜狗',
    icon: 'https://www.sogou.com/favicon.ico',
    url: 'https://www.sogou.com/web?query='
  }
])
const activeEngine = ref(engines.value[0])

// 应用列表
const apps = ref([
  {
    title: '豆包',
    icon: 'chrome-extension://dbjibobgilijgolhjdcbdebjhejelffo/assets/icon.png',
    link: 'https://doubao.com'
  },
  {
    title: 'B站',
    icon: 'https://cdn.yuanshikong.net/NewTab/icons/61bc4a2410915ef5fe36eb8d.svg',
    link: 'https://www.bilibili.com/'
  },
  {
    title: '知乎',
    icon: 'https://static.zhihu.com/heifetz/favicon.ico',
    link: 'https://www.zhihu.com/'
  }
])



// 加载数据相关
const loading = ref(true);         // 加载状态
const showError = ref(false);      // 错误状态标识
const errorMessage = ref("");      // 错误信息
const loaded = ref(false);         // 用于动画触发

// 加载数据
const loadData = async () => {
  // 重置状态
  loading.value = true;
  showError.value = false;
  errorMessage.value = "";
  loaded.value = false;

  try {
    const resApply = await request.get('/apply')
    if (resApply.code === '200') {
      apps.value = resApply.data || [];
    } else {
      showError.value = true;
      ElNotification.error({
        title: 'app加载失败',
        message: errorMessage.value
      })
    }
  } catch (error) {
    showError.value = true;
    errorMessage.value = '无法连接到服务器，请检查网络';
    ElNotification.error({
      title: '网络错误',
      message: errorMessage.value
    });
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  setInterval(updateTime, 1000)
  loadData();
})
// 时间计算逻辑
const now = ref(new Date())
const updateTime = () => {
  now.value = new Date()
}
// 时间转换逻辑
const year = now.value.getFullYear();
const month = now.value.getMonth() + 1; // 月份从0开始，需+1
const date = now.value.getDate();
const solar = Solar.fromYmd(year, month, date);
// 转换为农历
const lunar = solar.getLunar(); // 获取农历对象
// 提取农历信息
const week = ['日', '一', '二', '三', '四', '五', '六'][now.value.getDay()];
const lunarMonth = lunar.getMonth(); // 从农历对象中获取月份
const lunarDay = lunar.getDay();     // 从农历对象中获取日期

// 计算时分秒（带前导零）
const hours = computed(() => String(now.value.getHours()).padStart(2, '0'))
const minutes = computed(() => String(now.value.getMinutes()).padStart(2, '0'))
const seconds = computed(() => String(now.value.getSeconds()).padStart(2, '0'))

// 核心逻辑
const isFullMode = ref(true)
const isEngineDropdownOpen = ref(false)
const toggleMode = () => {
  if (isEngineDropdownOpen.value) {
    isEngineDropdownOpen.value = false
  }
  isFullMode.value = !isFullMode.value
}

const toggleEngineDropdown = () => {
  isEngineDropdownOpen.value = !isEngineDropdownOpen.value
}

const selectEngine = (engine) => {
  activeEngine.value = engine
  isEngineDropdownOpen.value = false
}

const handleSearch = () => {
  if (!searchQuery.value.trim()) return
  window.open(`${activeEngine.value.url}${encodeURIComponent(searchQuery.value)}`, '_blank')
  searchQuery.value = ''
}

const openApp = (link) => window.open(link, '_blank')

</script>

<style scoped>
/* 基础样式重置 */
html, body {
  margin: 0;
  padding: 0;
  height: 100%;
  width: 100%;
  overflow: hidden; /* 禁止页面滚动 */
}

.full-container {
  height: 100vh;
  width: 100vw;
}

/* 背景图样式 */
.page-background {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  z-index: 1;
}

/* 导航栏样式 */
.transparent-header {
  position: sticky;
  top: 0;
  z-index: 100;
  background: transparent;
  padding: 0 20px;
  margin: 0;
  border: none;
  height: auto;
}

/* 主内容区域 */
.main-content {
  position: relative;
  z-index: 10;
  padding: 0;
  margin: 0;
  height: calc(100vh - var(--el-header-height, 60px));
}

.desktop-container {
  width: 100%;
  height: 100%;
  position: relative;
  overflow: hidden;
}



@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.layout-wrapper {
  position: absolute;
  width: 100%;
  transition: all 0.6s cubic-bezier(0.4, 0, 0.2, 1);
  z-index: 10;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-top: 10vh;
  height: 100%;
  box-sizing: border-box;
}

.layout-wrapper.compact {
  top: 35%;
  transform: translateY(-50%);
  padding-top: 30vh;
}

.layout-wrapper.full {
  top: 0;
}

/* 时间卡片样式 */
.time-card {
  color: #fff;
  text-align: center;
  cursor: pointer;
  user-select: none;
  transition: all 0.5s cubic-bezier(0.4, 0, 0.2, 1);
  padding: 20px;
  border-radius: 12px;
  margin-bottom: 20px;
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(10px);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  width: 60%;
  min-width: 400px;
  z-index: 15;

}

.time-card:hover {
  background: rgba(255, 255, 255, 0.08);
}

.time-card.compact {
  transform: scale(1.1);
  margin-bottom: 40px;
}

.time {
  font-size: 64px;
  font-weight: 700;
  margin-bottom: 12px;
  letter-spacing: 2px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.time .colon {
  margin: 0 8px;
  animation: blink 1.5s infinite;
}

.date {
  font-size: 16px;
  opacity: 0.9;
  letter-spacing: 0.5px;
  margin-bottom: 8px;
}

.mode-indicator {
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.7);
  opacity: 0.8;
  transition: all 0.3s ease;
  padding: 4px 8px;
  border-radius: 12px;
  width: fit-content;
  margin: 0 auto;
}

.mode-indicator:hover {
  opacity: 1;
  background: rgba(255, 255, 255, 0.1);
}

.mode-indicator span {
  margin-left: 6px;
}

/* 搜索框样式 */
.search-box-wrapper {
  width: 60%;
  min-width: 400px;
  transition: all 0.5s cubic-bezier(0.4, 0, 0.2, 1);
  margin-bottom: 20px;
  z-index: 15;
}

.search-box {
  display: flex;
  align-items: center;
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(15px);
  border-radius: 28px;
  padding: 8px 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  transition: all 0.3s ease;
}

.search-box:focus-within {
  background: rgba(255, 255, 255, 0.2);
  box-shadow: 0 6px 25px rgba(0, 0, 0, 0.2);
}

/* 搜索引擎选择器 - 核心样式 */
.engine-picker {
  position: relative;
  width: 120px;
  margin-right: 16px;
  transition: all 0.3s ease;
}

.engine-picker.compact {
  width: 100px;
}

.engine-picker.full {
  padding-top: 8px;
}

.engine-current {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 6px 8px;
  border-radius: 14px;
  transition: background 0.2s;
}

.engine-current:hover {
  background: rgba(255, 255, 255, 0.1);
}

.engine-icon {
  width: 24px;
  height: 24px;
  margin-right: 8px;
  border-radius: 4px;
}

.engine-name {
  color: #fff;
  font-size: 14px;
  transition: all 0.3s ease;
}

/* 下拉箭头 - 核心样式 */
.dropdown-arrow {
  color: #fff;
  margin-left: 4px;
  font-size: 16px;
  transition: transform 0.3s ease;
}

.dropdown-arrow.flipped {
  transform: rotate(180deg);
}

.engine-picker:hover .dropdown-arrow:not(.flipped) {
  transform: translateY(2px);
}

.engine-picker:hover .dropdown-arrow.flipped {
  transform: rotate(180deg) translateY(-2px); /* 完整模式箭头向上移动 */
}

/* 下拉菜单 - 核心样式 */
.engine-dropdown {
  position: absolute;
  width: 120px;
  background: rgba(15, 15, 15, 0.85);
  backdrop-filter: blur(10px);
  border-radius: 10px;
  padding: 8px;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.25);
  z-index: 5; /* 低于应用列表 */
}

/* 精简模式：向下弹出 */
.engine-dropdown.compact {
  top: calc(100% + 10px);
  left: 0;
  animation: dropdownFadeDown 0.3s ease;
}

/* 完整模式：向上弹出 */
.engine-dropdown.full {
  bottom: calc(100% + 10px); /* 从底部向上计算位置 */
  left: 0;
  animation: dropdownFadeUp 0.3s ease;
}

.engine-item {
  display: flex;
  align-items: center;
  color: #fff;
  padding: 6px 8px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
  font-size: 14px;
  opacity: 0;
  transform: translateY(10px);
  animation: itemFadeIn 0.3s forwards;
}

.engine-item:hover {
  background: rgba(255, 255, 255, 0.15);
  transform: translateX(3px);
}

.search-inner {
  flex: 1;
  display: flex;
  align-items: center;
}

.search-input {
  flex: 1;
  background: transparent;
  border: none;
  outline: none;
  color: #fff;
  font-size: 15px;
  padding: 10px 8px;
  letter-spacing: 0.3px;
}

.search-input::placeholder {
  color: rgba(255, 255, 255, 0.6);
  transition: all 0.3s ease;
}

.search-box:focus-within .search-input::placeholder {
  color: rgba(255, 255, 255, 0.4);
}

.search-btn {
  width: 38px;
  height: 38px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.2);
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
  transition: all 0.3s ease;
  color: #fff;
}

.search-btn:hover, .search-btn.active {
  background: rgba(255, 255, 255, 0.35);
  transform: scale(1.05);
}

/* 应用列表容器 - 用于处理滚动 */
.apps-container {
  width: 60%;
  min-width: 400px;
  max-height: 45vh; /* 限制最大高度 */
  margin-top: 20px;
  z-index: 15;
  position: relative;
}

/* 应用列表样式 */
.apps {
  display: flex;
  flex-wrap: wrap;
  justify-content: flex-start;
  gap: 24px;
  padding: 20px;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(10px);
  width: 100%;
  box-sizing: border-box;
  overflow-y: auto;
  max-height: 45vh;
}

.apps::-webkit-scrollbar {
  width: 8px;
}

.apps::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.2);
  border-radius: 4px;
}

.apps::-webkit-scrollbar-track {
  background: rgba(255, 255, 255, 0.05);
}

.app-card {
  width: 85px;
  height: 100px;
  display: flex;
  flex-direction: column;
  justify-content: space-evenly;
  align-items: center;
  color: #fff;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  opacity: 0;
  transform: translateY(20px);
  animation: appFadeIn 0.5s forwards;
}

.app-icon-container {
  width: 80px;
  height: 80px;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.15);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 10px;
  transition: all 0.3s ease;
}

.app-card:hover .app-icon-container {
  transform: translateY(-5px) scale(1.1);
  background: rgba(255, 255, 255, 0.25);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.2);
}

.app-icon {
  width: 36px;
  height: 36px;
  transition: transform 0.3s ease;
}

.app-card:hover .app-icon {
  transform: scale(1.1);
}

.app-name {
  font-size: 13px;
  opacity: 0.9;
  margin-top: 5px;
  transition: all 0.3s ease;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  width: 100%;
  text-align: center;
}

.app-card:hover .app-name {
  opacity: 1;
  transform: translateY(2px);
}

/* 动画效果 */
@keyframes blink {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.4; }
}

@keyframes dropdownFadeDown {
  from {
    opacity: 0;
    transform: translateY(-10px) scale(0.95);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

@keyframes dropdownFadeUp {
  from {
    opacity: 0;
    transform: translateY(10px) scale(0.95);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

@keyframes itemFadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes appFadeIn {
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.search-transition-enter-active,
.search-transition-leave-active {
  transition: all 0.5s cubic-bezier(0.4, 0, 0.2, 1);
}

.search-transition-enter-from,
.search-transition-leave-to {
  opacity: 0;
  transform: translateY(-20px) scale(0.95);
}

.apps-transition-enter-active,
.apps-transition-leave-active {
  transition: all 0.5s cubic-bezier(0.4, 0, 0.2, 1);
}

.apps-transition-enter-from,
.apps-transition-leave-to {
  opacity: 0;
  transform: translateY(30px) scale(0.97);
}

.pulse {
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.05); }
}

/* 响应式适配 */
@media (max-width: 1024px) {
  .time-card,
  .search-box-wrapper,
  .apps-container {
    width: 70%;
  }
}

@media (max-width: 768px) {
  .time-card,
  .search-box-wrapper,
  .apps-container {
    width: 90%;
  }

  .search-box-wrapper {
    min-width: auto;
  }

  .time {
    font-size: 48px;
  }

  .engine-picker {
    width: 100px;
  }

  .engine-name {
    font-size: 13px;
  }

  .apps {
    gap: 18px;
    padding: 15px;
  }

  .app-card {
    width: 70px;
    height: 90px;
  }

  .bg-selector {
    width: 250px;
  }
}

@media (max-width: 480px) {
  .time {
    font-size: 36px;
  }

  .date {
    font-size: 14px;
  }

  .engine-picker {
    width: 80px;
    margin-right: 8px;
  }

  .engine-icon {
    width: 20px;
    height: 20px;
  }

  .engine-name {
    display: none;
  }

  .search-input {
    font-size: 14px;
  }

  .app-card {
    width: 60px;
    height: 80px;
  }

  .app-icon-container {
    width: 50px;
    height: 50px;
  }

  .app-icon {
    width: 30px;
    height: 30px;
  }

  .app-name {
    font-size: 12px;
  }

  .bg-selector {
    width: calc(100% - 40px);
    left: 20px;
    right: 20px;
  }

  .bg-thumbs {
    grid-template-columns: repeat(3, 1fr);
  }

  .apps-container {
    min-width: auto;
  }
}
</style>



