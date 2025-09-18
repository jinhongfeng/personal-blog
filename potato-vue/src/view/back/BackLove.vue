<template>
  <el-card class="header-card">
    <div class="button-container">
      <!-- 左侧区域：搜索框和搜索按钮 -->
      <div class="left-group">
        <el-input
            v-model="inputSearch"
            :style="{ width: isMobile ? '100%' : '240px' }"
            placeholder="输入要搜索的内容"
            :prefix-icon="Search"
            clearable
            @keyup.enter="handleSearch"
        />
        <el-button type="info" class="ml-2 sm:ml-4" @click="handleSearch">搜索</el-button>
      </div>

      <!-- 中间区域：组件切换按钮 -->
      <div class="center-content">
        <el-button
            v-for="(item, index) in componentMap"
            :key="index"
            :type="currentComponent === item.title ? 'primary' : 'default'"
            @click="handleComponentChange(item)"
            size="small"
            class="mr-1 sm:mr-2"
        >
          {{ item.title }}
        </el-button>
      </div>

      <!-- 右侧区域：新建和批量删除按钮 -->
      <div class="right-group">
        <el-button
            type="primary"
            @click="handleCreate"
            :disabled="currentComponent === '旅行相册'"
            size="small"
            class="mr-1 sm:mr-2"
        >
          <el-icon><CirclePlus /></el-icon>
          <span class="ml-1 hidden sm:inline">新建</span>
        </el-button>
        <el-button
            type="warning"
            :disabled="selectedRows.length === 0"
            @click="batchDeleteVisible = true"
            size="small"
        >
          <span class="hidden sm:inline">批量删除</span>
          <span class="sm:hidden">批量删</span>
        </el-button>
      </div>
    </div>
  </el-card>

  <!-- 主体内容区域 -->
  <el-card class="homeMain-box mt-2 sm:mt-4 mb-2 sm:mb-4">
    <!-- 时间线表格 -->
    <el-table
        v-if="currentComponent === '时间线'"
        :data="timeLineList"
        stripe
        style="width: 100%"
        @selection-change="handleSelectionChange"
        v-loading="loading"
        :cell-class-name="tableCellClass"
    >
      <el-table-column type="selection" width="55" />
      <el-table-column prop="content" label="内容" :show-overflow-tooltip="true" />
      <el-table-column prop="author" label="作者" :width="isMobile ? 100 : 200" />
      <el-table-column prop="publish" label="发布时间" :width="isMobile ? 120 : 200" />
      <el-table-column label="操作" :width="isMobile ? 120 : 180">
        <template #default="scope">
          <el-button size="small" @click="handleEdit(scope.row)" class="mr-1">编</el-button>
          <el-button size="small" type="danger" @click="handleDelete(scope.row)">删</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 旅行相册表格（仅作为入口展示） -->
    <el-table
        v-else-if="currentComponent === '旅行相册'"
        :data="travelAlbumList"
        style="z-index: 1"
        @selection-change="handleSelectionChange"
        v-loading="loading"
        :cell-class-name="tableCellClass"
    >
      <el-table-column type="selection" width="55" />
      <el-table-column prop="title" label="分类" :width="isMobile ? 100 : 200" />
      <el-table-column prop="imageList" label="图片" :width="isMobile ? 120 : 300">
        <template #default="scope">
          <el-image
              v-if="scope.row.imageList && scope.row.imageList.length"
              :src="scope.row.imageList[0].image"
              alt="图片"
              show-progress
              :preview-src-list="scope.row.imageList.map(item => item.image)"
              fit="cover"
              preview-teleport="body"
              :style="{ width: isMobile ? '60px' : '100px', height: isMobile ? '60px' : '100px' }"
          >
            <template #progress="{ activeIndex, total }">
              <span>{{ activeIndex + 1 + '-' + total }}</span>
            </template>
          </el-image>
        </template>
      </el-table-column>
      <el-table-column label="描述" :width="isMobile ? 100 : 200">
        <template #default="scope">
          <span v-if="scope.row.imageList && scope.row.imageList.length" :title="scope.row.imageList[0].desc">
            {{ (scope.row.imageList[0].desc || '无描述').slice(0, isMobile ? 5 : 15) }}{{ (scope.row.imageList[0].desc || '无描述').length > (isMobile ? 5 : 15) ? '...' : '' }}
          </span>
        </template>
      </el-table-column>
      <el-table-column label="发布时间" :width="isMobile ? 100 : 120">
        <template #default="scope">
          <span v-if="scope.row.imageList && scope.row.imageList.length">
            {{ scope.row.imageList[0].publicTime || '无时间' }}
          </span>
        </template>
      </el-table-column>
      <el-table-column label="操作" :width="isMobile ? 120 : 180">
        <template #default="scope">
          <el-button
              size="small"
              @click="handleAlbumEdit(scope.row)"
              class="mr-1"
          >
            编
          </el-button>
          <el-button
              size="small"
              type="danger"
              @click="handleDelete(scope.row)"
          >
            删
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 留言表格 -->
    <el-table
        v-else-if="currentComponent === '留言'"
        :data="commentsList"
        @selection-change="handleSelectionChange"
        v-loading="loading"
        :cell-class-name="tableCellClass"
    >
      <el-table-column type="selection" width="55" />
      <el-table-column prop="id" :width="isMobile ? 60 : 100" label="ID" />
      <el-table-column prop="content" label="内容" :show-overflow-tooltip="true" />
      <el-table-column prop="publish" label="发布时间" :width="isMobile ? 120 : 200" />
      <el-table-column label="操作" :width="isMobile ? 120 : 180">
        <template #default="scope">
          <el-button size="small" @click="handleEdit(scope.row)" class="mr-1">编</el-button>
          <el-button size="small" type="danger" @click="handleDelete(scope.row)">删</el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-card>

  <!-- 分页 -->
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
      max-height="80vh"
      overflow-y="auto"
  >
    <el-form
        :model="payLoadData"
        ref="formRef"
        :rules="formRules"
        :label-width="isMobile ? '80px' : '100px'"
    >
      <!-- 时间线专属字段 -->
      <template v-if="currentComponent === '时间线'">
        <el-form-item label="内容" prop="content" required>
          <el-input v-model="payLoadData.content" type="textarea" rows="4" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="发布时间" prop="publish" required>
          <el-date-picker
              v-model="payLoadData.publish"
              type="datetime"
              placeholder="选择发布时间"
              value-format="YYYY-MM-DD HH:mm"
              :style="{ width: '100%' }"
          />
        </el-form-item>
        <el-form-item label="作者" prop="author" required>
          <el-input v-model="payLoadData.author" placeholder="请输入作者" />
        </el-form-item>
      </template>

      <!-- 旅行相册专属字段 -->
      <template v-if="currentComponent === '旅行相册'">
        <el-form-item label="分类标题" prop="title" required>
          <el-input v-model="payLoadData.title" placeholder="请输入分类标题" />
        </el-form-item>
        <el-form-item label="图片预览">
          <div v-if="payLoadData.imageList && payLoadData.imageList.length" class="image-preview">
            <el-image
                v-for="(item, index) in payLoadData.imageList"
                :key="index"
                :src="item.image || item.url"
                alt="图片"
                :style="{ width: isMobile ? '60px' : '100px', height: isMobile ? '60px' : '100px', marginRight: '10px' }"
                fit="cover"
            />
          </div>
          <div v-else class="text-gray-500">无图片</div>
        </el-form-item>
        <el-form-item>
          <el-button
              type="primary"
              @click="handleAlbumEdit(payLoadData)"
              class="w-full"
          >
            前往相册详情页编辑
          </el-button>
        </el-form-item>
      </template>

      <!-- 留言专属字段 -->
      <template v-if="currentComponent === '留言'">
        <el-form-item label="内容" prop="content" required>
          <el-input v-model="payLoadData.content" type="textarea" rows="4" placeholder="请输入留言内容" />
        </el-form-item>
        <el-form-item label="发布时间" prop="publish" required>
          <el-date-picker
              v-model="payLoadData.publish"
              type="date"
              placeholder="选择发布日期"
              value-format="YYYY-MM-DD"
              :style="{ width: '100%' }"
          />
        </el-form-item>
        <el-form-item label="ID" v-if="payLoadData.isEdit" prop="id">
          <el-input v-model="payLoadData.id" disabled />
        </el-form-item>
      </template>
    </el-form>

    <template #footer>
      <div class="dialog-footer">
        <el-button type="danger" @click="resetForm" size="small">取消</el-button>
        <el-button
            type="success"
            @click="handleSave"
            :disabled="currentComponent === '旅行相册'"
            size="small"
        >
          {{ payLoadData.isEdit ? '更新' : '保存' }}
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
    <template #content>
      <p>您确定要删除选中的 <span class="text-danger">{{ selectedRows.length }}</span> 条数据吗？</p>
      <p class="mt-2 text-warning">此操作不可撤销！</p>
    </template>
    <template #footer>
      <el-button @click="batchDeleteVisible = false" size="small">取消</el-button>
      <el-button type="danger" @click="handleBatchDelete" size="small">确认删除</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, watch, onMounted, onBeforeUnmount } from "vue";
