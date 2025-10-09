<template>
  <div class="management-container">
    <!-- 头部 -->
    <el-card :style="{ height: isSmallScreen ? 'auto' : 'auto', padding: isSmallScreen ? '0' : '0' }">
      <div class="button-container">
        <!-- 左侧区域：搜索框和搜索按钮 -->
        <div class="left-group">
          <el-input
              v-model="inputSearch"
              :style="{width: searchInputWidth}"
              placeholder="输入标题搜索"
              :prefix-icon="Search"
              clearable
              @keyup.enter="handleSearch"
          />
          <el-button type="info" class="ml-2" @click="handleSearch" :icon="Search">搜索</el-button>
        </div>

        <!-- 右侧区域：新建和批量删除按钮 -->
        <div class="right-group">
          <el-button type="primary" @click="handleCreate">
            <el-icon><CirclePlus /></el-icon>
            <span class="ml-1">新建</span>
          </el-button>
          <el-button
              type="warning"
              :disabled="selectedRows.length === 0"
              @click="batchDeleteVisible = true"
              class="ml-2"
          >
            <el-icon class="mr-1"><delete /></el-icon>
            批量删除
          </el-button>
        </div>
      </div>
    </el-card>

    <!-- 主要内容 -->
    <el-card class="homeMain-box" :style="{ padding: isSmallScreen ? '0' : '0' }">
      <div class="table-scroll-container">
        <el-table
            :data="tableData"
            stripe
            style="width: 100%"
            v-loading="searchLoading"
            @selection-change="handleSelectionChange"
            empty-text="暂无数据"
            :cell-style="tableCellStyle"
            :header-cell-style="tableHeaderStyle"
        >
          <el-table-column type="selection" width="35" />
          <el-table-column label="标题" prop="title" :width="isSmallScreen ? 150 : 200" />
          <el-table-column label="封面" :width="isSmallScreen ? 120 : 200">
            <template #default="scope">
              <img
                  v-if="scope.row.picture"
                  :src="scope.row.picture"
                  alt="封面"
                  :style="{width: isSmallScreen ? '80px' : '100px', height: isSmallScreen ? '60px' : '80px', objectFit: 'cover'}"
              />
              <span v-else>无封面</span>
            </template>
          </el-table-column>
          <el-table-column label="发布时间" prop="publishTime" :width="isSmallScreen ? 120 : 150" />
          <el-table-column label="作者" prop="author" :width="isSmallScreen ? 80 : 100" />
          <el-table-column label="标签" :show-overflow-tooltip="true" :width="isSmallScreen ? 130 : 180">
            <template #default="scope">
              <el-tag
                  v-for="(tag, idx) in scope.row.badge ? scope.row.badge.split(',') : []"
                  :key="idx"
                  class="mr-1"
                  size="small"
              >
                {{ tag }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="浏览量" prop="pageview" :width="100" />
          <el-table-column label="操作" :width="isSmallScreen ? 'auto' : 'auto'">
            <template #default="scope">
              <el-button-group size="small">
                <el-button
                    type="success"
                    :icon="Edit"
                    @click="handleEdit(scope.row)"
                    size="small"
                    :title="isSmallScreen ? '编辑' : ''"
                />
                <el-popconfirm
                    title="是否确认删除？"
                    placement="top"
                    @confirm="handleSingleDelete(scope.row)"
                    @cancel="() => {}"
                >
                  <template #reference>
                    <el-button
                        type="danger"
                        :icon="Delete"
                        size="small"
                        :title="isSmallScreen ? '删除' : ''"
                    />
                  </template>
                </el-popconfirm>
              </el-button-group>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-card>

    <!-- 分页 -->
    <el-card class="center-content" :style="{
        height: isSmallScreen ? 'auto' : 'auto',
        padding: isSmallScreen ? '0' : '0'
      }">
      <div class="pagination-block">
        <el-pagination
            v-model:current-page="pagination.pageNum"
            v-model:page-size="pagination.pageSize"
            :page-sizes="isSmallScreen ? [5, 10, 15] : [10, 15, 20, 30]"
            :layout="isSmallScreen ? 'prev, pager, next' : 'sizes, prev, pager, next, total'"
            :total="pagination.total"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 新建/编辑对话框 -->
    <el-dialog
        v-model="dialogBuildVisible"
        :title="payloadData.id ? '编辑内容' : '新建内容'"
        :width="dialogWidth"
        :before-close="resetForm"
        max-height="90vh"
        overflow-y="auto"
    >
      <el-form
          :model="payloadData"
          :label-width="isSmallScreen ? '80px' : '120px'"
          ref="formRef"
          :rules="formRules"
          class="full-width-form"
      >
        <el-form-item label="标题" prop="title">
          <el-input
              v-model="payloadData.title"
              autocomplete="off"
              style="width: 100%"
              max-length="50"
              show-word-limit
              :disabled="saveLoading"
          />
        </el-form-item>

        <el-form-item label="封面" prop="picture">
          <!-- 上传方式选择 -->
          <el-radio-group
              v-model="uploadMode"
              class="mb-3"
              :disabled="saveLoading"
              @change="handleUploadModeChange"
          >
            <el-radio label="url">输入图片链接</el-radio>
            <el-radio label="local">本地上传</el-radio>
          </el-radio-group>

          <!-- URL上传方式 -->
          <el-form-item
              v-if="uploadMode === 'url'"
              class="mb-0"
              key="url-upload"
          >
            <el-input
                v-model="payloadData.picture"
                autocomplete="off"
                style="width: 100%"
                max-length="200"
                show-word-limit
                placeholder="请输入有效的图片地址（http://或https://开头）"
                :disabled="saveLoading"
            />
            <el-text size="small" type="info" class="mt-1">
              提示：请输入有效的图片URL地址（http://或https://开头）
            </el-text>

            <!-- 图片预览 -->
            <div v-if="payloadData.picture" class="mt-2">
              <el-image
                  :src="payloadData.picture"
                  alt="封面预览"
                  :style="{width: isSmallScreen ? '120px' : '150px', height: isSmallScreen ? '80px' : '100px', objectFit: 'cover'}"
                  fit="cover"
                  :preview-src-list="[payloadData.picture]"
                  preview-teleport="body"
              >
                <template #error>
                  <div class="image-error">图片无法显示</div>
                </template>
                <template #placeholder>
                  <div class="image-placeholder">加载中...</div>
                </template>
              </el-image>
            </div>
          </el-form-item>

          <!-- 本地上传方式 -->
          <el-form-item
              v-if="uploadMode === 'local'"
              class="mb-0"
              key="local-upload"
          >
            <el-upload
                v-model:file-list="fileList"
                action="#"
                list-type="picture"
                :on-success="handleUploadSuccess"
                :on-error="handleUploadError"
                :limit="1"
                :on-exceed="handleUploadExceed"
                :before-upload="beforeUpload"
                :http-request="handleAvatarUpload"
                :disabled="saveLoading"
                :file-list="initFileList"
            >
              <el-button type="primary" :style="{width: isSmallScreen ? '150px' : '200px'}">上传封面</el-button>
              <template #tip>
                <div class="el-upload__tip">
                  支持jpg/png格式，文件大小不超过500kb
                </div>
              </template>
            </el-upload>
          </el-form-item>
        </el-form-item>

        <el-form-item label="标签">
          <div class="tag-input-container">
            <el-input
                v-model="tagInput"
                placeholder="请输入标签（最多3个）"
                @keyup.enter.native="addTag"
                :style="{width: isSmallScreen ? '100%' : '400px', marginRight: isSmallScreen ? '0' : '10px', marginBottom: isSmallScreen ? '10px' : '0'}"
                max-length="10"
                :disabled="saveLoading"
            >
              <template #append>
                <el-button @click="addTag" :disabled="saveLoading">添加</el-button>
              </template>
            </el-input>
          </div>
          <div class="tag-container" style="margin-top: 10px;">
            <el-tag
                v-for="(tag, index) in payloadData.badgeList"
                :key="index"
                type="warning"
                closable
                @close="removeTag(index)"
                class="ml-2"
                :disabled="saveLoading"
                size="small"
            >
              {{ tag }}
            </el-tag>
          </div>
        </el-form-item>

        <el-form-item label="内容" prop="information">
            <div class="markdown-container">
              <EditorMD
                  v-model="payloadData.information"
                  :disabled="saveLoading"
                  :height="isSmallScreen ? '300px' : '500px'"
                  :autofocus="false"
              />
            </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="center-content">
          <el-button type="danger" @click="resetForm" :disabled="saveLoading" class="mr-2">取消</el-button>
          <el-button type="success" @click="handleSave" :loading="saveLoading">保存</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 批量删除对话框 -->
    <el-dialog v-model="batchDeleteVisible" title="批量删除确认" :width="isSmallScreen ? '90%' : '400px'">
      <p>您确定要删除选中的 <span>{{ selectedCount }}</span> 条数据吗？</p>
      <p>此操作不可撤销，请谨慎操作！</p>
      <template #footer>
        <el-button @click="batchDeleteVisible = false" class="mr-2">取消</el-button>
        <el-button type="danger" @click="handleBatchDelete">确认删除</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { CirclePlus, Delete, Edit, Search } from '@element-plus/icons-vue'
