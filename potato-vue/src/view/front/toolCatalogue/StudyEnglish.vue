<template>
  <div class="page-studyEnglish-container">
    <!-- 页面头部 -->
    <div class="header-container">
      <img :src="pageIcon" alt="studyEnglish" class="header-image"/>
      <h1 class="header-title">{{ pageTitle }}</h1>
    </div>

    <!-- 控制区 -->
    <div class="control-group">
      <el-button @click="shuffleCards" size="large">随机排序</el-button>
      <el-button @click="resetProgress" size="large">重置进度</el-button>
      <el-button @click="toggleTestMode" size="large">测试模式</el-button>
      <el-button @click="showFavorites = true" size="large">收藏夹</el-button>
    </div>

    <!-- 难度控制 -->
    <div class="control-group no-wrap">
      <el-select
          v-model="selectedCount"
          placeholder="选择卡片数量"
          class="select-style"
          @change="handleCountChange"
          size="large"
      >
        <el-option :label="'5 张'" :value="5" />
        <el-option :label="'10 张'" :value="10" />
        <el-option :label="'20 张'" :value="20" />
        <el-option :label="'50 张'" :value="50" />
      </el-select>
      <el-button>难度控制</el-button>
      <el-slider v-model="difficultyControl" :format-tooltip="difficultTip"></el-slider>
    </div>

    <!-- 语速控制 -->
    <div class="control-group no-wrap">
      <el-button class="auto-play-active" v-if="isAutoPlay === true" @click="toggleAutoPlay" size="large">自动朗读(开)</el-button>
      <el-button v-else @click="toggleAutoPlay" size="large">自动朗读(关)</el-button>
      <el-button size="large">语速控制</el-button>
      <el-slider v-model="speechControl"></el-slider>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-container">
      <el-card v-for="(item, index) in cardList" :key="index" class="stat-card">
        <div class="card-content">
          <span class="card-num">{{ item.num }}</span>
          <span class="card-title">{{ item.name }}</span>
        </div>
      </el-card>
    </div>

    <!-- 进度条 -->
    <div class="progress-container">
      <el-progress
          :percentage="progress"
          :text-inside="true"
          stroke-width="20"
          stroke-linecap="round"
          striped
          :duration="10"
          style="width: 80%;"
          :color="progressColor"
      />
    </div>

    <!-- 测试模式 -->
    <div v-if="isTestMode" class="test-mode-container">
      <EnglishTextModule
          :current-card="currentCard"
          :filtered-cards="filteredCards"
          :current-card-index="currentCardIndex"
          :generate-test-options="generateTestOptions"
          :shuffle-array="shuffleArray"
          @next-card="nextCard"
          @answer-selected="handleTestAnswer"
      />
    </div>

    <!-- 学习模式卡片 -->
    <div v-else class="card-wrapper" @click="flipCard">
      <div class="card-flip" :class="{ flipped: isFlipped}">
        <!-- 正面 -->
        <div class="card-face front">
          <div class="difficulty-tag">{{ currentCard.level || '简单' }}</div>
          <div class="word-star" @click.stop="toggleFavorite">
            <el-icon :filled="currentCard.isFavorite" :class="{ 'active': currentCard.isFavorite}">
              <Star />
            </el-icon>
          </div>
          <div class="flip-hint">点击卡片翻转查看答案</div>
          <div class="word-content">
            <h2 class="word">{{ currentCard.word }}</h2>
            <p class="word-type">{{ currentCard.type }}</p>
            <p class="word-phonetic">{{ currentCard.phonetic }}</p>
          </div>
          <div class="pronunciation-btn" @click.stop="playPronunciation">
            <font-awesome-icon icon="volume-up" />
          </div>
        </div>

        <!-- 反面 -->
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
      <el-button @click="prevCard" :disabled="currentCardIndex === 0" size="large">上一张</el-button>
      <el-button @click="flipCard" size="large">翻转卡片</el-button>
      <!-- 禁用条件：1. 是最后一张 + 已回答；2. 无卡片数据 -->
      <el-button
          :disabled="(currentCardIndex === filteredCards.length - 1 && currentCard.isCorrect !== null) || filteredCards.length === 0"
          @click="nextCard" size="large"
      >
        下一张
      </el-button>
    </div>

    <!-- 收藏夹抽屉 -->
    <el-drawer
        title="我的收藏"
        :show-close="false"
        :with-header="false"
        :model-value="showFavorites"
        @update:model-value="showFavorites = $event"
        placement="right"
        :width="isMobile ? '100%' : '450px'"
        :fullscreen="isMobile"
    >
      <div class="favorites-container">
        <div class="favorites-header">
          <h2>收藏单词</h2>
          <el-button @click="showFavorites = false" size="small" class="close-btn">
            <el-icon><Close /></el-icon>
          </el-button>
        </div>
        <div v-if="favoriteCards.length === 0" class="empty-favorites">
          <p>暂无收藏的单词</p>
          <el-button @click="showFavorites = false">返回</el-button>
        </div>
        <div v-else class="favorites-list">
          <div v-for="(card, index) in favoriteCards" :key="index" class="favorite-item">
            <div class="favorite-content">
              <div class="favorite-word">{{ card.word }}</div>
              <div class="favorite-phonetic">{{ card.phonetic }}</div>
              <div class="favorite-translation">{{ card.translation }}</div>
            </div>
            <div class="favorite-actions">
              <el-button size="small" @click.stop="toggleFavoriteFromList(card)" class="remove-btn">
                <el-icon :filled="true"><Star /></el-icon>
              </el-button>
            </div>
          </div>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script setup>
