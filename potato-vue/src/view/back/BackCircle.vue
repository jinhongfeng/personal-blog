<template>
  <div class="circle-management-container">
    <!-- 头部 -->
    <el-card class="header-card">
      <div class="button-container">
        <!-- 左侧区域：搜索框和搜索按钮 -->
        <div class="left-group">
          <el-input
              v-model="searchParams.content"
              :style="{ width: isSmallScreen ? '100%' : '240px' }"
              placeholder="输入要搜索的内容"
              :prefix-icon="Search"
              clearable
              @keyup.enter="handleSearch"
          />
          <el-button
              type="info"
              :class="isSmallScreen ? 'mt-2 w-full' : 'ml-10'"
              @click="handleSearch"
          >
            搜索
          </el-button>
        </div>

        <!-- 右侧区域：操作按钮 -->
        <div class="right-group">
          <el-button
              type="primary"
              @click="handleCreate"
              :size="isSmallScreen ? 'small' : 'default'"
          >
            <el-icon><CirclePlus /></el-icon>
            <span :class="{ 'hidden-sm': isSmallScreen }">新建</span>
          </el-button>
          <el-button
              type="warning"
              :disabled="selectedRows.length === 0"
              @click="batchDeleteVisible = true"
              :class="isSmallScreen ? 'mt-2' : 'ml-4'"
              :size="isSmallScreen ? 'small' : 'default'"
          >
            <el-icon><Delete /></el-icon>
            <span :class="{ 'hidden-sm': isSmallScreen }">批量删除</span>
          </el-button>
        </div>
      </div>
    </el-card>

    <!-- 主体 -->
    <el-card class="main-card">
      <el-table
          :data="tableData"
          stripe
          style="width: 100%"
          @selection-change="handleSelectionChange"
          v-loading="tableLoading"
          :cell-style="{ padding: isSmallScreen ? '4px 8px' : '8px 12px' }"
          :header-cell-style="{ padding: isSmallScreen ? '6px 8px' : '10px 12px' }"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column
            prop="content"
            label="内容"
            :width="isSmallScreen ? 150 : 250"
        />
        <el-table-column
            label="图片"
            :width="isSmallScreen ? 120 : 250"
        >
          <template #default="scope">
            <el-image
                v-if="scope.row.images && scope.row.images.length"
                :src="scope.row.images[0].imageUrl"
                alt="图片"
                show-progress
                :preview-src-list="scope.row.images.map(img => img.imageUrl)"
                class="table-image"
                :error="handleImageError"
                :style="{ width: isSmallScreen ? '60px' : '80px', height: isSmallScreen ? '60px' : '80px' }"
            />
          </template>
        </el-table-column>
        <el-table-column
            prop="publishTime"
            label="发布时间"
            :width="isSmallScreen ? 130 : 200"
        />
        <el-table-column
            prop="likeCount"
            label="点赞量"
            width="100"
        />
        <el-table-column
            label="操作"
            :width="isSmallScreen ? 140 : 180"
        >
          <template #default="scope">
            <el-button-group>
              <el-button
                  type="success"
                  :icon="Edit"
                  @click="handleEdit(scope.row)"
                  size="small"
                  tooltip="编辑"
              />
              <el-button
                  type="primary"
                  :icon="View"
                  @click="handleView(scope.row)"
                  size="small"
                  class="ml-1"
                  tooltip="查看"
              />
              <el-popconfirm title="是否确认删除？" @confirm="handleDelete(scope.row)">
                <template #reference>
                  <el-button
                      type="danger"
                      :icon="Delete"
                      size="small"
                      class="ml-1"
                      tooltip="删除"
                  />
                </template>
              </el-popconfirm>
            </el-button-group>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 分页 -->
    <el-card class="pagination-card">
      <el-pagination
          v-model:current-page="pagination.currentPage"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 15, 20, 30]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="pagination.total"
          @size-change="handlePageChange"
          @current-change="handlePageChange"
          :small="isSmallScreen"
      />
    </el-card>

    <!-- 内容编辑对话框 -->
    <el-dialog
        v-model="dialogBuildVisible"
        :title="payLoadData.id ? '编辑内容' : '新建内容'"
        :width="isSmallScreen ? '90%' : '800px'"
        :before-close="resetForm"
        max-height="80vh"
        overflow-y="auto"
    >
      <el-form
          :model="payLoadData"
          :label-width="isSmallScreen ? '80px' : '120px'"
          class="content-form"
          ref="formRef"
      >
        <!-- 内容输入区域 -->
        <el-form-item
            label="内容"
            prop="content"
            :rules="[{ required: true, message: '请输入内容', trigger: 'blur' }]"
        >
          <el-input
              v-model="payLoadData.content"
              placeholder="请输入内容（可直接输入表情符号😊🎉）"
              style="width: 100%;"
              ref="contentInputRef"
              @focus="showEmojiPicker = true"
              @blur="handleInputBlur"
              type="textarea"
              :rows="isSmallScreen ? 3 : 4"
          />
          <!-- Emoji选择器 -->
          <div
              v-if="showEmojiPicker && dialogBuildVisible"
              ref="emojiContainerRef"
              class="emoji-container"
              :style="{ maxWidth: isSmallScreen ? '100%' : '500px' }"
          >
            <EmojiPicker :native="true" @select="insertEmoji" />
          </div>
        </el-form-item>

        <!-- 上传图片区域 -->
        <el-form-item label="上传图片">
          <!-- 上传方式选择 - 放在最上方 -->
          <el-radio-group
              v-model="uploadMode"
              class="mb-4 upload-mode-selector"
          >
            <el-radio label="url">输入图片链接</el-radio>
            <el-radio label="local">本地上传</el-radio>
          </el-radio-group>

          <!-- 上传区域容器 - 统一放在选择方式下方 -->
          <div class="upload-area-container">
            <!-- URL上传方式 -->
            <div v-if="uploadMode === 'url'" class="url-upload-panel">
              <el-input
                  v-model="urlInput"
                  placeholder="请输入有效的图片地址（http://或https://开头）"
                  :disabled="payLoadData.images.length >= 9"
                  class="url-input"
              />
              <el-button
                  type="primary"
                  class="mt-2 url-add-btn"
                  @click="addImageByUrl"
                  :disabled="!isValidUrl(urlInput) || payLoadData.images.length >= 9"
                  :size="isSmallScreen ? 'small' : 'default'"
              >
                添加图片
              </el-button>
              <el-alert
                  v-if="duplicateUrlError"
                  title="该图片已添加，请不要重复添加"
                  type="warning"
                  :closable="false"
                  class="mt-2"
                  style="font-size: 12px;"
              />
            </div>

            <!-- 本地上传方式 -->
            <div v-if="uploadMode === 'local'" class="local-upload-panel">
              <el-upload
                  v-model:file-list="imageFileList"
                  action="#"
                  list-type="picture-card"
                  :on-success="handleImageUpload"
                  :on-remove="handleImageRemove"
                  :before-upload="beforeImageUpload"
                  :auto-upload="true"
                  :http-request="handleHttpUpload"
                  multiple
                  :disabled="payLoadData.images.length >= 9"
              >
                <el-icon><Plus /></el-icon>
                <div class="upload-hint" :class="{ 'hidden-xs': isXsScreen }">点击或拖拽文件至此处上传</div>
                <div class="upload-tip" :class="{ 'hidden-xs': isXsScreen }">支持 JPG/PNG/WEBP 格式，单张不超过4MB</div>
              </el-upload>

              <!-- 上传进度条 -->
              <el-progress
                  v-for="(progress, key) in uploadProgress"
                  :key="key"
                  :percentage="progress"
                  :status="progress === 100 ? 'success' : null"
                  stroke-width="4"
                  class="mt-2"
                  v-if="progress<100"
              />
            </div>
          </div>

          <!-- 图片数量提示 -->
          <div class="image-count-tip text-sm text-gray-500 mt-3">
            已添加 {{ payLoadData.images.length }}/9 张图片
          </div>

          <!-- 图片网格展示（根据屏幕大小调整列数） -->
          <div class="mt-4">
            <div
                class="image-grid"
                :style="{
                  gridTemplateColumns: `repeat(${isXsScreen ? 2 : 3}, 1fr)`,
                  gap: isSmallScreen ? '8px' : '10px'
                }"
            >
              <div
                  v-for="(item, index) in payLoadData.images"
                  :key="item.uid"
                  class="image-grid-item"
                  :draggable="true"
                  @dragstart="handleDragStart(index)"
                  @dragenter.prevent
                  @dragover.prevent
                  @drop="handleDrop"
                  :class="{ 'dragging': dragIndex === index }"
              >
                <el-image
                    :src="item.imageUrl"
                    alt="图片预览"
                    class="grid-image"
                    :error="handleImageError"
                    :style="{ height: isSmallScreen ? '120px' : '160px' }"
                />
                <el-button
                    type="text"
                    class="delete-image-btn"
                    @click="removeImage(index)"
                >
                  <el-icon><Delete /></el-icon>
                </el-button>
              </div>
            </div>
          </div>
        </el-form-item>

        <el-form-item label="点赞量">
          <el-input-number
              v-model="payLoadData.likeCount"
              :min="0"
              controls-position="right"
              :size="isSmallScreen ? 'small' : 'default'"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <div class="dialog-footer">
          <el-button
              @click="resetForm"
              :size="isSmallScreen ? 'small' : 'default'"
          >
            取消
          </el-button>
          <el-button
              type="primary"
              @click="handleSave"
              :size="isSmallScreen ? 'small' : 'default'"
          >
            保存内容
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 查看详情对话框 -->
    <el-dialog
        v-model="viewDialogVisible"
        title="内容详情"
        :width="isSmallScreen ? '90%' : '800px'"
    >
      <div class="view-content">
        <p class="view-item"><span class="label">ID：</span>{{ currentCircle.id }}</p>
        <p class="view-item"><span class="label">内容：</span>{{ currentCircle.content }}</p>
        <p class="view-item"><span class="label">图片：</span></p>
        <div
            class="image-grid"
            :style="{
              gridTemplateColumns: `repeat(${isXsScreen ? 2 : 3}, 1fr)`,
              gap: isSmallScreen ? '8px' : '10px'
            }"
        >
          <el-image
              v-for="(img, index) in currentCircle.images"
              :key="index"
              :src="img.imageUrl"
              alt="图片"
              class="grid-image"
              :preview-src-list="currentCircle.images.map(item => item.imageUrl)"
              :error="handleImageError"
              :style="{ height: isSmallScreen ? '120px' : '160px' }"
          />
        </div>
        <p class="view-item"><span class="label">点赞量：</span>{{ currentCircle.likeCount }}</p>
        <p class="view-item"><span class="label">发布时间：</span>{{ currentCircle.publishTime }}</p>
      </div>
    </el-dialog>

    <!-- 批量删除对话框 -->
    <el-dialog
        v-model="batchDeleteVisible"
        title="批量删除确认"
        :width="isSmallScreen ? '90%' : '400px'"
    >
      <p>您确定要删除选中的 <span>{{ selectedRows.length }}</span> 条数据吗？</p>
      <p>此操作不可撤销，请谨慎操作！</p>
      <template #footer>
        <el-button
            @click="batchDeleteVisible = false"
            :size="isSmallScreen ? 'small' : 'default'"
        >
          取消
        </el-button>
        <el-button
            type="danger"
            @click="handleBatchDelete"
            :size="isSmallScreen ? 'small' : 'default'"
        >
          确认删除
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import {CirclePlus, Delete, Edit, Plus, Search, View} from "@element-plus/icons-vue";
import {nextTick, onMounted, onUnmounted, reactive, ref, watchEffect} from "vue";
import {ElLoading, ElMessage, ElMessageBox} from "element-plus";
import request from '@/utils/request';
import EmojiPicker from 'vue3-emoji-picker';
import 'vue3-emoji-picker/css';