import { ref, computed, onMounted, nextTick, watchEffect } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request';
import { debounce } from 'lodash';
// 导入封装的Markdown编辑器组件
import {defaultWebName} from "@/utils/defaultConfig";
import EditorMD from "@/components/subComponents/EditorMD.vue";

// 响应式相关
const isSmallScreen = ref(window.innerWidth < 768)
const isMobileScreen = ref(window.innerWidth < 576)
const searchInputWidth = computed(() => {
  if (isMobileScreen.value) return '100%'
  if (isSmallScreen.value) return '180px'
  return '240px'
})
const dialogWidth = computed(() => {
  if (isMobileScreen.value) return '95%'
  if (isSmallScreen.value) return '80%'
  return '900px'
})

// 监听窗口大小变化
watchEffect(() => {
  const handleResize = () => {
    isSmallScreen.value = window.innerWidth < 768
    isMobileScreen.value = window.innerWidth < 576
  }

  window.addEventListener('resize', handleResize)
  return () => window.removeEventListener('resize', handleResize)
})

// 表格样式
const tableCellStyle = computed(() => {
  return {
    fontSize: isSmallScreen.value ? '12px' : '14px'
  }
})

const tableHeaderStyle = computed(() => {
  return {
    fontSize: isSmallScreen.value ? '13px' : '14px',
    fontWeight: 'bold'
  }
})

