<template>
  <div class="page-studyEnglish-container">
    <!-- 页面头部 -->
    <div class="header-container">
      <img :src="pageIcon" alt="studyEnglish" class="header-image"/>
      <h1 class="header-title">{{ pageTitle }}</h1>
    </div>

    <!-- 控制区 -->
    <div class="control-group">
      <el-button @click="shuffleCards">随机排序</el-button>
      <el-button @click="resetProgress">重置进度</el-button>
      <el-button>测试模式</el-button>
      <el-button>收藏夹</el-button>
      <!-- 卡片数量选择 -->
      <el-select
          v-model="selectedCount"
          placeholder="选择卡片数量"
          style="width: 120px"
          @change="handleCountChange"
      >
        <!-- 固定选项为5, 10, 20, 50 -->
        <el-option :label="'5 张'" :value="5" />
        <el-option :label="'10 张'" :value="10" />
        <el-option :label="'20 张'" :value="20" />
        <el-option :label="'50 张'" :value="50" />
      </el-select>
    </div>

    <!-- 难度控制 -->
    <div class="control-group no-wrap">
      <el-button>难度控制</el-button>
      <el-slider v-model="difficultyControl" :format-tooltip="difficultTip"></el-slider>
    </div>

    <!-- 语速控制 -->
    <div class="control-group no-wrap">
      <el-button>自动朗读</el-button>
      <el-button>语速控制</el-button>
      <el-slider v-model="speechControl"></el-slider>
    </div>

    <!-- 显示的卡片数据 -->
    <div class="stats-container">
      <el-card v-for="(item, index) in cardList" :key="index" class="stat-card">
        <div class="card-content">
          <span class="card-num">{{ item.num }}</span>
          <span class="card-title">{{ item.name }}</span>
        </div>
      </el-card>
    </div>

    <!-- 完成总进度条 -->
    <div class="progress-container">
      <el-progress
          :percentage="progress"
          stroke-width="12"
          stroke-linecap="round"
          style="width: 80%;"
          :stroke-color="progressColor"
      />
    </div>

    <!-- 卡片翻转提示 -->
    <div class="flip-hint">点击卡片翻转查看答案</div>

    <!-- 具体的单词卡片 -->
    <div class="card-wrapper" @click="flipCard">
      <div class="card-flip" :class="{ flipped: isFlipped}">
        <!-- 卡片正面 -->
        <div class="card-face front">
          <div class="difficulty-tag">{{ currentCard.level || '简单' }}</div>
          <!-- 收藏按钮 -->
          <div class="word-star" @click.stop="toggleFavorite">
            <el-icon :filled="currentCard.isFavorite" :class="{ 'active': currentCard.isFavorite}">
              <Star />
            </el-icon>
          </div>
          <div class="word-content">
            <h2 class="word">{{ currentCard.word }}</h2>
            <p class="word-type">{{ currentCard.type }}</p>
            <p class="word-phonetic">{{ currentCard.phonetic }}</p>
          </div>
          <div class="pronunciation-btn" @click.stop="playPronunciation">
            <font-awesome-icon icon="volume-up" />
          </div>
        </div>

        <!-- 卡片反面 -->
        <div class="card-face back">
          <div class="example-sentence">
            <p>{{ currentCard.example }}</p>
            <p class="example-translation">{{ currentCard.translation }}</p>
          </div>
          <div class="card-actions mt-30">
            <el-button size="large" @click.stop="handleAnswer(true)" class="card-btn-right">答对了</el-button>
            <el-button size="large" @click.stop="handleAnswer(false)" class="card-btn-wrong">答错了</el-button>
          </div>

          <div class="pronunciation-btn" @click.stop="playPronunciation">
            <font-awesome-icon icon="volume-up" />
          </div>
        </div>
      </div>
    </div>

    <!-- 换卡控制 -->
    <div class="navigation-buttons">
      <el-button @click="prevCard" :disabled="currentCardIndex === 0">上一张</el-button>
      <el-button @click="flipCard">翻转卡片</el-button>
      <el-button @click="nextCard" :disabled="currentCardIndex >= effectiveCount - 1">下一张</el-button>
    </div>
  </div>
</template>

<script setup>
import {computed, ref, watch, onMounted} from "vue";
import {Star} from "@element-plus/icons-vue"