// 响应式相关
const isSmallScreen = ref(window.innerWidth < 768)
const isXsScreen = ref(window.innerWidth < 576)

// 监听窗口大小变化
watchEffect(() => {
  const handleResize = () => {
    isSmallScreen.value = window.innerWidth < 768
    isXsScreen.value = window.innerWidth < 576
  }

  window.addEventListener('resize', handleResize)
  return () => window.removeEventListener('resize', handleResize)
})

// 状态定义
const searchParams = reactive({
  content: '',
  publishTime: ''
});

const selectedRows = ref([]);
const dialogBuildVisible = ref(false);
const batchDeleteVisible = ref(false);
const viewDialogVisible = ref(false);
const imageFileList = ref([]); // 上传组件的文件列表
const tableLoading = ref(false);
const currentCircle = ref({
  images: [] // 存储图片对象数组 {id, circleId, imageUrl, sort}
});
const dragIndex = ref(-1);
const duplicateUrlError = ref(false); // 重复URL错误提示
const uploadProgress = ref({}); // 上传进度记录

// 分页参数
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
});

// 表格数据
const tableData = ref([]);

// 表单数据
const payLoadData = ref({
  isEdit: false,
  id: null,
  content: '',
  images: [], // 存储图片对象数组 {id, circleId, imageUrl, sort, uid}
  likeCount: 0,
  publishTime: null
});

