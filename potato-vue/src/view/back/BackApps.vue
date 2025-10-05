<template>
  <div class="apply-management-container">
    <!-- 头部 -->
    <el-card :style="{ height: isSmallScreen ? 'auto' : 'auto', padding: isSmallScreen ? '0' : '0' }">
      <div class="button-container">
        <!-- 左侧区域：搜索框和搜索按钮 -->
        <div class="left-group">
          <el-input
              v-model="inputSearch"
              :style="{ width: isSmallScreen ? '100%' : '240px' }"
              placeholder="输入标题搜索"
              :prefix-icon="Search"
              clearable
              @keyup.enter="handleSearch"
              :disabled="searchLoading"
              :size="isSmallScreen ? 'small' : 'default'"
          />
          <el-button
              type="info"
              :class="isSmallScreen ? 'mt-2 w-full' : 'ml-2'"
              @click="handleSearch"
              :loading="searchLoading"
              :size="isSmallScreen ? 'small' : 'default'"
              :icon="Search"
          >
            搜索
          </el-button>
        </div>

        <!-- 右侧区域：新建和批量删除按钮 -->
        <div class="right-group">
          <el-button
              type="primary"
              @click="handleCreate"
              :loading="saveLoading"
              :size="isSmallScreen ? 'small' : 'default'"
              :class="isSmallScreen ? 'w-full mb-2' : ''"
          >
            <el-icon class="mr-1"><CirclePlus /></el-icon>
            <span class="ml-1">新建</span>
          </el-button>
          <el-button
              type="warning"
              :disabled="selectedRows.length === 0"
              @click="batchDeleteVisible = true"
              :class="isSmallScreen ? 'w-full' : 'ml-2'"
              :loading="deleteLoading"
              :size="isSmallScreen ? 'small' : 'default'"
              :icon="Delete"
          >
            批量删除
          </el-button>
        </div>
      </div>
    </el-card>

    <!-- 主要内容 -->
    <el-card class="homeMain-box" :style="{ height: isSmallScreen ? 'auto' : '75vh', padding: isSmallScreen ? '0' : '0' }">
      <div class="table-scroll-container">
        <el-table
            :data="tableData" stripe
            style="width: 100%; margin-bottom: 10px"
            v-loading="searchLoading"
            @selection-change="handleSelectionChange"
            empty-text="暂无数据"
            :row-key="row => row.id"
            :cell-style="{ padding: isSmallScreen ? '4px 8px' : '5px 5px' }"
            :header-cell-style="{ padding: isSmallScreen ? '6px 8px' : '5px 12px' }"
        >
          <el-table-column type="selection" width="50" />
          <el-table-column label="名称" prop="title" :width="isSmallScreen ? 100 : 100"/>
          <el-table-column label="描述" prop="description" :width="isSmallScreen ? 150 : 150"/>
          <el-table-column label="分类" prop="category" :width="isSmallScreen ? 100 : 100">
            <template #default="scope">
              <el-tag type="primary">{{ scope.row.category }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="链接" prop="link"
                           :show-overflow-tooltip="true" :width="isSmallScreen ? 180 : 200">
            <template #default="scope">
              <a
                  :href="scope.row.link"
                  target="_blank"
                  class="link-external"
                  v-if="scope.row.link"
              >
                {{ scope.row.link.length > (isSmallScreen ? 10 : 20)
                  ? scope.row.link.slice(0, isSmallScreen ? 10 : 20) + '...'
                  : scope.row.link
                }}
              </a>
              <span v-else>无链接</span>
            </template>
          </el-table-column>
          <el-table-column label="图标" prop="icon" :width="isSmallScreen ? 100 : 150">
            <template #default="scope">
              <el-image
                  v-if="scope.row.icon"
                  :src="scope.row.icon"
                  alt="图标"
                  :style="{
                    width: isSmallScreen ? '60px' : '100px',
                    height: isSmallScreen ? '50px' : '80px',
                    objectFit: 'cover',
                    background: `${scope.row.astyle}`
                  }"
                  fit="cover"
                  :preview-src-list="[scope.row.icon]"
                  preview-teleport="body"
              >
                <template #error>
                  <div class="image-error" :style="{
                  width: isSmallScreen ? '60px' : '100px',
                  height: isSmallScreen ? '50px' : '80px'
                }">
                    加载失败
                  </div>
                </template>
              </el-image>
              <el-image v-else :src="defaultToolImg" alt="默认图片"></el-image>
            </template>
          </el-table-column>
          <el-table-column label="样式" prop="astyle" :width="isSmallScreen ? 150 : 150">
            <template #default="scope">
              <div v-if="scope.row.astyle"
                  :style="{ backgroundColor: scope.row.astyle, width: '30px',height: '30px',borderRadius: '50%' }"
                  class="color-tag">
              </div>
              <div v-else>无样式</div>
            </template>
          </el-table-column>
          <el-table-column label="操作" :width="isSmallScreen ? 140 : ''">
            <template #default="scope">
              <el-button-group>
                <el-button
                    type="success"
                    :icon="Edit"
                    @click="handleEdit(scope.row)"
                    size="small"
                    :loading="saveLoading"
                    title="编辑"
                >
                  <span class="ml-1">编辑</span>
                </el-button>
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
                        :loading="deleteLoading"
                        title="删除"
                    >
                      <span class="ml-1">删除</span>
                    </el-button>
                  </template>
                </el-popconfirm>
              </el-button-group>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-card>

    <!-- 分页 -->
    <el-card
        class="center-content"
        :style="{
      height: isSmallScreen ? 'auto' : 'auto',
      padding: isSmallScreen ? '0' : '0'
    }"
    >
      <div class="pagination-block" :style="{ textAlign: isSmallScreen ? 'center' : 'right' }">
        <el-pagination
            v-model:current-page="pagination.pageNum"
            v-model:page-size="pagination.pageSize"
            :page-sizes="[10, 15, 20, 30]"
            layout="sizes, prev, pager, next, total"
            :total="pagination.total"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
            :disabled="searchLoading"
            :small="isSmallScreen"
        />
      </div>
    </el-card>

    <!-- 新建/编辑对话框 -->
    <el-dialog
        v-model="dialogBuildVisible"
        :title="payloadData.id ? '编辑内容' : '新建内容'"
        :width="isSmallScreen ? '90%' : '800px'"
        :before-close="resetForm"
        max-height="80vh"
        overflow-y="auto"
        :close-on-click-modal="false"
    >
      <el-form
          :model="payloadData"
          :label-width="isSmallScreen ? '80px' : '120px'"
          ref="formRef"
          :rules="formRules"
          status-icon
      >
        <el-form-item label="名称" prop="title">
          <el-input
              v-model="payloadData.title"
              autocomplete="off"
              style="width: 100%"
              max-length="50"
              show-word-limit
              placeholder="请输入名称"
              :size="isSmallScreen ? 'small' : 'default'"
          />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input  v-model="payloadData.description"
                     autocomplete="off"
                     style="width: 100%"
                     max-length="50"
                     show-word-limit
                     placeholder="请输入链接"
                     :size="isSmallScreen ? 'small' : 'default'">
          </el-input>
        </el-form-item>

        <el-form-item label="分类" prop="category">
          <el-select
              v-model="payloadData.category"
              placeholder="分类字段"
              clearable
          >
            <el-option label="小工具" value="toolsList" />
            <el-option label="一些网站" value="websiteList" />
            <el-option label="博客" value="blogList" />
          </el-select>
        </el-form-item>

        <el-form-item label="链接" prop="link">
          <el-input
              v-model="payloadData.link"
              autocomplete="off"
              style="width: 100%"
              max-length="200"
              show-word-limit
              placeholder="请输入链接"
              :size="isSmallScreen ? 'small' : 'default'"
          />
        </el-form-item>

        <el-form-item label="图标" prop="icon">
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
                v-model="payloadData.icon"
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
            <div v-if="payloadData.icon" class="mt-2">
              <el-image
                  :src="payloadData.icon"
                  alt="封面预览"
                  :style="{width: isSmallScreen ? '120px' : '150px', height: isSmallScreen ? '80px' : '100px', objectFit: 'cover'}"
                  fit="cover"
                  :preview-src-list="[payloadData.icon]"
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

        <el-form-item label="样式" prop="astyle">
          <el-color-picker v-model="payloadData.astyle" />
        </el-form-item>
      </el-form>

      <template #footer>
        <div class="center-content">
          <el-button
              type="danger"
              @click="resetForm"
              :size="isSmallScreen ? 'small' : 'default'"
          >
            取消
          </el-button>
          <el-button
              type="success"
              @click="handleSave"
              :loading="saveLoading"
              :size="isSmallScreen ? 'small' : 'default'"
              class="ml-2"
          >
            保存
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 批量删除对话框 -->
    <el-dialog
        v-model="batchDeleteVisible"
        title="批量删除确认"
        :width="isSmallScreen ? '90%' : '400px'"
        :close-on-click-modal="false"
    >
      <p>您确定要删除选中的 <span class="text-danger font-bold">{{ selectedCount }}</span> 条数据吗？</p>
      <p class="text-warning">此操作不可撤销，请谨慎操作！</p>
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
            :loading="deleteLoading"
            :size="isSmallScreen ? 'small' : 'default'"
            class="ml-2"
        >
          确认删除
        </el-button>
      </template>
    </el-dialog>
  </div>


