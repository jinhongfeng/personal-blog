<template>
  <div v-if="isLoading" class="loading-overlay">
    <div class="spinner"></div>
    <p class="loading-text">加载中...</p>
  </div>

  <div class="contact-container" v-else>
    <!-- 左侧功能区 -->
    <div class="contact-left">
      <div class="avatar-wrapper">
        <el-image :src="personalArray.avatar || defaultAvatar" alt="个人头像" fit="cover" class="avatar-img"/>
      </div>
      <div class="left-nav">
        <div class="nav-item" v-for="(item, index) in componentMap" :key="index"
             @click="handleComponentChange(item)" :class="{ active: currentComponent === item.title }">
          <el-image :src="item.img" alt="应用图标" class="nav-img" />
          <span class="nav-font">{{ item.title }}</span>
        </div>
      </div>
    </div>
    <!-- 中间好友请求区 -->
    <div class="contact-center">
      <!-- 顶部：搜索框 + 联系人信息 -->
      <div class="contact-top">
        <div class="top-center-wrapper">
          <el-input v-model="searchValue" placeholder="搜索" class="search-input" :prefix-icon="Search" />
        </div>
      </div>
      <!-- 下方内容区 -->
      <div class="contact-center-bottom">
        <div class="bottom-center-left">
          <div class="request-title">
            <div v-if="currentComponent === '聊天'" class="request-title-wrapper" @click="handleNoticeClick(noticeData[0])">
              <div class="request-img">
                <el-image :src="require('@/assets/chatBox/notice.png')" alt="公告" fit="cover"/>
              </div>
              <span class="request-font ml-10">公告页面</span>
            </div>
            <div v-if="currentComponent === '好友'" class="request-title-wrapper">
              <div class="request-img">
                <el-image :src="require('@/assets/chatBox/useradd.png')" alt="好友" fit="cover"/>
              </div>
              <span class="request-font ml-10">好友请求</span>
            </div>
            <div v-if="currentComponent === '群聊'" class="request-title-wrapper">
              <div class="request-img">
                <el-image :src="require('@/assets/chatBox/groupchat.png')" alt="群聊" fit="cover"/>
              </div>
              <span class="request-font ml-10">群聊列表</span>
            </div>
          </div>
          <div class="mt-10">{{ currentDataList.length }} 位联系人</div>
          <!-- 下方的联系人信息 -->
          <div class="contact-list">
            <div class="contact-item"
                 v-for="(fri, friIndex) in currentDataList"
                 :key="friIndex"
                 @click="handleContactClick(fri)"
                 :class="{ active: currentContact && currentContact.title === fri.title }">
              <div class="contact-avatar ml-2">
                <el-image :src="fri.img || defaultChat" alt="头像" fit="cover">
                  <template #error>
                    <el-image :src="defaultChat" alt="默认头像" fit="cover"  />
                  </template>
                </el-image>
              </div>
              <div class="contact-name">{{ fri.title }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <!-- 右侧聊天区域 -->
    <div class="contact-right">
      <div class="top-right-wrapper">
        <div v-if="currentContact" >
          <el-image :src="currentContact.img" alt="头像" class="header-avatar" >
            <template #error>
              <el-image :src="defaultChat" alt="错误头像" class="header-avatar" />
            </template>
          </el-image>
          <span class="header-name">{{ currentContact.title }}</span>
        </div>
      </div>

      <div>
        <ChatDialogue v-model:currentContact="currentContact"
                      v-model:is-chatting="isChatting"/>
      </div>
    </div>
  </div>
</template>

<script setup>
import {defaultAvatar, defaultChat, defaultWebName} from "@/utils/defaultConfig";
import {nextTick, onMounted, reactive, ref} from "vue";
import {Search} from "element-plus";
import ChatDialogue from "@/view/front/subComponent/ChatDialogue.vue";

// 控制加载状态
const isLoading = ref(true);

const searchValue = ref("");
const personalArray = ref({
  avatar: '',
  contacts: [],
});
const chatList = ref([]);
const contactList = ref([
  { title: '站长' || defaultWebName, img: '' || defaultAvatar, gender: '男', introduction: '遇事不决，可问春风' },
  { title: '李四', img: 'https://cube.elemecdn.com/3/28/37513937e16f159150ddd5532768jpeg.jpeg', gender: '男', introduction: '前端开发者' }
]);
const groupChat = ref([
  { title: '前端技术交流群', img: 'https://cube.elemecdn.com/6/94/4d3ea53c084bad6931a56d5158ajpeg.jpeg', message: '' }
]);
const componentMap = reactive([
  {title: "聊天", img: require('@/assets/chatBox/chat.png'), data: chatList, url: ""},
  {title: "好友", img: require('@/assets/chatBox/contact.png'), data: contactList, url: ""},
  {title: "群聊", img: require('@/assets/chatBox/group.png'), data: groupChat, url: ""},
]);

const currentComponent = ref('聊天');
const currentDataList = ref([]);
const currentContact = ref(null);
const isChatting = ref(false); // true: 直接进入聊天页面

const handleComponentChange = (item) => {
  currentComponent.value = item.title;
  currentDataList.value = item.data;
  currentContact.value = null;
  isChatting.value = false;
};

const handleContactClick = (contact) => {
  currentContact.value = contact;
  // 点击联系人时，先显示“信息页”
  isChatting.value = false;
};

const noticeData = ref([
  { title: '公告', img: require('@/assets/chatBox/notice.png'), message: '今天免费啊！', role: 'notice' }
])
const handleNoticeClick = (item) => {
  currentContact.value = item;
  isChatting.value = true;
}

onMounted(() => {
  // 简化加载逻辑，使用 setTimeout 模拟网络请求或资源加载
  setTimeout(() => {
    nextTick(() => {
      isLoading.value = false;
      if (componentMap.length > 0) {
        handleComponentChange(componentMap[0]);
      }
    });
  }, 800); // 模拟0.8秒的加载时间
});
</script>

<style scoped>
/* ... (loading-overlay, spinner, loading-text, @keyframes spin 保持不变) ... */
.loading-overlay { position: fixed; top: 0; left: 0; width: 100%; height: 100%; background-color: rgba(255, 255, 255, 0.9); display: flex; flex-direction: column; justify-content: center; align-items: center; z-index: 9999; }
.spinner { width: 50px; height: 50px; border: 5px solid #f3f3f3; border-top: 5px solid #409eff; border-radius: 50%; animation: spin 1s linear infinite; }
.loading-text { margin-top: 20px; font-size: 18px; color: #606266; }
@keyframes spin { 0% { transform: rotate(0deg); } 100% { transform: rotate(360deg); } }


.contact-container {
  display: flex;
  background-color: #ffffff;
  border-radius: 10px;
  position: absolute;
  width: 70vw;
  max-width: 1000px;
  height: 80vh;
  top: 50%;
  left: 50%;
  transform: translateX(-50%) translateY(-50%);
  overflow: hidden;
  user-select: text;
  --sidebar-width: 240px;
  --avatar-width: 48px;
}

.contact-left {
  width: 60px;
  padding: 10px;
  border-right: 1px solid #e4e7ed;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.avatar-wrapper {
  width: var(--avatar-width);
  height: var(--avatar-width);
  overflow: hidden;
  border-radius: 50%;
}
.avatar-img { width: 100%; height: 100%; object-fit: cover; }

.left-nav { width: 100%; margin-top: 20px; }
.nav-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  justify-content: center;
  gap: 5px;
  cursor: pointer;
  color: #606266;
  transition: background-color 0.2s;
  padding: 10px 0;
  &.active {
    color: #409eff;
    background-color: #f0f9ff;
  }
  .nav-img { width: calc(var(--avatar-width) - 10px); height: calc(var(--avatar-width) - 10px); }
  .nav-font { color: #666666; font-size: 0.8rem; }
}

.contact-center {
  display: flex;
  flex-direction: column;
}

.contact-top {
  padding: 10px 20px;
  height: 60px;
  display: flex;
  align-items: center;
  background-color: #fff;
  border-bottom: 1px solid #e4e7ed;
}
.top-center-wrapper {
  flex-grow: 1;
  max-width: 220px;
}
.search-input .el-input__wrapper {
  background-color: #f5f7fa;
}

.header-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  margin-right: 10px;
}
.header-name { font-size: 16px; color: #303133; font-weight: 500; }

.contact-center-bottom {
  display: flex;
  flex: 1;
}
.bottom-center-left {
  width: var(--sidebar-width);
  box-shadow: 3px 0 6px #e6e6e6;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.request-title {
  padding: 15px;
  cursor: pointer;
}
.request-title-wrapper {
  display: flex;
  align-items: center;
}
.request-img {
  width: 50px;
  height: 50px;
  margin-right: 10px;
}
.request-font {
  font-size: 17px;
  color: #333;
}
.mt-10 {
  padding: 0 15px;
  color: #8e8e93;
  margin-top: 10px;
}

.contact-list {
  flex: 1;
  overflow-y: auto;
}
.contact-item {
  display: flex;
  align-items: center;
  padding: 10px 16px;
  cursor: pointer;
  transition: background-color 0.2s;
  &:hover { background-color: #f5f7fa; }
  &.active { background-color: #eaf2ff; color: #409eff; }
}
.contact-avatar {
  width: 36px;
  height: 36px;
  border-radius: 8px;
  overflow: hidden;
  margin-right: 12px;
}
.contact-name {
  color: #303133;
}
.contact-right {
  flex: 1;
  position: relative;
}
.top-right-wrapper {
  padding: 10px 20px;
  height: 60px;
  display: flex;
  align-items: center;
  background-color: #fff;
  border-bottom: 1px solid #e4e7ed;
}
.bottom-right {
  flex: 1;
  position: relative;
}


</style>