// 页面基本信息
const pageTitle = ref('英语卡片学习');
const pageIcon = require('@/assets/tool/studyEnglish.svg');

// 控制滑块
const difficultyControl = ref(30);
const speechControl = ref(50);

// 卡片数据 (恢复 isFavorite 属性)
const cards = ref([
  {
    word: 'apple',
    type: '名词 (noun)',
    phonetic: '/ˈæpl/',
    example: 'I ate an apple for breakfast.',
    translation: '我早餐吃了一个苹果。',
    level: '简单',
    isCorrect: null,
    isFavorite: false // 恢复收藏状态
  },
  {
    word: 'run',
    type: '动词 (verb)',
    phonetic: '/rʌn/',
    example: 'She likes to run in the park every morning.',
    translation: '她喜欢每天早上在公园里跑步。',
    level: '简单',
    isCorrect: null,
    isFavorite: false // 恢复收藏状态
  },
  {
    word: 'beautiful',
    type: '形容词 (adjective)',
    phonetic: '/ˈbjuːtɪfl/',
    example: 'What a beautiful day it is today!',
    translation: '今天真是美好的一天！',
    level: '中等',
    isCorrect: null,
    isFavorite: false // 恢复收藏状态
  }
]);

// 响应式状态管理
const currentCardIndex = ref(0);
const isFlipped = ref(false);
const selectedCount = ref(5); // 选中的卡片数量，默认为5

// 计算属性：总卡片数
const totalCards = computed(() => cards.value.length);

// 计算属性：**实际有效的卡片数量**
// 这是解决“当卡片不够时”问题的核心。它取“用户选择的数量”和“实际总数量”中的较小值。
const effectiveCount = computed(() => {
  return Math.min(selectedCount.value, totalCards.value);
});

// 计算属性：当前显示的卡片
const currentCard = computed(() => {
  if (cards.value.length === 0) {
    return {word: '无卡片数据', type: '', phonetic: '', example: '', translation: '', isFavorite: false};
  }
  return cards.value[currentCardIndex.value];
});

// 计算属性：已回答的卡片数 (基于有效数量)
const totalAnswered = computed(() => {
  return cards.value.slice(0, effectiveCount.value).filter(card => card.isCorrect !== null).length;
});

// 计算属性：已回答中答对的数量 (基于有效数量)
const correctCount = computed(() => {
  return cards.value.slice(0, effectiveCount.value).filter(card => card.isCorrect === true).length;
});

// 计算属性：统计数据列表（已移除“收藏数”）
const cardList = computed(() => [
  {name: '总卡片数', num: effectiveCount.value.toString()}, // 显示有效数量
  {name: '已回答', num: totalAnswered.value.toString()},
  {name: '答对题数', num: correctCount.value.toString()},
  {
    name: '正确率',
    num: totalAnswered.value > 0
        ? `${Math.round((correctCount.value / totalAnswered.value) * 100)}%`
        : '0%'
  }
]);

// 计算属性：进度条百分比 (基于有效数量)
const progress = computed(() => {
  // 增加一个微小的延迟，确保UI有时间更新
  // eslint-disable-next-line vue/no-async-in-computed-properties
  setTimeout(() => {}, 0);
  return effectiveCount.value > 0
      ? Math.round((totalAnswered.value / effectiveCount.value) * 100)
      : 0;
});

// 进度条颜色
const progressColor = computed(() => {
  const p = progress.value;
  // 使用一个数组和索引来返回颜色，逻辑更清晰，避免任何潜在的判断问题
  const colors = ['#409EFF', '#67C23A', '#E6A23C'];
  let colorIndex = 0;
  if (p >= 30 && p < 70) colorIndex = 1;
  else if (p >= 70) colorIndex = 2;
  return colors[colorIndex];
});

// 难度提示文本
const difficultTip = (val) => {
  if (val <= 20) return '没啥难度';
  if (val <= 40) return '难度较低';
  if (val <= 60) return '难度适中';
  if (val <= 80) return '很有难度';
  return '难度爆棚';
};

// 切换当前卡片的收藏状态
const toggleFavorite = () => {
  currentCard.value.isFavorite = !currentCard.value.isFavorite;
};

// 卡片翻转
const flipCard = () => {
  isFlipped.value = !isFlipped.value;
};

// 播放发音
const playPronunciation = () => {
  console.log(`播放 ${currentCard.value.word} 的发音`);
};