// 表单引用
const formRef = ref(null);

// Emoji相关
const showEmojiPicker = ref(false);
const contentInputRef = ref(null);
const emojiContainerRef = ref(null);

// 上传方式相关
const uploadMode = ref('local'); // 默认本地上传
const urlInput = ref(''); // URL输入框

// 初始化加载数据
onMounted(() => {
  document.addEventListener('click', handleClickOutside);
  fetchCirclelData();
});

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside);
});

// 工具方法
const isValidUrl = (url) => {
  if (!url) return false;
  try {
    const parsedUrl = new URL(url);
    // 检查协议和图片文件扩展名
    const isValidProtocol = parsedUrl.protocol === 'http:' || parsedUrl.protocol === 'https:';
    const isValidExtension = /\.(jpg|jpeg|png|webp)$/i.test(parsedUrl.pathname);
    return isValidProtocol && isValidExtension;
  } catch (e) {
    return false;
  }
};

// 检查图片URL是否已存在
const isImageDuplicate = (url) => {
  return payLoadData.value.images.some(img => img.imageUrl === url);
};

const validateImageFile = (file) => {
  const validTypes = ['image/jpeg', 'image/png', 'image/webp'];
  const isTypeValid = validTypes.includes(file.type);
  const isSizeValid = file.size / 1024 / 1024 < 5; // 4MB

  if (!isTypeValid) {
    ElMessage.error('上传图片只能是 JPG/PNG/WEBP 格式!');
  }
  if (!isSizeValid) {
    ElMessage.error('上传图片大小不能超过 4MB!');
  }

  return isTypeValid && isSizeValid;
};

