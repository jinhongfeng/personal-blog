<template>
  <!-- 顶部操作栏 -->
  <el-card class="header-card">
    <div class="button-container">
      <!-- 左侧搜索区域 -->
      <div class="left-group">
        <el-input
            v-model="inputSearch"
            :style="{ width: isMobile ? '100%' : '240px' }"
            placeholder="输入要搜索的内容"
            :prefix-icon="Search"
            clearable
            @keyup.enter="handleSearch"
        />
        <el-button
            type="info"
            :class="isMobile ? 'mt-2 w-full' : 'ml-4'"
            @click="handleSearch"
            size="small"
        >
          搜索
        </el-button>
      </div>

      <!-- 右侧功能按钮 -->
      <div class="right-group">
        <el-button
            type="primary"
            @click="handleCreate"
            size="small"
            :class="isMobile ? 'w-full mb-2' : 'mr-2'"
        >
          <el-icon><CirclePlus /></el-icon>
          <span :class="isMobile ? '' : 'ml-2'">{{ isMobile ? '新增' : '新建' }}</span>
        </el-button>
        <el-button
            type="warning"
            :disabled="selectedRows.length === 0"
            @click="batchDeleteVisible = true"
            size="small"
            :class="isMobile ? 'w-full' : ''"
        >
          <span class="hidden sm:inline">批量删除</span>
          <span class="sm:hidden">批量删</span>
        </el-button>
      </div>
    </div>
  </el-card>

  <!-- 主体表格区域 -->
  <el-card class="homeMain-box mt-2 sm:mt-4 mb-2 sm:mb-4 table-container">
    <el-table
        :data="travelAlbumList"
        style="z-index: 1; width: 100%"
        @selection-change="handleSelectionChange"
        v-loading="tableLoading"
        :cell-class-name="tableCellClass"
    >
      <el-table-column type="selection" width="55" />
      <el-table-column prop="title" label="分类" :width="isMobile ? 100 : 150" />
      <el-table-column prop="astyle" label="标签颜色" :width="isMobile ? 100 : 120">
        <template #default="scope">
          <div
              :style="{
                backgroundColor: scope.row.astyle,
                width: '30px',
                height: '30px',
                borderRadius: '50%'
              }"
              class="color-tag"
          ></div>
        </template>
      </el-table-column>
      <el-table-column prop="imageList" label="图片" :width="isMobile ? 120 : 300">
        <template #default="scope">
          <el-image
              v-if="scope.row.images && scope.row.images.length"
              :src="scope.row.images[0].imageUrl"
              alt="图片"
              show-progress
              :preview-src-list="scope.row.images.map(item => item.imageUrl)"
              fit="cover"
              preview-teleport="body"
              :style="{
                width: isMobile ? '60px' : '80px',
                height: isMobile ? '60px' : '80px'
              }"
          >
            <template #progress="{ activeIndex, total }">
              <span>{{ activeIndex + 1 + '-' + total }}</span>
            </template>
          </el-image>
        </template>
      </el-table-column>

      <el-table-column label="描述" :width="isMobile ? 100 : 200">
        <template #default="scope">
          <span
              v-if="scope.row.images && scope.row.images.length"
              :title="scope.row.images[0].information || '无描述'"
          >
            {{ (scope.row.images[0].information || '无描述').slice(0, isMobile ? 5 : 15) }}{{
              (scope.row.images[0].information || '无描述').length > (isMobile ? 5 : 15) ? '...' : ''
            }}
          </span>
        </template>
      </el-table-column>
      <el-table-column label="发布时间" :width="isMobile ? 100 : 160">
        <template #default="scope">
          <span v-if="scope.row.images && scope.row.images.length">
            {{ formatDate(scope.row.images[0].publishTime) || '无时间' }}
          </span>
        </template>
      </el-table-column>
      <el-table-column label="操作" :width="isMobile ? 120 : ''">
        <template #default="scope">
          <el-button
              size="small"
              @click="handleEdit(scope.row)"
              class="mr-1"
          >
            {{ isMobile ? '编' : '编辑' }}
          </el-button>
          <el-button
              size="small"
              type="danger"
              @click="handleDelete(scope.row)"
          >
            {{ isMobile ? '删' : '删除' }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-card>

  <!-- 分页区域 -->
  <el-card class="pagination-card">
    <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="isMobile ? [5, 10, 15] : [10, 15, 20, 30]"
        layout="total, sizes, prev, pager, next"
        :total="currentTotal"
        @size-change="handlePageChange"
        @current-change="handlePageChange"
        :small="isMobile"
    />
  </el-card>

  <!-- 新建/编辑对话框 -->
  <el-dialog
      v-model="dialogBuildVisible"
      :title="payLoadData.isEdit ? '编辑' : '新建'"
      :width="isMobile ? '90%' : '800px'"
      :before-close="resetForm"
      max-height="90vh"
      overflow-y="auto"
  >
    <el-form
        :model="payLoadData"
        ref="formRef"
        :rules="formRules"
        :label-width="isMobile ? '70px' : '100px'"
    >
      <el-form-item label="分类标题" prop="title" required>
        <el-input v-model="payLoadData.title" placeholder="请输入分类标题" />
      </el-form-item>

      <el-form-item label="标签颜色" prop="astyle" required>
        <el-color-picker
            v-model="payLoadData.astyle"
            show-alpha
            placeholder="选择颜色"
        >
        </el-color-picker>
      </el-form-item>

      <el-form-item label="上传图片" required>
        <!-- 上传方式选择 -->
        <el-radio-group
            v-model="uploadMode"
            class="mb-3"
            @change="handleUploadModeChange"
            :size="isMobile ? 'small' : 'default'"
        >
          <el-radio label="url">输入图片链接</el-radio>
          <el-radio label="local">本地上传</el-radio>
        </el-radio-group>

        <!-- URL上传方式 -->
        <div v-if="uploadMode === 'url'">
          <el-input
              v-model="urlInput"
              autocomplete="off"
              style="width: 100%"
              max-length="200"
              show-word-limit
              placeholder="请输入有效的图片地址（http://或https://开头）"
              :size="isMobile ? 'small' : 'default'"
          />
          <el-button
              type="primary"
              class="mt-2"
              @click="addImageByUrl"
              :disabled="!isValidUrl(urlInput)"
              size="small"
              style="width: 100%"
          >
            添加图片
          </el-button>
          <el-text size="small" type="info" class="mt-1 ml-2 block">
            提示：请输入有效的图片URL地址（http://或https://开头）
          </el-text>
        </div>

        <!-- 本地上传方式 -->
        <div v-if="uploadMode === 'local'">
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
              :limit="isMobile ? 5 : 10"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
          <div class="mt-2 text-sm text-gray-500">
            支持多图上传，可拖拽调整顺序，首张图片将作为封面
          </div>
        </div>

        <!-- 图片详情列表（两种上传方式共用） -->
        <div v-if="payLoadData.images && payLoadData.images.length" class="mt-4">
          <div class="font-medium mb-2">已添加图片（{{ payLoadData.images.length }}张）</div>
          <!-- 支持拖拽排序的图片列表 -->
          <div
              class="image-list"
              @dragover.prevent
              @drop.prevent="handleDrop"
          >
            <div
                v-for="(item, index) in payLoadData.images"
                :key="item.uid"
                class="mb-4 border rounded p-3"
                :draggable="true"
                @dragstart="handleDragStart(index)"
                @dragenter.prevent
                :class="{ 'dragging': dragIndex === index }"
                :style="{ width: isMobile ? '100%' : 'auto' }"
            >
              <el-image
                  :src="item.imageUrl"
                  alt="图片预览"
                  :style="{ maxWidth: isMobile ? '100%' : '200px', marginBottom: '10px' }"
                  :error="handleImageError"
              />
              <el-input
                  v-model="item.information"
                  placeholder="输入图片描述（最多17字）"
                  maxlength="17"
                  show-word-limit
                  class="mb-2"
                  :size="isMobile ? 'small' : 'default'"
              />
              <el-date-picker
                  v-model="item.publishTime"
                  type="date"
                  placeholder="选择发布日期"
                  format="YYYY.MM.DD"
                  value-format="YYYY-MM-DD"
                  :size="isMobile ? 'small' : 'default'"
                  :style="{ width: '100%' }"
              />
              <el-button
                  type="text"
                  class="text-danger mt-2"
                  @click="removeImage(index)"
                  size="small"
              >
                <el-icon><Delete /></el-icon> 删除
              </el-button>
              <!-- 显示当前排序序号 -->
              <div class="mt-2 text-xs text-gray-500">
                排序：{{ index + 1 }}
              </div>
            </div>
          </div>
        </div>
      </el-form-item>
    </el-form>

    <template #footer>
      <div class="dialog-footer">
        <el-button
            type="danger"
            @click="resetForm"
            size="small"
            :class="isMobile ? 'w-1/3' : ''"
        >
          取消
        </el-button>
        <el-button
            type="success"
            @click="handleSave"
            size="small"
            :class="isMobile ? 'w-1/3' : ''"
        >
          保存
        </el-button>
      </div>
    </template>
  </el-dialog>

  <!-- 批量删除确认对话框 -->
  <el-dialog
      v-model="batchDeleteVisible"
      title="批量删除确认"
      :width="isMobile ? '90%' : '400px'"
  >
    <p>您确定要删除选中的 <span class="text-danger">{{ selectedRows.length }}</span> 条数据吗？</p>
    <p>此操作不可撤销，请谨慎操作！</p>
    <template #footer>
      <el-button
          @click="batchDeleteVisible = false"
          size="small"
          :class="isMobile ? 'w-1/3' : ''"
      >
        取消
      </el-button>
      <el-button
          type="danger"
          @click="handleBatchDelete"
          size="small"
          :class="isMobile ? 'w-1/3' : ''"
      >
        确认删除
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import {nextTick, onBeforeUnmount, onMounted, reactive, ref} from "vue";
import {CirclePlus, Delete, Plus, Search} from "@element-plus/icons-vue";
import {ElLoading, ElMessage, ElMessageBox} from "element-plus";
import request from '@/utils/request';

// 响应式状态 - 判断是否为移动设备
const isMobile = ref(false);

// 检测屏幕尺寸变化
const checkScreenSize = () => {
  isMobile.value = window.innerWidth < 768;
};

// 初始化时检查屏幕尺寸
onMounted(() => {
  checkScreenSize();
  window.addEventListener('resize', checkScreenSize);
  fetchTravelData();
});

// 组件卸载前移除事件监听
onBeforeUnmount(() => {
  window.removeEventListener('resize', checkScreenSize);
});

// 状态管理
const dialogBuildVisible = ref(false);
const batchDeleteVisible = ref(false);
const tableLoading = ref(false);
const selectedRows = ref([]);
const currentPage = ref(1);
const pageSize = ref(10);
const currentTotal = ref(0);
const inputSearch = ref('');
const formRef = ref(null);
const imageFileList = ref([]);
// 拖拽排序相关状态
const dragIndex = ref(-1);
const dropIndex = ref(-1);
// 新增：上传方式切换
const uploadMode = ref('local'); // 默认本地上传
const urlInput = ref(''); // URL输入框

// 数据列
const travelAlbumList = ref([]);

// 表单数据
const payLoadData = ref({
  isEdit: false,
  id: null,
  title: '',
  astyle: '#409EFF', // 默认蓝色
  images: [] // 存储图片对象数组
});

// 表单验证规则
const formRules = reactive({
  title: [
    { required: true, message: '请输入分类标题', trigger: 'blur' },
    { max: 50, message: '标题长度不能超过50个字符', trigger: 'blur' }
  ],
  astyle: [
    { required: true, message: '请选择标签颜色', trigger: 'change' }
  ]
});

// 表格单元格样式处理函数
const tableCellClass = ({ }) => {
  return isMobile.value ? 'mobile-table-cell' : '';
};

// 获取旅行相册数据（按sort排序图片）
const fetchTravelData = async () => {
  tableLoading.value = true;
  try {
    const params = {
      pageNum: currentPage.value,
      pageSize: pageSize.value
    };

    if (inputSearch.value.trim()) {
      params.title = inputSearch.value.trim();
    }

    const res = await request.get('/travel/page', { params });

    // 获取每个旅行项的图片列表，并按sort字段排序
    travelAlbumList.value = await Promise.all(res.data.records.map(async (item) => {
      const imageRes = await request.get('/travelImage/listByTravelId', {
        params: {travelId: item.id}
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
    currentTotal.value = res.data.total;
  } catch (error) {
    ElMessage.error('数据加载失败：' + error.message);
  } finally {
    tableLoading.value = false;
  }
};

// 搜索功能
const handleSearch = () => {
  currentPage.value = 1;
  fetchTravelData();
};

// 分页变更
const handlePageChange = () => {
  fetchTravelData();
};

// 选择行变更
const handleSelectionChange = (val) => {
  selectedRows.value = val;
};

// 新建按钮
const handleCreate = () => {
  resetForm();
  dialogBuildVisible.value = true;
};

// 编辑按钮
const handleEdit = async (row) => {
  try {
    // 获取图片数据（后端应按sort排序返回，若未排序则前端二次排序）
    const imageRes = await request.get('/travelImage/listByTravelId', {
      params: { travelId: row.id }
    });

    const images = imageRes.data || [];
    // 确保图片按sort升序排序
    const sortedImages = images.sort((a, b) => a.sort - b.sort);

    // 转换为上传组件需要的格式，确保每个图片都有uid
    imageFileList.value = sortedImages.map(img => ({
      url: img.imageUrl,
      name: `image-${img.id}`,
      uid: `img-${img.id}`,
      status: 'success'
    }));

    // 为图片添加uid用于前端标识
    const imagesWithUid = sortedImages.map(img => ({
      ...img,
      uid: `img-${img.id}`
    }));

    // 赋值表单数据
    payLoadData.value = {
      isEdit: true,
      id: row.id,
      title: row.title,
      astyle: row.astyle,
      images: imagesWithUid
    };

    dialogBuildVisible.value = true;
  } catch (error) {
    ElMessage.error('加载编辑数据失败：' + error.message);
  }
};

// 保存数据
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
    let travelRes;
    let travelId = null;

    // 1. 保存主表数据
    if (payLoadData.value.isEdit) {
      // 编辑：直接使用已有ID
      travelRes = await request.post('/travel/update', {
        id: payLoadData.value.id,
        title: payLoadData.value.title,
        astyle: payLoadData.value.astyle
      });
      travelId = payLoadData.value.id;
    } else {
      // 新增：先创建分类
      travelRes = await request.post('/travel', {
        title: payLoadData.value.title,
        astyle: payLoadData.value.astyle
      });

      // 检查分类是否创建成功
      if (travelRes.code !== "200" || !travelRes.data) {
        throw new Error(`创建分类失败：${travelRes.msg || '未知错误'}`);
      }

      // 2. 查询最新创建的分类ID
      const latestRes = await request.get('/travel/page', {
        params: {
          pageNum: 1,
          pageSize: 1,
          orderByColumn: 'id',
          isAsc: 'desc'
        }
      });

      // 提取最新记录的ID
      if (latestRes.data?.records?.length > 0) {
        travelId = latestRes.data.records[0].id;
        // 二次验证：确保标题匹配
        if (latestRes.data.records[0].title !== payLoadData.value.title) {
          throw new Error("获取ID失败：检测到并发创建，请重试");
        }
      } else {
        throw new Error("创建分类成功，但未查询到最新记录");
      }

      // 最终验证ID有效性
      if (!travelId || isNaN(Number(travelId))) {
        throw new Error(`获取到无效ID：${travelId}`);
      }
    }

    // 3. 编辑时先删除原有图片（全量更新策略）
    if (payLoadData.value.isEdit) {
      await request.delete('/travelImage/deleteByTravelId', {
        params: { travelId }
      });
    }

    // 4. 准备图片数据（按当前顺序设置sort，从1开始）
    const imageData = payLoadData.value.images.map((img, index) => ({
      travelId: Number(travelId), // 确保为数字类型
      imageUrl: img.imageUrl,
      information: img.information || '',
      sort: index + 1,
      publishTime: img.publishTime ? new Date(img.publishTime) : new Date()
    }));

    // 5. 批量保存图片到travel_image表
    if (imageData.length > 0) {
      const imageRes = await request.post('/travelImage/batchSave', imageData);
      if (imageRes.code !== "200" || !imageRes.data) {
        ElMessage.success('图片批量保存成功！')
      }
    }

    ElMessage.success(payLoadData.value.isEdit ? '编辑成功' : '新增成功');
    resetForm();
    await fetchTravelData();
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
    const loading = ElLoading.service({ text: '删除中...' });
    try {
      // 1. 先删除关联图片
      await request.delete('/travelImage/deleteByTravelId', {
        params: { travelId: row.id }
      });

      // 2. 再删除主表数据
      const response = await request.delete(`/travel/${row.id}`);

      // 验证删除结果
      if (response) {
        ElMessage.success('删除成功');
        await fetchTravelData();
      } else {
        ElMessage.error('删除失败：' + (response?.msg || '未知错误'));
      }
    } catch (error) {
      ElMessage.error('删除失败：' + (error.response?.msg || error.message));
    } finally {
      loading.close();
    }
  });
};

// 批量删除
const handleBatchDelete = async () => {
  if (selectedRows.value.length === 0) return;

  const loading = ElLoading.service({ text: '删除中...' });
  try {
    const ids = selectedRows.value.map(row => row.id);

    // 1. 先批量删除所有关联图片
    for (const id of ids) {
      await request.delete('/travelImage/deleteByTravelId', {
        params: { travelId: id }
      });
    }

    // 2. 再批量删除主表数据
    const response = await request.post('/travel/del/batch', ids);

    // 验证删除结果
    if (response) {
      ElMessage.success('批量删除成功');
      selectedRows.value = [];
      batchDeleteVisible.value = false;
      await fetchTravelData();
    } else {
      ElMessage.error('批量删除失败：' + (response?.msg || '未知错误'));
    }
  } catch (error) {
    ElMessage.error('批量删除失败：' + (error.response?.msg || error.message));
  } finally {
    loading.close();
  }
};

// 图片上传前验证
const beforeImageUpload = (file) => {
  const isJPGOrPNG = file.type === 'image/jpeg' || file.type === 'image/png' || file.type === 'image/webp';
  const isLt2M = file.size / 1024 / 1024 < 2;

  if (!isJPGOrPNG) ElMessage.error('上传图片只能是 JPG/PNG/WEBP 格式!');
  if (!isLt2M) ElMessage.error('上传图片大小不能超过 2MB!');

  return isJPGOrPNG && isLt2M;
};

// 自定义图片上传
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

// 重置表单
const resetForm = () => {
  if (formRef.value) {
    formRef.value.resetFields();
  }

  payLoadData.value = {
    isEdit: false,
    id: null,
    title: '',
    astyle: '#409EFF',
    images: []
  };

  imageFileList.value = [];
  urlInput.value = '';
  uploadMode.value = 'local'; // 重置为默认上传方式
  dialogBuildVisible.value = false;
  dragIndex.value = -1;
  dropIndex.value = -1;
};

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return '';

  const date = new Date(dateString);
  return `${date.getFullYear()}.${(date.getMonth() + 1).toString().padStart(2, '0')}.${date.getDate().toString().padStart(2, '0')}`;
};

// 拖拽排序相关方法
const handleDragStart = (index) => {
  dragIndex.value = index;
};

const handleDrop = (e) => {
  // 获取鼠标位置下的元素索引
  const elements = document.querySelectorAll('.image-list > div');
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

// 新增：上传方式切换处理
const handleUploadModeChange = (mode) => {
  // 切换模式时清空当前输入
  if (mode === 'url') {
    urlInput.value = '';
  }
};

// 新增：验证URL是否有效
const isValidUrl = (url) => {
  if (!url) return false;
  try {
    const parsedUrl = new URL(url);
    return parsedUrl.protocol === 'http:' || parsedUrl.protocol === 'https:';
  } catch (e) {
    return false;
  }
};

// 新增：通过URL添加图片
const addImageByUrl = () => {
  if (!isValidUrl(urlInput.value)) {
    ElMessage.warning('请输入有效的图片URL');
    return;
  }

  // 检查是否已添加相同URL的图片
  const isDuplicate = payLoadData.value.images.some(img => img.imageUrl === urlInput.value);
  if (isDuplicate) {
    ElMessage.warning('该图片已添加');
    return;
  }

  // 生成唯一uid
  const uid = `url-${Date.now()}-${Math.random().toString(36).substr(2, 9)}`;

  // 添加到图片列表
  payLoadData.value.images.push({
    imageUrl: urlInput.value,
    information: '',
    publishTime: new Date().toISOString().split('T')[0],
    uid: uid
  });

  // 清空输入框
  urlInput.value = '';

  ElMessage.success('图片添加成功');
};
</script>

<style scoped>
/* 样式定义 */
.button-container {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
  align-items: center;
  padding: 6px 10px;
  gap: 10px;
}

.left-group, .right-group {
  display: flex;
  align-items: center;
}

.left-group {
  flex: 1;
  min-width: 200px;
}

.right-group {
  flex: 0 0 auto;
}

.table-image {
  object-fit: cover;
}

.table-container {
  overflow-y: auto;
  padding: 0;
}
.color-tag {
  margin: 0 auto;
}

.center-content {
  display: flex;
  justify-content: center;
  align-items: center;
}

.text-danger {
  color: #ff4d4f;
  font-weight: bold;
}

.text-warning {
  color: #faad14;
}

/* 响应式边距 */
.mt-2 {
  margin-top: 2px;
}

.mt-4 {
  margin-top: 4px;
}

.mb-2 {
  margin-bottom: 2px;
}

.mb-4 {
  margin-bottom: 4px;
}

.ml-2 {
  margin-left: 2px;
}

.ml-4 {
  margin-left: 4px;
}

.mr-1 {
  margin-right: 1px;
}

.mr-2 {
  margin-right: 2px;
}

.p-3 {
  padding: 12px;
}

/* 拖拽排序样式 */
.image-list {
  min-height: 100px;
}

.dragging {
  opacity: 0.5;
  border: 2px dashed #409EFF;
}

/* 表格样式优化 */
.table-container >>> .el-card__body {
  padding: 15px;
  margin: 0;
}

/* 表格行高优化 */
.table-container >>> .el-table__row {
  height: 80px;
}

/* 修复滚动条与表格内容重叠问题 */
.table-container::-webkit-scrollbar {
  width: 8px;
}
.table-container::-webkit-scrollbar-track {
  background: #f5f5f5;
}
.table-container::-webkit-scrollbar-thumb {
  background: #c0c4cc;
  border-radius: 4px;
}

/* 图片列表样式增强 */
.font-medium {
  font-weight: 500;
}

/* 移动端表格样式 */
.mobile-table-cell {
  font-size: 12px;
}

/* 对话框底部按钮 */
.dialog-footer {
  display: flex;
  justify-content: center;
  gap: 10px;
  width: 100%;
}

.header-card, .pagination-card {
  height: auto;
  min-height: 12%;
}

/* 响应式工具类 */
@media (max-width: 767px) {
  .button-container {
    flex-direction: column;
    align-items: stretch;
  }

  .left-group, .right-group {
    width: 100%;
    justify-content: center;
    margin-bottom: 8px;
  }

  .left-group {
    flex-wrap: wrap;
  }

  .homeMain-box {
    max-height: calc(100vh - 250px);
  }

  /* 表格列溢出处理 */
  .table-container >>> .el-table__header,
  .table-container >>> .el-table__body {
    width: 100% !important;
  }

  /* 修复移动端表格横向滚动问题 */
  .table-container {
    overflow-x: auto;
  }
}

/* 平板及以上设备样式 */
@media (min-width: 768px) {
  .sm:hidden {
    display: none;
  }

  .sm:inline {
    display: inline;
  }

  .sm:ml-4 {
    margin-left: 4px;
  }

  .sm:mr-2 {
    margin-right: 2px;
  }
}
</style>