import {computed, ref, watch, onMounted, onUnmounted, nextTick} from "vue";
import {Star, Close} from "@element-plus/icons-vue"
import EnglishTextModule from "@/view/front/subComponent/EnglishTextModule.vue";

// 页面基础配置
const pageTitle = ref('英语卡片学习');
const pageIcon = require('@/assets/tool/studyEnglish.svg');

// 控制参数
const difficultyControl = ref(30);
const speechControl = ref(50);

// 卡片数据
const cards = ref([
  {
    word: 'apple',
    type: '名词 (noun)',
    phonetic: '/ˈæpl/',
    example: 'I ate an apple for breakfast.',
    translation: '我早餐吃了一个苹果。',
    level: '简单',
    difficulty: 10,
    isCorrect: null,
    isFavorite: false
  },
  {
    word: 'run',
    type: '动词 (verb)',
    phonetic: '/rʌn/',
    example: 'She likes to run in the park every morning.',
    translation: '她喜欢每天早上在公园里跑步。',
    level: '简单',
    difficulty: 15,
    isCorrect: null,
    isFavorite: false
  },
  {
    word: 'beautiful',
    type: '形容词 (adjective)',
    phonetic: '/ˈbjuːtɪfl/',
    example: 'What a beautiful day it is today!',
    translation: '今天真是美好的一天！',
    level: '中等',
    difficulty: 40,
    isCorrect: null,
    isFavorite: false
  },
  {
    word: 'delicious',
    type: '形容词 (adjective)',
    phonetic: '/dɪˈlɪʃəs/',
    example: 'The food was absolutely delicious.',
    translation: '这食物非常美味。',
    level: '中等',
    difficulty: 35,
    isCorrect: null,
    isFavorite: false
  },
  {
    word: 'interesting',
    type: '形容词 (adjective)',
    phonetic: '/ˈɪntrəstɪŋ/',
    example: 'That movie was really interesting.',
    translation: '那部电影真的很有趣。',
    level: '中等',
    difficulty: 45,
    isCorrect: null,
    isFavorite: false
  },
  {
    word: 'ambiguous',
    type: '形容词 (adjective)',
    phonetic: '/æmˈbɪɡjuəs/',
    example: 'The instructions were ambiguous and confusing.',
    translation: '这些指示含糊不清，令人困惑。',
    level: '困难',
    difficulty: 75,
    isCorrect: null,
    isFavorite: false
  },
  {
    word: 'consequence',
    type: '名词 (noun)',
    phonetic: '/ˈkɑːnsɪkwens/',
    example: 'We must consider the consequences of our actions.',
    translation: '我们必须考虑我们行为的后果。',
    level: '困难',
    difficulty: 80,
    isCorrect: null,
    isFavorite: false
  }
]);

