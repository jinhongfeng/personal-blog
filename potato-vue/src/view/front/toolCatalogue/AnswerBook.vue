<template>
  <div class="book-container">
    <div class="canvas" ref="canvasRef">
      <img :src="backgroundImage" alt="答案之书背景" class="background-image" />

      <div v-if="showAnswer" class="answer-container">
        <div class="answer-text">{{ answer.part1 }}</div>
        <div class="answer-text second-line">{{ answer.part2 }}</div>
      </div>

      <div v-if="showAnswer" class="explanation">
        遇到问题、犹豫不决时，试试拿走这个简单的答案或暗示。
      </div>

      <button
          v-if="!showAnswer"
          class="action-button"
          @click="generateAnswer"
      >
        默念问题、寻找答案
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue';

// 背景图片
const backgroundImage = 'https://img.alicdn.com/imgextra/i3/O1CN01FSw38T1Ne6L2mg3Mb_!!6000000001594-2-tps-936-1200.png';

// 画布引用
const canvasRef = ref(null);

// 答案数据
const answer = reactive({
  part1: '',
  part2: ''
});

// 显示状态
const showAnswer = ref(false);

// 预设的答案库
const answerLibrary = [
  { part1: '当下困难', part2: '即将解决' },
  { part1: '心中的她', part2: '即将出现' },
  { part1: '前方路途', part2: '渐趋明朗' },
  { part1: '内心不安', part2: '终将平息' },
  { part1: '今日运势', part2: '值得期待' },
  { part1: '努力方向', part2: '正在路上' },
  { part1: '心中所想', part2: '终会实现' },
  { part1: '未来可期', part2: '就在眼前' },
  { part1: '内心声音', part2: '值得倾听' },
  { part1: '前方机会', part2: '即将来临' },
  { part1: '困难时期', part2: '即将过去' },
  { part1: '内心疑惑', part2: '即将解开' },
  { part1: '今日心情', part2: '会变更好' },
  { part1: '心中所愿', part2: '终会达成' },
  { part1: '前方光明', part2: '就在眼前' },
  { part1: '内心平静', part2: '即将到来' },
  { part1: '今日好运', part2: '正在路上' },
  { part1: '困难面前', part2: '勇敢面对' },
  { part1: '心中希望', part2: '终会实现' },
  { part1: '未来之路', part2: '渐趋清晰' },
  { part1: '内心不安', part2: '终会消散' },
  { part1: '前方机会', part2: '值得把握' },
  { part1: '今日运势', part2: '渐入佳境' },
  { part1: '心中所求', part2: '终会得到' },
  { part1: '困难重重', part2: '终会解决' },
  { part1: '内心声音', part2: '值得相信' },
  { part1: '前方光明', part2: '正在到来' },
  { part1: '今日心情', part2: '逐渐好转' },
  { part1: '心中所愿', part2: '即将实现' },
  { part1: '未来可期', part2: '值得等待' },
  { part1: '内心平静', part2: '正在回归' },
  { part1: '今日好运', part2: '即将降临' },
  { part1: '困难面前', part2: '终会跨越' },
  { part1: '心中希望', part2: '值得坚持' },
  { part1: '前方机会', part2: '即将到来' },
  { part1: '内心不安', part2: '即将平息' },
  { part1: '今日运势', part2: '会变更好' },
  { part1: '心中所求', part2: '终会满足' },
  { part1: '困难重重', part2: '终会过去' },
  { part1: '内心声音', part2: '终会清晰' },
  { part1: '前方光明', part2: '终会到来' },
  { part1: '今日心情', part2: '日渐明朗' },
  { part1: '心中所愿', part2: '终会成真' },
  { part1: '未来可期', part2: '渐近眼前' },
  { part1: '内心平静', part2: '即将回归' },
  { part1: '今日好运', part2: '正在降临' },
  { part1: '困难面前', part2: '终会克服' },
  { part1: '心中希望', part2: '终会实现' },
  { part1: '前方机会', part2: '值得期待' },
  { part1: '内心不安', part2: '终会消解' }
];

// 生成随机答案
const generateAnswer = () => {
  const randomIndex = Math.floor(Math.random() * answerLibrary.length);
  const selectedAnswer = answerLibrary[randomIndex];

  answer.part1 = selectedAnswer.part1;
  answer.part2 = selectedAnswer.part2;

  showAnswer.value = true;
};
</script>

<style scoped>
.book-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #2c3e50, #1a1a2e);
  padding: 20px;
}

.canvas {
  position: relative;
  width: 380px;
  height: 700px;
  overflow: hidden;
  border-radius: 15px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
  border: 2px solid rgba(255, 255, 255, 0.1);
}

.background-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  position: absolute;
  top: 0;
  left: 0;
}

.answer-container {
  position: absolute;
  top: 44px;
  left: 32px;
  width: 300px;
  height: 190px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.answer-text {
  font-size: 60px;
  line-height: 65px;
  color: #F9DBB3;
  font-family: 'Georgia', 'SimSun', '宋体', serif;
  font-weight: bold;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 100%;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.5);
  letter-spacing: 2px;
}

.second-line {
  margin-top: 5px;
}

.explanation {
  position: absolute;
  top: calc(44px + 190px + 22px);
  left: 32px;
  width: 300px;
  font-size: 16px;
  line-height: 26px;
  color: rgba(255, 255, 255, 0.9);
  font-family: 'Georgia', 'SimSun', '宋体', serif;
  text-align: left;
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.7);
}

.action-button {
  position: absolute;
  bottom: 16px;
  left: 50%;
  transform: translateX(-50%);
  width: auto;
  height: 32px;
  background: linear-gradient(45deg, #333, #111);
  color: #F9DBB3;
  border: 1px solid #555;
  border-radius: 16px;
  font-size: 16px;
  padding: 0 20px;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.3);
}

.action-button:hover {
  background: linear-gradient(45deg, #444, #222);
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.4);
  transform: translateX(-50%) translateY(-2px);
}

.action-button:active {
  transform: translateX(-50%) translateY(1px);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

@media (min-width: 768px) {
  .canvas {
    width: 80%; /* 根据需要调整 */
    max-width: 400px; /* 可以适当增加最大宽度 */
  }
}

/* 针对1024px及以上设备的样式 */
@media (min-width: 1024px) {
  .canvas {
    width: 70%; /* 根据需要调整 */
    max-width: 450px; /* 可以适当增加最大宽度 */
  }
}

/* 针对1300px及以上设备的样式 */
@media (min-width: 1300px) {
  .canvas {
    width: 60%; /* 根据需要调整 */
    max-width: 500px; /* 可以适当增加最大宽度 */
  }
}
</style>



