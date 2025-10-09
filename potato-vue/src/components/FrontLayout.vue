<template>
    <el-container class="layout-container">
      <!-- 头部内容-->
<!--      <el-header>-->

<!--      </el-header>-->
      <!-- 显示内容-->
      <el-main >
        <FrontHeader />
        <router-view />
      </el-main>
      <!-- 底部内容-->
      <el-footer>
        <FrontFoot />
      </el-footer>

      <!-- 右侧跟随滚动的回顶组件 -->
      <RopeEffect />
      <!-- 点击效果 -->
      <ClickEffect />
      <!-- 鼠标拖尾 -->
      <MouseTail />

      <!-- 右下角固定按钮：显示滚动百分比 + 回顶 -->
      <ScrollingUp />
      <!-- 右下角固定按钮：页面换颜色 -->
      <ConvertColor />

    </el-container>

</template>

<script setup>

import FrontHeader from "@/components/FrontHeader.vue";
import FrontFoot from "@/components/FrontFoot.vue";
import RopeEffect from "@/components/subComponents/RopeEffect.vue";
import ScrollingUp from "@/components/subComponents/ScrollingUp.vue";
import ClickEffect from "@/components/subComponents/ClickEffect.vue";
import MouseTail from "@/components/subComponents/MouseTail.vue";
import ConvertColor from "@/components/subComponents/ConvertColor.vue";

</script>

<style scoped>

.layout-container {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  position: relative;
}

el-main {
  position: relative;
  z-index: 10;
  flex: 1;
}

::v-deep .el-main {
  --el-main-padding: 0 !important; /* 移除默认内边距变量 */
  padding: 0 !important; /* 直接设置内边距为0 */
  position: relative;
  z-index: 10;
  flex: 1;
  overflow: auto;
}

el-header, el-footer {
  flex-shrink: 0; /* 防止头部和底部被挤压 */
}

.el-footer {
  min-height: 80px;
  --el-footer-padding: 0;

}
@media (max-width: 768px) {
  .el-footer {
    height: 40px;
  }
}
</style>
