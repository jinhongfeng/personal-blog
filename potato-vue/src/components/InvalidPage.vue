<template>
  <div class="no-found-container">
    <!-- 背景图 -->
    <div class="bg-decoration"></div>
    <!-- 内容 -->
    <div class="content-invalid">
      <!-- 标题 -->
      <div class="title-invalid">
        <h1 class="error-code">404</h1>
        <p class="error-message">页面不存在</p>
        <p class="error-description">抱歉，您访问的页面好像去了外太空...</p>
      </div>
      <!-- 动画元素 -->
      <div class="astronaut-container">
        <!-- 头 -->
        <div class="head">
          <div class="visor"></div>
        </div>
        <!-- 身体 -->
        <div class="body">
          <div class="oxygen-tank"></div>
          <div class="left-arm"></div>
          <div class="right-arm"></div>
        </div>
        <!-- 腿 -->
        <div class="legs">
          <div class="left-leg"></div>
          <div class="right-leg"></div>
        </div>
      </div>
      <!-- 按钮 -->
      <div class="center-content">
        <div class="btn-box" @click="router.push('/welcome')">
          <span>进入BLOG！</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>

import router from "@/router";
</script>

<style scoped>
.no-found-container {
  top: 0;
  left: 0;
  margin: 0;
  position: fixed;
  width: 100%;
  height: 100%;
  overflow: hidden;
  background: linear-gradient(135deg, #0f0c29 0%, #302b63 50%, #24243e 100%);
  color: white;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;;
}
.bg-decoration {
  position: absolute;
  top: 0;
  left: 0;
  width: 150%;
  height: 150%;
  background-image:
      radial-gradient(circle at 25px 25px, rgba(255, 255, 255, 0.1) 2%, transparent 0%),
      radial-gradient(circle at 75px 75px, rgba(255, 255, 255, 0.1) 2%, transparent 0%);
  background-size: 100px 100px;
  z-index: 0;
  transform: translate(-25%, -25%);
}
/* 内容 */
.content-invalid {
  position: relative;
  z-index: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  padding: 2rem;
}
.title-invalid {
  text-align: center;
  margin-bottom: 4rem;
}
.error-code {
  font-size: 10rem;
  margin: 0;
  font-weight: bold;
  background: linear-gradient(90deg, #ff8a00, #e52e71);
  color: transparent;
  text-shadow: 0 0 30px rgba(255, 138, 0, 0.3);
  animation: pulse 3s infinite;

  -webkit-background-clip: text;
  background-clip: text;
  user-select: none;
}
.error-message {
  font-size: 2.5rem;
  margin: 1rem 0;
  font-weight: 300;
  user-select: none;
}
.error-description {
  font-size: 1.2rem;
  color: rgba(255, 255, 255, 0.7);
  user-select: none;
}
/* 宇航员 */
.astronaut-container {
  width: 200px;
  height: 160px;
  position: absolute;
  top: 20%;
  left: 70%;
  transform: translate(-50%, -50%) rotate(20deg);
  animation: rotate 20s linear infinite;
}
.head {
  width: 60px;
  height: 60px;
  background: white;
  border-radius: 50%;
  margin: 0 auto;
  position: relative;
  z-index: 2;
  box-shadow: 0 0 15px rgba(255, 255, 255, 0.5);
}
.visor {
  width: 40px;
  height: 40px;
  background: #3a4cad;
  border-radius: 50%;
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  box-shadow: inset 0 0 10px rgba(0, 0, 0, 0.5);
}
.body {
  width: 80px;
  height: 90px;
  background: #e6e6e6;
  border-radius: 25% 25% 15% 15%;
  margin: -5px auto 0;
  position: relative;
  z-index: 1;
}
.oxygen-tank {
  width: 60px;
  height: 40px;
  background: #333333;
  border-radius: 10px;
  position: absolute;
  bottom: 10px;
  left: 50%;
  transform: translateX(-50%);
}
.left-arm, .right-arm {
  width: 20px;
  height: 60px;
  background: #e6e6e6;
  position: absolute;
  top: 20px;
}
.left-arm {
  left: -15px;
  transform: rotate(30deg);
  border-radius: 10px 0 0 10px;
}

.right-arm {
  right: -15px;
  transform: rotate(-30deg);
  border-radius: 0 10px 10px 0;
}
.legs {
  width: 80px;
  height: 40px;
  margin: 0 auto;
  position: relative;
}
.left-leg, .right-leg {
  width: 30px;
  height: 40px;
  background: #e6e6e6;
  position: absolute;
  bottom: -20px;
}
.left-leg {
  left: 5px;
  transform: rotate(15deg);
  border-radius: 0 0 10px 10px;
}
.right-leg {
  right: 5px;
  transform: rotate(-15deg);
  border-radius: 0 0 10px 10px;
}
.center-content {
  display: flex;
  justify-content: center;
  align-items: center;
  user-select: none;

}
.btn-box {
  position: relative;
  color: #e6e6e6;
  width: 10vw;
  border: 2px solid #23d5ab;
  border-radius: 8px;
  padding: 10px 10px;
  margin: 0;
  text-align: center;
  cursor: pointer;
  transition: all 0.5s ease;
  animation: pulse 2s infinite alternate;

  background-size: 0 100%;
  background: #23d5ab;

  &:hover {
    background-size: 100% 100%;
    border-color: orange;
    background: linear-gradient(to right, #ff9800, #ff9800) no-repeat left bottom;
  }
}
@keyframes pulse {
  0% { transform: scale(1); }
  100% { transform: scale(1.05); }
}
</style>
