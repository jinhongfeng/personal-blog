<template>
  <footer class="footer-container mt-10">
    <!-- 左侧 -->
    <div class="left-footer">
      <el-image :src="imageFooter" class="footer-image" alt="飞机图标"></el-image>
    </div>

    <!-- 右侧 -->
    <div class="right-footer">
      <div class="emphasize-font">{{ desc_1 }}</div>
      <div class="sub-footer">{{ desc_2 }}</div>
      <div class="emphasize-font">{{ desc_3 }}</div>
      <div class="contact-section">
        <font-awesome-icon icon="hand" class="hand-icon" />
        <el-link class="footer-link" @click="router.push('/front/message')">
          {{ desc_4 }}
        </el-link>
      </div>
    </div>
  </footer>
</template>

<script setup>
import {ref} from "vue";
import router from "@/router";
import imageFooter from "@/assets/plane.png"; // 统一使用import方式引入图片

// 描述文本
const desc_1 = ref('有的人出生就是“婆罗门”，有的人出生就是“缺一门”')
const desc_2 = ref('网站内容来源于网上，代码纯手工编写!')
const desc_3 = ref('不断更新，自己使用！如有建议，请不要吝啬，点击留言。')
const desc_4 = ref('如需联系，请点击导航栏的留言，看到了第一时间会回复！')
</script>

<style scoped>
.footer-container {
  display: flex;
  justify-content: space-between;
  align-items: center;

  border-radius: 8px;
  user-select: none;
  background: url("@/assets/top-image/image_1.jpg") center/cover no-repeat;

  min-height: 22vh;
  box-sizing: border-box;

}

.left-footer {
  width: 20%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.right-footer {
  width: 75%;
  padding: 10px;
  box-sizing: border-box;
}

.sub-footer {
  font-family: "华文新魏", serif;
  font-size: 17px;
  color: #e67979;
  margin-top: 8px;
  line-height: 1.5;
}

.emphasize-font {
  font-size: 20px;
  font-family: "华文新魏", serif;
  color: #804aea;
  margin-top: 8px;
  line-height: 1.5;
}

.contact-section {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: 10px;
}

.hand-icon {
  color: white;
  margin-right: 5px;
}

.footer-link {
  font-size: 17px;
  color: #f19090;
  font-family: "华文新魏", serif;
  transition: all 0.3s ease;
}

.footer-link:hover {
  color: #0f0c29;
  font-size: 19px;
  text-decoration: underline;
}

.footer-image {
  width: 60%;
  max-width: 120px;
  position: relative; /* 为动画提供定位上下文 */
  animation: planeMove 8s linear infinite;
}

/* 修复并优化飞机动画 */
@keyframes planeMove {
  0% {
    transform: translateX(-50%) scale(0.8);
    opacity: 0;
  }
  20% {
    opacity: 1;
  }
  80% {
    opacity: 1;
  }
  100% {
    transform: translateX(150%) scale(0.8);
    opacity: 0;
  }
}

/* 响应式适配 - 平板设备 */
@media (max-width: 768px) {
  .footer-container {
    flex-direction: column;
    min-height: auto;
    padding: 15px;
  }

  .left-footer, .right-footer {
    width: 100%;
  }

  .left-footer {
    margin-bottom: 15px;
  }

  .footer-image {
    width: 40%;
    max-width: 100px;
  }

  .emphasize-font {
    font-size: 18px;
    text-align: center;
  }

  .sub-footer, .footer-link {
    font-size: 15px;
    text-align: center;
  }
}

/* 响应式适配 - 移动设备 */
@media (max-width: 480px) {
  .emphasize-font {
    font-size: 16px;
  }

  .sub-footer, .footer-link {
    font-size: 14px;
  }

  .footer-image {
    width: 30%;
    max-width: 80px;
  }

  .footer-container {
    padding: 10px;
    margin-top: 20px;
  }
}
</style>
