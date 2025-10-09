<template>
  <div class="full-container">
    <!-- 全屏背景图 -->
    <div class="page-background" :style="{ backgroundImage: `url(${currentBgUrl || defaultBg})` }"></div>
    <!-- 背景切换组件 -->
    <ToggleBackgroundSelector
        v-model:show="isBgSelectorVisible"
        @change="handleBgChange"
    />
    <!-- 聊天框组件 -->
    <ChatBox />
  </div>
</template>

<script setup>
import ToggleBackgroundSelector from "@/view/front/subComponent/ToggleBackgroundSelector.vue";
import ChatBox from "@/view/front/subComponent/ChatBox.vue";
import { ref } from "vue";
import {defaultBg} from "@/utils/defaultConfig";

// 当前背景索引 & 面板显隐
const isBgSelectorVisible = ref(false);
const currentBgUrl = ref('');


const handleBgChange = (bgObject) => {
  if (bgObject && bgObject.url) {
    currentBgUrl.value = bgObject.url;
  }
};
</script>

<style scoped>
.full-container {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  overflow: hidden;
  display: flex;
  justify-content: center;
  align-items: center;
}

.page-background {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-size: cover;
  background-position: center;
  z-index: -1;
}
</style>
