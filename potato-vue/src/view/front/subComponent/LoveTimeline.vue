<template>
  <div class="timeline-container">
    <!-- 标题 -->
    <div class="timeline-title">
      <span>点点滴滴</span>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="state-container loading-container">
      <el-loading :fullscreen="false" text="加载中..."></el-loading>
    </div>

    <!-- 错误状态 -->
    <div v-else-if="showError" class="state-container error-container">
      <el-icon class="error-icon"><Warning /></el-icon>
      <p class="error-text">{{ errorMessage }}</p>
      <el-button type="primary" @click="loadData">重试</el-button>
    </div>

    <!-- 数据加载完成但为空 -->
    <div v-else-if="LineContentList.length === 0" class="state-container empty-container">
      <el-empty description="暂无日记数据"></el-empty>
    </div>

    <!-- 正常内容 -->
    <el-timeline
        v-else
        class="content-timeline timeline-width">
      <el-timeline-item
          v-for="(line, index) in LineContentList"
          :key="index"
          :color="getTimeBasedColor(line.publish)"
          placement="top"
      >
        <el-card class="timeline-card">
          <!-- 上方内容 -->
          <div class="card-topContent">
            <span>{{ line.content }}</span>
          </div>
          <div class="divider"></div>
          <!-- 下方时间 -->
          <div class="card-bottomTime">
            <span>{{ line.publishTime }}</span>
          </div>
        </el-card>
      </el-timeline-item>
    </el-timeline>
  </div>
</template>

<script setup>
import { onMounted, ref } from "vue";
import request from '@/utils/request';
import { Warning } from "@element-plus/icons-vue";
import { ElNotification, ElLoading } from "element-plus";

// 时间线内容列表
const LineContentList = ref([
  { content: '你好，很高兴认识你。', publish: '2018-12-2 20:46' },
  { content: '今天发工资了，我一个月工资800，你猜我会给你多少? hahaha', publish: '2018-3-2 20:57' },
  { content: '你好，很高兴认识你。', publish: '2018-2-2 20:46' },
  { content: '今天发工资了，我一个月工资800，你猜我会给你多少? hahaha', publish: '2018-5-2 20:57' },
  { content: '你好，很高兴认识你。', publish: '2018-4-2 20:46' },
  { content: '今天发工资了，我一个月工资800，你猜我会给你多少? hahaha', publish: '2018-8-2 20:57' },
]);

// 状态管理
const loading = ref(true);         // 加载状态
const showError = ref(false);      // 错误状态标识
const errorMessage = ref("");      // 错误信息

// 根据时间戳生成不同的颜色（按月份）
const getTimeBasedColor = (timeStr) => {
  try {
    const date = new Date(timeStr);
    const month = date.getMonth() + 1;
    // 每个月对应不同颜色，使用element-plus的主题色
    const colors = [
      '#409EFF', '#67C23A', '#E6A23C', '#F56C6C',
      '#909399', '#97B6E9', '#85CE61', '#EABC8A',
      '#F78989', '#C0C4CC', '#5CB3FF', '#36D399'
    ];
    return colors[(month - 1) % colors.length];
  } catch (error) {
    return '#409EFF'; // 默认颜色
  }
};

// 加载数据
const loadData = async () => {
  // 重置状态
  loading.value = true;
  showError.value = false;
  errorMessage.value = "";

  try {
    // 创建局部加载实例
    const loadingInstance = ElLoading.service({
      lock: false,
      text: '加载中...',
      background: 'rgba(255, 255, 255, 0.7)'
    });

    const resLoveLine = await request.get('/loveline');

    if (resLoveLine.code === '200') {
      LineContentList.value = resLoveLine.data || [];
    } else {
      throw new Error(resLoveLine.msg || '获取数据失败！');
    }

    // 关闭加载动画
    loadingInstance.close();
    loading.value = false;
  } catch (err) {
    // 关闭加载动画
    ElLoading.service().close();

    // 更新错误状态
    loading.value = false;
    showError.value = true;
    errorMessage.value = err.message || '获取日记列表失败';

    // 显示错误通知
    ElNotification.error({
      title: '加载失败',
      message: errorMessage.value
    });
  }
};