// 响应式状态
const currentCardIndex = ref(0);
const isFlipped = ref(false);
const selectedCount = ref(5);
const isAutoPlay = ref(true);
const showFavorites = ref(false);
const isTestMode = ref(false);
const isMobile = ref(false);

// 计算属性：筛选卡片（难度匹配）
const filteredCards = computed(() => {
  return cards.value.filter(card => card.difficulty <= difficultyControl.value);
});

// 计算属性：有效卡片数（选数量和筛选后取最小）
const effectiveCount = computed(() => {
  return Math.min(selectedCount.value, filteredCards.value.length);
});

// 计算属性：当前显示卡片
const currentCard = computed(() => {
  if (filteredCards.value.length === 0) {
    return {word: '无卡片数据', type: '', phonetic: '', example: '', translation: '', isFavorite: false, level: '', isCorrect: null};
  }
  return filteredCards.value[currentCardIndex.value];
});

// 计算属性：已回答/答对数量
const totalAnswered = computed(() => {
  return filteredCards.value.slice(0, effectiveCount.value).filter(card => card.isCorrect !== null).length;
});
const correctCount = computed(() => {
  return filteredCards.value.slice(0, effectiveCount.value).filter(card => card.isCorrect === true).length;
});

// 计算属性：收藏卡片
const favoriteCards = computed(() => {
  return cards.value.filter(card => card.isFavorite);
});

// 计算属性：统计数据
const cardList = computed(() => [
  {name: '总卡片数', num: effectiveCount.value.toString()},
  {name: '已回答', num: totalAnswered.value.toString()},
  {name: '答对题数', num: correctCount.value.toString()},
  {
    name: '正确率',
    num: totalAnswered.value > 0 ? `${Math.round((correctCount.value / totalAnswered.value) * 100)}%` : '0%'
  }
]);

// 计算属性：进度条百分比
const progress = computed(() => {
  return effectiveCount.value > 0 ? Math.round((totalAnswered.value / effectiveCount.value) * 100) : 0;
});

// 进度条颜色
const progressColor = computed(() => {
  const p = progress.value;
  const colors = ['#6f7ad3', '#5cb87a', '#1989fa', '#e6a23c', '#f56c6c'];
  if (p >= 0 && p < 20) return colors[0];
  if (p >= 20 && p < 40) return colors[1];
  if (p >= 40 && p < 60) return colors[2];
  if (p >= 60 && p < 80) return colors[3];
  return colors[4];
});

// 难度提示
const difficultTip = (val) => {
  if (val <= 20) return '简单';
  if (val <= 40) return '较低';
  if (val <= 60) return '适中';
  if (val <= 80) return '较高';
  return '困难';
};

// 收藏切换
const toggleFavorite = () => {
  currentCard.value.isFavorite = !currentCard.value.isFavorite;
};
const toggleFavoriteFromList = (card) => {
  card.isFavorite = !card.isFavorite;
};

// 卡片翻转
const flipCard = () => {
  isFlipped.value = !isFlipped.value;
};

// 播放发音
const playPronunciation = () => {
  console.log(`播放 ${currentCard.value.word} 的发音`);
};

// 自动朗读切换
const toggleAutoPlay = async () => {
  isAutoPlay.value = !isAutoPlay.value;
  await nextTick();
  if (isAutoPlay.value) playPronunciation();
};