// 搜索相关
const inputSearch = ref('')
const searchLoading = ref(false)  // 统一的加载状态

// 表格数据
const tableData = ref([])

// 分页信息
const pagination = ref({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

// 加载数据
const loadData = async () => {
  try {
    searchLoading.value = true
    // 构造请求参数
    const params = {
      pageNum: pagination.value.pageNum,
      pageSize: pagination.value.pageSize,
      ...(inputSearch.value.trim() && { title: inputSearch.value.trim() })  // 搜索标题
    }

    const res = await request.get('/blog/page', { params })

    if (res.code === "200") {
      tableData.value = res.data.records || []
      pagination.value.total = res.data.total || 0
    } else {
      ElMessage.error('获取数据失败：' + (res.msg || '未知错误'))
    }
  } catch (error) {
    ElMessage.error('请求错误：' + (error.message || '网络异常'))
  } finally {
    searchLoading.value = false
  }
}

// 上传相关
const uploadMode = ref('local') // 默认本地上传
const fileList = ref([])
const saveLoading = ref(false)

// 处理上传方式切换
const handleUploadModeChange = async (mode) => {
  try {
    // 清除图片相关验证
    if (formRef.value) {
      await formRef.value.clearValidate('picture');
    }

    // 切换模式时的清理工作
    if (mode === 'url') {
      fileList.value = []; // 切换到URL模式时清空上传列表
    } else {
      // 切换到本地模式时无需清空URL，保持数据连贯性
    }
  } catch (error) {
    ElMessage.error(`切换失败: ${error.message || '未知错误'}`);
  }
};

// 上传图片
const handleAvatarUpload = async (params) => {
  try {
    const formData = new FormData();
    formData.append('file', params.file);
    const response = await request.post('/file/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
      responseType: 'text'
    });

    if (response && response.startsWith('http')) {
      payloadData.value.picture = response;
      ElMessage.success('图片上传成功');
      params.onSuccess(response);
    } else {
      throw new Error('上传失败，服务器返回异常: ' + response);
    }

  } catch (error) {
    ElMessage.error('图片上传失败: ' + error.message)
    params.onError(error)
  }
}

// 上传前验证
const beforeUpload = (file) => {
  // 验证文件类型
  const isJpgOrPng = file.type === 'image/jpeg' || file.type === 'image/png'
  if (!isJpgOrPng) {
    ElMessage.error('只能上传JPG/PNG格式的图片')
    return false
  }

  // 验证文件大小
  const isLt500K = file.size / 1024 < 500
  if (!isLt500K) {
    ElMessage.error('图片大小不能超过500KB')
    return false
  }

  return true
}

// 上传成功处理
const handleUploadSuccess = () => {
  ElMessage.success('图片上传成功');
};

// 上传失败处理
const handleUploadError = (err) => {
  ElMessage.error('图片上传失败: ' + (err.message || '未知错误'));
};

// 搜索处理（防抖300ms）
const handleSearch = debounce(() => {
  pagination.value.pageNum = 1  // 重置到第一页
  loadData()
}, 300)

// 分页事件处理
const handleSizeChange = (pageSize) => {
  pagination.value.pageSize = pageSize
  loadData()
}
const handleCurrentChange = (pageNum) => {
  pagination.value.pageNum = pageNum
  loadData()
}

// 初始化加载数据
onMounted(() => {
  loadData()
})

// 选中行管理
const selectedRows = ref([])
const selectedCount = ref(0);
const handleSelectionChange = (rows) => {
  selectedRows.value = rows
  selectedCount.value = rows.length;
}

// 对话框相关
const dialogBuildVisible = ref(false)
const batchDeleteVisible = ref(false)
const formRef = ref(null)  // 表单引用

// 表单数据
const payloadData = ref({
  id: null,
  title: '',
  picture: '',
  pictureList: [],
  information: '',
  badge: '',
  badgeList: [],
  author: '',
  pageview: 0
})
const tagInput = ref('')  // 标签输入框

// 表单验证规则
const formRules = ref({
  title: [
    { required: true, message: '请输入标题', trigger: 'blur' },
    { max: 50, message: '标题长度不能超过50个字符', trigger: 'blur' }
  ],
  information: [
    { required: true, message: '请输入内容', trigger: 'blur' },
    { max: 2000, message: '内容长度不能超过2000个字符', trigger: 'blur' }
  ],
  picture: [
    {
      validator: (rule, value, callback) => {
        // URL模式且有输入时验证URL格式
        if (uploadMode.value === 'url' && value) {
          const urlRegex = /^(https?:\/\/)/;
          if (!urlRegex.test(value)) {
            callback(new Error('请输入以http://或https://开头的有效URL'));
            return;
          }
        }
        callback();
      },
      trigger: 'blur'
    }
  ]
});

// 初始化文件列表（用于编辑时回显）
const initFileList = computed(() => {
  return payloadData.value.picture
      ? [{ url: payloadData.value.picture, name: 'cover' }]
      : [];
});

// 重置表单
const resetForm = () => {
  payloadData.value = {
    id: null,
    title: '',
    picture: '',
    pictureList: [],
    information: '',
    badge: '',
    badgeList: [],
    author: '',
    pageview: 0
  }
  tagInput.value = ''
  fileList.value = []
  uploadMode.value = 'local' // 重置为默认上传方式
  dialogBuildVisible.value = false
  // 重置表单验证状态
  nextTick(() => {
    formRef.value?.resetFields()
  })
}

// 新建按钮点击
const handleCreate = () => {
  resetForm()
  dialogBuildVisible.value = true
}

// 标签操作
const addTag = () => {
  const tag = tagInput.value.trim()
  if (!tag) {
    ElMessage.warning('标签内容不能为空')
    return
  }
  if (payloadData.value.badgeList.length >= 3) {
    ElMessage.warning('最多只能添加3个标签')
    return
  }
  if (payloadData.value.badgeList.includes(tag)) {
    ElMessage.warning('该标签已存在')
    return
  }
  payloadData.value.badgeList.push(tag)
  tagInput.value = ''  // 清空输入框
}
const removeTag = (index) => {
  payloadData.value.badgeList.splice(index, 1)
}

// 文件上传超出限制回调
const handleUploadExceed = () => {
  ElMessage.warning('最多只能上传1张封面图片')
}

// 保存/更新操作
const handleSave = async () => {
  try {
    // 表单验证
    await formRef.value.validate();

    saveLoading.value = true
    // 处理标签
    payloadData.value.badge = payloadData.value.badgeList.join(',')
    payloadData.value.author = defaultWebName
    let res
    if (payloadData.value.id) {
      // 编辑操作
      res = await request.post('/blog/update', payloadData.value)
    } else {
      // 新建操作
      res = await request.post('/blog', payloadData.value)
    }

    if (res.code === "200") {
      ElMessage.success(payloadData.value.id ? '更新成功' : '创建成功')
      dialogBuildVisible.value = false
      await loadData()
    } else {
      ElMessage.error(res.msg || '操作失败')
    }
  } catch (error) {
    // 表单验证失败不显示错误提示
    if (!(error.name === 'ValidationError')) {
      ElMessage.error('保存失败：' + (error.message || '未知错误'))
    }
  } finally {
    saveLoading.value = false
  }
}

// 编辑操作
const handleEdit = (row) => {
  // 填充表单数据
  payloadData.value = {
    id: row.id,
    title: row.title,
    picture: row.picture || '',
    pictureList: row.picture ? [{ url: row.picture }] : [],
    information: row.information || '',
    badge: row.badge || '',
    badgeList: row.badge ? row.badge.split(',') : [],
    author: row.author || '',
    pageview: row.pageview || 0
  }

  // 根据图片地址自动选择合适的上传模式
  if (row.picture) {
    fileList.value = [{ url: row.picture, name: 'cover' }]
    uploadMode.value = row.picture.startsWith('http') ? 'url' : 'local'
  } else {
    fileList.value = []
    uploadMode.value = 'local'
  }

  dialogBuildVisible.value = true
}

// 单个删除
const handleSingleDelete = async (row) => {
  try {
    const res = await request.delete(`/blog/${row.id}`)
    if (res.code === "200") {
      ElMessage.success('删除成功')
      tableData.value = tableData.value.filter(item => item.id !== row.id)  // 本地刷新
      pagination.value.total--  // 总数减1
    } else {
      ElMessage.error('删除失败：' + (res.msg || '未知错误'))
    }
  } catch (error) {
    ElMessage.error('删除失败：' + error.message)
  }
}

// 批量删除
const handleBatchDelete = async () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请先选择要删除的内容')
    return
  }

  try {
    const ids = selectedRows.value.map(row => row.id)
    const res = await request.post('/blog/del/batch', ids)  // 匹配后端批量删除接口
    if (res.code === "200") {
      ElMessage.success('批量删除成功')
      // 本地刷新表格
      tableData.value = tableData.value.filter(
          item => !selectedRows.value.some(row => row.id === item.id)
      )
      pagination.value.total -= selectedRows.value.length  // 总数减少
      selectedRows.value = []
      batchDeleteVisible.value = false
    } else {
      ElMessage.error('批量删除失败：' + (res.msg || '未知错误'))
    }
  } catch (error) {
    ElMessage.error('批量删除失败：' + error.message)
  }
}
</script>

