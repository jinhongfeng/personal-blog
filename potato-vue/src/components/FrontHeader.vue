<template>
  <div>
    <transition name="fade">
      <div
          class="toolbar-content"
          v-show="isVisible"
          @mouseleave="isHovered = false"
          @mouseenter="isHovered = true"
      >
        <!-- 网站名称 -->
        <div class="toolbar-title" @click="handleAddress('/welcome')">
          <h2 :class="{ 'text-white': scrollTop > 80 || isHovered || isScrollingUp}">
            {{ webName }}
          </h2>
        </div>

        <!-- 桌面导航列表 -->
        <div class="toolbar-navigation desktop-nav">
          <div class="scroll-menu">
            <!-- 首页 -->
            <div class="nav-item-wrapper" @click="handleAddress('/front/home')">
              <div class="my-menu">
                🏡 <span :class="{ 'text-white': scrollTop > 80 || isHovered || isScrollingUp}">首页</span>
              </div>
            </div>

            <!-- 记录下拉菜单 -->
            <div class="nav-item-wrapper dropdown-wrapper">
              <el-dropdown
                  trigger="hover"
                  placement="bottom"
                  class="dropdown-container"
              >
                <div class="dropdown-trigger">
                  <div class="my-menu">
                    📒 <span :class="{ 'text-white': scrollTop > 80 || isHovered || isScrollingUp}">记录</span>
                    <el-icon :size="14" class="dropdown-arrow" :class="{ 'text-white': scrollTop > 80 || isHovered || isScrollingUp}">
                      <ArrowDown />
                    </el-icon>
                  </div>
                </div>
                <template #dropdown>
                  <el-dropdown-menu class="custom-dropdown-menu">
                    <el-dropdown-item v-for="(item, index) in pageAddress" :key="index" class="custom-dropdown-item">
                      <div class="dropdown-item-content" @click="handleAddress(item.address)">
                        <span class="dropdown-item-icon">{{ getIconByIndex(index) }}</span>
                        <span class="dropdown-item-text">{{ item.title }}</span>
                      </div>
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>

            <!-- 朋友圈 -->
            <div class="nav-item-wrapper" @click="handleAddress('/front/circle')">
              <div class="my-menu">
                💬 <span :class="{ 'text-white': scrollTop > 80 || isHovered || isScrollingUp}">朋友圈</span>
              </div>
            </div>

            <!-- 爱情买卖 -->
            <div class="nav-item-wrapper" @click="handleAddress('/front/love')">
              <div class="my-menu">
                ❤️‍🔥 <span :class="{ 'text-white': scrollTop > 80 || isHovered || isScrollingUp}">爱情买卖</span>
              </div>
            </div>

            <!-- 旅拍 -->
            <div class="nav-item-wrapper" @click="handleAddress('/front/travel')">
              <div class="my-menu">
                🌏 <span :class="{ 'text-white': scrollTop > 80 || isHovered || isScrollingUp}">旅拍</span>
              </div>
            </div>

            <!-- 百宝箱下拉菜单 -->
            <div class="nav-item-wrapper dropdown-wrapper">
              <el-dropdown
                  trigger="hover"
                  placement="bottom"
                  class="dropdown-container"
              >
                <div class="dropdown-trigger">
                  <div class="my-menu">
                    🗃️ <span :class="{ 'text-white': scrollTop > 80 || isHovered || isScrollingUp}">百宝箱</span>
                    <el-icon :size="14" class="dropdown-arrow" :class="{ 'text-white': scrollTop > 80 || isHovered || isScrollingUp}">
                      <ArrowDown />
                    </el-icon>
                  </div>
                </div>
                <template #dropdown>
                  <el-dropdown-menu class="custom-dropdown-menu">
                    <el-dropdown-item v-for="(item, index) in TreasureBox" :key="index" class="custom-dropdown-item">
                      <div class="dropdown-item-content" @click="handleAddress(item.address)">
                        <span class="dropdown-item-icon">{{ item.icon }}</span>
                        <span class="dropdown-item-text">{{ item.title }}</span>
                      </div>
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>

            <!-- 留言 -->
            <div class="nav-item-wrapper" @click="handleAddress('/front/message')">
              <div class="my-menu">
                📪 <span :class="{ 'text-white': scrollTop > 80 || isHovered || isScrollingUp}">留言</span>
              </div>
            </div>

            <!-- 关于 -->
            <div class="nav-item-wrapper" @click="handleAddress('/concern')">
              <div class="my-menu">
                💻️ <span :class="{ 'text-white': scrollTop > 80 || isHovered || isScrollingUp}">关于</span>
              </div>
            </div>

            <!-- 个人中心下拉菜单 -->
            <div class="nav-item-wrapper center-box">
              <el-dropdown placement="bottom-end" class="user-dropdown">
                <el-avatar class="user-avatar" :size="36" :src="personalAvatar" />
                <template #dropdown>
                  <el-dropdown-menu class="custom-dropdown-menu user-dropdown-menu">
                    <el-dropdown-item class="custom-dropdown-item">
                      <div class="dropdown-item-content" @click="handleAddress('/login')">
                        <span class="dropdown-item-icon">🔑</span>
                        <span class="dropdown-item-text">登录</span>
                      </div>
                    </el-dropdown-item>
                    <el-dropdown-item class="custom-dropdown-item">
                      <div class="dropdown-item-content" @click="handleLogout()">
                        <span class="dropdown-item-icon">🚪</span>
                        <span class="dropdown-item-text">退出</span>
                      </div>
                    </el-dropdown-item>
                    <el-dropdown-item class="custom-dropdown-item">
                      <div class="dropdown-item-content" @click="handleAddress('/back/home')">
                        <span class="dropdown-item-icon">⚙️</span>
                        <span class="dropdown-item-text">后台</span>
                      </div>
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
          </div>
        </div>

        <!-- 移动端菜单按钮 -->
        <div class="mobile-menu-btn" @click="showMobileMenu = !showMobileMenu">
          <el-icon :size="24" :class="{ 'text-white': scrollTop > 80 || isHovered || isScrollingUp}">
            <Menu />
          </el-icon>
        </div>
      </div>
    </transition>

    <!-- 移动端导航菜单 -->
    <transition name="slide-up">
      <div class="mobile-menu" v-if="showMobileMenu">
        <div class="mobile-menu-content">
          <!-- 关闭按钮 -->
          <div class="mobile-close-btn" @click="showMobileMenu = false">
            <el-icon :size="24"><Close /></el-icon>
          </div>

          <!-- 移动端导航列表 -->
          <div class="mobile-nav-list">
            <div class="mobile-nav-item" @click="handleAddress('/front/home'); showMobileMenu = false">
              🏡 首页
            </div>

            <!-- 记录下拉 -->
            <div class="mobile-nav-item dropdown-mobile" @click="toggleDropdown('records')">
              📒 记录
              <el-icon :size="16" class="dropdown-icon">
                <ArrowDown v-if="!dropdownOpen.records" />
                <ArrowUp v-if="dropdownOpen.records" />
              </el-icon>

              <div class="mobile-dropdown" v-if="dropdownOpen.records">
                <div
                    class="mobile-dropdown-item"
                    v-for="(item, index) in pageAddress"
                    :key="index"
                    @click="handleAddress(item.address); showMobileMenu = false"
                >
                  {{ getIconByIndex(index) }} {{ item.title }}
                </div>
              </div>
            </div>

            <div class="mobile-nav-item" @click="handleAddress('/front/circle'); showMobileMenu = false">
              💬 朋友圈
            </div>

            <div class="mobile-nav-item" @click="handleAddress('/front/love'); showMobileMenu = false">
              ❤️‍🔥 爱情买卖
            </div>

            <div class="mobile-nav-item" @click="handleAddress('/front/travel'); showMobileMenu = false">
              🌏 旅拍
            </div>

            <!-- 百宝箱下拉 -->
            <div class="mobile-nav-item dropdown-mobile" @click="toggleDropdown('treasure')">
              🗃️ 百宝箱
              <el-icon :size="16" class="dropdown-icon">
                <ArrowDown v-if="!dropdownOpen.treasure" />
                <ArrowUp v-if="dropdownOpen.treasure" />
              </el-icon>

              <div class="mobile-dropdown" v-if="dropdownOpen.treasure">
                <div
                    class="mobile-dropdown-item"
                    v-for="(item, index) in TreasureBox"
                    :key="index"
                    @click="handleAddress(item.address); showMobileMenu = false"
                >
                  {{ item.icon }} {{ item.title }}
                </div>
              </div>
            </div>

            <div class="mobile-nav-item" @click="handleAddress('/front/message'); showMobileMenu = false">
              📪 留言
            </div>

            <div class="mobile-nav-item" @click="handleAddress('/concern'); showMobileMenu = false">
              💻️ 关于
            </div>

            <div class="mobile-nav-item" @click="handleAddress('/login'); showMobileMenu = false">
              🔑 登录
            </div>

            <div class="mobile-nav-item" @click="handleLogout(); showMobileMenu = false">
              🚪 退出
            </div>

            <div class="mobile-nav-item" @click="handleAddress('/back/home'); showMobileMenu = false">
              ⚙️ 后台
            </div>
          </div>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { getCurrentInstance, onMounted, onUnmounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { Menu, Close, ArrowDown, ArrowUp } from '@element-plus/icons-vue'
import personalAvatar from "@/assets/personAvatar.jpg";

// 网站名称
const webName = ref('POTATO')

// 路由实例
const router = useRouter()

// 页面地址数据
const pageAddress = ref([
  { title: '我的博客', address: '/front/blog' },
  { title: '个人日记', address: '/front/diary' },
  { title: '学习笔记', address: '/front/note' }
])

const TreasureBox = ref([
  { title: '音乐', address: '/music', icon: '🎵' },
  { title: '小工具', address: '/front/tools', icon: '🔧' },
])

// 状态管理
const isHovered = ref(false);
const isVisible = ref(true);
const scrollTop = ref(0);
const isScrollingUp = ref(false);
const lastScrollTop = ref(0);
const showMobileMenu = ref(false);
const dropdownOpen = ref({
  records: false,
  treasure: false
});

// 为下拉菜单项提供图标
const getIconByIndex = (index) => {
  const icons = ['📝', '📖', '✏️'];
  return icons[index] || '📌';
}

// 处理滚动事件
const handleScroll = () => {
  const currentScrollTop = window.pageYOffset || document.documentElement.scrollTop;
  scrollTop.value = currentScrollTop;

  // 判断滚动方向
  isScrollingUp.value = currentScrollTop < lastScrollTop.value;

  // 控制导航栏显示/隐藏
  if (currentScrollTop <= 80) {
    isVisible.value = true;
  } else if (currentScrollTop > lastScrollTop.value && currentScrollTop > 80) {
    isVisible.value = false;
  } else {
    isVisible.value = true;
  }

  // 滚动时关闭移动菜单
  if (showMobileMenu.value && Math.abs(currentScrollTop - lastScrollTop.value) > 10) {
    showMobileMenu.value = false;
  }

  lastScrollTop.value = currentScrollTop;
}

// 监听滚动事件
onMounted(() => {
  window.addEventListener('scroll', handleScroll, { passive: true });
  // 初始化滚动位置
  handleScroll();
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll);
})

