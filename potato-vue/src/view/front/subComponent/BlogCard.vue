<template>
  <div class="blog-card-wrapper" ref="cardWrapper">
    <el-card class="mainBlog-card" @click="handleClick" :style="{ cursor: 'pointer' }">
      <!-- 图片 -->
      <div class="mainBlog-image">
        <el-image :src="imageUrl" class="card-img" lazy :alt="item.title"/>
      </div>
      <!-- 发布时间 -->
      <div class="mainBlog-time">
        <el-icon size="15" color="orange"><Timer /></el-icon>
        <span style="margin-left: 5px;">发布于 {{ formatTime(item.publishTime) || '未知时间' }}</span>
      </div>
      <!-- 主题 -->
      <el-popover trigger="hover" placement="top-start" effect="dark"
                  :content="item.title" :width="200">
        <template #reference>
          <span class="secondFont">{{ item.title }}</span>
        </template>
      </el-popover>
      <!-- 一些评价内容 -->
      <div class="mainBlog-time">
        <font-awesome-icon icon="eye" style="margin-left: 5px"/>
        <span style="margin-left: 5px">浏览量：</span>
        <span>{{ item.pageview }}</span>
      </div>
      <!-- 标签 -->
      <div class="mainBlog-badge">
        <el-button type="danger" size="small">{{ item.author || "POTATO" }}</el-button>
        <el-button type="info" size="small" plain>{{ badgeText }}</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, ref, computed } from "vue";
import { ElIcon } from "element-plus";
import { Timer } from "@element-plus/icons-vue";
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome";
import { formatTime } from "@/utils/defaultConfig";

const props = defineProps({
  item: {type: Object, required: true,},
  type: {type: String, required: true,},
  index: {type: Number, required: true,},
});

const emit = defineEmits(["click"]);
const cardWrapper = ref(null);

const imageUrl = computed(() => {
  if (props.type === "travel") {
    return props.item.coverImage;
  }
  return props.item.picture;
});

const badgeText = computed(() => {
  if (props.type === "travel") {
    return props.item.description || "生活";
  }
  return props.item.badge;
});

const handleClick = () => {
  emit("click", props.item.id, props.type);
};

onMounted(() => {
  // 为每个卡片设置一个递增的延迟，创造出依次入场的效果
  const delay = props.index * 100;
  setTimeout(() => {
    if (cardWrapper.value) {
      cardWrapper.value.classList.add("animate-in");
    }
  }, delay);
});
</script>

<style scoped>
.blog-card-wrapper {
  opacity: 0;
  transform: scale(0.9); /* 初始为90%大小 */
  transition: opacity 0.5s ease-out, transform 0.5s ease-out;
}

.blog-card-wrapper.animate-in {
  opacity: 1;
  transform: scale(1); /* 恢复到100%大小 */
}

.mainBlog-card {
  height: 100%;
  display: flex;
  flex-direction: column;
  transition: all 0.3s ease;
  overflow: hidden;

}

.mainBlog-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.mainBlog-image {
  width: 100%;
  height: 150px;
  overflow: hidden;
}

.card-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
}

.card-img:hover {
  transform: scale(1.05);
}

.mainBlog-time {
  font-size: 12px;
  color: #999;
  display: flex;
  align-items: center;
}

.secondFont {
  font-size: clamp(1rem, 2vw, 1.25rem);
  color: #333;
  transition: color 0.3s ease;
  display: block;
  margin: 10px 0;

  text-align: left;
  white-space: nowrap; /* 一行显示 */
  text-overflow: ellipsis; /* 省略号代替 */
  overflow: hidden;
  max-width: 200px;

}

.secondFont:hover {
  color: #ff7d00;
}

.mainBlog-badge {
  margin-top: auto;
  padding-top: 10px;
  display: flex;
  gap: 5px;
  flex-wrap: wrap;
}

@media (max-width: 1300px) {
  .secondFont {
    max-width: 220px;
  }
}

@media (max-width: 992px) {
  .secondFont {
    max-width: 400px;
  }
}

@media (max-width: 768px) {
  .secondFont {
    max-width: 300px;
  }
}
@media (max-width: 576px) {
  .secondFont {
    max-width: 250px;
  }
}

@media (max-width: 360px) {
  .secondFont {
    max-width: 180px;
  }
}
</style>