import { CirclePlus, Search, Plus } from "@element-plus/icons-vue";
import { ElMessage, ElMessageBox, ElLoading } from "element-plus";
import router from "@/router";
import request from "@/utils/request"; // 引入请求工具

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
  fetchData();
});

// 组件卸载前移除事件监听
onBeforeUnmount(() => {
  window.removeEventListener('resize', checkScreenSize);
});

// 状态管理
const dialogBuildVisible = ref(false);
const batchDeleteVisible = ref(false);
const currentComponent = ref("时间线"); // 默认显示时间线
const loading = ref(false);
const selectedRows = ref([]);
const currentPage = ref(1);
const pageSize = ref(10);
const currentTotal = ref(0);
const inputSearch = ref("");
const formRef = ref(null);

// 数据列表
const timeLineList = ref([]);
const travelAlbumList = ref([]);
const commentsList = ref([]);

// 组件映射
const componentMap = reactive([
  {title: "时间线", data: timeLineList, url: "/loveline"},
  {title: "旅行相册", data: travelAlbumList, url: "/back/travel", disabled: false},
  {title: "留言", data: commentsList, url: "/lovemessage"},
]);

// 表单验证规则
const formRules = reactive({
  content: [{required: true, message: "请输入内容", trigger: "blur"}],
  publish: [{required: true, message: "请选择发布时间", trigger: "change"}],
  author: [{required: true, message: "请输入作者", trigger: "blur"}],
  title: [{required: true, message: "请输入分类标题", trigger: "blur"}],
});

