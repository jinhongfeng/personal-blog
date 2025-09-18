<template>
  <div
      class="sidebar-wrapper sidebar-collapsed">
    <el-menu
        default-active="0"
        class="el-menu-vertical-demo"
        :collapse-transition="true"
        background-color="#f5f7fa"
        text-color="#333"
        active-text-color="#ff7d00"
    >
      <!-- 首页 -->
      <el-menu-item index="0" @click="router.push(nameList[0].address)" >
        <font-awesome-icon :icon="nameList[0].icon" />
        <template #title>
          <span class="ml-10">{{ nameList[0].title }}</span>
        </template>
      </el-menu-item>
      <!-- 记录 -->
      <el-sub-menu index="1">
        <template #title>
          <font-awesome-icon :icon="nameList[1].icon" />
          <span class="ml-10">{{ nameList[1].title }}</span>
        </template>
        <el-menu-item-group>
          <el-menu-item index="2-1" @click="router.push(nameList[2].address)">
            <font-awesome-icon :icon="nameList[2].icon" />
            <span class="ml-10">{{ nameList[2].title }}</span>
          </el-menu-item>
          <el-menu-item index="2-2" @click="router.push(nameList[3].address)">
            <font-awesome-icon :icon="nameList[3].icon" />
            <span class="ml-10">{{ nameList[3].title }}</span>
          </el-menu-item>
          <el-menu-item index="2-3" @click="router.push(nameList[4].address)">
            <font-awesome-icon :icon="nameList[4].icon" />
            <span class="ml-10">{{ nameList[4].title }}</span>
          </el-menu-item>
        </el-menu-item-group>
      </el-sub-menu>
      <!-- 朋友圈 -->
      <el-menu-item index="3" @click="router.push(nameList[5].address)">
        <font-awesome-icon :icon="nameList[5].icon" />
        <template #title>
          <span class="ml-10">{{ nameList[5].title }}</span>
        </template>
      </el-menu-item>
      <!-- 爱情买卖 -->
      <el-menu-item index="4" @click="router.push(nameList[6].address)">
        <font-awesome-icon :icon="nameList[6].icon" />
        <template #title>
          <span class="ml-10">{{ nameList[6].title }}</span>
        </template>
      </el-menu-item>
      <!-- 旅拍 -->
      <el-menu-item index="5" @click="router.push(nameList[7].address)">
        <font-awesome-icon :icon="nameList[7].icon" />
        <template #title>
          <span class="ml-10">{{ nameList[7].title }}</span>
        </template>
      </el-menu-item>
      <!-- 留言 -->
      <el-menu-item index="6" @click="router.push(nameList[8].address)">
        <font-awesome-icon :icon="nameList[8].icon" />
        <template #title>
          <span class="ml-10">{{ nameList[8].title }}</span>
        </template>
      </el-menu-item>
      <!-- 关于 -->
      <el-menu-item index="7" @click="router.push(nameList[9].address)">
        <font-awesome-icon :icon="nameList[9].icon" />
        <template #title>
          <span class="ml-10">{{ nameList[9].title }}</span>
        </template>
      </el-menu-item>
      <!-- 应用 -->
      <el-menu-item index="8" @click="router.push(nameList[10].address)">
        <font-awesome-icon :icon="nameList[10].icon" />
        <template #title>
          <span class="ml-10">{{ nameList[10].title }}</span>
        </template>
      </el-menu-item>
      <!-- 百宝箱 -->
      <el-sub-menu index="9">
        <template #title>
          <font-awesome-icon :icon="nameList[11].icon" />
          <span class="ml-10">{{ nameList[11].title }}</span>
        </template>
        <el-menu-item-group>
          <el-menu-item index="9-1" @click="router.push(nameList[12].address)">
            <font-awesome-icon :icon="nameList[12].icon" />
            <span class="ml-10">{{ nameList[12].title }}</span>
          </el-menu-item>
          <el-menu-item index="9-2" @click="router.push(nameList[13].address)">
            <font-awesome-icon :icon="nameList[13].icon" />
            <span class="ml-10">{{ nameList[13].title }}</span>
          </el-menu-item>
        </el-menu-item-group>
      </el-sub-menu>
    </el-menu>
  </div>
