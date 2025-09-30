<template>
  <div class="food-lottery">
    <!-- 头部标题 -->
    <header class="lottery-header">
      <h1>今天吃什么</h1>
      <p class="slogan">选择口味，帮你决定味蕾之旅</p>
    </header>

    <!-- 口味筛选区 -->
    <div class="filter-container">
      <label class="filter-label">选择口味：</label>
      <select
          v-model="selectedTaste"
          class="taste-select"
          @change="filterFoodList"
      >
        <option value="all">全部口味</option>
        <option value="light">清淡</option>
        <option value="spicy">辛辣</option>
        <option value="savory">咸鲜</option>
        <option value="sweet">甜味</option>
      </select>
    </div>

    <!-- 抽奖展示区（图文结合） -->
    <div class="lottery-display">
      <div
          class="food-card"
          :class="{ 'active': isRolling, 'result': !isRolling && selectedFood }"
      >
        <!-- 菜品图片 -->
        <div class="food-img-container">
          <img
              v-if="displayFoodObj.name"
              :src="displayFoodObj.img"
              :alt="displayFoodObj.name"
              class="food-img"
              :class="{ 'rolling-img': isRolling }"
          >
          <div v-else class="empty-img-placeholder">点击开始抽取</div>
        </div>
        <!-- 菜品名称 -->
        <div class="food-name">{{ displayFoodObj.name || '点击开始抽取' }}</div>
      </div>
    </div>

    <!-- 操作按钮 -->
    <div class="btn-container">
      <button
          class="lottery-btn"
          :disabled="isRolling"
          @click="startLottery"
      >
        {{ isRolling ? '抽取中...' : '开始抽取' }}
      </button>
      <button
          class="reset-btn"
          :disabled="isRolling || !selectedFood"
          @click="resetLottery"
      >
        重新选择
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';

// 原始菜品数据（新增img字段存储图片链接）
const foodData = ref([
  { name: '清蒸鱼', taste: 'light', img: 'https://picsum.photos/seed/fish/300/200' },
  { name: '蔬菜沙拉', taste: 'light', img: 'https://picsum.photos/seed/salad/300/200' },
  { name: '小米粥', taste: 'light', img: 'https://picsum.photos/seed/porridge/300/200' },
  { name: '水煮牛肉', taste: 'spicy', img: 'https://picsum.photos/seed/beef/300/200' },
  { name: '重庆火锅', taste: 'spicy', img: 'https://picsum.photos/seed/hotpot/300/200' },
  { name: '麻辣香锅', taste: 'spicy', img: 'https://picsum.photos/seed/spicy锅/300/200' },
  { name: '番茄牛腩', taste: 'savory', img: 'https://picsum.photos/seed/beefsteak/300/200' },
  { name: '可乐鸡翅', taste: 'savory', img: 'https://picsum.photos/seed/chicken/300/200' },
  { name: '酱油炒饭', taste: 'savory', img: 'https://picsum.photos/seed/rice/300/200' },
  { name: '提拉米苏', taste: 'sweet', img: 'https://picsum.photos/seed/cake1/300/200' },
  { name: '芒果糯米饭', taste: 'sweet', img: 'https://picsum.photos/seed/rice2/300/200' },
  { name: '草莓蛋糕', taste: 'sweet', img: 'https://picsum.photos/seed/cake2/300/200' },
]);

// 响应式变量
const selectedTaste = ref('all');
const filteredFoods = ref([]);
const isRolling = ref(false);
const selectedFood = ref(null); // 存储最终选中的完整菜品对象（含img和name）
const displayFoodObj = ref({ name: '', img: '' }); // 动画过程中展示的菜品对象

// 筛选菜品池
const filterFoodList = () => {
  filteredFoods.value = selectedTaste.value === 'all'
      ? [...foodData.value]
      : foodData.value.filter(food => food.taste === selectedTaste.value);

  if (selectedFood.value) resetLottery();
};

// 开始抽取（图文同步动画）
const startLottery = () => {
  if (filteredFoods.value.length === 0) {
    displayFoodObj.value = { name: '该口味暂无菜品', img: '' };
    return;
  }

  isRolling.value = true;
  selectedFood.value = null;
  let rollCount = 0;
  const maxRollCount = Math.floor(Math.random() * 31) + 30; // 30-60次随机滚动
  const rollInterval = setInterval(() => {
    // 随机选取筛选池中的菜品，同步更新图片和名称
    const randomIndex = Math.floor(Math.random() * filteredFoods.value.length);
    displayFoodObj.value = { ...filteredFoods.value[randomIndex] };

    rollCount++;
    if (rollCount >= maxRollCount) {
      clearInterval(rollInterval);
      // 确定最终菜品并定格
      const finalIndex = Math.floor(Math.random() * filteredFoods.value.length);
      selectedFood.value = filteredFoods.value[finalIndex];
      displayFoodObj.value = { ...selectedFood.value };
      isRolling.value = false;
    }
  }, 80);
};

