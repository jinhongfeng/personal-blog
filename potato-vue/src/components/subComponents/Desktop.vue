<template>
  <div class="desktop-container" @click="handleContainerClick">
    <!-- 时间显示区域，点击切换显示状态 -->
    <div
        class="time-area"
        @click.stop="toggleShowFull"
    >
      <div class="time">{{ hours }}:{{ minutes }}:{{ seconds }}</div>
      <div class="date">{{ month }}月{{ day }}日  {{ week }}  农历{{ lunarMonth }}月{{ lunarDay }}</div>
    </div>

    <!-- 完整界面结构（搜索框 + 应用图标），根据 showFull 状态显示 -->
    <transition-group name="fade" class="full-content" v-show="showFull">
      <div class="search-box" key="search">
        <!-- 搜索框区域，可根据之前的需求自定义搜索引擎等 -->
        <div class="search-inner">
          <input
              v-model="searchValue"
              class="search-input"
              placeholder="输入并搜索"
              @keyup.enter="handleSearch"
          >
          <div class="search-btn" @click="handleSearch">
            <el-icon><Search /></el-icon>
          </div>
        </div>
      </div>
      <div class="apps" key="apps">
        <div
            v-for="(app, index) in apps"
            :key="index"
            class="app-item"
            @click="openApp(app.link)"
        >
          <img :src="app.icon" alt="应用图标" class="app-icon">
          <div class="app-name">{{ app.name }}</div>
        </div>
      </div>
    </transition-group>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { ElIcon, Search } from 'element-plus';
// 可替换成实际农历计算库，这里简单模拟
import { getLunar } from './lunar-utils';

// 响应式数据
const showFull = ref(false);
const searchValue = ref('');
const dateObj = ref(new Date());
const apps = ref([
  {
    name: '必应',
    link: 'https://www.bing.com',
    icon: 'https://img.icons8.com/color/48/000000/bing.png'
  },
  {
    name: '豆包',
    link: 'https://doubao.com',
    icon: 'https://img.icons8.com/color/48/000000/chatbot.png'
  },

]);

// 时间相关计算
const updateTime = () => {
  dateObj.value = new Date();
  // 同步农历日期（若用真实库，需在这里重新计算）
  const { lunarMonth, lunarDay } = getLunar(dateObj.value);
  lunarMonth.value = lunarMonth;
  lunarDay.value = lunarDay;
};
const hours = computed(() =>
    String(dateObj.value.getHours()).padStart(2, '0')
);
const minutes = computed(() =>
    String(dateObj.value.getMinutes()).padStart(2, '0')
);
const seconds = computed(() =>
    String(dateObj.value.getSeconds()).padStart(2, '0')
);
const month = computed(() => dateObj.value.getMonth() + 1);
const day = computed(() => dateObj.value.getDate());
const weekList = ['日','一','二','三','四','五','六'];
const week = computed(() =>
    `星期${weekList[dateObj.value.getDay()]}`
);
const lunarMonth = ref(0);
const lunarDay = ref(0);

// 搜索与应用跳转逻辑
const handleSearch = () => {
  if (searchValue.value.trim()) {
    // 这里可结合之前的搜索引擎逻辑，示例直接用默认搜索引擎
    window.open(`https://www.baidu.com/s?wd=${encodeURIComponent(searchValue.value)}`, '_blank');
  }
};
const openApp = (link) => {
  window.open(link, '_blank');
};

// 点击时间区域切换显示状态
const toggleShowFull = () => {
  showFull.value = !showFull.value;
};
// 点击容器其他区域隐藏完整结构
const handleContainerClick = (e) => {
  const target = e.target;
  // 判断点击目标是否是时间区域，不是则隐藏
  if (!target.closest('.time-area')) {
    showFull.value = false;
  }
};

// 定时更新时间
onMounted(() => {
  setInterval(updateTime, 1000);
  updateTime();
});
</script>

<style scoped>
.desktop-container {
  width: 100vw;
  height: 100vh;
  position: relative;
  overflow: hidden;
}

.time-area {
  position: absolute;
  top: 20px;
  left: 20px;
  color: #fff;
  cursor: pointer;
  user-select: none;
}
.time {
  font-size: 24px;
  font-weight: bold;
}

.date {
  font-size: 16px;
  margin-top: 4px;
}

/* 完整内容区域过渡动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease, transform 0.3s ease;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
  transform: translateY(-20px);
}

.full-content {
  position: absolute;
  top: 100px;
  left: 50%;
  transform: translateX(-50%);
  width: 80%;
  max-width: 1200px;
}
.search-box {
  background-color: rgba(255, 255, 255, 0.3);
  border-radius: 28px;
  padding: 8px 16px;
  margin-bottom: 20px;
}
.search-inner {
  display: flex;
  align-items: center;
}
.search-input {
  flex: 1;
  background: transparent;
  border: none;
  outline: none;
  color: #fff;
  font-size: 16px;
  padding: 6px 0;
}
.search-input::placeholder {
  color: rgba(255, 255, 255, 0.6);
}
.search-btn {
  cursor: pointer;
  color: #fff;
}

.apps {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  justify-content: center;
}
.app-item {
  width: 80px;
  text-align: center;
  cursor: pointer;
  transition: transform 0.2s ease;
}
.app-item:hover {
  transform: translateY(-5px);
}
.app-icon {
  width: 48px;
  height: 48px;
  margin-bottom: 4px;
}
.app-name {
  color: #fff;
  font-size: 14px;
}
</style>
