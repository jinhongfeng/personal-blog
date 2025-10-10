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
        <div v-else >
          <!-- 聊天消息区域 -->
          <div class="chat-messages" ref="messagesContainer">
            <div v-for="(msg, idx) in messages" :key="idx"
                 :class="['message-item', msg.sender === currentUserId ? 'message-right' : 'message-left']">
              <!-- 对方的消息 -->
              <template v-if="msg.sender !== currentUserId">
                <div class="message-avatar">
                  <el-image :src="currentContact.img" alt="对方头像" fit="cover">
                    <template #error>
                      <el-image :src="defaultChat" alt="错误头像" class="header-avatar" />
                    </template>
                  </el-image>
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
          <div class="chat-input-wrapper">
            <div v-if="currentContact && isChatting && currentContact.role !== 'notice'" class="chat-input-area" >
              <el-input
                  v-model="inputMessage"
                  type="textarea"
                  placeholder="请输入消息..."
                  class="message-input"
                  :rows="3"
                  resize="none"
                  @keydown.enter.exact.prevent="sendMessage"
                  @keydown.ctrl.enter.prevent="inputMessage += '\n'"
              />
              <el-button type="primary" @click="sendMessage" :disabled="!inputMessage.trim()">发送</el-button>
            </div>
          </div>
        </div>
      </div>
    </div>

  </div>
</template>

<script setup>
import {ref, watch, nextTick, onMounted} from "vue";
import { defaultChat, defaultAvatar } from "@/utils/defaultConfig";
import { EditPen, Message } from "@element-plus/icons-vue";
import { ElNotification } from "element-plus";

const props = defineProps({
  currentContact: { type: Object, default: null },
  isChatting: { type: Boolean, default: false },
});

const emit = defineEmits(['update:currentContact', 'update:is-chatting']);

// 点击“发消息”时，通知父组件切换到“聊天页”
const startChat = () => {
  emit('update:is-chatting', true);
};

const inputMessage = ref("");
const messages = ref([]); // 初始为空，将在切换联系人时初始化
const currentUserId = ref("current_user");
const messagesContainer = ref(null); // 用于自动滚动
const personalAvatar = ref(defaultAvatar); // 为当前用户添加默认头像

// 切换联系人时，重置消息列表
watch(() => props.currentContact, (newContact) => {
  if (newContact) {
    messages.value = [
      { content: `你好，我是${newContact.title}！`, sender: "other_user", time: new Date().toLocaleTimeString('zh-CN', { hour: "2-digit", minute: "2-digit" }) },
    ];
  }
});

// 发送消息逻辑
const sendMessage = () => {
  if (!inputMessage.value.trim() || !props.currentContact) return;
  const newMessage = {
    content: inputMessage.value,
    sender: currentUserId.value,
    time: new Date().toLocaleTimeString('zh-CN', { hour: "2-digit", minute: "2-digit" }),
  };
  messages.value.push(newMessage);
  inputMessage.value = "";

  // 模拟对方回复
  setTimeout(() => {
    messages.value.push({
      content: `我收到了你的消息："${newMessage.content}"`,
      sender: "other_user",
      time: new Date().toLocaleTimeString('zh-CN', { hour: "2-digit", minute: "2-digit" }),
    });
  }, 1000);
};

// 编辑按钮提示
const handleAlert = () => {
  ElNotification.info({ title: '该按钮功能暂未启用', message: '后续将支持编辑联系人信息',});
};

// 自动滚动到最新消息
const scrollToBottom = () => {
  nextTick(() => {
    if (messagesContainer.value) {
      messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight;
    }
  });
};

// 当消息列表变化时，滚动到底部
watch(() => messages.value.length, scrollToBottom);

// 当切换到聊天视图时，也应滚动到底部
watch(() => props.isChatting, (newVal) => {
  if (newVal) {
    scrollToBottom();
  }
});

// 组件挂载时，如果已是聊天状态，也应滚动到底部
onMounted(() => {
  if (props.isChatting) {
    scrollToBottom();
  }
});
</script>

<style scoped>
.chat-container {
  height: 80vh;
  top: 0;
  left: 0;
  background-color: #f0f2f5;
}

.chat-content {
  flex: 1;
  position: relative;
  height: calc(80vh - 60px);
}

/* 未选联系人提示 */
.no-contact-selected {
  color: #909399;
  text-align: center;
  display: flex;
  justify-content: center;
  align-items: center;
  height: calc(80vh - 60px);
}

/* 联系人信息页样式 (保持不变) */
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
  color: #797979;
  font-size: 16px;
}
.value {
  flex: 1;
  color: #333;
  font-size: 16px;
}
.profile-actions {
  width: 100%;
  max-width: 400px;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 15px;
  padding: 10px 10px;
  height: calc(70vh - 100px);
  max-height: 65vh;
}

.message-item {
  display: flex;
  align-items: flex-start;
  max-width: 75%;
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
  border-radius: 8px;
  overflow: hidden;
  margin: 0 10px;
  flex-shrink: 0;
}

.message-bubble {
  padding: 8px 12px;
  border-radius: 6px;
  word-break: break-all;
  position: relative;
  max-width: calc(100% - 60px);
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
}

.left-bubble {
  background-color: #ffffff;
  border-top-left-radius: 4px;
}

.right-bubble {
  background-color: #95ec69;
  border-top-right-radius: 4px;
}

.bubble-content {
  font-size: 16px;
  line-height: 1.4;
}

.message-time {
  font-size: 11px;
  color: #999;
  margin-top: 4px;
  text-align: right;
}

.chat-input-wrapper {
  height: 120px;
  border-top: 2px solid #e5e5e5;
  display: flex;
}

.chat-input-area {
  margin-top: 10px;
  width: 100%;

  gap: 10px;
  padding: 10px;
  background-color: #ffffff;
  display: flex;
  align-items: center;
}

.message-input {
  flex: 1;
  min-height: 44px;
  max-height: 120px;

  resize: none;
  border-radius: 8px;
  border: 1px solid #d9d9d9;
  padding: 8px 12px;
  font-size: 16px;
  line-height: 1.4;
  box-sizing: border-box;

}
.message-input:focus {
  border-color: #409eff;
  outline: none;
}

.el-button {
  height: 44px; /* 与输入框高度一致 */
  padding: 0 18px;
  border-radius: 22px; /* 圆形按钮 */
}
</style>
