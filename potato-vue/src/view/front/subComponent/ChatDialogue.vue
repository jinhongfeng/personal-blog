<template>
  <div class="chat-container">
    <!-- 内容区域 -->
    <div class="chat-content">
      <!-- 未选联系人时的提示 -->
      <div v-if="!currentContact" class="no-contact-selected">
        <h3>请在左侧选择一个联系人开始聊天</h3>
      </div>

      <!-- 已选联系人时 -->
      <div v-else>
        <!-- 视图1：联系人信息页 -->
        <div v-if="!isChatting" class="profile-view">
          <div class="profile-header">
            <el-image :src="currentContact.img || defaultChat" alt="头像" class="profile-avatar" >
              <template #error>
                <el-image :src="defaultChat" alt="错误头像" class="profile-avatar" />
              </template>
            </el-image>
            <div class="profile-name">
              <h2>{{ currentContact.title }}</h2>
              <el-icon @click="handleAlert()" class="edit-icon"><EditPen /></el-icon>
            </div>
          </div>

          <div class="profile-details">
            <div class="detail-item">
              <span class="label">用户名</span>
              <span class="value">{{ currentContact.title }}</span>
            </div>
            <div class="detail-item">
              <span class="label">性别</span>
              <span class="value">{{ currentContact.gender || '未知' }}</span>
            </div>
            <div class="detail-item">
              <span class="label">简介</span>
              <span class="value">{{ currentContact.introduction || '该用户未填写简介' }}</span>
            </div>
          </div>

          <div class="profile-actions">
            <el-button type="primary" size="large" @click="startChat">
              <el-icon><Message /></el-icon> 发消息
            </el-button>
          </div>
        </div>

        <!-- 视图2：聊天界面 -->
        <div v-else class="chat-view">
          <!-- 聊天消息区域 -->
          <div class="chat-messages" ref="messagesContainer">
            <div v-for="(msg, idx) in messages" :key="idx"
                 :class="['message-item', msg.sender === currentUserId ? 'message-right' : 'message-left']">
              <!-- 对方的消息 -->
              <template v-if="msg.sender !== currentUserId">
                <div class="message-avatar">
                  <el-image :src="currentContact.img || defaultChat" alt="对方头像" fit="cover"/>
                </div>
                <div class="message-bubble left-bubble">
                  <div class="bubble-content">{{ msg.content }}</div>
                  <div class="message-time">{{ msg.time }}</div>
                </div>
              </template>
              <!-- 自己的消息 -->
              <template v-else>
                <div class="message-bubble right-bubble">
                  <div class="bubble-content">{{ msg.content }}</div>
                  <div class="message-time">{{ msg.time }}</div>
                </div>
                <div class="message-avatar">
                  <el-image :src="personalAvatar || defaultChat" alt="自己头像" fit="cover"/>
                </div>
              </template>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 输入区域 (固定在底部) -->
    <div v-if="currentContact && isChatting && isInputArea" class="chat-input-area" >
      <el-input
          v-model="inputMessage"
          type="textarea"
          placeholder="请输入消息..."
          class="message-input"
          :rows="3"
          resize="none"
          @keyup.enter.native="sendMessage"
          @keyup.ctrl.enter.native="inputMessage += '\n'"
      />
      <el-button type="primary" @click="sendMessage">发送</el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, onMounted, nextTick } from "vue";
import { defaultChat } from "@/utils/defaultConfig";
import { EditPen, Message } from "@element-plus/icons-vue";
import { ElNotification } from "element-plus";

const props = defineProps({
  currentContact: { type: Object, default: null },
  isChatting: { type: Boolean, default: false },
  isInputArea: { type: Boolean, default: true }
});

const emit = defineEmits(['update:currentContact', 'update:is-chatting']);

// 点击“发消息”时，通知父组件切换到“聊天页”
const startChat = () => {
  emit('update:is-chatting', true);
};

