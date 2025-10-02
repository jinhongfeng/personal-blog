<template>
  <div class="test-card">
    <h2 class="test-word">{{ currentCard.word }}</h2>
    <p class="test-phonetic">{{ currentCard.phonetic }}</p>

    <div class="test-options">
      <el-button
          v-for="(option, idx) in testOptions"
          :key="idx"
          class="test-option"
          :class="{
                'correct-option': currentCard.isCorrect === true && option === currentCard.translation,
                'incorrect-option': currentCard.isCorrect === false && option !== currentCard.translation
              }"
          @click="handleTestAnswer(option)"
          :disabled="currentCard.isCorrect !== null"
      >
        {{ option }}
      </el-button>
    </div>

    <!-- 答错时显示“下一张”，最后一张也可点击（用于标记回答状态） -->
    <div v-if="currentCard.isCorrect === false" class="test-result">
      <el-button @click="$emit('nextCard')" class="next-test-btn">下一题</el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, defineProps, defineEmits } from "vue";

const props = defineProps({
  currentCard: Object,
  filteredCards: Array,
  currentCardIndex: Number,
  generateTestOptions: Function,
  shuffleArray: Function,
});

const emit = defineEmits({
  nextCard: () => true,
  'answer-selected': (isCorrect) => typeof isCorrect === 'boolean'
});

const testOptions = ref([]);

// 切换卡片时重新生成选项
watch(() => props.currentCard, () => {
  testOptions.value = props.generateTestOptions(
      props.currentCard,
      props.filteredCards,
      props.currentCardIndex
  );
}, { immediate: true });

// 处理测试模式答题（只传状态，父组件保存）
const handleTestAnswer = (selectedOption) => {
  if (props.currentCard.isCorrect !== null) return;
  const isCorrect = selectedOption === props.currentCard.translation;
  emit('answer-selected', isCorrect);
  if (isCorrect) emit('nextCard'); // 答对自动跳
};
</script>

<style scoped>
.test-card {
  background-color: white;
  border-radius: 16px;
  padding: 40px 0;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.15);
  display: flex;
  flex-direction: column;
  align-items: center;
}

.test-word {
  font-size: 2.5rem;
  font-weight: bold;
  color: #4a6cf7;
  margin-bottom: 10px;
}

.test-phonetic {
  font-size: 1.2rem;
  color: #666;
  font-style: italic;
  margin-bottom: 25px;
}

.test-options {
  display: flex;
  flex-direction: column;
  gap: 10px;
  width: 100%;
}

.test-option {
  background-color: #f5f7fa;
  color: #333;
  border: 1px solid #e4e7ed;
  padding: 12px 15px;
  text-align: left;
  border-radius: 8px;
  transition: all 0.2s;

  &:hover {
    background-color: #ad69ed;
    border-color: #c6e2ff;
    color: #409eff;
  }
}

.correct-option {
  background-color: #f0f9eb !important;
  border-color: #c2e7b0 !important;
  color: #67c23a !important;
}

.incorrect-option {
  background-color: #fef0f0 !important;
  border-color: #fbc4c4 !important;
  color: #f56c6c !important;
}

.test-result {
  margin-top: 25px;
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 100%;
}

.next-test-btn {
  background-color: #4a6cf7;
  color: white;
  border: none;

  &:hover {
    background-color: #3a5ce7;
  }
}
</style>