// 处理地址跳转
const handleAddress = (url) => {
  router.push(url);
}

// 处理退出登录
const handleLogout = () => {
  const { proxy } = getCurrentInstance();

  // 清除本地存储中的token
  localStorage.removeItem('token');

  // 重置用户状态
  if (proxy?.$store) {
    proxy.$store.commit('resetUser');
  }

  // 跳转到登录页
  proxy?.$router.push('/login');

  // 显示退出提示
  proxy?.$message.success('退出成功');
}

// 切换移动端下拉菜单
const toggleDropdown = (type) => {
  dropdownOpen.value[type] = !dropdownOpen.value[type];

  // 关闭其他下拉菜单
  Object.keys(dropdownOpen.value).forEach(key => {
    if (key !== type) {
      dropdownOpen.value[key] = false;
    }
  });
}
</script>

<style scoped>
.toolbar-content {
  width: 100%;
  height: 60px;
  position: fixed;
  z-index: 999;
  user-select: none;
  transition: all 0.3s ease-in-out;
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: space-between;
  top: 0;
  left: 0;
  margin: 0;
  padding: 0 20px;
  box-sizing: border-box;
  background-color: rgba(0, 0, 0, 0.7);
}

.toolbar-content:hover {
  background-color: rgba(0, 0, 0, 0.7);
}