</template>

<script setup>
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome';
import router from "@/router";

// 菜单数据
const nameList = [
  { title: '首页', icon: 'home', address: '/back/home' },

  { title: '记录', icon: 'file-text', address: '' },
  { title: '博客', icon: 'area-chart', address: '/back/blog' },
  { title: '个人日记', icon: 'calendar', address: '/back/diary' },
  { title: '学习笔记', icon: 'sticky-note', address: '/back/note' },
  { title: '朋友圈', icon: 'comments', address: '/back/circle' },
  { title: '爱情买卖', icon: 'heartbeat', address: '/back/love' },
  { title: '旅拍', icon: 'camera', address: '/back/travel' },
  { title: '留言', icon: 'pencil', address: '/back/message' },
  { title: '关于', icon: 'user', address: '/back/concern' },
  { title: '应用', icon: 'desktop', address: '/back/apply' },
  { title: '百宝箱', icon: 'shopping-bag', address: '' },
  { title: '音乐', icon: 'music', address: '/back/music' },
  { title: '背景图', icon: 'image', address: '/back/bgImage' },
];

</script>

<style scoped>
/* 侧边栏容器 */
.sidebar-wrapper {
  position: fixed;
  top: 10vh;
  left: 0;
  height: calc(100vh - 5vh);
  transition: all 0.3s ease;
  z-index: 100;
  overflow: hidden;
}

/* 展开状态 */
.sidebar-collapsed {
  width: 180px;
}

/* 菜单基础样式 */
.el-menu-vertical-demo {
  height: 100%;
  border-right: 1px solid #ebeef5;
  transition: all 0.3s ease;
  padding-bottom: 20px;
}

/* 菜单滚动优化 */
.el-menu-vertical-demo::-webkit-scrollbar {
  width: 6px;
}

.el-menu-vertical-demo::-webkit-scrollbar-track {
  background: #f1f1f1;
}

.el-menu-vertical-demo::-webkit-scrollbar-thumb {
  background: #ddd;
  border-radius: 3px;
}

.el-menu-vertical-demo::-webkit-scrollbar-thumb:hover {
  background: #bbb;
}

.sidebar-collapsed {
  right: -10px;
}

/* 菜单项样式优化 */
.el-menu-item, .el-sub-menu__title {
  height: 50px !important;
  line-height: 50px !important;
  transition: all 0.2s ease;
}

.el-menu-item:hover, .el-sub-menu__title:hover {
  background-color: rgba(255, 125, 0, 0.05) !important;
}

.el-menu-item.is-active {
  background-color: rgba(255, 125, 0, 0.1) !important;
}

/* 图标与文字间距 */
.ml-10 {
  margin-left: 10px;
}

/* 子菜单样式调整 */
.el-sub-menu .el-menu-item {
  padding-left: 40px !important;
}

/* 折叠状态下的样式调整 */
.el-menu--collapse .el-sub-menu .el-sub-menu__list {
  left: 100% !important;
  top: 0;
  min-width: 180px;
}

/* 移动端适配 */
@media (max-width: 767px) {
  .sidebar-wrapper {
    top: 10vh;
    height: 100vh;
  }

  /* 移动端点击展开后全屏显示 */
  .sidebar-wrapper:not(.sidebar-collapsed) {
    width: 100%;
    background-color: rgba(0, 0, 0, 0.7);
  }

  .sidebar-wrapper:not(.sidebar-collapsed) .el-menu-vertical-demo {
    width: 70%;
    max-width: 280px;
    box-shadow: 2px 0 10px rgba(0, 0, 0, 0.2);
  }

  .sidebar-collapsed .ml-10,
  .sidebar-collapsed .el-sub-menu__title span,
  .sidebar-collapsed .el-menu-item span {
    display: none !important;
  }
  .sidebar-collapsed {
    width: 60px;
  }
}

/* 平板及以上设备样式 */
@media (min-width: 768px) and (max-width: 1023px) {
  .sidebar-wrapper {
    top: 10vh;
    height: 100vh;
  }

  .sidebar-collapsed {
    width: 100px;
  }
}
</style>