// 学习模式答题处理
const handleAnswer = (isCorrect) => {
  // if (currentCard.value.isCorrect !== null) return; // 已回答则不重复处理
  currentCard.value.isCorrect = isCorrect; // 保存回答结果
  // 非最后一张则自动跳
  if (currentCardIndex.value < filteredCards.value.length - 1 && !isTestMode.value) {
    nextCard();
  }
};

// 上一张卡片
const prevCard = () => {
  if (currentCardIndex.value > 0) {
    currentCardIndex.value--;
    isFlipped.value = false;
    if (isAutoPlay.value) setTimeout(() => playPronunciation(), 300);
  }
};

// 下一张卡片（核心修改：最后一张也执行回答状态更新）
const nextCard = () => {
  // 未回答的卡片，点击“下一张”默认标记为“正确”
  if (currentCard.value.isCorrect === null) {
    currentCard.value.isCorrect = true;
  }
  // 有下一张则跳转，无则只更新状态
  if (currentCardIndex.value < filteredCards.value.length - 1) {
    currentCardIndex.value++;
    isFlipped.value = false;
    if (isAutoPlay.value) setTimeout(() => playPronunciation(), 300);
  }
};

// 测试模式答题处理
const handleTestAnswer = (isCorrect) => {
  currentCard.value.isCorrect = isCorrect; // 保存测试模式回答结果
};

// 切换卡片数量重置进度
const handleCountChange = () => {
  resetProgress();
};

// 随机排序卡片
const shuffleCards = () => {
  const currentRange = filteredCards.value.slice(0, effectiveCount.value);
  for (let i = currentRange.length - 1; i > 0; i--) {
    const j = Math.floor(Math.random() * (i + 1));
    [currentRange[i], currentRange[j]] = [currentRange[j], currentRange[i]];
  }
  currentRange.forEach((card) => {
    const originalIdx = cards.value.findIndex(c => c.word === card.word);
    if (originalIdx !== -1) cards.value[originalIdx] = card;
  });
  resetProgress();
};

// 重置进度
const resetProgress = () => {
  filteredCards.value.slice(0, effectiveCount.value).forEach(card => {
    card.isCorrect = null;
  });
  currentCardIndex.value = 0;
  isFlipped.value = false;
  if (isAutoPlay.value) setTimeout(() => playPronunciation(), 300);
};

// 切换测试模式
const toggleTestMode = () => {
  isTestMode.value = !isTestMode.value;
  isFlipped.value = false; // 切换模式时重置翻转状态
};

// 生成测试选项（子组件用）
const shuffleArray = (array) => {
  const newArr = [...array];
  for (let i = newArr.length - 1; i > 0; i--) {
    const j = Math.floor(Math.random() * (i + 1));
    [newArr[i], newArr[j]] = [newArr[j], newArr[i]];
  }
  return newArr;
};
const generateTestOptions = (currentCard, filteredCards, currentCardIndex) => {
  const correct = currentCard.translation;
  const others = filteredCards.filter((c, idx) => idx !== currentCardIndex);
  const wrongs = [];
  while (wrongs.length < 3 && others.length > 0) {
    const rIdx = Math.floor(Math.random() * others.length);
    const rCard = others.splice(rIdx, 1)[0];
    if (!wrongs.includes(rCard.translation) && rCard.translation !== correct) {
      wrongs.push(rCard.translation);
    }
  }
  return shuffleArray([correct, ...wrongs]);
};

// 屏幕尺寸检测
const checkScreenSize = () => {
  isMobile.value = window.innerWidth < 768;
};

// 监听卡片索引变化（重置翻转）
watch(currentCardIndex, () => {
  isFlipped.value = false;
  if (isAutoPlay.value) setTimeout(() => playPronunciation(), 300);
});

// 监听难度变化（重置进度）
watch(difficultyControl, () => {
  resetProgress();
});