/* 导航栏显示/隐藏动画 */
.fade-enter-active, .fade-leave-active {
  transition: opacity 0.3s, transform 0.3s;
}
.fade-enter-from, .fade-leave-to {
  opacity: 0;
  transform: translateY(-100%);
}

/* 背景样式 */
.bg-transparent {
  background-color: transparent;
}
.bg-black {
  background-color: rgba(0, 0, 0, 0.7);
}
.shadow-md {
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
}

/* 文本颜色 */
.text-white {
  color: white !important;
}

/* 网站标题 */
.toolbar-title {
  color: white;
  cursor: pointer;
  transition: transform 0.3s ease;
}
.toolbar-title:hover {
  transform: scale(1.1);
}
.toolbar-title h2 {
  margin: 0;
  font-size: 1.5rem;
  transition: color 0.3s ease;
}

/* 导航容器样式 */
.toolbar-navigation {
  flex: 1;
  display: flex;

  justify-content: right;
}

/* 导航项样式 */
.scroll-menu {
  list-style: none;
  margin: 0;
  padding: 0;
  display: flex;
  height: 100%;
  align-items: center;
  gap: 12px;
}

.nav-item-wrapper {
  cursor: pointer;
  position: relative;
}

.my-menu {
  display: flex;
  align-items: center;
  padding: 0 12px;
  height: 100%;
  color: white;
  transition: transform 0.2s ease, color 0.3s ease;
  white-space: nowrap;
}
.my-menu:hover {
  transform: scale(1.1);
}
.my-menu span {
  margin-left: 5px;
  font-size: 17px;
  transition: color 0.3s ease;
}

