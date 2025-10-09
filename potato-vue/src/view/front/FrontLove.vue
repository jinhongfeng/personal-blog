<template>
  <div v-if="openState === true">
    <!-- 顶部 -->
    <div class="top-banner"
         :style="{
          backgroundImage: `url('${backgroundImage}')`,
          backgroundRepeat: 'no-repeat',
          backgroundPosition: 'center center',
          backgroundSize: 'cover'
        }">
      <div class="banner-box">
        <div class="banner-content">
          <!-- 左侧区域 -->
          <div class="avatar-wrapper left-area">
            <el-avatar :src="avatarImage[0].image" class="banner-avatar" />
            <span class="banner-title">{{ avatarImage[0].title }}</span>
          </div>

          <!-- 中间区域 - 红心 -->
          <div class="heart-area">
            <el-image :src="heartImage" class="heart-image animated-pulse" />
          </div>

          <!-- 右侧区域 -->
          <div class="avatar-wrapper right-area">
            <el-avatar :src="avatarImage[1].image" class="banner-avatar" />
            <span class="banner-title">{{ avatarImage[1].title }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 主题内容 -->
    <div class="love-bgBox"
         :style="{
          backgroundImage: `url('${backgroundBox}')`,
          backgroundRepeat: 'no-repeat',
          backgroundPosition: 'center center',
          backgroundSize: 'cover'
        }">
      <!-- 时间 -->
      <div class="love-timeBox">
        <!-- 标题 -->
        <div class="timeBox-title">这是我们一起走过的</div>
        <!-- 累积时间显示 -->
        <div class="timeBox-display">
          <span class="static-text">第</span>
          <span class="dynamic-number">{{ years }}</span>
          <span class="static-text">年</span>
          <span class="dynamic-number">{{ months }}</span>
          <span class="static-text">月</span>
          <span class="dynamic-number">{{ days }}</span>
          <span class="static-text">日</span>
          <span class="dynamic-number">{{ hours }}</span>
          <span class="static-text">时</span>
          <span class="dynamic-number">{{ minutes }}</span>
          <span class="static-text">分</span>
          <span class="dynamic-number">{{ seconds }}</span>
          <span class="static-text">秒</span>
        </div>
        <!-- 春节倒计时 -->
        <div class="timeBox-timeDown">
          春节倒计时: {{ festivalDays }}天{{ festivalHours }}时{{ festivalMinutes }}分{{ festivalSeconds }}秒
        </div>
      </div>
      <!-- 卡片 -->
      <div class="love-center">
        <el-card class="love-card"
                 :style="{
                    backgroundImage: `url('${backgroundCard}')`,
                    backgroundRepeat: 'no-repeat',
                    backgroundPosition: 'center center',
                    backgroundSize: 'cover'
                 }">
          <!-- 文字部分 -->
          <div class="text-container">
            <span class="car-text">飞车传信</span>
          </div>
          <div class="car-container">
            <CarAnimation class="mt-10"/>
          </div>
        </el-card>
      </div>
      <!-- 导航卡片 -->
      <div class="love-navigation">

        <div class="love-naviCard" v-for="(item, index) in contentList" :key="index"
             @click="switchComponent(index)"
             :class="{active: currentIndex === index}" >
          <!-- 左侧图片 -->
          <div class="naviCard-left">
            <el-image round :src="item.picture" class="naviCard-image"/>
          </div>
          <!-- 右侧内容 -->
          <div class="naviCard-right">
            <div class="naviCard-title">
              <span>{{item.title}}</span>
            </div>
            <div class="naviCard-desc">
              <font-awesome-icon :icon="item.faIcon" />
              <span  style="margin-left: 5px">{{item.desc}}</span>
            </div>
          </div>
        </div>

      </div>

      <!-- 具体内容 -->
      <div class="content-area">
        <component :is="currentComponent" v-if="currentComponent" />
      </div>
    </div>

  </div>
  <div v-else-if="openState === false">
      <div class="love-mask">
        <div style="max-width: 600px;" >
          <el-alert title="页面未开放" type="error" effect="dark" />
        </div>
      </div>
  </div>
</template>

<script setup>
import {onMounted, onUnmounted, ref} from "vue";
import dayjs from 'dayjs';
import durationPlugin from 'dayjs/plugin/duration'
import CarAnimation from "@/view/front/subComponent/CarAnimation.vue";
import TravelAlbum from "@/view/front/subComponent/TravelAlbum.vue";
import LoveTimeline from "@/view/front/subComponent/LoveTimeline.vue";
import LoveLeaveMsg from "@/view/front/subComponent/LoveLeaveMsg.vue";
dayjs.extend(durationPlugin)

const openState = ref(true)
const heartImage = require("@/assets/love/heart.png");
const backgroundImage = require("@/assets/love/love_card.jpg");
const backgroundBox = require("@/assets/love/love_background.png");
const backgroundCard = require("@/assets/love/love.gif");
const avatarImage = [
  { image: require("@/assets/love/avatar_1.jpeg"), title: "POTATO" },
  { image: require("@/assets/love/avatar_2.jpeg"), title: "TOMATO" }
];