const generateUid = (prefix = '') => {
  return `${prefix}-${Date.now()}-${Math.random().toString(36).substr(2, 9)}`;
};

// 通过URL添加图片 - 防止重复添加
const addImageByUrl = () => {
  // 清除之前的错误提示
  duplicateUrlError.value = false;

  // 检查是否已达最大数量
  if (payLoadData.value.images.length >= 9) {
    ElMessage.warning('最多只能添加9张图片');
    return;
  }

  if (!isValidUrl(urlInput.value)) {
    ElMessage.warning('请输入有效的图片URL');
    return;
  }

  // 检查是否重复添加
  if (isImageDuplicate(urlInput.value)) {
    duplicateUrlError.value = true;
    return;
  }

  // 生成唯一uid
  const uid = generateUid('url');

  // 添加到图片列表
  payLoadData.value.images.push({
    imageUrl: urlInput.value,
    uid: uid
  });

  // 清空输入框
  urlInput.value = '';
};

// 图片上传前验证
const beforeImageUpload = (file) => {
  // 检查是否已达最大数量
  if (payLoadData.value.images.length >= 9) {
    ElMessage.warning('最多只能添加9张图片');
    return false;
  }

  return validateImageFile(file);
};

// 拖拽排序相关方法
const handleDragStart = (index) => {
  dragIndex.value = index;
};

const handleDrop = (e) => {
  e.preventDefault();
  // 获取鼠标位置下的元素索引
  const elements = document.querySelectorAll('.image-grid-item');
  let newIndex = -1;

  elements.forEach((el, index) => {
    const rect = el.getBoundingClientRect();
    if (e.clientY >= rect.top && e.clientY <= rect.bottom) {
      newIndex = index;
    }
  });

  if (newIndex !== -1 && newIndex !== dragIndex.value) {
    // 复制当前图片数组
    const newImages = [...payLoadData.value.images];
    // 移除拖动的元素
    const [movedItem] = newImages.splice(dragIndex.value, 1);
    // 插入到新位置
    newImages.splice(newIndex, 0, movedItem);
    // 更新图片数组
    payLoadData.value.images = newImages;
    // 重置拖拽状态
    dragIndex.value = -1;
  }
};