// 重置状态
const resetLottery = () => {
  displayFoodObj.value = { name: '', img: '' };
  selectedFood.value = null;
};

// 初始化
onMounted(() => {
  filterFoodList();
});
</script>

<style scoped>
.food-lottery {
  min-height: 100vh;
  padding: 2rem;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  font-family: 'Arial Rounded MT Bold', sans-serif;
}

.lottery-header {
  text-align: center;
  margin-bottom: 2.5rem;
}

.lottery-header h1 {
  font-size: 2.2rem;
  color: #2c3e50;
  margin-bottom: 0.5rem;
}

.slogan {
  font-size: 1rem;
  color: #7f8c8d;
}

.filter-container {
  margin-bottom: 2rem;
  display: flex;
  align-items: center;
  gap: 1rem;
}

.filter-label {
  font-size: 1.1rem;
  color: #2c3e50;
}

.taste-select {
  padding: 0.6rem 1rem;
  border: none;
  border-radius: 20px;
  background: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  font-size: 1rem;
  color: #2c3e50;
  cursor: pointer;
  transition: all 0.3s ease;
}

.taste-select:focus {
  outline: none;
  box-shadow: 0 0 0 2px #3498db;
}

.lottery-display {
  width: 100%;
  max-width: 400px;
  margin-bottom: 2.5rem;
}

.food-card {
  width: 100%;
  padding: 2rem;
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  text-align: center;
  transition: all 0.4s ease;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1.5rem;
}

/* 抽取中动画状态 */
.food-card.active {
  animation: shake 0.1s infinite alternate;
  box-shadow: 0 6px 25px rgba(52, 152, 219, 0.3);
}

/* 结果定格状态 */
.food-card.result {
  transform: scale(1.03);
  box-shadow: 0 8px 30px rgba(46, 204, 113, 0.3);
}

/* 菜品图片容器 */
.food-img-container {
  width: 100%;
  height: 200px;
  border-radius: 12px;
  overflow: hidden;
  position: relative;
  background: #f8f9fa;
}

/* 菜品图片 */
.food-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: all 0.2s ease;
}

/* 抽取中图片动画（快速切换时的模糊过渡） */
.rolling-img {
  filter: blur(2px);
  transform: scale(1.05);
}

/* 初始空状态占位 */
.empty-img-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #7f8c8d;
  font-size: 1.1rem;
}

/* 菜品名称 */
.food-name {
  font-size: 1.8rem;
  color: #2c3e50;
  font-weight: bold;
  margin-bottom: 0.5rem;
}

.btn-container {
  display: flex;
  gap: 1.5rem;
}

.lottery-btn, .reset-btn {
  padding: 0.8rem 2rem;
  border: none;
  border-radius: 25px;
  font-size: 1.1rem;
  font-weight: bold;
  cursor: pointer;
  transition: all 0.3s ease;
}

.lottery-btn {
  background: #3498db;
  color: white;
}

.lottery-btn:disabled {
  background: #bdc3c7;
  cursor: not-allowed;
  transform: none;
}

.lottery-btn:hover:not(:disabled) {
  background: #2980b9;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(52, 152, 219, 0.2);
}

.reset-btn {
  background: #ecf0f1;
  color: #2c3e50;
}

.reset-btn:disabled {
  background: #f5f5f5;
  color: #bdc3c7;
  cursor: not-allowed;
}

.reset-btn:hover:not(:disabled) {
  background: #d5dbdb;
  transform: translateY(-2px);
}

/* 卡片抖动动画 */
@keyframes shake {
  from { transform: translateX(-5px) scale(1); }
  to { transform: translateX(5px) scale(1); }
}

/* 响应式适配 */
@media (max-width: 480px) {
  .food-card {
    padding: 1.5rem;
    gap: 1rem;
  }

  .food-img-container {
    height: 160px;
  }

  .food-name {
    font-size: 1.5rem;
  }

  .btn-container {
    flex-direction: column;
    width: 100%;
    max-width: 300px;
  }
}
</style>
