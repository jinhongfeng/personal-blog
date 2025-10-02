<template>

  <div class="top-banner"
       :style="{
          backgroundImage: `url('${backgroundImage}')`,
          backgroundRepeat: 'no-repeat',
          backgroundPosition: 'center center',
          backgroundSize: 'cover'
        }"></div>

  <div class="message-page">
    <!-- 带动画的背景装饰 -->
    <div class="background-decoration">
      <div class="gradient-overlay"></div>
      <div class="pattern"></div>
      <!-- 背景动画元素 -->
      <div class="animated-elements">
        <div class="element" :style="{animationDelay: '0s'}"></div>
        <div class="element" :style="{animationDelay: '0.5s'}"></div>
        <div class="element" :style="{animationDelay: '1s'}"></div>
        <div class="element" :style="{animationDelay: '1.5s'}"></div>
        <div class="element" :style="{animationDelay: '2s'}"></div>
      </div>
    </div>

    <!-- 页面标题区域 -->
    <div class="page-header">
      <h1 class="page-title">留言反馈</h1>
      <p class="page-subtitle">您的每一条建议都很重要</p>
    </div>

    <!-- 留言表单卡片 -->
    <div class="form-card">
      <el-card class="custom-card">
        <!-- 表单头部 -->
        <div class="form-header">
          <el-icon class="header-icon"><ChatRound /></el-icon>
          <h2 class="form-title">请告诉我们您的想法</h2>
        </div>

        <!-- 表单内容 -->
        <el-form
            :model="form"
            :rules="formRules"
            ref="formRef"
            label-width="auto"
            class="message-form"
        >
          <!-- 昵称输入 -->
          <el-form-item
              label="昵称"
              prop="nickname"
              class="form-item"
          >
            <el-input
                v-model="form.nickname"
                placeholder="怎么称呼您？"
                :class="{ 'input-focused': focusStates.nickname }"
                @focus="focusStates.nickname = true"
                @blur="focusStates.nickname = false"
            />
          </el-form-item>

          <!-- 主题选择 -->
          <el-form-item
              label="主题"
              prop="theme"
              class="form-item"
          >
            <el-radio-group v-model="form.theme" class="theme-radio-group">
              <el-radio
                  v-for="item in themeOptions"
                  :key="item"
                  :value="item"
                  class="theme-radio"
              >
                {{ item }}
              </el-radio>
            </el-radio-group>
          </el-form-item>

          <!-- 内容输入（减少textarea高度） -->
          <el-form-item
              label="内容"
              prop="content"
              class="form-item"
          >
            <el-input
                v-model="form.content"
                type="textarea"
                :rows="4"
            placeholder="请输入您的留言内容..."
            :class="{ 'input-focused': focusStates.content }"
            @focus="focusStates.content = true"
            @blur="focusStates.content = false"
            />
            <div class="char-count">
              {{ form.content.length }} / 500
            </div>
          </el-form-item>

          <!-- 联系方式 -->
          <el-form-item
              label="联系方式"
              prop="contact"
              class="form-item"
          >
            <el-input
                v-model="form.contact"
                placeholder="请输入邮箱或手机号"
                :class="{ 'input-focused': focusStates.contact }"
                @focus="focusStates.contact = true"
                @blur="focusStates.contact = false"
            />
          </el-form-item>
        </el-form>

        <!-- 表单按钮 -->
        <div class="form-actions">
          <el-button
              type="default"
              class="form-btn reset-btn"
              @click="showClearDialog = true"
          >
            <el-icon><Refresh /></el-icon>
            清除
          </el-button>
          <el-button
              type="primary"
              class="form-btn submit-btn"
              @click="handleSubmit"
          >
            <el-icon><Finished /></el-icon>
            提交留言
          </el-button>
        </div>
      </el-card>
    </div>

    <!-- 清除确认对话框 -->
    <el-dialog
        v-model="showClearDialog"
        title="确认清除"
        width="320px"
        :close-on-click-modal="false"
    >
      <p class="dialog-content">您确定要清除所有已填写的内容吗？</p>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="showClearDialog = false">取消</el-button>
          <el-button type="primary" @click="handleClear">确认</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>