// 初始化
onMounted(() => {
  selectedCount.value = 5;
  checkScreenSize();
  window.addEventListener('resize', checkScreenSize);
  if (isAutoPlay.value) setTimeout(() => playPronunciation(), 300);
});

// 清理
onUnmounted(() => {
  window.removeEventListener('resize', checkScreenSize);
});
</script>

<style scoped>
.page-studyEnglish-container {
  position: relative;
  min-height: 100vh;
  padding: 2rem;
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
  margin-top: 3vh;

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
  max-width: 768px;
  flex-wrap: wrap;
  padding: 5px 0;
  cursor: pointer;

  .el-slider {
    min-width: 100px;
  }

  .select-style {
    width: auto;
    min-width: 100px;
  }

  .auto-play-active {
    background-color: #39c5bb;
    opacity: 0.8;
    color: white;
    box-shadow: 0 2px 8px rgba(57, 197, 187, 0.4);
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
  height: 320px;
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

.word-star {
  align-self: flex-start;
  cursor: pointer;
  font-size: 1.2rem;
  margin-top: 5px;

  .el-icon {
    transition: color 0.3s;
  }

  .active {
    color: #FFD700;
  }

  .el-icon:not(.active) {
    color: rgba(255, 255, 255, 0.7);
  }
}

.word-content {
  text-align: center;
  color: white;

  .word {
    font-size: 3rem;
    margin: 17px 0;
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
  position: relative;
  z-index: 10;

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

:deep(.el-drawer__header) {
  display: none;
}

.favorites-container {
  display: flex;
  flex-direction: column;
  height: 100%;
  background-color: #f9f9f9;
  color: #333;
}

.favorites-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  background-color: #4a6cf7;
  color: white;

  h2 {
    margin: 0;
    font-size: 1.2rem;
  }

  .close-btn {
    background-color: rgba(255, 255, 255, 0.2);
    color: white;
    border: none;

    &:hover {
      background-color: rgba(255, 255, 255, 0.3);
    }
  }
}

.favorites-list {
  flex: 1;
  overflow-y: auto;
  padding: 15px;
}

.favorite-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  margin-bottom: 10px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  transition: transform 0.2s;
  min-height: 60px;

  &:hover {
    transform: translateY(-3px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  }
}

.favorite-content {
  flex: 1;
  margin-right: 15px;
  display: flex;
  align-items: center;
  gap: 15px;
  overflow: hidden;
}

.favorite-word {
  font-size: 1.2rem;
  font-weight: bold;
  color: #4a6cf7;
  min-width: 100px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.favorite-phonetic {
  font-style: italic;
  color: #666;
  min-width: 120px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.favorite-translation {
  color: #333;
  flex: 1;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.favorite-actions {
  display: flex;
  justify-content: flex-end;
}

.remove-btn {
  background-color: transparent;
  color: #f56c6c;
  border: none;

  &:hover {
    background-color: rgba(245, 108, 108, 0.1);
  }
}

.empty-favorites {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  color: #999;

  p {
    margin-bottom: 20px;
  }
}

.test-mode-container {
  width: 80%;
  max-width: 600px;
  margin: 10px 0;
}

/* 小设备适配（768px以下） */
@media (max-width: 768px) {
  .page-studyEnglish-container {
    padding: 1rem;
    gap: 15px;
  }

  .header-container {
    margin-top: 4vh;
    gap: 10px;

    .header-image {
      height: 40px;
    }

    .header-title {
      font-size: 1.8rem;
      letter-spacing: 0.1em;
    }
  }

  .control-group {
    gap: 8px;
    margin-top: 15px;

    .el-button {
      font-size: 1rem;
      padding: 8px 12px;
    }
  }

  .stats-container {
    margin-top: 15px;
    width: 95%;
    gap: 8px;
  }

  .stat-card {
    min-width: 120px;
  }

  .card-content {
    height: 120px;
    margin: 30px 0;
  }

  .card-num {
    font-size: 2.5rem;
    margin-bottom: 8px;
  }

  .card-title {
    font-size: 1.1rem;
  }

  .progress-container {
    width: 95%;
    margin: 15px 0;
    padding: 10px 0;

    :deep(.el-progress__text) {
      font-size: 1.5rem !important;
    }
  }

  .card-wrapper, .test-mode-container {
    width: 95%;
    height: 350px;
    margin: 15px 0;
  }

  .card-face {
    padding: 25px;
  }

  .flip-hint {
    font-size: 1.1rem;
    margin: 8px 0;
  }

  .word-content {
    .word {
      font-size: 3.5rem;
      margin: 20px 0;
    }

    .word-type {
      font-size: 1.2rem;
      margin: 8px 0;
    }

    .word-phonetic {
      font-size: 1.5rem;
    }
  }

  .example-sentence {
    margin-top: 30px;
    font-size: 1.3rem;
    line-height: 1.7;

    .example-translation {
      margin-top: 15px;
      font-size: 1.2rem;
    }
  }

  .pronunciation-btn {
    font-size: 1.8rem;
    bottom: 20px;
  }

  .card-actions {
    gap: 15px;
    margin: 35px 0 20px;

    .el-button {
      font-size: 1.2rem;
      padding: 12px 20px;
    }
  }

  .navigation-buttons {
    gap: 15px;
    margin: 25px 0 35px;

    .el-button {
      font-size: 1.2rem;
      padding: 10px 20px;
    }
  }

  .favorites-header {
    padding: 15px;

    h2 {
      font-size: 1.5rem;
    }
  }

  .favorites-list {
    padding: 15px 10px;
  }

  .favorite-item {
    padding: 15px;
    margin-bottom: 12px;
    min-height: 70px;
  }

  .favorite-content {
    gap: 10px;
  }

  .favorite-word {
    font-size: 1.5rem;
    min-width: 120px;
  }

  .favorite-phonetic {
    font-size: 1.2rem;
    min-width: 130px;
  }

  .favorite-translation {
    font-size: 1.2rem;
  }

  .empty-favorites {
    p {
      font-size: 1.2rem;
      margin-bottom: 25px;
    }

    .el-button {
      font-size: 1.2rem;
      padding: 10px 20px;
    }
  }
}

/* 中等设备适配（768px - 1300px） */
@media (min-width: 769px) and (max-width: 1500px) {

  .header-container {
    margin-top: 5vh;
    gap: 10px;

    .header-image {
      height: 40px;
    }

    .header-title {
      font-size: 2rem;
      letter-spacing: 0.3em;
    }
  }

  .control-group {
    gap: 8px;
    margin-top: 15px;
    width: auto;

    .el-button {
      font-size: 1.3rem;
      padding: 12px 15px;
    }
  }

  .stats-container {
    margin: 30px 0;
    width: 95%;
    gap: 8px;
  }

  .page-studyEnglish-container {
    padding: 1.7rem;
  }

  .progress-container {
    width: 95%;
    margin: 15px 0;
    padding: 10px 0;

    :deep(.el-progress__text) {
      font-size: 1.8rem !important;
    }
  }
  .difficulty-tag {
    font-size: 1rem;
  }
  .card-wrapper, .test-mode-container {
    width: 95%;
    height: 350px;
    margin: 30px 0;
  }

  .difficulty-tag {
    font-size: 1.2rem;
  }

  .word-star {
    font-size: 1.8rem;
  }
  .header-title {
    font-size: 2.4rem;
  }

  .card-num {
    font-size: 2.4rem;
  }

  .card-title {
    font-size: 1.3rem;
  }

  .word {
    font-size: 3.3rem;
  }

  .test-word {
    font-size: 2.3rem;
  }

  .pronunciation-btn {
    font-size: 1.8rem;
    bottom: 20px;
  }
}

</style>