// 处理答题结果
const handleAnswer = (isCorrect) => {
  currentCard.value.isCorrect = isCorrect;
  if (currentCardIndex.value < effectiveCount.value - 1) {
    nextCard();
  }
};

// 上一张卡片
const prevCard = () => {
  if (currentCardIndex.value > 0) {
    currentCardIndex.value--;
    isFlipped.value = false;
  }
};

// 下一张卡片
const nextCard = () => {
  if (currentCard.value.isCorrect === null) {
    currentCard.value.isCorrect = true;
  }
  if (currentCardIndex.value < effectiveCount.value - 1) {
    currentCardIndex.value++;
    isFlipped.value = false;
  }
};

// 切换卡片数量时重置进度
const handleCountChange = () => {
  resetProgress();
};

// 随机排序卡片 (基于有效数量)
const shuffleCards = () => {
  const currentRange = cards.value.slice(0, effectiveCount.value);
  for (let i = currentRange.length - 1; i > 0; i--) {
    const j = Math.floor(Math.random() * (i + 1));
    [currentRange[i], currentRange[j]] = [currentRange[j], currentRange[i]];
  }
  cards.value.splice(0, effectiveCount.value, ...currentRange);
  resetProgress();
};

// 重置进度
const resetProgress = () => {
  cards.value.slice(0, effectiveCount.value).forEach(card => {
    card.isCorrect = null;
    // 保留收藏状态
  });
  currentCardIndex.value = 0;
  isFlipped.value = false;
};

// 卡片索引变化时重置翻转状态
watch(currentCardIndex, () => {
  isFlipped.value = false;
});

// 当总卡片数变化时，更新有效数量
watch(totalCards, () => {
  // 如果当前选择的数量大于总卡片数，下一次操作时会自动使用总卡片数
});

// 初始化时设置默认选中的卡片数
onMounted(() => {
  // 默认选中5张，但实际有效数量会被 computed 属性自动修正
  selectedCount.value = 5;
});
</script>

<style scoped>
/* ... (所有样式保持不变，已包含进度条文字样式) ... */
.page-studyEnglish-container {
  position: relative;
  min-height: 100vh;
  padding: 1rem;
  background-color: #4a6cf7;
  background-image: radial-gradient(rgba(255, 255, 255, 0.15) 1px, transparent 1px);
  background-size: 20px 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: flex-start;
  font-family: 'Arial Rounded MT Bold', sans-serif;
  gap: 10px;
  color: #e6e6e6;
  user-select: none;
}

.header-container {
  display: flex;
  align-items: center;
  gap: 12px;

  .header-image {
    height: 50px;
    width: auto;
  }

  .header-title {
    margin: 0;
    font-size: 2rem;
    letter-spacing: 0.2em;
    color: #e6e6e6;
  }
}

.control-group {
  display: flex;
  align-items: center;
  gap: 15px;
  max-width: 1000px;
  flex-wrap: wrap;
  padding: 10px 0;

  .el-slider {
    flex-grow: 1;
    min-width: 200px;
  }
}

.control-group.no-wrap {
  flex-wrap: nowrap;
}

.stats-container {
  display: flex;
  justify-content: center;
  gap: 15px;
  width: 90%;
  max-width: 1000px;
  flex-wrap: wrap;
}

.stat-card {
  flex: 1;
  min-width: 100px;
  background-color: rgba(255, 255, 255, 0.15);
  border: none;
  color: white;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s;

  &:hover {
    transform: translateY(-5px);
  }
}

.card-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100px;
  padding: 10px;
}

.card-num {
  font-size: 2rem;
  font-weight: bold;
  color: #ffffff;
  margin-bottom: 5px;
}

.card-title {
  font-size: 0.9rem;
  color: #e0e0e0;
  text-align: center;
}

.progress-container {
  width: 90%;
  max-width: 1000px;
  color: #F4E1C0;
  display: flex;
  justify-content: center;
  align-items: center;
  margin: 10px 0;
}

/* 进度条文字样式 */
:deep(.el-progress__text) {
  color: white !important;
  font-size: 1.2rem !important;
  font-weight: bold !important;
}

.flip-hint {
  color: #e0e0e0;
  font-size: 0.9rem;
  margin: 5px 0;
}

.card-wrapper {
  width: 80%;
  max-width: 600px;
  height: 350px;
  perspective: 1000px;
  margin: 10px 0;
}