<script setup>
import { reactive, ref, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import {ChatRound, Finished, Refresh} from '@element-plus/icons-vue';
import request from '@/utils/request';

const backgroundImage = require("@/assets/top-image/image_2.jpg")
// 表单引用
const formRef = ref(null);

// 表单数据
const form = reactive({
  nickname: '',
  theme: '',
  content: '',
  contact: ''
});

// 输入框焦点状态
const focusStates = reactive({
  nickname: false,
  content: false,
  contact: false
});

// 对话框状态
const showClearDialog = ref(false);
// 主题选项
const themeOptions = ['建议', '询问', '联系', '其他'];

// 表单验证规则
const formRules = {
  nickname: [
    { required: true, message: '请输入您的昵称', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        const sensitiveWords = ['爸爸', '傻逼'];
        if (sensitiveWords.some(word => value.trim() === word)) {
          callback(new Error('昵称包含敏感词，请重新输入'));
        } else {
          callback();
        }
      },
      trigger: 'blur'
    }
  ],
  theme: [
    { required: true, message: '请选择留言主题', trigger: 'change' }
  ],
  content: [
    { required: true, message: '请输入留言内容', trigger: 'blur' },
    { min: 10, message: '留言内容不能少于10个字', trigger: 'blur' },
    { max: 500, message: '留言内容不能超过500个字', trigger: 'blur' }
  ],
  contact: [
    { required: true, message: '请输入联系方式', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        // 验证手机号或邮箱格式
        const phoneReg = /^1[3-9]\d{9}$/;
        const emailReg = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;

        if (!phoneReg.test(value) && !emailReg.test(value)) {
          callback(new Error('请输入有效的手机号或邮箱'));
        } else {
          callback();
        }
      },
      trigger: 'blur'
    }
  ]
};

// 处理表单提交
const handleSubmit = async () => {
  formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        // 处理匿名情况
        if (!form.nickname.trim()) {
          form.nickname = '陌生人';
        }

        // 提交表单数据
        const res = await request.post('/message', form);

        if (res.code === '200') {
          // 显示成功提示
          ElMessage.success('提交成功！请等待联系您！')
          // 重置表单
          formRef.value.resetFields();
        } else {
          ElMessage.error('提交失败：' + (res.msg || '服务器错误，请稍后重试'));
        }
      } catch (error) {
        ElMessage.error('网络错误，请稍后重试');
      }
    }
  });
};

// 处理清除表单
const handleClear = () => {
  formRef.value.resetFields();
  showClearDialog.value = false;
};

// 页面加载动画
onMounted(() => {
  const page = document.querySelector('.message-page');
  setTimeout(() => {
    page.classList.add('page-loaded');
  }, 100);
});
</script>

<style scoped>

.top-banner {
  animation: slideDown 0.8s ease-out forwards;;
}

/* 页面基础样式 */
.message-page {
  min-height: 100vh;
  padding: 1rem;  /* 减少整体内边距 */
  position: relative;
  opacity: 0;
  transform: translateY(20px);
  transition: opacity 0.8s ease-out, transform 0.8s ease-out;

}

.message-page.page-loaded {
  opacity: 1;
  transform: translateY(0);
}

/* 背景装饰 */
.background-decoration {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: -1;
  overflow: hidden;
}

.gradient-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 350px;  /* 减少高度 */

  /* 渐变背景动画 */
  background-size: 200% 200%;
  animation: gradientShift 15s ease infinite;
}

/* 背景渐变动画 */
@keyframes gradientShift {
  0% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
  100% { background-position: 0% 50%; }
}

@keyframes patternMove {
  0% { background-position: 0 0, 10px 10px; }
  100% { background-position: 20px 20px, 30px 30px; }
}

/* 背景动画元素 */
.animated-elements {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
}

.element {
  position: absolute;
  border-radius: 50%;
  background: rgb(211, 21, 232);
  backdrop-filter: blur(5px);
  animation: float 15s infinite ease-in-out;
}

/* 随机大小和位置的动画元素 */
.element:nth-child(1) { width: 60px; height: 60px; top: 10%; left: 20%; }
.element:nth-child(2) { width: 100px; height: 100px; top: 60%; left: 70%; }
.element:nth-child(3) { width: 40px; height: 40px; top: 30%; left: 80%; }
.element:nth-child(4) { width: 80px; height: 80px; top: 80%; left: 30%; }
.element:nth-child(5) { width: 50px; height: 50px; top: 40%; left: 10%; }

/* 浮动动画 */
@keyframes float {
  0% { transform: translateY(0) translateX(0); }
  50% { transform: translateY(-20px) translateX(10px); }
  100% { transform: translateY(0) translateX(0); }
}

/* 页面标题 */
.page-header {
  margin-top: 7vh;
  text-align: center;
  margin-bottom: 1.5rem;  /* 大幅减少底部间距 */
  padding-top: 1.5rem;   /* 减少顶部间距 */
  color: #fff;
  text-shadow: 0 2px 10px rgba(0, 0, 0, 0.15);
}

