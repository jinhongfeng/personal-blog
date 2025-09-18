<template>
  <div>

    <!-- 小车元素 -->
    <div class="car"
         :style="{
            backgroundImage: `url('${carImage}')`,
            backgroundRepeat: 'no-repeat',
            backgroundPosition: 'center',
            backgroundSize: 'contain',
          }"/>
  </div>
</template>

<script setup>

const carImage = require("@/assets/car.png");
</script>

<style scoped>



/* 小车样式 */
.car {
  position: absolute;
  left: -12px;
  top: 50%;
  transform: translateY(-50%);
  width: 24px;
  height: 24px;

  /* 从左到右移动 + 渐隐 */
  animation: carMove 2s linear forwards infinite;
}

/* 动画关键帧定义 */
@keyframes carMove {
  0% {
    left: 70px;
    opacity: 1;
  }
  80% {
    opacity: 1;
  }
  100% {
    left: calc(100% + 12px);
    opacity: 0;
  }
}
</style>