// 自定义图片上传 - 带进度反馈
const handleHttpUpload = async (params) => {
  const formData = new FormData();
  formData.append('file', params.file);

  try {
    // 调用图片上传接口
    const res = await request.post('/file/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    });

    // 处理不同格式的响应
    let imageUrl = '';
    if (typeof res === 'string' && res.startsWith('http')) {
      imageUrl = res;
    } else if (res.data && typeof res.data === 'string' && res.data.startsWith('http')) {
      imageUrl = res.data;
    } else if (res.data && res.data.url) {
      imageUrl = res.data.url;
    } else {
      throw new Error('上传失败，服务器返回无效URL格式');
    }

    // 将URL返回给上传组件
    params.onSuccess({ url: imageUrl });
    ElMessage.success('图片上传成功');
  } catch (error) {
    ElMessage.error('图片上传失败：' + error.message);
    params.onError(error);
  }
};

// 图片上传成功处理
const handleImageUpload = (response, uploadFile, uploadFiles) => {
  // 确保响应包含有效的URL
  if (response && response.url && typeof response.url === 'string' && response.url.startsWith('http')) {
    // 检查是否已存在相同URL的图片（避免重复添加）
    const isDuplicate = payLoadData.value.images.some(img => img.imageUrl === response.url);
    if (!isDuplicate) {
      // 添加图片到表单数据，包含临时uid用于前端标识
      payLoadData.value.images.push({
        imageUrl: response.url,
        information: '',
        publishTime: new Date().toISOString().split('T')[0],
        uid: uploadFile.uid // 使用上传组件生成的uid
      });
    }

    // 更新文件列表显示，确保只保留有效文件
    nextTick(() => {
      imageFileList.value = uploadFiles.filter(file =>
          file.status === 'success' && file.url && file.url.startsWith('http')
      );
    });
  }
};

// 图片加载错误处理
const handleImageError = (e) => {
  e.target.src = require('@/assets/defaultImage.jpeg');
  ElMessage.warning('图片加载失败');
};

// 移除图片方法
const removeImage = (index) => {
  // 从图片数组中移除
  payLoadData.value.images.splice(index, 1);

  // 同步更新上传组件的文件列表
  imageFileList.value = imageFileList.value.filter((_, i) => i !== index);
};

// 上传组件移除图片处理
const handleImageRemove = (file, uploadFiles) => {
  // 从表单数据中移除图片
  payLoadData.value.images = payLoadData.value.images.filter(
      img => img.imageUrl !== file.url
  );
  // 同步更新文件列表
  imageFileList.value = uploadFiles;
};

// 获取表格数据（分页+条件）
const fetchCirclelData = async () => {
  tableLoading.value = true;

  try {
    const params = {
      pageNum: pagination.currentPage,
      pageSize: pagination.pageSize
    };
    if (searchParams.content.trim()) {
      params.content = searchParams.content.trim();
    }
    const res = await request.get('/circle/page', { params });

    // 为每条数据添加图片信息
    tableData.value = await Promise.all(res.data.records.map(async (item) => {
      const imageRes = await request.get('/circleImage/listByCircleId', {
        params: {circleId: item.id}
      });

      // 按sort字段升序排序，确保展示顺序正确
      const sortedImages = (imageRes.data || []).sort((a, b) => a.sort - b.sort);

      // 为每个图片添加uid，用于前端标识
      const imagesWithUid = sortedImages.map(img => ({
        ...img,
        uid: `img-${img.id}` // 使用后端返回的id生成uid
      }));

      return {
        ...item,
        images: imagesWithUid
      };
    }));
    pagination.total = res.data.total;
  } catch (error) {
    ElMessage.error('数据加载失败：' + error.message);
  } finally {
    tableLoading.value = false;
  }
};

// 搜索功能
const handleSearch = () => {
  pagination.currentPage = 1;
  fetchCirclelData();
};

// 分页变更
const handlePageChange = () => {
  fetchCirclelData();
};

// 选择行变更
const handleSelectionChange = (val) => {
  selectedRows.value = val;
};

// 新建
const handleCreate = () => {
  resetForm();
  dialogBuildVisible.value = true;
};

// 查看详情
const handleView = async (row) => {
  try {
    // 调用浏览量统计接口
    await request.post(`/circle/${row.id}/hasCount`);

    // 并行请求主表数据和图片数据，提高性能
    const [circleRes, imageRes] = await Promise.all([
      request.get(`/circle/${row.id}`),
      request.get('/circleImage/listByCircleId', {
        params: { circleId: row.id }
      })
    ]);

    currentCircle.value = {
      ...circleRes.data,
      images: imageRes.data || []
    };

    viewDialogVisible.value = true;

    // 只更新当前行的点赞量，避免全表刷新
    updateLikeCountInTable(row.id, circleRes.data.likeCount);
  } catch (error) {
    ElMessage.error('获取详情失败：' + error.message);
  }
};

// 更新表格中特定行的点赞量
const updateLikeCountInTable = (id, newLikeCount) => {
  tableData.value = tableData.value.map(item => {
    if (item.id === id) {
      return {...item, likeCount: newLikeCount};
    }
    return item;
  });
};

// 编辑
const handleEdit = async (row) => {
  // 获取图片数据
  const imageRes = await request.get('/circleImage/listByCircleId', {
    params: { circleId: row.id }
  })

  const images = imageRes.data || [];

  // 转换为上传组件需要的格式
  imageFileList.value = images.map(img => ({
    url: img.imageUrl,
    name: `image-${img.id}`,
    uid: `uid-${img.id}`
  }));

  // 赋值表单数据
  payLoadData.value = {
    isEdit: true,
    id: row.id,
    content: row.content,
    images: [...images],
    likeCount: row.likeCount || 0,
    publishTime: row.publishTime
  };

  dialogBuildVisible.value = true;
};

// 保存（新增/编辑）
const handleSave = async () => {
  // 表单验证
  if (!formRef.value) return;
  try {
    await formRef.value.validate();
  } catch (error) {
    return;
  }

  // 验证图片（新建时必传）
  if (payLoadData.value.images.length === 0 && !payLoadData.value.isEdit) {
    ElMessage.warning('请至少上传一张图片');
    return;
  }

  const loading = ElLoading.service({ text: '保存中...' });
  try {
    let circleRes;
    let circleId = null;

    // 1. 保存主表数据
    if (payLoadData.value.isEdit) {
      // 编辑：直接使用已有ID
      circleRes = await request.post('/circle/update', {
        id: payLoadData.value.id,
        content: payLoadData.value.content,
        likeCount: payLoadData.value.likeCount
      });
      circleId = payLoadData.value.id;
    } else {
      // 新增：创建圈子
      circleRes = await request.post('/circle', {
        content: payLoadData.value.content,
        likeCount: payLoadData.value.likeCount,
      });
      // 检查圈子是否创建成功 circleRes.data === true
      if (circleRes.code !== "200" || !circleRes.data) {
        throw new Error(`创建圈子失败：${circleRes.msg || '未知错误'}`);
      }

      const latestRes = await request.get('/circle/page', {
        params: {
          pageNum: 1,
          pageSize: 1,
          orderByColumn: 'id',
          isAsc: 'desc'
        }
      });
      // 提取最新记录的ID
      if (latestRes.data?.records?.length > 0) {
        circleId = latestRes.data.records[0].id;
        // 二次验证：确保标题匹配
        if (latestRes.data.records[0].content !== payLoadData.value.content) {
          throw new Error("获取ID失败：检测到并发创建，请重试");
        }
      } else {
        throw new Error("创建分类成功，但未查询到最新记录");
      }

      // 验证ID有效性
      if (!circleId || isNaN(Number(circleId))) {
        throw new Error(`获取到无效ID：${circleId}`);
      }
    }

    // 2. 编辑时先删除原有图片（全量更新策略）
    if (payLoadData.value.isEdit) {
      await request.delete('/circleImage/deleteByCircleId', {
        params: { circleId }
      });
    }

    // 3. 准备图片数据（按当前顺序设置sort，从1开始）
    const imageData = payLoadData.value.images.map((img, index) => ({
      circleId: Number(circleId),
      imageUrl: img.imageUrl,
      sort: index + 1,
    }));

    // 4. 批量保存图片到circle_image表
    if (imageData.length > 0) {
      const imageRes = await request.post('/circleImage/batchSave', imageData);

      if (imageRes.code !== "200" || !imageRes.data) {
        ElMessage.warning('图片批量保存成功！');
      }
    }

    ElMessage.success(payLoadData.value.isEdit ? '编辑成功' : '新增成功');
    resetForm();
    await fetchCirclelData();
  } catch (error) {
    ElMessage.error('保存失败：' + error.message);
  } finally {
    loading.close();
  }
};

// 单个删除
const handleDelete = (row) => {
  ElMessageBox.confirm(
      '确定要删除这条数据吗？',
      '删除确认',
      {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'warning',
      }
  ).then(async () => {
    const loading = ElLoading.service({text: '删除中...'});
    try {
      // 先删除关联图片
      await request.delete('/circleImage/deleteByCircleId', {
        params: { circleId: row.id }
      })
      // 再删除主表数据
      await request.delete(`/circle/${row.id}`)

      ElMessage.success('删除成功');
      await fetchCirclelData();
    } catch (error) {
      ElMessage.error('删除失败：' + (error.response?.data?.msg || error.message));
    } finally {
      loading.close();
    }
  });
};

// 批量删除
const handleBatchDelete = async () => {
  if (selectedRows.value.length === 0) return;

  const loading = ElLoading.service({text: '删除中...'});
  try {
    const ids = selectedRows.value.map(row => row.id);

    // 批量删除所有关联图片
    await Promise.all(ids.map(id => request.delete('/circleImage/deleteByCircleId', {
      params: { circleId: id }
    })))

    // 批量删除主表数据
    await request.post(`/circle/del/batch`, ids);

    ElMessage.success('批量删除成功');
    selectedRows.value = [];
    batchDeleteVisible.value = false;
    await fetchCirclelData();
  } catch (error) {
    ElMessage.error('批量删除失败：' + (error.response?.data?.msg || error.message));
  } finally {
    loading.close();
  }
};


// Emoji处理
const insertEmoji = (emoji) => {
  if (!emoji || !contentInputRef.value) return;

  // 优先从i属性获取emoji符号，增加兼容性处理
  const emojiSymbol = emoji.i || emoji.emoji || emoji.symbol || emoji.character || '';

  if (!emojiSymbol) {
    ElMessage.warning('无法插入表情，请重试');
    return;
  }

  const inputEl = contentInputRef.value.$el.querySelector('textarea');
  if (!inputEl) return;

  const cursorPos = inputEl.selectionStart;
  const currentValue = payLoadData.value.content || '';

  // 在光标位置插入表情
  payLoadData.value.content = currentValue.substring(0, cursorPos) +
      emojiSymbol +
      currentValue.substring(cursorPos);

  // 重新设置光标位置
  nextTick(() => {
    inputEl.focus();
    inputEl.selectionStart = cursorPos + emojiSymbol.length;
    inputEl.selectionEnd = cursorPos + emojiSymbol.length;
  });
};

// 点击外部隐藏emoji选择器
const handleClickOutside = (e) => {
  if (!dialogBuildVisible.value) return;
  if (!contentInputRef.value || !emojiContainerRef.value) return;

  const inputEl = contentInputRef.value.$el;
  const emojiEl = emojiContainerRef.value;

  const isInInput = inputEl.contains(e.target);
  const isInEmoji = emojiEl.contains(e.target);

  if (!isInInput && !isInEmoji) {
    showEmojiPicker.value = false;
  }
};

// 输入框失焦处理
const handleInputBlur = () => {
  setTimeout(() => {
    if (!emojiContainerRef.value) {
      showEmojiPicker.value = false;
      return;
    }

    if (!emojiContainerRef.value.contains(document.activeElement)) {
      showEmojiPicker.value = false;
    }
  }, 200);
};

// 重置表单
const resetForm = () => {
  showEmojiPicker.value = false;
  duplicateUrlError.value = false;
  uploadProgress.value = {};

  if (formRef.value) {
    formRef.value.resetFields();
  }

  payLoadData.value = {
    isEdit: false,
    id: null,
    content: '',
    images: [],
    likeCount: 0,
    publishTime: null
  };

  imageFileList.value = [];
  urlInput.value = '';
  dialogBuildVisible.value = false;
};
</script>

<style scoped>
.circle-management-container {
  padding: 15px;
  min-height: 100vh;
  box-sizing: border-box;
}

.header-card {
  margin-bottom: 10px;
}

.main-card {
  margin: 10px 0;
  padding: 15px;
}

.pagination-card {
  padding: 15px;
  display: flex;
  justify-content: center;
}

.button-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 10px;
}

