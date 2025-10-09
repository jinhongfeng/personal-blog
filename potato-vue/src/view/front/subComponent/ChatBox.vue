<template>
  <div v-if="isLoading" class="loading-overlay">
    <div class="spinner"></div>
    <p class="loading-text">加载中...</p>
  </div>

  <div class="contact-container">
    <!-- 左侧功能区 -->
    <div class="contact-left">
      <div class="avatar-wrapper">
        <el-image :src="personalArray.avatar" alt="个人头像" fit="cover" class="avatar-img"/>
      </div>
      <div class="left-nav">
        <div class="nav-item" v-for="(item, index) in componentMap" :key="index"
             @click="handleComponentChange(item)">
          <el-image :src="item.img" alt="应用图标" class="nav-img" />
          <span class="nav-font active">{{ item.title }}</span>
        </div>
      </div>
    </div>
    <!-- 右侧聊天/好友请求区 -->
    <div class="contact-right">
      <!-- 顶部：搜索框 + 联系人信息 -->
      <div class="contact-top">
        <div class="top-left-wrapper">
          <el-input v-model="searchValue" placeholder="搜索" class="search-input" :prefix-icon="Search" />
        </div>
        <div v-if="currentContact" class="top-right-wrapper">
          <el-image :src="currentContact.img" alt="头像" class="header-avatar" >
            <template #error>
              <el-image :src="defaultChat" alt="错误头像" class="header-avatar" />
            </template>
          </el-image>
          <span class="header-name">{{ currentContact.title }}</span>
        </div>
      </div>
      <!-- 下方内容区 -->
      <div class="contact-bottom">
        <div class="bottom-left">
          <div class="request-title active">

            <div v-if="currentComponent === '聊天'" class="request-title-wrapper" @click="handleNoticeClick(noticeData[0])">
              <div class="request-img">
                <el-image :src="require('@/assets/chatBox/notice.png')" alt="麦克风" />
              </div>
              <span class="request-font ml-10">公告页面</span>
            </div>
            <div v-if="currentComponent === '好友'" class="request-title-wrapper">
              <div class="request-img">
                <el-image :src="require('@/assets/chatBox/useradd.png')" alt="好友" />
              </div>
              <span class="request-font ml-10">好友请求</span>
            </div>
            <div v-if="currentComponent === '群聊'" class="request-title-wrapper">
              <div class="request-img">
                <el-image :src="require('@/assets/chatBox/groupchat.png')" alt="群聊" />
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
                <el-image :src="fri.img" alt="头像" fit="cover">
                  <template #error>
                    <el-image :src="defaultChat" alt="默认头像" fit="cover"  />
                  </template>
                </el-image>

              </div>
              <div class="contact-name">{{ fri.title }}</div>
            </div>
          </div>
        </div>
        <div class="bottom-right">
          <ChatDialogue v-model:currentContact="currentContact"
                        v-model:is-chatting="isChatting"
                        v-model:is-input-area="isInputArea"/>
        </div>
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
  avatar: '' || defaultAvatar,
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
const isInputArea = ref(false); // true: 不显示输入框

const handleComponentChange = (item) => {
  currentComponent.value = item.title;
  currentDataList.value = item.data;
  currentContact.value = null;
  isChatting.value = false;
  isInputArea.value = true;
};

const handleContactClick = (contact) => {
  currentContact.value = contact;
  // 点击联系人时，先显示“信息页”，而非直接进入聊天
  isChatting.value = false;
  isInputArea.value = true;
};

const noticeData = ref([
  { title: '公告', img: require('@/assets/chatBox/notice.png'), message: '今天免费啊！' }
])
const handleNoticeClick = (item) => {
  currentContact.value = item;
  isChatting.value = true;
  isInputArea.value = false;
}