// 表单数据
const payLoadData = ref({
  isEdit: false,
  content: "",
  publish: "",
  author: "",
  title: "",
  imageList: [],
  id: "",
});

// 监听组件切换，重新加载数据
watch(currentComponent, () => {
  currentPage.value = 1; // 重置页码
  inputSearch.value = ""; // 清空搜索
  selectedRows.value = []; // 清空选择项
  fetchData(); // 加载新组件数据
}, {immediate: false});

// 表格单元格样式处理函数
const tableCellClass = ({column, row, rowIndex, columnIndex}) => {
  return isMobile.value ? 'mobile-table-cell' : '';
};

// 分页/页大小变更
const handlePageChange = () => {
  fetchData();
};

// 加载数据
const fetchData = async () => {
  const currentItem = componentMap.find(item => item.title === currentComponent.value);
  if (!currentItem) return;

  // 如果是旅行相册，特殊处理（假设需要单独加载）
  if (currentItem.title === "旅行相册") {
    try {
      loading.value = true;
      const res = await request.get(currentItem.url);
      // 直接替换数组，确保响应式更新
      travelAlbumList.value = res.data || [];
      currentTotal.value = res.total || travelAlbumList.value.length;
    } catch (error) {
      ElMessage.error(`加载旅行相册数据失败：${error.message || "未知错误"}`);
    } finally {
      loading.value = false;
    }
    return;
  }

  loading.value = true;
  try {
    const params = {
      pageNum: currentPage.value,
      pageSize: pageSize.value,
      content: inputSearch.value.trim() || undefined,
    };

    const res = await request.get(currentItem.url, {params});

    // 根据组件类型处理数据
    let newData = [];
    if (currentItem.title === "时间线") {
      // 格式化时间线数据
      newData = (res.data || []).map(item => ({
        ...item,
        publish: item.publishTime // 确保publish字段正确映射
      }));
      timeLineList.value = newData; // 直接赋值新数组
    } else if (currentItem.title === "留言") {
      newData = res.data || [];
      commentsList.value = newData; // 直接赋值新数组
    }

    // 处理总条数（根据实际后端结构调整）
    currentTotal.value = res.total || res.data?.length || newData.length;
  } catch (error) {
    ElMessage.error(`加载${currentComponent.value}数据失败：${error.message || "未知错误"}`);
  } finally {
    loading.value = false;
  }
};

// 切换组件
const handleComponentChange = (item) => {
  if (item.title === "旅行相册") {
    // 跳转至相册页面
    router.push(item.url);
    return;
  }
  currentComponent.value = item.title;
};