const START_DATE = dayjs('2025-09-05 00:00:00') // 起始时间
const SPRING_FESTIVAL_2025 = dayjs('2026-02-16 00:00:00') // 2026年春节时间
// 响应式数据
const years = ref(0)
const months = ref(0)
const days = ref(0)
const hours = ref(0)
const minutes = ref(0)
const seconds = ref(0)

const festivalDays = ref(0)
const festivalHours = ref(0)
const festivalMinutes = ref(0)
const festivalSeconds = ref(0)

// 时间更新逻辑
let timer = null
const updateTime = () => {
  const now = dayjs()

  // 计算一起走过的时间差
  const diff = now.diff(START_DATE)
  const duration = dayjs.duration(diff)

  years.value = duration.years()
  months.value = duration.months()
  days.value = duration.days()
  hours.value = duration.hours()
  minutes.value = duration.minutes()
  seconds.value = duration.seconds()

  // 计算春节倒计时（正数为剩余时间，负数为已过）
  const festivalDiff = SPRING_FESTIVAL_2025.diff(now)
  if (festivalDiff > 0) {
    // 计算总天数
    festivalDays.value = Math.floor(festivalDiff / (1000 * 60 * 60 * 24))

    // 计算剩余小时
    const remainingHours = festivalDiff % (1000 * 60 * 60 * 24)
    festivalHours.value = Math.floor(remainingHours / (1000 * 60 * 60))

    // 计算剩余分钟
    const remainingMinutes = remainingHours % (1000 * 60 * 60)
    festivalMinutes.value = Math.floor(remainingMinutes / (1000 * 60))

    // 计算剩余秒数
    const remainingSeconds = remainingMinutes % (1000 * 60)
    festivalSeconds.value = Math.floor(remainingSeconds / 1000)
  } else {

    festivalDays.value = 0
    festivalHours.value = 0
    festivalMinutes.value = 0
    festivalSeconds.value = 0
  }
}

// 导航索引列表
const contentList = ref([
  {title: '点点滴滴', desc: '今朝有酒今朝醉', faIcon: 'sun', picture: require('@/assets/avatar/avatar_1.webp')},
  {title: '时光相册', desc: '记录美好瞬间', faIcon: 'camera-retro', picture: require('@/assets/avatar/avatar_2.webp')},
  {title: '祝福板', desc: '写下你们的祝福', faIcon: 'sticky-note', picture: require('@/assets/avatar/avatar_3.webp')}
])

// 具体内容
const currentIndex = ref(0)
const currentComponent = ref(null)
const componentMap = {
  0: LoveTimeline,
  1: TravelAlbum,
  2: LoveLeaveMsg
}
const switchComponent = (index) => {
  currentIndex.value = index
  currentComponent.value = componentMap[index]
}

// 组件生命周期
onMounted(() => {
  currentComponent.value = componentMap[0]
  updateTime() // 初始化渲染
  timer = setInterval(updateTime, 1000) // 每秒更新
})

onUnmounted(() => {
  clearInterval(timer) // 清除定时器
})
</script>

<style scoped>
:root {
  --primary-color: #FF9800; /* 橙色主题色 */
  --primary-light: #FFB74D; /* 浅橙色 */
  --primary-dark: #F57C00; /* 深橙色 */
  --text-color: #333333;
  --bg-light: #f9f9f9;
}


.banner-box {
  width: 60%;
  height: 35vh;
  margin: 15vh 20% 0 20%;
  background: rgba(255, 255, 255, 0.5); /* 透明背景 */
  z-index: 1;
  border: 2px solid rgba(255, 255, 255, 0.6);
  border-radius: 12px;
  backdrop-filter: blur(5px);
}
.banner-content {
  display: flex;
  justify-content: space-around; /* 三列等距分布 */
  align-items: center;
  height: 100%;
  width: 100%;
}

.love-bgBox {
  object-fit: cover;
  position: relative;
}
/* 左右两侧区域 */
.avatar-wrapper {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  text-align: center;
  width: 15%;
}
.left-area, .right-area {
  height: 100%;
}
.banner-avatar {
  width: 100px;
  height: 100px;
  margin-bottom: 15px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
}
.banner-title {
  font-size: 1.2rem;
  color: #fff;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.5);
  font-weight: bold;
}
/* 中间红心区域 */
.heart-area {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 30%; /* 中间占30% */
  height: 100%;
}
.heart-image {
  width: 120px;
  height: 120px;
  object-fit: contain; /* 保持图片比例 */
}
/* 红心跳动动画 */
.animated-pulse {
  animation: pulse 2s infinite ease-in-out;
}

@keyframes pulse {
  0% { transform: scale(1); }
  50% { transform: scale(1.2); }
  100% { transform: scale(1); }
}