/* 下拉菜单箭头 */
.dropdown-arrow {
  margin-left: 5px;
  transition: transform 0.2s ease;
}
.dropdown-wrapper:hover .dropdown-arrow {
  transform: translateY(2px) rotate(180deg);
  transition: transform 0.2s ease;
}

/* 下拉菜单样式优化 */
.dropdown-container {
  cursor: pointer;
}

/* 核心优化：下拉菜单居中显示并美化 */
:deep(.custom-dropdown-menu) {
  background-color: rgba(255, 255, 255, 0.95);
  border-radius: 12px;
  padding: 12px 0;
  margin-top: 8px;
  box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.1), 0 8px 10px -6px rgba(0, 0, 0, 0.1);
  border: none;
  min-width: 200px;
  transform-origin: center top;
  animation: dropdownFadeIn 0.25s ease-out;
}

/* 下拉菜单项样式 */
:deep(.custom-dropdown-item) {
  color: #333;
  padding: 0;
  margin: 0 8px;
  transition: all 0.2s ease;
  cursor: pointer;
  height: auto;
  line-height: normal;
}

.dropdown-item-content {
  display: flex;
  align-items: center;
  padding: 10px 16px;
  border-radius: 8px;
  transition: all 0.2s ease;
}

.dropdown-item-content:hover {
  background-color: #f0f2f5;
  transform: translateX(4px);
}

.dropdown-item-icon {
  margin-right: 10px;
  width: 20px;
  text-align: center;
}

.dropdown-item-text {
  flex: 1;
  text-align: left;
}

/* 用户头像下拉菜单 */
:deep(.user-dropdown-menu) {
  margin-top: 8px;
  min-width: 160px;
}

/* 个人头像 */
.user-avatar {
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}
.user-avatar:hover {
  transform: scale(1.1);
  box-shadow: 0 0 10px rgba(255, 255, 255, 0.3);
}
.center-box {
  display: flex;
  align-items: center;
  margin-left: 10px;
}

/* 移动端菜单按钮 */
.mobile-menu-btn {
  display: none;
  cursor: pointer;
  padding: 8px;
  color: white;
}

/* 移动端导航菜单 */
.mobile-menu {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.95);
  z-index: 998;
  display: flex;
  justify-content: center;
  align-items: center;
}

.mobile-menu-content {
  width: 90%;
  max-width: 400px;
  height: 90%;
  position: relative;
}

.mobile-close-btn {
  position: absolute;
  top: 0;
  right: 0;
  color: white;
  cursor: pointer;
  padding: 10px;
  transition: transform 0.2s ease;
}
.mobile-close-btn:hover {
  transform: scale(1.1);
}

.mobile-nav-list {
  margin-top: 50px;
  display: flex;
  flex-direction: column;
  gap: 8px;

}

.mobile-nav-item {
  color: white;
  padding: 15px 20px;
  background-color: rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.mobile-nav-item:hover {
  background-color: rgba(255, 255, 255, 0.15);
  transform: translateX(5px);
}

.dropdown-mobile {
  position: relative;
}

.dropdown-icon {
  transition: transform 0.2s ease;
}

.mobile-dropdown {
  margin-top: 8px;
  margin-left: 10px;
  display: flex;
  flex-direction: column;
  gap: 5px;
  animation: fadeIn 0.2s ease;
}

.mobile-dropdown-item {
  color: white;
  padding: 12px 20px;
  background-color: rgba(255, 255, 255, 0.05);
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
}
.mobile-dropdown-item:hover {
  background-color: rgba(255, 255, 255, 0.1);
  padding-left: 25px;
}

/* 动画效果 */
@keyframes dropdownFadeIn {
  from {
    opacity: 0;
    transform: scale(0.95) translateY(-10px);
  }
  to {
    opacity: 1;
    transform: scale(1) translateY(0);
  }
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

/* 移动端菜单动画 */
.slide-up-enter-active, .slide-up-leave-active {
  transition: transform 0.3s ease, opacity 0.3s ease;
}
.slide-up-enter-from {
  transform: translateY(100%);
  opacity: 0;
}
.slide-up-leave-to {
  transform: translateY(100%);
  opacity: 0;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .my-menu span {
    font-size: 15px;
  }
  .desktop-nav {
    display: none;
  }

  .mobile-menu-btn {
    display: block;
  }
  .scroll-menu {
    gap: 8px;
  }
  .toolbar-title h2 {
    font-size: 1.5rem;
  }
}

@media (max-width: 768px) {
  .desktop-nav {
    display: none;
  }

  .mobile-menu-btn {
    display: block;
  }

  .toolbar-title h2 {
    font-size: 1.25rem;
  }
}

/* 样式穿透 */
:deep(.el-tooltip__trigger:focus-visible) {
  outline: unset;
}

:deep(.el-dropdown-menu__item) {
  padding: 0 !important;
}
</style>
