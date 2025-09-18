<template>
  <div class="scalable-container">
    <!-- 背景 -->
    <div class="top-banner"
         :style="{
          backgroundImage: `url('${backgroundImage}')`,
          backgroundRepeat: 'no-repeat',
          backgroundPosition: 'top right',
          backgroundSize: 'cover'
        }"></div>

    <!-- 头部内容-时光相册 -->
    <div class="travel-box"
         :style="{
          backgroundImage: `url('${travelImage}')`,
          backgroundRepeat: 'repeat',
          backgroundPosition: 'center center',
        }">
      <div class="travel-theme">
        <span class="theme-item">时光相册</span>
      </div>
      <div class="travel-title">
        <span>{{ noticeList }}</span>
      </div>
    </div>

    <!-- 主题内容 -->
    <TravelAlbum />

  </div>
</template>

<script setup>
import TravelAlbum from "@/view/front/subComponent/TravelAlbum.vue";
import {ref} from "vue";
const noticeList = ref(`每一张照片都是记录美好生活`);
const backgroundImage = require("@/assets/top-image/image_5.jpg");
const travelImage = require("@/assets/travel.gif");
</script>

<style scoped>
.scalable-container {
  transform-origin: top left;
  width: 100%;
  height: 100%;
}

.top-banner {
  height: 40vh;
  animation: slideDown 0.8s ease-out forwards;
}
@keyframes slideDown {
  from {
    opacity: 0;
    transform: translateY(-100%);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 头部 */
.travel-box {
  height: 17vh;
  width: 80%;
  margin-top: 45vh;
  margin-left: 10%;
  margin-right: 10%;
  border-radius: 12px;
}
.travel-theme {
  display: flex;
  justify-content: left;
}
.theme-item {
  font-size: 1rem;
  color: var(--white);
  margin-top: 15px;
  margin-left: 15px;
}
.travel-title {
  display: flex;
  justify-content: center;
  margin-top: 2vh;
  margin-left: 10px;
  color: var(--black);
  font-family: "华文新魏", serif;
  font-size: 2rem;
}

@media (max-width: 768px) {
  .travel-title {
    display: flex;
    justify-content: center;
    margin-left: 10px;
    color: var(--black);
    font-family: "华文新魏", serif;
    font-size: 1.4rem;
  }
}
</style>