onMounted(() => {
  // 收集所有需要预加载的图片 URL
  const imageUrls = new Set(); // 使用 Set 避免重复加载

  // 添加个人头像
  imageUrls.add(personalArray.value.avatar || defaultAvatar);

  // 添加左侧导航图标
  componentMap.forEach(item => imageUrls.add(item.img));

  // 添加联系人、群聊、公告的头像
  [...contactList.value, ...groupChat.value, ...noticeData.value].forEach(item => {
    imageUrls.add(item.img);
  });

  // 添加默认头像
  imageUrls.add(defaultAvatar);
  imageUrls.add(defaultChat);

  // 将 Set 转换为 Promise 数组
  const imagePromises = Array.from(imageUrls).map(url => {
    return new Promise((resolve, reject) => {
      const img = new Image();
      img.src = url;
      img.onload = resolve;   // 图片加载成功
      img.onerror = resolve;  // 图片加载失败也 resolve，不要卡住整个页面
    });
  });

  // 设置一个最长加载时间
  const timeoutPromise = new Promise(resolve => setTimeout(resolve, 3000));

  // 等待所有图片加载完成，或者超时
  Promise.race([Promise.all(imagePromises), timeoutPromise]).then(() => {
    // 使用 nextTick 确保 DOM 更新完成后再隐藏加载动画
    nextTick(() => {
      isLoading.value = false;
      // 页面显示后，再初始化组件
      if (componentMap.length > 0) {
        handleComponentChange(componentMap[0]);
      }
    });
  });
});
</script>

<style scoped>

.loading-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(255, 255, 255, 0.9);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  z-index: 9999;
  transition: opacity 0.5s ease-out;
}

.spinner {
  width: 50px;
  height: 50px;
  border: 5px solid #f3f3f3;
  border-top: 5px solid #409eff;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

.loading-text {
  margin-top: 20px;
  font-size: 18px;
  color: #606266;
  font-family: "微软雅黑", sans-serif;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* 当 isLoading 为 false 时，显示主页面并添加淡入效果 */
.contact-container:not([v-if]) {
  opacity: 1;
}

.contact-container {
  display: flex;
  width: 1000px;
  height: 700px;
  background-color: #fff;
  border-radius: 8px;
  border: 1px solid #797979;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  overflow: hidden;

  opacity: 0;
  transition: opacity 0.5s ease-in;

  --sidebar-width: 240px;
  --avatar-width: 48px;
}

.contact-left {
  width: 80px;
  border-right: 2px solid #e4e7ed;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 10px 0;
}

.avatar-wrapper {
  width: var(--avatar-width);
  height: var(--avatar-width);
  overflow: hidden;
  padding: 1px;



}
.left-nav {
  width: 100%;
  margin-top: 20px;
}

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
  .nav-img {
    margin-top: 10px;
    width: calc(var(--avatar-width) - 10px);
    height: calc(var(--avatar-width) - 10px);
  }
  .nav-font {
    color: #666666;
    font-family: "楷体", serif;
    font-size: 1rem;
  }
}

.nav-item.active {
  color: #409eff;
  background-color: #f0f9ff;
}

.contact-right {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.contact-top {
  padding: 10px 20px;
  min-height: 60px;
  display: flex;
  align-items: center;
  background-color: #fff;
  border-bottom: 1px solid #e4e7ed;


}

.top-left-wrapper {
  flex-grow: 1;
  max-width: 220px;
}

.top-right-wrapper {
  display: flex;
  align-items: center;
  padding-left: 20px;

}

.header-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  margin-right: 10px;
}

.header-name {
  font-size: 16px;
  color: #303133;
  font-weight: 500;
}

.contact-bottom {
  display: flex;
  flex: 1;
  height: 100%;
}

.bottom-left {
  width: var(--sidebar-width);
  border-right: 1px solid #e4e7ed;
  display: flex;
  flex-direction: column;
}

.request-title {
  display: flex;
  align-items: center;
  padding: 5px 15px;
  cursor: pointer;
  border-radius: 4px;
  transition: background-color 0.2s;
  .request-title-wrapper {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  .request-font {
    font-family: "微软雅黑 Light",serif;
    color: #666666;
  }
  .request-img {
    display: flex;
    align-items: center;
    justify-content: center;
    color: #ffffff;
    padding: 15px 18px;
    border-radius: 50px;
    height: 100px;
    width: 100px;
  }
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
  border-radius: 4px;
  transition: background-color 0.2s;
}

.contact-item:hover {
  background-color: #f5f7fa;
}

.contact-item.active {
  background-color: #eaf2ff;
  color: #409eff;
}

.contact-avatar {
  width: 36px;
  height: 36px;
  overflow: hidden;
  margin-right: 8px;
  padding: 1px;
  border: #e6e6e6;
}

.contact-name {
  font-size: 14px;
  color: #303133;
}

.bottom-right {
  flex: 1;
  background-color: #f0f2f5;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}
</style>
