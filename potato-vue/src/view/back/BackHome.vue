<template>
  <div class="admin-home-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1>后台管理中心</h1>
      <div class="user-info">
        <el-avatar :size="40" :src="userAvatar" class="user-avatar" />
        <div class="user-details">
          <div class="username">管理员</div>
          <div class="last-login">上次登录: {{ lastLoginTime }}</div>
        </div>
      </div>
    </div>

    <div class="content-grid">
      <!-- 左侧欢迎区域 -->
      <el-card class="welcome-card">
        <div class="welcome-header">
          <el-icon class="welcome-icon"><House /></el-icon>
          <h2>欢迎回来！</h2>
        </div>
        <el-divider />
        <div class="welcome-message">
          <p>您好！欢迎来到个人信息管理系统。</p>
          <p>在这里您可以管理个人资料、发布内容和查看数据统计。</p>
        </div>
        <div class="inspiration-quote">
          <el-icon class="quote-icon"><Quote /></el-icon>
          <p>{{ information }}</p>
        </div>
        <div class="system-status">
          <div class="status-item">
            <el-icon><CheckCircle /></el-icon>
            <span>系统状态正常</span>
          </div>
          <div class="status-item">
            <el-icon><Cloud /></el-icon>
            <span>服务器负载: {{ serverLoad }}%</span>
          </div>
          <div class="status-item">
            <el-icon><Clock /></el-icon>
            <span>当前时间: {{ currentTime }}</span>
          </div>
        </div>
      </el-card>

      <!-- 中间快捷操作 -->
      <div class="quick-actions">
        <el-card class="action-card">
          <div slot="header" class="card-title">
            <el-icon><Grid /></el-icon>
            <span>快捷操作</span>
          </div>
          <div class="action-buttons">
            <el-button
                type="primary"
                class="action-btn"
                @click="$router.push('/back/concern')"
            >
              <el-icon><User /></el-icon>
              <span>个人信息管理</span>
            </el-button>
            <el-button
                type="success"
                class="action-btn"
                @click="$router.push('/back/note')"
            >
              <el-icon><EditPen /></el-icon>
              <span>学习笔记</span>
            </el-button>
            <el-button
                type="info"
                class="action-btn"
                @click="$router.push('/back/circle')"
            >
              <el-icon><Picture /></el-icon>
              <span>朋友圈</span>
            </el-button>
            <el-button
                type="warning"
                class="action-btn"
                @click="$router.push('/back/message')"
            >
              <el-icon><Setting /></el-icon>
              <span>留言</span>
            </el-button>
          </div>
        </el-card>

        <el-card class="stats-card">
          <div slot="header" class="card-title">
            <el-icon><BarChart /></el-icon>
            <span>数据统计</span>
          </div>
          <div class="stats-grid">
            <div class="stat-item">
              <div class="stat-label">文章总数</div>
              <div class="stat-value">{{ stats.articleNum }}</div>
              <div class="stat-trend" :class="stats.articleTrend > 0 ? 'positive' : 'negative'">
                <el-icon v-if="stats.articleTrend > 0"><ArrowUp /></el-icon>
                <el-icon v-else><ArrowDown /></el-icon>
                <span>{{ Math.abs(stats.articleTrend).toFixed(1) }}%</span>
              </div>
            </div>
            <div class="stat-item">
              <div class="stat-label">朋友圈数量</div>
              <div class="stat-value">{{ stats.circleNum }}</div>
              <div class="stat-trend" :class="stats.circleTrend > 0 ? 'positive' : 'negative'">
                <el-icon v-if="stats.circleTrend > 0"><ArrowUp /></el-icon>
                <el-icon v-else><ArrowDown /></el-icon>
                <span>{{ Math.abs(stats.circleTrend).toFixed(1) }}%</span>
              </div>
            </div>
            <div class="stat-item">
              <div class="stat-label">旅拍数量</div>
              <div class="stat-value">{{ stats.travelNum }}</div>
              <div class="stat-trend" :class="stats.travelTrend > 0 ? 'positive' : 'negative'">
                <el-icon v-if="stats.travelTrend > 0"><ArrowUp /></el-icon>
                <el-icon v-else><ArrowDown /></el-icon>
                <span>{{ Math.abs(stats.travelTrend).toFixed(1) }}%</span>
              </div>
            </div>
            <div class="stat-item">
              <div class="stat-label">留言数量</div>
              <div class="stat-value">{{ stats.messageNum }}</div>
              <div class="stat-trend" :class="stats.messageTrend > 0 ? 'positive' : 'negative'">
                <el-icon v-if="stats.messageTrend > 0"><ArrowUp /></el-icon>
                <el-icon v-else><ArrowDown /></el-icon>
                <span>{{ Math.abs(stats.messageTrend).toFixed(1) }}%</span>
              </div>
            </div>
          </div>
        </el-card>
      </div>

      <!-- 右侧最近动态 -->
      <el-card class="activities-card">
        <div slot="header" class="card-title">
          <el-icon><Time /></el-icon>
          <span>最近操作</span>
        </div>
        <div class="activities-list">
          <div
              v-for="(activity, index) in recentActivities"
              :key="index"
              class="activity-item"
          >
            <div class="activity-icon">
              <el-icon :class="activity.iconClass"></el-icon>
            </div>
            <div class="activity-content">
              <div class="activity-text">{{ activity.text }}</div>
              <div class="activity-time">{{ activity.time }}</div>
            </div>
          </div>
        </div>
        <el-button type="text" class="view-more">查看更多</el-button>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from "vue";