<style scoped>
/* 基础样式 */
.mt-2 {
  margin-top: 10px !important;
}

.ml-2 {
  margin-left: 8px !important;
}

.mr-1 {
  margin-right: 4px !important;
}

.mr-2 {
  margin-right: 8px !important;
}

/* 按钮容器布局 */
.button-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
  flex-wrap: wrap;
  gap: 10px;
}

.left-group, .right-group {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
}

/* 分页样式 */
.pagination-block {
  text-align: right;
  padding: 10px 0;
  width: 100%;
}

.center-content {
  display: flex;
  justify-content: center;
  align-items: center;
}

/* 表格滚动容器 */
.table-scroll-container {
  max-height: calc(100vh - 300px);
  overflow-y: auto;
  overflow-x: auto;
  cursor: pointer;
}

.table-scroll-container::-webkit-scrollbar {
  width: 6px; /* 滚动条宽度 */
  height: 6px; /* 横向滚动条高度 */
}

.table-scroll-container::-webkit-scrollbar-track {
  background: #f1f1f1; /* 轨道背景 */
  border-radius: 3px;
}

.table-scroll-container::-webkit-scrollbar-thumb {
  background: #c1c1c1; /* 滚动条颜色 */
  border-radius: 3px;
}

.table-scroll-container::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8; /* hover时颜色 */
}

