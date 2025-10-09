<template>
  <div class="bg-selector-container">
    <!-- 背景切换按钮 -->
    <div class="bg-switcher" @click="togglePanel">
      <font-awesome-icon icon="globe" />
    </div>

    <!-- 背景选择面板 -->
    <div v-if="isPanelVisible" class="bg-selector"
         ref="bgSelectorRef" v-click-outside="handleClickOutside">
      <div class="bg-selector-header">
        <h3>选择背景</h3>
        <el-icon class="close-btn" @click="togglePanel"><Close /></el-icon>
      </div>
      <div class="bg-thumbs">
        <!-- 使用组件内部的 backgroundImages 列表 -->
        <div
            v-for="(bg, index) in backgroundImages"
            :key="bg.id || index"
            class="bg-thumb"
            :class="{ active: currentBgIndex === index }"
            @click="selectBackground(index)"
        >
          <img :src="bg.thumbnail" :alt="bg.name" class="bg-thumb-img" />
          <span class="bg-name">{{ bg.name }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ElIcon, ElNotification } from "element-plus";
import { Close } from "@element-plus/icons-vue";
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome";
import {ref, watch, onMounted, defineProps, defineEmits, nextTick} from "vue";
import request from "@/utils/request";

const props = defineProps({
  show: { type: Boolean, default: false },
});

const emit = defineEmits({
  // 当面板显示状态改变时通知父组件
  "update:show": (value) => typeof value === "boolean",
  // 当背景图片选中时，直接 emit 选中的背景对象
  change: (bgObject) => bgObject && typeof bgObject === "object",
});

// 背景图列表
const backgroundImages = ref([
  {
    name: "动漫",
    url: require("@/assets/desktop.jpg"),
    thumbnail: require("@/assets/desktop.jpg"),
  },
  {
    name: "精灵之泪",
    url: "https://cdn.yuanshikong.net/NewTab/wallpaper/itab/645324c962269728166a7246.jpe?imageMogr2",
    thumbnail:
        "https://cdn.yuanshikong.net/NewTab/wallpaper/itab/645324c962269728166a7246.jpe?imageMogr2/thumbnail/450x300",
  },
]);

// --- Refs ---
const bgSelectorRef = ref(null);
const isPanelVisible = ref(props.show);
const currentBgIndex = ref(0);   // 组件内部维护当前选中索引
const isDataLoaded = ref(false); // 增加一个加载状态标记

// 监听父组件传来的 show prop
watch(() => props.show, (newValue) => {
  isPanelVisible.value = newValue;
});

// 当组件内部的选中索引变化时，更新 localStorage 并通知父组件
watch(currentBgIndex, (newIndex) => {
  if (backgroundImages.value.length === 0) return;

  const selectedBg = backgroundImages.value[newIndex];
  localStorage.setItem("selectedBgIndex", newIndex);
  localStorage.setItem("selectedBg", JSON.stringify(selectedBg));
  emit("change", selectedBg);
});

onMounted(async () => {
  // 首次挂载时加载数据
  if (!isDataLoaded.value) {
    await loadData();
  }
});

// --- Methods ---
const loadData = async () => {
  try {
    const res = await request.get('/bgImage');
    if (res.code === '200') {
      backgroundImages.value = res.data || [];
      isDataLoaded.value = true;
      // 数据加载后，尝试从 localStorage 恢复选择
      restoreSavedBackground();
    } else {
      ElNotification.error({
        title: '背景图片加载失败',
        message: res.msg || '获取背景图片列表失败'
      });
    }
  } catch (err) {
    ElNotification.error({
      title: '网络错误',
      message: '无法连接到服务器，请检查网络'
    });
    // 请求异常，则使用备用图片
    if (backgroundImages.value.length === 0) {
      backgroundImages.value = [
        {
          name: "默认备用",
          url: require("@/assets/desktop.jpg"),
          thumbnail: require("@/assets/desktop.jpg"),
        }
      ];
      restoreSavedBackground();
    }
  }
};


// 从 localStorage 恢复上次选择的背景
const restoreSavedBackground = () => {
  if (backgroundImages.value.length === 0) return;

  const savedBgIndex = localStorage.getItem('selectedBgIndex');
  let selectedIndex = 0; // 默认选中第一个

  if (savedBgIndex !== null) {
    const parsedIndex = parseInt(savedBgIndex, 10);
    if (!isNaN(parsedIndex) && parsedIndex >= 0 && parsedIndex < backgroundImages.value.length) {
      selectedIndex = parsedIndex;
    }
  }

  currentBgIndex.value = selectedIndex;

  // 使用 nextTick 延迟 emit，确保父组件能接收到
  nextTick(() => {
    emit("change", backgroundImages.value[selectedIndex]);
  });
};

 // 切换面板的显示和隐藏
const togglePanel = () => {
  isPanelVisible.value = !isPanelVisible.value;
  emit("update:show", isPanelVisible.value);
};

const handleClickOutside = () => {
  if (isPanelVisible.value) {
    isPanelVisible.value = false;
    emit("update:show", false);
  }
};

const selectBackground = (index) => {
  currentBgIndex.value = index;
  togglePanel();
};
</script>

<style scoped>

.bg-switcher {
  position: fixed;
  bottom: 20px;
  right: 20px;
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(10px);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  cursor: pointer;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
  transition: all 0.3s ease;
  z-index: 100;
}

.bg-switcher:hover {
  background: rgba(255, 255, 255, 0.25);
  transform: scale(1.1);
}

.bg-selector {
  position: fixed;
  bottom: 80px;
  right: 20px;
  width: 300px;
  background: rgba(15, 15, 15, 0.85);
  backdrop-filter: blur(15px);
  border-radius: 12px;
  padding: 15px;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.3);
  z-index: 100;
  animation: slideUp 0.3s ease-out forwards;
}

.bg-selector-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: white;
  margin-bottom: 15px;
}
.close-btn {
  color: #ccc;
  cursor: pointer;
  transition: color 0.2s;
}
.close-btn:hover {
  color: white;
}
.bg-thumbs {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(70px, 1fr));
  gap: 10px;
  max-height: 300px;
  overflow-y: auto;
  padding-right: 5px;
}
.bg-thumbs::-webkit-scrollbar {
  width: 5px;
}

.bg-thumbs::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.2);
  border-radius: 5px;
}
.bg-thumb {
  width: 100%;
  height: 70px;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  position: relative;
  transition: transform 0.2s;
}
.bg-thumb:hover {
  transform: scale(1.05);
}
.bg-thumb.active::after {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  border: 2px solid #409eff;
  border-radius: 8px;
}
.bg-thumb-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.bg-name {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 2px;
  background: rgba(0, 0, 0, 0.5);
  color: white;
  font-size: 12px;
  text-align: center;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@media (max-width: 768px) {
  .bg-selector {
    width: 250px;
  }
  .bg-thumbs {
    gap: 8px;
  }
}

@media (max-width: 480px) {
  .bg-selector {
    width: calc(100% - 40px);
    left: 20px;
    right: 20px;
    bottom: 70px;
    padding: 12px;
  }

  .bg-thumbs {
    grid-template-columns: repeat(auto-fill, minmax(60px, 1fr));
    gap: 8px;
  }
  .bg-thumb {
    height: 60px;
  }
}
</style>