import {
  House, Quote, CheckCircle, Cloud, Clock, Grid,
  User, EditPen, Picture, Setting, BarChart,
  ArrowUp, ArrowDown, Time, Edit, DocumentAdd, Delete, Save
} from '@element-plus/icons-vue';
import { ElLoading, ElMessage } from 'element-plus';
import request from "@/utils/request";

// 常量定义
const DEFAULT_AVATAR = require("@/assets/personAvatar.jpg");
const QUOTES = [
  '华丽的跌倒，胜过无谓的徘徊。',
  '努力是为了更好的选择。',
  '今天的积累是明天的基石。',
  '管理时间就是管理人生。'
];

// 状态管理
const information = ref(QUOTES[0]);
const userAvatar = ref(DEFAULT_AVATAR);
const serverLoad = ref(32);
const lastLoginTime = ref('');
const currentTime = ref('');
const stats = ref({
  articleNum: 0,
  articleTrend: 0,
  circleNum: 0,
  circleTrend: 0,
  travelNum: 0,
  travelTrend: 0,
  messageNum: 0,
  messageTrend: 0
});
const recentActivities = ref([
  {
    iconClass: 'activity-save',
    text: '更新了个人信息',
    time: '今天 10:23',
    icon: Save
  },
  {
    iconClass: 'activity-add',
    text: '添加了新书籍《解忧杂货铺》',
    time: '昨天 16:45',
    icon: DocumentAdd
  },
  {
    iconClass: 'activity-edit',
    text: '编辑了个人简介',
    time: '2023-06-18 09:12',
    icon: Edit
  },
  {
    iconClass: 'activity-delete',
    text: '删除了过期的电影记录',
    time: '2023-06-17 14:30',
    icon: Delete
  }
]);

// 定时器ID管理
let timeUpdateTimer = null;
let serverLoadTimer = null;

/**
 * 格式化日期为友好显示格式
 * @param {string} dateString - 日期字符串
 * @returns {string} 格式化后的日期
 */
const formatDate = (dateString) => {
  if (!dateString) return '';

  const date = new Date(dateString);
  const now = new Date();
  const diffMs = now - date;
  const diffDays = Math.floor(diffMs / (1000 * 60 * 60 * 24));

  // 今天
  if (diffDays === 0) {
    return `今天 ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`;
  }
  // 昨天
  if (diffDays === 1) {
    return `昨天 ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`;
  }
  // 一周内
  if (diffDays < 7) {
    return `${diffDays}天前`;
  }
  // 超过一周
  return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')} ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`;
};