/* 时间盒子 */
.love-timeBox {
  text-align: center;
  padding: 10px;
  color: #333333;
  font-family: var(--globalFont);
  font-weight: bold;
  margin-top: 50vh;
  .timeBox-title {
    font-size: 2.5rem;
    color: #4b89dc;
    margin-bottom: 15px;
    font-weight: 500;
    animation: titlePulse 3s infinite ease-in-out;
    transition: all 0.3s ease;
  }
  .timeBox-display {
    font-weight: bold;
    margin-bottom: 20px;
    letter-spacing: 1px;
    .static-text {
      font-size: 2.5rem;
      vertical-align: bottom;
      margin: 0 2px;
    }
    .dynamic-number {
      font-size: 4rem;
      vertical-align: bottom;
      margin: 0 2px;
      color: #ff6b6b;
    }
  }
  .timeBox-timeDown {
    font-size: 1.5rem;
    color: #666;
  }
}

@keyframes titlePulse {
  0% {
    color: #FF9A00;
    text-shadow: 0 0 5px rgba(255, 154, 0, 0.3);
  }
  25% {
    color: #FF7A5A;
    text-shadow: 0 0 8px rgba(255, 122, 90, 0.4);
  }
  50% {
    color: #FFB64D;
    text-shadow: 0 0 10px rgba(255, 182, 77, 0.5);
  }
  75% {
    color: #FF7A5A;
    text-shadow: 0 0 8px rgba(255, 122, 90, 0.4);
  }
  100% {
    color: #FF9A00;
    text-shadow: 0 0 5px rgba(255, 154, 0, 0.3);
  }
}

/* 中心卡片 */
.love-center {
  display: flex;
  justify-content: center;
  align-items: center;

  .love-card {
    width: 20%;
    height: 15vh;
    border-radius: 12px;
    margin-top: 20px;
  }
}

.car-container {
  display: flex;
  align-items: center;

  padding: 10px 20px;
  position: relative;
  overflow: hidden;
  width: 100%;

}
.text-container {
  display: flex;
  justify-content: left;
  font-family: var(--globalFont);
  font-weight: bold;
  .car-text {
    font-size: 1.5rem;
    color: #e94c6d;
    font-weight: bold;
    position: relative;
    z-index: 1;
  }
}

/* 导航索引 */

.love-navigation {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  width: 80%;
  gap: 5px;
  overflow: hidden;
  margin: 10vh 10%;

  .love-naviCard {
    display: flex;
    justify-content: center;
    align-items: center;
    background: #F4E1C0;
    border: 2px solid var(--miniWhiteMask);
    border-radius: 12px;
    margin: 0 10px;
    cursor: pointer;
    transition: all 0.3s ease;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);

    &:hover {
      transform: translateY(-5px) scale(1.03);
      box-shadow: 0 10px 20px rgba(255, 152, 0, 0.3);
    }

    &.active {
      background: linear-gradient(135deg, var(--primary-color), var(--primary-light));

      &:hover {
        transform: translateY(-8px) scale(1.05);
        box-shadow: 0 15px 30px rgba(255, 152, 0, 0.4);
      }

    }
  }
}

.naviCard-left {
  width: 45%;
  height: 100%;
  display: flex;
  justify-content: flex-start;
  align-items: center;
  padding: 10px;
  .naviCard-image {
    margin: 10px;
    width: 70%;
    height: 70%;
    object-fit: cover;
    border-radius: 50%;
    transition: all 0.3s ease;

    &.active {
      transform: scale(1.05);
    }
  }
}
.naviCard-right {
  flex: 1;

  padding: 0 10px;
  font-family: var(--globalFont);
  font-weight: bold;
  .naviCard-title{
    display: flex;

    color: var(--text-color);
    font-size: 1.5rem;
    margin-bottom: 5px;
    transition: all 0.3s ease;
    font-weight: bold;
  }
  .naviCard-desc {
    color: #666;
    font-size: 0.9rem;
    display: flex;
    align-items: center;
    margin: 5px 0;
  }
}

/* 具体内容 */
.content-area {
  max-width: 1200px;
  margin: 30px auto;
}

/* 页面关闭状态 */
.love-mask {
  width: 100%;
  height: 100%;
  background: white;
  text-align: center;
}
/* 响应式适配 */
@media (max-width: 768px) {
  .banner-box {
    width: 90%;
    height: 30vh;
  }

  .banner-avatar {
    width: 80px;
    height: 80px;
  }

  .heart-image {
    width: 80px;
    height: 80px;
  }

  .banner-title {
    font-size: 1rem;
  }
  .love-center {
    display: flex;
    justify-content: center;
    align-items: center;

    .love-card {
      width: 40%;
      height: 15vh;
      border-radius: 12px;
      margin-top: 20px;
    }
  }
  @media (max-width: 576px) {
    .timeBox-display {
      font-size: 28px;
    }
    .timeBox-timeDown {
      font-size: 16px;
    }
    .static-text {
      font-size: 28px;
    }
    .dynamic-number {
      font-size: 36px;
    }
  }
  .love-navigation {
    grid-template-columns: 1fr; /* 小屏幕单列显示 */
    gap: 15px;
  }

  .love-naviCard {
    margin: 0 !important; /* 移除水平间距 */
  }
  .love-center {
    display: flex;
    justify-content: center;
    align-items: center;

    .love-card {
      width: 60%;
      height: 15vh;
      border-radius: 12px;
      margin-top: 20px;
    }
  }
}

</style>