.page-title {
  font-size: 2rem;  /* 缩小字体 */
  font-weight: 700;
  margin-bottom: 0.5rem;  /* 减少底部间距 */
  letter-spacing: 0.3px;
}

.page-subtitle {
  font-size: 1rem;  /* 缩小字体 */
  opacity: 0.9;
  max-width: 600px;
  margin: 0 auto;
}

/* 表单卡片容器 */
.form-card {
  max-width: 700px;  /* 减少最大宽度 */
  margin: 0 auto;
  padding: 0 0.5rem;
}

/* 自定义卡片样式（减少阴影和圆角） */
.custom-card {
  border: none;
  border-radius: 12px;  /* 减少圆角 */
  box-shadow: 0 6px 20px -5px rgba(0, 0, 0, 0.1);  /* 减少阴影 */
  overflow: hidden;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  background-color: #fff;
}

.custom-card:hover {
  transform: translateY(-3px);  /* 减少悬停提升 */
  box-shadow: 0 8px 25px -5px rgba(0, 0, 0, 0.15);
}

/* 表单头部（压缩内边距） */
.form-header {
  display: flex;
  align-items: center;
  gap: 8px;  /* 减少间距 */
  padding: 1rem 1rem 0.5rem;  /* 减少内边距 */
  border-bottom: 1px solid #f1f5f9;
}

.header-icon {
  color: #6366f1;
  font-size: 24px;  /* 缩小图标 */
  background-color: #f0fdfa;
  width: 36px;  /* 缩小图标容器 */
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.form-title {
  font-size: 1.2rem;  /* 缩小标题 */
  font-weight: 600;
  color: #1e293b;
  margin: 0;
}

/* 表单样式（压缩内边距） */
.message-form {
  padding: 1rem;  /* 减少内边距 */
}

/* 表单项（大幅压缩间距） */
.form-item {
  margin-bottom: 0.8rem;  /* 大幅减少项间距 */
  transition: all 0.3s ease;
}

.el-form-item__label {
  font-weight: 500;
  color: #475569;
  padding-right: 8px;  /* 减少标签右间距 */
  font-size: 0.95rem;  /* 缩小标签字体 */
}

/* 输入框样式（减少圆角和内边距） */
.el-input, .el-textarea {
  border-radius: 8px;  /* 减少圆角 */
  border-color: #e2e8f0;
  transition: all 0.3s ease;
  font-size: 0.95rem;
}

.el-input.input-focused, .el-textarea.input-focused {
  border-color: #818cf8;
  box-shadow: 0 0 0 2px rgba(99, 102, 241, 0.1);  /* 减少焦点阴影 */
}

.el-input__wrapper, .el-textarea__wrapper {
  border-radius: 8px;
  padding: 0 12px;  /* 减少输入框内边距 */
}

.el-textarea__wrapper {
  min-height: 100px !important;  /* 限制textarea高度 */
}

/* 字数统计（压缩字体和间距） */
.char-count {
  text-align: right;
  font-size: 0.8rem;  /* 缩小字体 */
  color: #94a3b8;
  margin-top: 0.3rem;  /* 减少间距 */
}

/* 主题单选按钮（压缩间距） */
.theme-radio-group {
  display: flex;
  flex-wrap: wrap;
  gap: 0.6rem;  /* 减少选项间距 */
  margin-top: 0.3rem;  /* 减少上边距 */
}

.theme-radio {
  display: flex;
  align-items: center;
  padding: 6px 12px;  /* 减少内边距 */
  border-radius: 6px;  /* 减少圆角 */
  cursor: pointer;
  transition: all 0.2s ease;
  font-size: 0.95rem;  /* 缩小字体 */
}

/* 表单按钮（压缩间距和大小） */
.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 0.8rem;  /* 减少按钮间距 */
  padding: 1rem;  /* 减少内边距 */
  border-top: 1px solid #f1f5f9;
  background-color: #f8fafc;
  border-radius: 0 0 12px 12px;
  .el-button + .el-button {
    margin-left: 0 !important; /* 设为 0 取消间距，!important 确保优先级（可选，若默认样式优先级高则加） */
  }
}

.form-btn {
  padding: 8px 20px;  /* 减少按钮内边距 */
  border-radius: 8px;  /* 减少圆角 */
  font-weight: 500;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 6px;  /* 减少图标与文字间距 */
  font-size: 0.95rem;  /* 缩小字体 */
}

/* 响应式设计调整 */
@media (max-width: 768px) {
  .page-title {
    font-size: 1.8rem;
  }

  .form-actions {
    flex-direction: column;
    gap: 0.6rem;
  }

  .form-btn {
    width: 100%;
    padding: 10px;
  }
}
</style>