</template>

<script setup>
import {CirclePlus, Delete, Edit, Search} from '@element-plus/icons-vue'
import {ref, onMounted, nextTick, reactive, watchEffect, computed} from 'vue'
import {ElMessage} from 'element-plus'
import request from '@/utils/request';
import {debounce} from 'lodash';
import {defaultToolImg} from "@/utils/defaultConfig";

// 响应式相关
const isSmallScreen = ref(window.innerWidth < 768)
const isXsScreen = ref(window.innerWidth < 576)
const uploadMode = ref('local') // 默认本地上传
// 监听窗口大小变化
watchEffect(() => {
  const handleResize = () => {
    isSmallScreen.value = window.innerWidth < 768
    isXsScreen.value = window.innerWidth < 576
  }

  window.addEventListener('resize', handleResize)
  return () => window.removeEventListener('resize', handleResize)
})

// 搜索相关
const inputSearch = ref('')
const searchLoading = ref(false)  // 搜索加载状态

// 表格数据
const tableData = ref([])

// 分页信息
const pagination = ref({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

// 操作状态管理
const saveLoading = ref(false)    // 保存/编辑加载状态
const deleteLoading = ref(false)  // 删除加载状态

// 加载数据
const loadData = async () => {
  try {
    searchLoading.value = true
    // 构造请求参数
    const params = {
      pageNum: pagination.value.pageNum,
      pageSize: pagination.value.pageSize,
      ...(inputSearch.value.trim() && {title: inputSearch.value.trim()})  // 搜索标题
    }

    const res = await request.get('/apps/page', {params})

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

// 搜索处理（防抖300ms，避免频繁请求）
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

// 表单验证规则
const formRules = reactive({
  title: [
    {required: true, message: '请输入名称', trigger: 'blur'},
    {min: 1, max: 50, message: '名称长度在 1 到 50 个字符', trigger: 'blur'}
  ],
  category: [
    {required: true, message: '请输入分类', trigger: 'blur'},
    {min: 1, max: 20, message: '名称长度在 1 到 20 个字符', trigger: 'blur'}
  ],
})

// 表单数据
const payloadData = ref({
  id: null,  // 用于编辑时的唯一标识
  title: '',
  description: '',
  category: '',
  link: '',
  icon: '',
  astyle: ''
})

// 重置表单
const resetForm = () => {
  payloadData.value = {
    id: null,
    title: '',
    description: '',
    category: '',
    link: '',
    icon: '',
    astyle: ''
  }
  fileList.value = []
  uploadMode.value = 'local'
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

// 保存/更新操作
const handleSave = async () => {
  // 表单验证
  if (!formRef.value) return;
  // 手动检查表单值是否存在
  if (!payloadData.value.title.trim()) {
    ElMessage.warning('请输入名称')
    return
  }
  if (!payloadData.value.category) {
    ElMessage.warning('请输入分类')
    return
  }
  if (!payloadData.value.link.trim()) {
    ElMessage.warning('请输入链接')
    return
  }

  try {
    // 执行表单验证
    await formRef.value.validate()
  } catch (error) {
    if (error && error.errors && error.errors.length) {
      ElMessage.warning(error.errors[0].message)
    } else {
      ElMessage.warning('请完善表单信息后再提交')
    }
    return
  }

  try {
    saveLoading.value = true

    // 准备提交的数据
    const submitData = {
      title: payloadData.value.title,
      description: payloadData.value.description,
      category: payloadData.value.category,
      link: payloadData.value.link,
      icon: payloadData.value.icon || fileList,
      astyle: payloadData.value.astyle
    }

    // 如果是编辑操作，添加id
    if (payloadData.value.id) {
      submitData.id = payloadData.value.id
    }
    let res
    if (payloadData.value.id) {
      // 编辑操作
      res = await request.post('/apps/update', submitData)
    } else {
      // 新建操作
      res = await request.post('/apps', submitData)
    }

    if (res.code === "200") {
      ElMessage.success(payloadData.value.id ? '更新成功' : '创建成功')
      dialogBuildVisible.value = false  // 关闭对话框
      await loadData() // 重新加载数据
    } else {
      ElMessage.error(res.msg || '操作失败')
    }
  } catch (error) {
    ElMessage.error('保存失败：' + error.message)
  } finally {
    saveLoading.value = false
  }
}

// 编辑操作
const handleEdit = (row) => {
  // 深拷贝数据
  payloadData.value = {
    id: row.id || null,
    title: row.title || '',
    description: row.description || '',
    category: row.category || '',
    link: row.link || '',
    icon: row.icon || '',
    astyle: row.astyle || ''
  }
  // 初始化文件列表
  if (payloadData.value.icon) {
    fileList.value = [{ url: payloadData.value.icon, name: 'cover' }];
  } else {
    fileList.value = [];
  }

  nextTick(() => {
    formRef.value?.clearValidate()
  })
  dialogBuildVisible.value = true
}

// 处理上传方式切换
const fileList = ref([])
const handleUploadModeChange = async (mode) => {
  try {
    // 清除图片相关验证
    if (formRef.value) {
      await formRef.value.clearValidate('icon');
    }

    // 切换模式时的清理工作
    if (mode === 'url') {
      fileList.value = []; // 切换到URL模式时清空上传列表
    } else {
      // 切换到本地模式时无需清空URL，保持数据连贯性
    }
  } catch (error) {
    console.error('切换上传方式错误:', error);
    ElMessage.error(`切换失败: ${error.message || '未知错误'}`);
  }
};

// 上传图片
const handleAvatarUpload = async (params) => {
  try {
    fileList.value = [];
    const formData = new FormData();
    formData.append('file', params.file);
    const response = await request.post('/file/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });

    if (response && response.startsWith('http')) {
      payloadData.value.icon = response;
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

// 初始化文件列表
const initFileList = computed(() => {
  return payloadData.value.icon
      ? [{ url: payloadData.value.icon, name: 'cover' }]
      : [];
});

const handleUploadExceed = () => {
  ElMessage.warning('最多只能上传1张封面图片')
}
// 上传前验证
const beforeUpload = (file) => {
  // 验证文件类型
  const isJpgOrPng = file.type === 'image/jpeg' || file.type === 'image/png' || file.type === 'svg' || file.type === 'icon' || file.type === 'webp'
  if (!isJpgOrPng) {

    ElMessage.error('只能上传jpg,png, svg, icon, webp图片')
    return false
  }

  // 验证文件大小
  const isLt10M = file.size / 1024 < 5000
  if (!isLt10M) {
    ElMessage.error('图片大小不能超过10M')
    return false
  }

  return true
}

// 上传成功处理
const handleUploadSuccess = () => {

};

// 上传失败处理
const handleUploadError = (err) => {
  ElMessage.error('图片上传失败: ' + (err.message || '未知错误'));
};

// 单个删除
const handleSingleDelete = async (row) => {
  try {
    deleteLoading.value = true
    const res = await request.delete(`/apps/${row.id}`)
    if (res.code === "200") {
      ElMessage.success('删除成功')
      await loadData() // 重新加载数据
    } else {
      ElMessage.error('删除失败：' + (res.msg || '未知错误'))
    }
  } catch (error) {
    ElMessage.error('删除失败：' + error.message)
  } finally {
    deleteLoading.value = false
  }
}

// 批量删除
const handleBatchDelete = async () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请先选择要删除的内容')
    return
  }

  try {
    deleteLoading.value = true
    const ids = selectedRows.value.map(row => row.id)
    const res = await request.post('/apps/del/batch', ids)
    if (res.code === "200") {
      ElMessage.success('批量删除成功')
      await loadData()
      selectedRows.value = []
      batchDeleteVisible.value = false
    } else {
      ElMessage.error('批量删除失败：' + (res.msg || '未知错误'))
    }
  } catch (error) {
    ElMessage.error('批量删除失败：' + error.message)
  } finally {
    deleteLoading.value = false
  }
}
</script>

<style scoped>
.apply-management-container {
  display: flex;
  flex-direction: column;
  height: 88vh;
  overflow-y: hidden;
}

.ml-2 {
  margin-left: 8px !important;
}

.mt-1 {
  margin-top: 5px !important;
}

.mb-2 {
  margin-bottom: 8px !important;
}

/* 按钮容器 */
.button-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0;
  flex-wrap: wrap;
  gap: 10px;
}

/* 让主体卡片自动伸缩，并成为内部Flex容器 */
.homeMain-box {
  margin: 0;

  display: flex;
  flex-direction: column;
  min-height: 0;
  overflow-y: auto;
}

.left-group, .right-group {
  display: flex;
  align-items: center;
  width: 100%;
}

.left-group {
  flex-wrap: wrap;
}

.right-group {
  justify-content: flex-end;
}

.color-tag {
  margin: 0;
}
/* 在大屏幕上调整左右分组宽度 */
@media (min-width: 768px) {
  .left-group, .right-group {
    width: auto;
  }

  .right-group {
    flex-direction: row;
  }
}

/* 在小屏幕上调整布局 */
@media (max-width: 767px) {
  .button-container {
    flex-direction: column;
    align-items: stretch;
  }

  .right-group {
    flex-direction: column;
    width: 100%;
    margin-top: 10px;
    gap: 5px;
  }

  .left-group {
    margin-bottom: 0;
  }

  /* 小屏幕按钮样式优化 */
  :deep(.el-button) {
    padding: 5px 8px;
    font-size: 12px;
  }

  :deep(.el-button .el-icon) {
    font-size: 14px;
  }
}

/* 分页样式 */
.pagination-block {
  padding: 10px 0;
}

.center-content {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 0;
}

.table-scroll-container {
  flex-grow: 1;
  overflow-y: auto;
  overflow-x: hidden;
  cursor: pointer;
}

.table-scroll-container::-webkit-scrollbar {
  width: 6px;
}

.table-scroll-container::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.table-scroll-container::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.table-scroll-container::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

/* 图片错误提示 */
.image-error {
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f5f5;
  color: #f56c6c;
  font-size: 12px;
}

/* 链接样式 */
.link-external {
  color: #409eff;
  text-decoration: none;
  transition: color 0.2s;
}

.link-external:hover {
  color: #66b1ff;
  text-decoration: underline;
}

/* 文本样式 */
.text-danger {
  color: #f56c6c;
}

.font-bold {
  font-weight: bold;
}

.text-warning {
  color: #e6a23c;
}

/* 移动端适配 */
@media (max-width: 768px) {

  /* 调整表格行高 */
  :deep(.el-table__row) {
    height: 60px !important;
  }

  /* 调整分页控件 */
  :deep(.el-pagination .el-pagination__sizes) {
    margin-right: 5px;
  }

  :deep(.el-select .el-input) {
    width: 80px;
  }
}

@media (max-width: 576px) {
  /* 简化分页显示 */
  :deep(.el-pagination__total) {
    display: none;
  }
}

/* 6. 确保头部和底部卡片不会被压缩 */
.el-card:first-child, .el-card:last-child {
  flex-shrink: 0; /* 关键：防止被压缩 */
}
</style>