// 搜索功能
const handleSearch = () => {
  currentPage.value = 1; // 重置页码
  fetchData();
};

// 新建按钮
const handleCreate = () => {
  // 根据当前组件类型初始化表单
  payLoadData.value = {
    isEdit: false,
    content: "",
    publish: new Date().toISOString().slice(0, 16).replace("T", " "), // 默认当前时间
    author: "",
    title: "",
    imageList: [],
    id: "",
  };
  dialogBuildVisible.value = true;
};

// 编辑按钮
const handleEdit = (row) => {
  // 深拷贝数据，避免直接修改原数据
  payLoadData.value = {
    isEdit: true,
    ...JSON.parse(JSON.stringify(row)),
  };

  dialogBuildVisible.value = true;
};

// 相册编辑（跳转至详情页）
const handleAlbumEdit = (row) => {
  resetForm();
  router.push(`/back/travel/edit?id=${row.id || ''}`); // 假设编辑页路径
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

  const currentItem = componentMap.find(item => item.title === currentComponent.value);
  if (!currentItem) return;

  const loading = ElLoading.service({text: '保存中...'});
  try {
    if (payLoadData.value.isEdit) {
      // 编辑操作
      await request.post(`${currentItem.url}/update`, payLoadData.value);
      ElMessage.success('编辑成功');
    } else {
      // 新增操作
      await request.post(currentItem.url, payLoadData.value);
      ElMessage.success('新增成功');
    }
    resetForm();
    await fetchData(); // 重新加载数据
  } catch (error) {
    ElMessage.error('保存失败：' + (error.message || '未知错误'));
  } finally {
    loading.close();
  }
};

// 单个删除
const handleDelete = (row) => {
  const currentItem = componentMap.find(item => item.title === currentComponent.value);
  if (!currentItem) return;

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
      await request.delete(`${currentItem.url}/${row.id}`);
      ElMessage.success('删除成功');
      await fetchData(); // 重新加载数据
    } catch (error) {
      ElMessage.error('删除失败：' + (error.message || '未知错误'));
    } finally {
      loading.close();
    }
  });
};

// 批量删除
const handleBatchDelete = async () => {
  const currentItem = componentMap.find(item => item.title === currentComponent.value);
  if (!currentItem || selectedRows.value.length === 0) return;

  const loading = ElLoading.service({text: '批量删除中...'});
  try {
    const ids = selectedRows.value.map(row => row.id);
    await request.post(`${currentItem.url}/batch-delete`, {ids});
    ElMessage.success('批量删除成功');
    selectedRows.value = [];
    batchDeleteVisible.value = false;
    fetchData(); // 重新加载数据
  } catch (error) {
    ElMessage.error('批量删除失败：' + (error.message || '未知错误'));
  } finally {
    loading.close();
  }
};

// 重置表单
const resetForm = () => {
  if (formRef.value) {
    formRef.value.resetFields();
  }
  dialogBuildVisible.value = false;
  payLoadData.value = {
    isEdit: false,
    content: "",
    publish: "",
    author: "",
    title: "",
    imageList: [],
    id: "",
  };
};

// 选择行变更
const handleSelectionChange = (val) => {
  selectedRows.value = val;
};
</script>

<style scoped>
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

.center-content {
  display: flex;
  justify-content: center;
  align-items: center;
}

.header-card, .pagination-card {
  height: auto;
  min-height: 12%;
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

/* 文本样式 */
.text-danger {
  color: #ff4d4f;
  font-weight: bold;
}

.text-warning {
  color: #faad14;
}

.text-gray-500 {
  color: #8c8c8c;
  font-size: 14px;
}

/* 图片预览 */
.image-preview {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 10px;
}

/* 主内容区域 */
.homeMain-box {
  max-height: calc(100vh - 200px);
  overflow-y: auto;
}

/* 对话框底部按钮 */
.dialog-footer {
  display: flex;
  justify-content: center;
  gap: 10px;
}

/* 滚动条样式优化 */
.homeMain-box::-webkit-scrollbar {
  width: 6px;
}

.homeMain-box::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.homeMain-box::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.homeMain-box::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

/* 移动端表格样式 */
.mobile-table-cell {
  font-size: 12px;
}

/* 响应式工具类 */
@media (max-width: 767px) {
  .button-container {
    flex-direction: column;
    align-items: stretch;
  }

  .left-group, .center-content, .right-group {
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