/* 图片错误和占位样式 */
.image-error, .image-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f5f5f5;
  color: #999;
  font-size: 12px;
}

/* 标签容器样式优化 */
.tag-container {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 10px;
}

.tag-input-container {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
}

/* 让表单充分利用对话框宽度 */
.full-width-form {
  width: 100%;
  box-sizing: border-box;
  padding: 0 10px;
}

/* 响应式适配 */
@media (max-width: 768px) {
  .button-container {
    flex-direction: column;
    align-items: stretch;
  }

  .left-group, .right-group {
    width: 100%;
    justify-content: flex-start;
  }

  .right-group {
    justify-content: flex-end;
  }

  .pagination-block {
    text-align: center;
  }

  .el-table .el-button--small {
    padding: 5px;
  }

  .el-tag {
    white-space: nowrap;
  }
}

@media (max-width: 576px) {
  .left-group, .right-group {
    flex-direction: column;
    align-items: stretch;
  }

  .left-group .el-input,
  .left-group .el-button,
  .right-group .el-button {
    width: 100% !important;
    margin-left: 0 !important;
    margin-bottom: 8px;
  }

  .el-dialog__header {
    padding: 15px 15px 5px;
  }

  .el-dialog__body {
    padding: 15px;
  }

  .el-dialog__footer {
    padding: 10px 15px 15px;
  }

  .el-form-item__label {
    padding-right: 5px;
  }
}
</style>