/**
 * 更新当前时间显示
 */
const updateCurrentTime = () => {
  const now = new Date();
  currentTime.value = now.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  });
};

/**
 * 随机更新服务器负载（模拟实时数据）
 */
const updateServerLoad = () => {
  // 在当前负载上下5%范围内波动，但不超过80%
  const newLoad = Math.min(80, Math.max(10, serverLoad.value + (Math.random() * 10 - 5)));
  serverLoad.value = Math.round(newLoad);
};

/**
 * 随机切换名言
 */
const changeRandomQuote = () => {
  const randomIndex = Math.floor(Math.random() * QUOTES.length);
  information.value = QUOTES[randomIndex];
};

/**
 * 计算增长率
 * @param {number} current - 当前值
 * @param {number} previous - 先前值
 * @returns {number} 增长率百分比
 */
const calculateGrowthRate = (current, previous) => {
  if (previous === 0) {
    return current > 0 ? 100 : 0;
  }
  return ((current - previous) / previous) * 100;
};

/**
 * 获取文章总数及增长率（日记 + 笔记 + 博客）
 */
const fetchArticleCount = async () => {
  try {
    // 获取当前数据
    const [currentDiary, currentNote, currentBlog] = await Promise.all([
      request.get('/diary'),
      request.get('/note'),
      request.get('/blog')
    ]);

    // 获取上期数据....................
    const [prevDiary, prevNote, prevBlog] = await Promise.all([
      request.get('/diary?period=previous'),
      request.get('/note?period=previous'),
      request.get('/blog?period=previous')
    ]);

    // 检查所有请求是否成功
    const currentDataValid = [currentDiary, currentNote, currentBlog].every(res => res.code === '200');
    const prevDataValid = [prevDiary, prevNote, prevBlog].every(res => res.code === '200');

    if (currentDataValid) {
      const currentTotal = currentDiary.data.length + currentNote.data.length + currentBlog.data.length;

      if (prevDataValid) {
        const prevTotal = prevDiary.data.length + prevNote.data.length + prevBlog.data.length;
        const growthRate = calculateGrowthRate(currentTotal, prevTotal);
        return { count: currentTotal, rate: growthRate };
      }

      return { count: currentTotal, rate: 0 };
    }

    ElMessage.warning('部分文章数据获取失败');
    return { count: 0, rate: 0 };
  } catch (error) {
    ElMessage.error(`文章数据获取失败: ${error.message}`);
    return { count: 0, rate: 0 };
  }
};

/**
 * 获取单个类型的数据总数及增长率
 * @param {string} url - 请求地址
 * @param {string} label - 数据类型标签，用于错误提示
 */
const fetchSingleCountWithTrend = async (url, label) => {
  try {
    // 获取当前数据
    const currentResponse = await request.get(url);
    // 获取上期数据
    const prevResponse = await request.get(`${url}?period=previous`);

    if (currentResponse.code === '200') {
      const currentCount = currentResponse.data.length;

      if (prevResponse.code === '200') {
        const prevCount = prevResponse.data.length;
        const growthRate = calculateGrowthRate(currentCount, prevCount);
        return { count: currentCount, rate: growthRate };
      }

      return { count: currentCount, rate: 0 };
    }

    ElMessage.warning(`${label}数据获取失败`);
    return { count: 0, rate: 0 };
  } catch (error) {
    ElMessage.error(`${label}数据获取失败: ${error.message}`);
    return { count: 0, rate: 0 };
  }
};

/**
 * 获取用户信息，包括上次登录时间
 */