.card-flip {
  position: relative;
  width: 100%;
  height: 100%;
  transition: transform 0.6s cubic-bezier(0.4, 0, 0.2, 1);
  transform-style: preserve-3d;
}

.flipped {
  transform: rotateY(180deg);
}

.card-face {
  position: absolute;
  width: 100%;
  height: 100%;
  backface-visibility: hidden;
  border-radius: 16px;
  padding: 30px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.15);
  transition: box-shadow 0.3s;

  &:hover {
    box-shadow: 0 12px 40px rgba(0, 0, 0, 0.2);
  }
}

.front {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.back {
  background: linear-gradient(135deg, #84fab0 0%, #8fd3f4 100%);
  transform: rotateY(180deg);
}

.difficulty-tag {
  position: absolute;
  top: 20px;
  right: 20px;
  background-color: rgba(255, 255, 255, 0.3);
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 0.8rem;
  color: white;
}

/* 收藏图标样式 */
.word-star {
  align-self: flex-start;
  cursor: pointer;
  font-size: 1.2rem;
  margin-top: 5px;

  .el-icon {
    transition: color 0.3s;
  }

  .active {
    color: #FFD700; /* 金色填充 */
  }

  .el-icon:not(.active) {
    color: rgba(255, 255, 255, 0.7);
  }
}

.word-content {
  text-align: center;
  color: white;
  margin-top: 30px;
}

.word {
  font-size: 3rem;
  margin: 20px 0;
  font-weight: bold;
}

.word-type {
  font-size: 1rem;
  opacity: 0.9;
  margin: 5px 0;
}

.word-phonetic {
  font-size: 1.2rem;
  opacity: 0.8;
  font-style: italic;
}

.example-sentence {
  color: #333;
  margin-top: 40px;
  font-size: 1.1rem;
  line-height: 1.6;

  .example-translation {
    margin-top: 15px;
    font-size: 1rem;
    color: #555;
    opacity: 0.9;
  }
}

.pronunciation-btn {
  align-self: flex-end;
  color: rgba(255, 255, 255, 0.8);
  cursor: pointer;
  font-size: 1.5rem;
  transition: color 0.3s;

  &:hover {
    color: white;
  }
}

.card-actions {
  display: flex;
  justify-content: center;
  gap: 15px;
  margin: 30px 0;

  .card-btn-right {
    background-color: #39c5bb;
    border-color: #39c5bb;

    &:hover {
      background-color: #2da89f;
      border-color: #2da89f;
    }
  }

  .card-btn-wrong {
    background-color: #ed8b8b;
    border-color: #ed8b8b;

    &:hover {
      background-color: #e67a7a;
      border-color: #e67a7a;
    }
  }
}

.navigation-buttons {
  display: flex;
  gap: 15px;
  margin: 20px 0 30px;
}

.el-button, .el-select {
  background-color: rgba(255, 255, 255, 0.2);
  border-color: transparent;
  color: white;
  transition: all 0.3s;

  &:hover, &:focus {
    background-color: rgba(255, 255, 255, 0.3);
    border-color: transparent;
    color: white;
  }

  &:disabled {
    background-color: rgba(255, 255, 255, 0.1);
    color: rgba(255, 255, 255, 0.5);
    cursor: not-allowed;
  }
}

.el-select .el-input__inner {
  background-color: white !important;
  border-color: rgba(255, 255, 255, 0.3);
  color: #333 !important;
}

:deep(.el-select-dropdown) {
  background-color: white !important;
  border-color: #ddd;
}

:deep(.el-select-dropdown__item) {
  color: #333 !important;

  &:hover {
    background-color: #f5f7fa !important;
  }

  &.selected {
    background-color: #eaf2ff !important;
    color: #409eff !important;
  }
}

.mt-30 {
  margin-top: 30px;
}

@media (max-width: 768px) {
  .control-group {
    width: 95%;
    gap: 10px;
  }

  .control-group.no-wrap {
    overflow-x: auto;
    padding-bottom: 5px;
  }

  .stats-container {
    width: 95%;
    gap: 10px;
  }

  .card-wrapper {
    width: 95%;
    height: 300px;
  }

  .word {
    font-size: 2.5rem;
  }

  .stat-card {
    min-width: 80px;
  }

  .card-num {
    font-size: 1.5rem;
  }
}
</style>
