<template>
  <div class="sidebar-wrapper sidebar-collapsed">
    <el-menu
        default-active="home"
        class="el-menu-vertical-demo"
        :collapse-transition="true"
        background-color="#f5f7fa"
        text-color="#333"
        active-text-color="#ff7d00"
    >
      <!-- 首页 -->
      <el-menu-item index="home" @click="router.push(menuItems.home.address)">
        <font-awesome-icon :icon="menuItems.home.icon" />
        <template #title>
          <span class="ml-10">{{ menuItems.home.title }}</span>
        </template>
      </el-menu-item>

      <!-- 记录 -->
      <el-sub-menu index="record">
        <template #title>
          <font-awesome-icon :icon="menuItems.record.icon" />
          <span class="ml-10">{{ menuItems.record.title }}</span>
        </template>
        <el-menu-item-group>
          <el-menu-item
              index="record-blog"
              @click="router.push(menuItems.record.blog.address)"
              class="ml-2"
          >
            <font-awesome-icon :icon="menuItems.record.blog.icon" />
            <span class="ml-10">{{ menuItems.record.blog.title }}</span>
          </el-menu-item>
          <el-menu-item
              index="record-diary"
              @click="router.push(menuItems.record.diary.address)"
          >
            <font-awesome-icon :icon="menuItems.record.diary.icon" />
            <span class="ml-10">{{ menuItems.record.diary.title }}</span>
          </el-menu-item>
          <el-menu-item
              index="record-note"
              @click="router.push(menuItems.record.note.address)"
          >
            <font-awesome-icon :icon="menuItems.record.note.icon" />
            <span class="ml-10">{{ menuItems.record.note.title }}</span>
          </el-menu-item>
        </el-menu-item-group>
      </el-sub-menu>

      <!-- 朋友圈 -->
      <el-menu-item index="circle" @click="router.push(menuItems.circle.address)">
        <font-awesome-icon :icon="menuItems.circle.icon" />
        <template #title>
          <span class="ml-10">{{ menuItems.circle.title }}</span>
        </template>
      </el-menu-item>

      <!-- 爱情买卖 -->
      <el-menu-item index="love" @click="router.push(menuItems.love.address)">
        <font-awesome-icon :icon="menuItems.love.icon" />
        <template #title>
          <span class="ml-10">{{ menuItems.love.title }}</span>
        </template>
      </el-menu-item>

      <!-- 旅拍 -->
      <el-menu-item index="travel" @click="router.push(menuItems.travel.address)">
        <font-awesome-icon :icon="menuItems.travel.icon" />
        <template #title>
          <span class="ml-10">{{ menuItems.travel.title }}</span>
        </template>
      </el-menu-item>

      <!-- 留言 -->
      <el-menu-item index="message" @click="router.push(menuItems.message.address)">
        <font-awesome-icon :icon="menuItems.message.icon" />
        <template #title>
          <span class="ml-10">{{ menuItems.message.title }}</span>
        </template>
      </el-menu-item>

      <!-- 关于 -->
      <el-menu-item index="concern" @click="router.push(menuItems.concern.address)">
        <font-awesome-icon :icon="menuItems.concern.icon" />
        <template #title>
          <span class="ml-10">{{ menuItems.concern.title }}</span>
        </template>
      </el-menu-item>

      <!-- 应用 -->
      <el-menu-item index="apply" @click="router.push(menuItems.apply.address)">
        <font-awesome-icon :icon="menuItems.apply.icon" />
        <template #title>
          <span class="ml-10">{{ menuItems.apply.title }}</span>
        </template>
      </el-menu-item>

      <!-- 百宝箱 -->
      <el-sub-menu index="box">
        <template #title>
          <font-awesome-icon :icon="menuItems.box.icon" />
          <span class="ml-10">{{ menuItems.box.title }}</span>
        </template>
        <el-menu-item-group>
          <el-menu-item
              index="box-tool"
              @click="router.push(menuItems.box.tool.address)"
          >
            <font-awesome-icon :icon="menuItems.box.tool.icon" />
            <span class="ml-10">{{ menuItems.box.tool.title }}</span>
          </el-menu-item>
          <el-menu-item
              index="box-bg"
              @click="router.push(menuItems.box.bgImage.address)"
          >
            <font-awesome-icon :icon="menuItems.box.bgImage.icon" />
            <span class="ml-10">{{ menuItems.box.bgImage.title }}</span>
          </el-menu-item>
        </el-menu-item-group>
      </el-sub-menu>
    </el-menu>
  </div>
</template>

<script setup>
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome';
import router from "@/router";

// 语义化菜单配置
const menuItems = {
  home: { title: '首页', icon: 'home', address: '/back/home' },
  record: {
    title: '记录',
    icon: 'file-text',
    blog: { title: '博客', icon: 'area-chart', address: '/back/blog' },
    diary: { title: '个人日记', icon: 'calendar', address: '/back/diary' },
    note: { title: '学习笔记', icon: 'sticky-note', address: '/back/note' }
  },
  circle: { title: '朋友圈', icon: 'comments', address: '/back/circle' },
  love: { title: '爱情买卖', icon: 'heartbeat', address: '/back/love' },
  travel: { title: '旅拍', icon: 'camera', address: '/back/travel' },
  message: { title: '留言', icon: 'pencil', address: '/back/message' },
  concern: { title: '关于', icon: 'user', address: '/back/concern' },
  apply: { title: '应用', icon: 'desktop', address: '/back/apply' },
  box: {
    title: '百宝箱',
    icon: 'shopping-bag',
    tool: { title: '工具图标', icon: 'wrench', address: '/back/apps' },
    bgImage: { title: '背景图', icon: 'image', address: '/back/bgImage' }
  }
};
</script>

<style scoped>
.sidebar-wrapper {
  position: fixed;
  top: 10vh;
  left: 0;
  height: calc(100vh - 5vh);
  transition: all 0.3s ease;
  z-index: 100;
  overflow: hidden;
}

.sidebar-collapsed {
  width: 180px;
}

.el-menu-vertical-demo {
  height: 100%;
  border-right: 1px solid #ebeef5;
  transition: all 0.3s ease;
  padding-bottom: 20px;
  overflow-y: auto;
}

/* 滚动条样式保持不变 */
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

.ml-10 {
  margin-left: 10px;
}

.el-menu-item-group .el-menu-item {
  padding-left: 25px !important;

}

.el-menu--collapse .el-sub-menu .el-sub-menu__list {
  left: 100% !important;
  top: 0;
  min-width: 180px;
}

@media (max-width: 767px) {
  .sidebar-wrapper {
    top: 10vh;
    height: 100vh;
  }

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