const fetchUserInfo = async () => {
  try {
    const response = await request.get('/concern');
    if (response.code === '200') {
      // 更新头像
      if (response.data?.avatar) {
        userAvatar.value = response.data.avatar;
      }

      // 更新上次登录时间
      if (response.data?.lastLoginTime) {
        lastLoginTime.value = formatDate(response.data.lastLoginTime);
      } else {
        lastLoginTime.value = '首次登录';
      }
    }
  } catch (error) {
    ElMessage.error(`用户信息获取失败: ${error.message}`);
    lastLoginTime.value = '获取失败';
  }
};

/**
 * 加载所有统计数据
 */
const loadStatisticsData = async () => {
  const loading = ElLoading.service({
    lock: true,
    text: '加载中...',
    background: 'rgba(0, 0, 0, 0.7)'
  });

  try {
    // 并行请求提高效率
    const [articleData, circleData, travelData, messageData] = await Promise.all([
      fetchArticleCount(),
      fetchSingleCountWithTrend('/circle', '朋友圈'),
      fetchSingleCountWithTrend('/travel', '旅拍'),
      fetchSingleCountWithTrend('/message', '留言')
    ]);

    // 更新统计数据
    stats.value = {
      articleNum: articleData.count,
      articleTrend: articleData.rate,
      circleNum: circleData.count,
      circleTrend: circleData.rate,
      travelNum: travelData.count,
      travelTrend: travelData.rate,
      messageNum: messageData.count,
      messageTrend: messageData.rate
    };

    // 获取用户信息（包含上次登录时间）
    await fetchUserInfo();
  } catch (error) {
    ElMessage.error(`数据加载异常: ${error.message}`);
  } finally {
    loading.close();
  }
};

/**
 * 防抖处理函数
 * @param {Function} func - 需要防抖的函数
 * @param {number} delay - 延迟时间(ms)
 * @returns {Function} 防抖处理后的函数
 */
const debounce = (func, delay) => {
  let timer = null;
  return (...args) => {
    clearTimeout(timer);
    timer = setTimeout(() => {
      func.apply(this, args);
    }, delay);
  };
};

// 防抖处理数据加载函数，避免频繁请求
const debouncedLoadData = debounce(loadStatisticsData, 300);

/**
 * 组件初始化
 */
const initialize = () => {
  // 初始化时间
  updateCurrentTime();
  // 每分钟更新一次时间
  timeUpdateTimer = setInterval(updateCurrentTime, 60000);

  // 每30秒更新一次服务器负载
  serverLoadTimer = setInterval(updateServerLoad, 30000);

  // 每5分钟切换一次名言
  setInterval(changeRandomQuote, 300000);

  // 加载数据
  debouncedLoadData();
};

// 组件挂载时初始化
onMounted(initialize);

// 组件卸载时清理资源
onUnmounted(() => {
  if (timeUpdateTimer) clearInterval(timeUpdateTimer);
  if (serverLoadTimer) clearInterval(serverLoadTimer);
});
</script>

<style scoped>
/* 样式内容保持不变 */
.admin-home-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #e5e6eb;
}

.page-header h1 {
  font-size: 24px;
  margin: 0;
  color: #1f2329;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-avatar {
  border: 2px solid #e5e6eb;
  transition: transform 0.3s ease;
}

.user-avatar:hover {
  transform: scale(1.05);
}

.user-details {
  text-align: right;
}

.username {
  font-weight: 500;
  color: #1f2329;
}

.last-login {
  font-size: 12px;
  color: #86909c;
}

.content-grid {
  display: grid;
  grid-template-columns: 1fr 1.5fr 1fr;
  gap: 20px;
}

/* 欢迎卡片样式 */
.welcome-card {
  display: flex;
  flex-direction: column;
  gap: 15px;
  transition: box-shadow 0.3s ease;
}

.welcome-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.welcome-header {
  display: flex;
  align-items: center;
  gap: 10px;
}

.welcome-icon {
  font-size: 24px;
  color: #409eff;
}

.welcome-header h2 {
  margin: 0;
  font-size: 18px;
  color: #1f2329;
}

.welcome-message {
  line-height: 1.6;
  color: #4e5969;
  padding: 5px 0;
}