.left-group {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  width: 100%;
  max-width: 350px;
}

.right-group {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
}

.emoji-container {
  margin-top: 10px;
  border: 1px solid #e5e7eb;
  border-radius: 6px;
  padding: 10px;
}

.table-image {
  width: 80px;
  height: 80px;
  object-fit: cover;
  cursor: pointer;
  transition: transform 0.2s;
}

.table-image:hover {
  transform: scale(1.05);
}

.view-content {
  padding: 10px 0;
}

.view-item {
  margin-bottom: 15px;
  line-height: 1.6;
}

.label {
  font-weight: bold;
  color: #666;
  display: inline-block;
  width: 80px;
}

/* 图片网格布局 */
.image-grid {
  display: grid;
  gap: 10px;
  margin: 10px 0;
}

.image-grid-item {
  position: relative;
  border-radius: 6px;
  overflow: hidden;
  border: 1px solid #e5e7eb;
  transition: all 0.2s;
}

.grid-image {
  width: 100%;
  object-fit: cover;
  display: block;
}

.delete-image-btn {
  position: absolute;
  top: 5px;
  right: 5px;
  width: 30px;
  height: 30px;
  padding: 0;
  background-color: rgba(255, 255, 255, 0.8);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.2s;
}

.image-grid-item:hover .delete-image-btn {
  opacity: 1;
}