// 页面加载时获取数据
onMounted(() => {
  loadData();
});
</script>

<style scoped>
/* 容器样式 */
.timeline-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px;
  box-sizing: border-box;
  width: 100%;
}

/* 标题样式 */
.timeline-title {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 30px;
  font-family: "华文新魏", serif;
  color: var(--articleFontColor);
  font-size: 30px;
  width: 100%;
  max-width: 1200px;
  padding: 0 20px;
  box-sizing: border-box;
}

/* 状态容器通用样式 */
.state-container {
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(10px);
  border-radius: 15px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  padding: 20px;
  width: 60%;
  min-width: 600px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  box-sizing: border-box;
}

/* 加载状态样式 */
.loading-container {
  min-height: 300px;
}

/* 错误状态样式 */
.error-container {
  text-align: center;
  padding: 60px 20px;
  min-height: 300px;
}

.error-icon {
  font-size: 64px;
  margin-bottom: 25px;
  color: #F56C6C;
  transition: transform 0.3s ease;
}

.error-container:hover .error-icon {
  transform: scale(1.1);
}

.error-text {
  font-size: 18px;
  margin-bottom: 30px;
  color: #333;
  line-height: 1.6;
}

.error-container .el-button {
  padding: 10px 24px;
  font-size: 16px;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.error-container .el-button:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 12px rgba(232, 21, 38, 0.2);
}

/* 空状态样式 */
.empty-container {
  min-height: 300px;
  padding: 60px 20px;
}

/* 时间轴容器 */
.content-timeline {
  margin-top: 30px;
  user-select: none;
}
.timeline-width {
  width: 60%;
  min-width: 600px;
}
/* 时间轴卡片 */
.timeline-card {
  padding: 10px;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  transition: box-shadow 0.3s ease;
}

.timeline-card:hover {
  box-shadow: 0 6px 15px rgba(0, 0, 0, 0.1);
}

.card-topContent {
  font-size: 16px;
  margin-bottom: 12px;
  line-height: 1.6;
  color: #333;
  display: flex;
}

.divider {
  width: 100%;
  height: 0;
  border-top: 1px dashed #ccc;
  margin: 8px 0;
}

.card-bottomTime {
  font-size: 14px;
  color: #666666;
  align-self: flex-end;
  margin-top: auto;
  display: flex;
}

/* 响应式调整 - 平板设备 */
@media (max-width: 992px) {
  .state-container,
  .content-timeline {
    width: 75%;
  }
  .timeline-width {
    width: 60%;
    min-width: 600px;
  }
}

/* 响应式调整 - 小型平板 */
@media (max-width: 768px) {
  .state-container,
  .content-timeline {
    width: 90%;
    min-width: auto;
  }

  .timeline-title {
    font-size: 24px;
  }

  .error-icon {
    font-size: 48px;
  }

  .error-text {
    font-size: 16px;
  }

  .card-topContent {
    font-size: 15px;
  }

  .timeline-width {
    width: 60%;
    min-width: 600px;
  }
}

/* 响应式调整 - 手机设备 */
@media (max-width: 576px) {
  .state-container,
  .content-timeline {
    width: 100%;
    padding: 15px;
  }

  .timeline-container {
    padding: 10px;
  }

  .timeline-title {
    font-size: 22px;
    margin-bottom: 20px;
  }

  .card-topContent {
    font-size: 14px;
    line-height: 1.5;
  }

  .card-bottomTime {
    font-size: 13px;
  }

  .error-icon {
    font-size: 40px;
    margin-bottom: 15px;
  }

  .error-text {
    font-size: 15px;
    margin-bottom: 20px;
  }

  .error-container .el-button {
    padding: 8px 16px;
    font-size: 14px;
  }

  .timeline-width {
    width: 60%;
    min-width: 400px;
  }
}

/* 响应式调整 - 小屏手机 */
@media (max-width: 360px) {
  .timeline-title {
    font-size: 20px;
  }

  .card-topContent {
    font-size: 13px;
  }

  .empty-container,
  .loading-container,
  .error-container {
    min-height: 200px;
    padding: 30px 10px;
  }

  .timeline-width {
    width: 60%;
    min-width: 380px;
  }
}
</style>