.inspiration-quote {
  background-color: #f0f7ff;
  border-left: 4px solid #409eff;
  padding: 15px;
  border-radius: 4px;
  margin: 10px 0;
  transition: background-color 0.3s ease;
}

.inspiration-quote:hover {
  background-color: #e6f4ff;
}

.quote-icon {
  color: #409eff;
  margin-right: 8px;
  opacity: 0.7;
}

.system-status {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-top: auto;
  padding-top: 10px;
  border-top: 1px dashed #e5e6eb;
}

.status-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #4e5969;
}

.status-item :deep(el-icon) {
  font-size: 16px;
}

/* 快捷操作区域 */
.quick-actions {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.card-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 500;
}

.action-buttons {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 15px;
  padding: 10px 0;
}

.action-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100px;
  gap: 8px;
  font-size: 14px;
  transition: all 0.3s ease;
}

.action-btn :deep(el-icon) {
  font-size: 24px;
}

.action-btn:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

/* 数据统计 */
.stats-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 15px;
  padding: 10px 0;
}

.stat-item {
  background-color: #fafafa;
  padding: 15px;
  border-radius: 6px;
  display: flex;
  flex-direction: column;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.stat-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.stat-label {
  font-size: 14px;
  color: #86909c;
  margin-bottom: 5px;
}

.stat-value {
  font-size: 24px;
  font-weight: 500;
  color: #1f2329;
  margin-bottom: 5px;
}

.stat-trend {
  font-size: 12px;
  display: flex;
  align-items: center;
  gap: 4px;
}

.positive {
  color: #52c41a;
}

.negative {
  color: #ff4d4f;
}

/* 最近活动 */
.activities-card {
  display: flex;
  flex-direction: column;
  transition: box-shadow 0.3s ease;
}

.activities-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.activities-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
  padding: 10px 0;
  flex-grow: 1;
}

.activity-item {
  display: flex;
  gap: 12px;
  padding-bottom: 15px;
  border-bottom: 1px dashed #e5e6eb;
  transition: background-color 0.2s ease;
  padding: 8px 0;
}

.activity-item:last-child {
  border-bottom: none;
}

.activity-item:hover {
  background-color: #fafafa;
  padding-left: 5px;
  border-radius: 4px;
}

.activity-icon {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.activity-save {
  background-color: #e6f7ff;
  color: #1890ff;
}

.activity-add {
  background-color: #f6ffed;
  color: #52c41a;
}

.activity-edit {
  background-color: #fff7e6;
  color: #faad14;
}

.activity-delete {
  background-color: #fff1f0;
  color: #ff4d4f;
}

.activity-content {
  flex-grow: 1;
}

.activity-text {
  color: #1f2329;
  margin-bottom: 4px;
}

.activity-time {
  font-size: 12px;
  color: #86909c;
}

.view-more {
  margin-top: 10px;
  color: #409eff;
  align-self: flex-start;
  transition: color 0.2s ease;
}

.view-more:hover {
  color: #1890ff;
  text-decoration: underline;
}

/* 响应式调整 */
@media (max-width: 1200px) {
  .content-grid {
    grid-template-columns: 1fr 1fr;
  }

  .activities-card {
    grid-column: span 2;
  }
}

@media (max-width: 768px) {
  .content-grid {
    grid-template-columns: 1fr;
  }

  .activities-card {
    grid-column: span 1;
  }

  .action-buttons, .stats-grid {
    grid-template-columns: 1fr;
  }

  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }

  .user-info {
    width: 100%;
    justify-content: flex-end;
  }
}

/* 动画效果 */
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

.welcome-card, .action-card, .stats-card, .activities-card {
  animation: fadeIn 0.5s ease forwards;
}

.action-card { animation-delay: 0.1s; opacity: 0; }
.stats-card { animation-delay: 0.2s; opacity: 0; }
.activities-card { animation-delay: 0.3s; opacity: 0; }
</style>
