<template>
  <div class="common-layout">
    <el-container class="layout-container">
      <!-- 头部内容-->
      <el-header>
        <FrontHeader />
      </el-header>
      <!-- 显示内容-->
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </div>
</template>

<script setup>

import FrontHeader from "@/components/FrontHeader.vue";
</script>

<style scoped>


</style>