const inputMessage = ref("");
const messages = ref([
  { content: "你好，很高兴认识你！", sender: "other_user", time: new Date().toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" }) },
]);
const currentUserId = ref("current_user");
const messagesContainer = ref(null);

// 发送者的头像
const personalAvatar = ref("");
// 切换联系人时，重置消息列表
watch(() => props.currentContact, (newContact) => {
  if (newContact) {
    messages.value = [
      { content: `你好，我是${newContact.title}！`, sender: "other_user", time: new Date().toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" }) },
    ];
  }
});

const scrollToBottom = () => {
  nextTick(() => {
    if (messagesContainer.value) {
      const container = messagesContainer.value;
      container.scrollTop = container.scrollHeight;
    }
  });
};

// 发送消息逻辑
const sendMessage = () => {
  if (!inputMessage.value.trim() || !props.currentContact) return;
  const newMessage = {
    content: inputMessage.value,
    sender: currentUserId.value,
    time: new Date().toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" }),
  };
  messages.value.push(newMessage);
  inputMessage.value = "";

  scrollToBottom();

  setTimeout(() => {
    messages.value.push({
      content: `我收到了你的消息："${newMessage.content}"`,
      sender: "other_user",
      time: new Date().toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" }),
    });
    scrollToBottom();
  }, 1000);

};

// 编辑按钮提示
const handleAlert = () => {
  ElNotification.info({ title: '该按钮功能暂未启用', message: '后续将支持编辑联系人信息',});
};

// 自动滚动到最新消息
watch(messages, scrollToBottom);
// 当切换到聊天视图时，也应滚动到底部
watch(() => props.isChatting, (newVal) => {
  if (newVal) scrollToBottom();
});
onMounted(() => {
  if (props.isChatting) {
    scrollToBottom();
  }
});
</script>

<style scoped>
.chat-container {
  display: flex;
  flex-direction: column;

  height: 75vh; /* 撑开整个容器 */
  background-color: #f0f2f5;
}

.chat-content {
  flex: 1;
  overflow-y: auto;
  display: flex;
  /* flex-direction: column; */
  justify-content: space-between;
  max-height: 550px;
}

/* 未选联系人提示 */
.no-contact-selected {
  color: #909399;
  margin: auto;
}

/* 联系人信息页样式 */
.profile-view {
  width: 600px;
  max-width: 600px;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 30px;
}

.profile-header {
  display: flex;
  align-items: center;
  margin-bottom: 40px;
}

.profile-avatar {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  margin-right: 20px;
  border: 4px solid #fff;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.profile-name {
  display: flex;
  align-items: center;
}

.profile-name h2 {
  margin: 0;
  font-size: 24px;
  color: #333;
}

.edit-icon {
  margin-left: 15px;
  color: #ff4b2b;
  cursor: pointer;
  font-size: 20px;
}

.profile-details {
  width: 100%;
  max-width: 400px;
  background-color: #fff;
  border-radius: 12px;
  padding: 25px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.05);
  margin-bottom: 40px;
}

.detail-item {
  display: flex;
  padding: 15px 0;
  border-bottom: 1px solid #f0f0f0;
}

.detail-item:last-child {
  border-bottom: none;
}

.label {
  width: 100px;
  font-family: "楷体", serif;
  color: #797979;
  font-size: 16px;
}

.value {
  flex: 1;
  font-family: "微软雅黑 Light", serif;
  color: #333;
  font-size: 16px;
}

/* 聊天界面样式 */
.chat-view {

  width: 650px;
  display: flex;
  flex-direction: column;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  display: flex;
  flex-direction: column;

  gap: 15px;
}

.message-item {
  display: flex;
  align-items: flex-start;
  max-width: 80%;
}

.message-left {
  align-self: flex-start;
}

.message-right {
  align-self: flex-end;
}

.message-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  overflow: hidden;
  margin: 0 10px;
  flex-shrink: 0;
}

.message-bubble {
  padding: 10px 14px;
  border-radius: 8px;
  word-break: break-all;
  position: relative;
  max-width: calc(100% - 60px);
}

.left-bubble {
  background-color: #ffffff;
  border: 1px solid #e4e7ed;
}

.right-bubble {
  background-color: #95ec69;
}

.bubble-content {
  font-size: 15px;
  line-height: 1.4;
}

.message-time {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
  text-align: right;
}

/* 输入区域样式 */
.chat-input-area {
  display: flex;
  gap: 10px;
  padding: 10px;
  background-color: #ffffff;
  border-top: 1px solid #e4e7ed;
}

.message-input {
  flex: 1;
  min-height: 70px;
  max-height: 120px;
  resize: none;
}
</style>
