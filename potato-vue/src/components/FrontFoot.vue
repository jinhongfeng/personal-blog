<template>
  <footer class="footer-container mt-10">

    <div class="right-footer">
      <div>
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

    </div>
  </footer>
</template>

<script setup>
import {ref} from "vue";
import router from "@/router";

// 描述文本
const desc_1 = ref('有的人出生就是“婆罗门”，有的人出生就是“缺一门”')
const desc_2 = ref('网站内容来源于网上，代码纯手工编写!')
const desc_3 = ref('不断更新，自己使用！如有建议，请不要吝啬，点击留言。')
const desc_4 = ref('如需联系，请点击导航栏的留言，看到了第一时间会回复！')
</script>

<style scoped>
.footer-container {
  display: flex;
  justify-content: center;
  align-items: center;
  border-radius: 15px;
  user-select: none;
  background: url("@/assets/desktop.jpg") center/cover no-repeat;

  min-height: 15vh;
  box-sizing: border-box;
  position: relative;
  overflow: hidden;
}

.footer-container::before {
  content: '';
  position: absolute;

  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  z-index: 1;
}

.right-footer {
  padding: 20px;
  box-sizing: border-box;

  position: relative;
  z-index: 2;

  font-family: "棉花糖", serif;
  font-weight: bold;
  color: #f4ece1;
  line-height: 1.6;
  text-align: center;
}

.sub-footer {
  margin-top: 8px;
}

.emphasize-font {
  margin-top: 8px;
  font-size: 1.2em;
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
  color: #ffffff;
  transition: all 0.3s ease;
  &:hover {
    color: #fff;
    font-size: 19px;

  }
}

@media (max-width: 768px) {
  .footer-container {
    flex-direction: column;
    min-height: auto;
    padding: 15px;
  }
  .right-footer {
    width: 100%;
  }
  .emphasize-font {
    font-size: 18px;
  }
  .sub-footer, .footer-link {
    font-size: 15px;
  }
}
@media (max-width: 480px) {
  .emphasize-font {
    font-size: 16px;
  }
  .sub-footer, .footer-link {
    font-size: 14px;
  }
  .footer-container {
    padding: 10px;
    margin-top: 20px;
  }
}
</style>