.delete-image-btn:hover {
  background-color: #fff;
  color: #ff4d4f;
}

.dialog-footer {
  display: flex;
  justify-content: center;
  gap: 10px;
}

/* 上传区域优化样式 */
.upload-mode-selector {
  padding: 8px 12px;
  background-color: #f5f7fa;
  border-radius: 4px;
  display: inline-block;
}

.upload-area-container {
  border: 1px solid #e5e7eb;
  border-radius: 6px;
  padding: 16px;
  margin-bottom: 8px;
  transition: border-color 0.2s;
}

.upload-area-container:hover {
  border-color: #4096ff;
}

/* URL上传面板样式 */
.url-upload-panel {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.url-input {
  width: 100%;
}

.url-add-btn {
  align-self: flex-start;
}

/* 本地上传面板样式 */
.local-upload-panel {
  width: 100%;
}

:deep(.el-upload--picture-card) {
  width: 100%;
  height: 160px;
}

:deep(.el-upload-list--picture-card .el-upload-list__item) {
  width: 100%;
  height: 160px;
  margin: 0;
}

:deep(.el-upload-list__item-thumbnail) {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.upload-hint {
  margin-top: 8px;
  font-size: 14px;
  color: #666;
}

.upload-tip {
  margin-top: 4px;
  font-size: 12px;
  color: #999;
}

:deep(.dragging) {
  border-color: #4096ff;
  background-color: rgba(64, 150, 255, 0.1);
}

.image-count-tip {
  margin: 5px 0 10px 0;
}

/* 响应式工具类 */
.hidden-sm {
  display: none !important;
}

.hidden-xs {
  display: none !important;
}

/* 响应式调整 */
@media (min-width: 576px) {
  .hidden-xs {
    display: inline !important;
  }
}

@media (min-width: 768px) {
  .hidden-sm {
    display: inline !important;
  }

  .left-group, .right-group {
    width: auto;
  }
}

@media (max-width: 768px) {
  .circle-management-container {
    padding: 10px;
  }

  .main-card {
    padding: 10px;
  }

  .label {
    width: 60px;
  }

  :deep(.el-upload--picture-card) {
    height: 120px;
  }

  :deep(.el-upload-list--picture-card .el-upload-list__item) {
    height: 120px;
  }
}

@media (max-width: 576px) {
  .pagination-card {
    padding: 10px 5px;
  }

  .view-item {
    margin-bottom: 10px;
  }

  .upload-hint, .upload-tip {
    font-size: 12px;
  }

  :deep(.el-pagination .el-pagination__jump) {
    display: none;
  }
}
</style>
