<template>
  <div class="management-container">
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
              @clear="handleSearch"
              :size="isSmallScreen ? 'small' : 'default'"
          />
          <el-button
              type="info"
              :class="isSmallScreen ? 'mt-2 w-full' : 'ml-2'"
              @click="handleSearch"
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
              :size="isSmallScreen ? 'small' : 'default'"
              :icon="Delete"
          >
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
            row-key="id"
            :cell-style="{ padding: isSmallScreen ? '4px 8px' : '8px 12px' }"
            :header-cell-style="{ padding: isSmallScreen ? '6px 8px' : '10px 12px' }"
        >
          <el-table-column type="selection" width="35" />
          <el-table-column
              prop="title"
              label="标题"
              :width="isSmallScreen ? 120 : 200"
              :show-overflow-tooltip="true"
          />
          <el-table-column
              label="封面"
              :width="isSmallScreen ? 100 : 200"
          >
            <template #default="scope">
              <el-image
                  v-if="scope.row.picture"
                  :src="scope.row.picture"
                  alt="笔记封面"
                  :style="{
                  width: isSmallScreen ? '60px' : '100px',
                  height: isSmallScreen ? '50px' : '80px',
                  objectFit: 'cover'
                }"
                  fit="cover"
                  preview-teleport="body"
                  :preview-src-list="[scope.row.picture]"
              />
              <span v-else class="text-gray-400">无封面</span>
            </template>
          </el-table-column>
          <el-table-column
              prop="publishTime"
              label="发布时间"
              :width="isSmallScreen ? 130 : 180"
              :formatter="formatDateTime"
          />
          <el-table-column
              prop="author"
              label="作者"
              :width="isSmallScreen ? 80 : 100"
              :show-overflow-tooltip="true"
          />
          <el-table-column
              label="标签"
              :width="isSmallScreen ? 120 : ''"
          >
            <template #default="scope">
              <el-tag
                  v-for="(tag, idx) in (scope.row.badge ? scope.row.badge.split(',') : [])"
                  :key="idx"
                  class="mr-1"
                  :max-width="isSmallScreen ? '60px' : '100px'"
                  :class="{ 'hidden-xs': isXsScreen && idx > 0 }"
              >
                {{ tag }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column
              prop="pageview"
              label="浏览量"
              :width="isSmallScreen ? 80 : ''"
          />
          <el-table-column
              label="操作"
              :width="isSmallScreen ? 140 : 150"
          >
            <template #default="scope">
              <el-button-group>
                <el-button
                    type="success"
                    :icon="Edit"
                    @click="handleEdit(scope.row)"
                    size="small"
                    title="编辑"
                >
                </el-button>
                <el-popconfirm
                    title="是否确认删除？"
                    placement="top"
                    @confirm="handleSingleDelete(scope.row)"
                    confirm-button-text="确认"
                    cancel-button-text="取消"
                    icon-color="#ff4d4f"
                >
                  <template #reference>
                    <el-button
                        type="danger"
                        :icon="Delete"
                        size="small"
                        title="删除"
                    >
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
        :title="payloadData.id ? '编辑笔记' : '新建笔记'"
        :width="isSmallScreen ? '90%' : '800px'"
        :before-close="resetForm"
        max-height="80vh"
        overflow-y="auto"
        :close-on-click-modal="false"
        :destroy-on-close="true"
    >
      <el-form
          :model="payloadData"
          :label-width="isSmallScreen ? '80px' : '120px'"
          ref="formRef"
          :rules="formRules"
          status-icon
      >
        <el-form-item label="标题" prop="title">
          <el-input
              v-model="payloadData.title"
              autocomplete="off"
              style="width: 100%"
              max-length="50"
              show-word-limit
              placeholder="请输入笔记标题"
              :size="isSmallScreen ? 'small' : 'default'"
          />
        </el-form-item>

        <el-form-item label="封面" prop="picture">
          <!-- 上传方式选择 -->
          <el-radio-group
              v-model="uploadMode"
              class="mb-3"
              @change="handleUploadModeChange"
              :size="isSmallScreen ? 'small' : 'default'"
          >
            <el-radio label="url" :class="isSmallScreen ? 'mr-4' : 'mr-6'">输入图片链接</el-radio>
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
                :size="isSmallScreen ? 'small' : 'default'"
            />
            <el-text size="small" type="info" class="mt-1">
              提示：请输入有效的图片URL地址（http://或https://开头）
            </el-text>

            <!-- 图片预览 -->
            <div v-if="payloadData.picture" class="mt-2">
              <el-image
                  :src="payloadData.picture"
                  alt="封面预览"
                  :style="{
                  width: isSmallScreen ? '120px' : '150px',
                  height: isSmallScreen ? '80px' : '100px',
                  objectFit: cover
                }"
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
                v-model:file-list="payloadData.pictureList"
                action="#"
                list-type="picture"
                :on-success="handleUploadSuccess"
                :limit="1"
                :on-exceed="handleUploadExceed"
                :http-request="handleAvatarUpload"
                :on-remove="handleFileRemove"
                :file-list="initFileList"
            >
              <el-button
                  type="primary"
                  :style="{ width: isSmallScreen ? '100%' : '200px' }"
                  :size="isSmallScreen ? 'small' : 'default'"
              >
                上传封面
              </el-button>
              <template #tip>
                <div class="el-upload__tip text-gray-500">
                  支持jpg/png格式，文件大小不超过500kb
                </div>
              </template>
            </el-upload>
          </el-form-item>
        </el-form-item>

        <el-form-item label="标签">
          <el-input
              v-model="tagInput"
              placeholder="请输入标签（最多3个）"
              @keyup.enter.native="addTag"
              :style="{ width: isSmallScreen ? '100%' : '200px', marginRight: isSmallScreen ? '0' : '10px' }"
              max-length="10"
              show-word-limit
              :size="isSmallScreen ? 'small' : 'default'"
          >
            <template #append>
              <el-button @click="addTag" :size="isSmallScreen ? 'small' : 'default'">添加</el-button>
            </template>
          </el-input>
          <div class="tag-container" style="margin-top: 10px;">
            <el-tag
                v-for="(tag, index) in payloadData.badgeList"
                :key="index"
                type="warning"
                closable
                @close="removeTag(index)"
                class="ml-2 mt-1"
                :max-width="isSmallScreen ? '80px' : '100px'"
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
            {{ payloadData.id ? '更新' : '保存' }}
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
      <p>您确定要删除选中的 <span class="text-danger font-bold">{{ selectedCount }}</span> 条笔记吗？</p>
      <p class="text-warning mt-2">此操作不可撤销，请谨慎操作！</p>
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
            :loading="batchDeleteLoading"
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
import {ref, computed, onMounted, nextTick, reactive, watchEffect} from 'vue'
import {ElMessage} from 'element-plus'
import MarkdownIt from 'markdown-it'
import hljs from 'highlight.js'
import 'highlight.js/styles/github.css'
import request from '@/utils/request';
import {debounce} from 'lodash';
import EditorMD from "@/components/subComponents/EditorMD.vue";
import {defaultWebName} from "@/utils/defaultConfig";

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

// 搜索相关
const inputSearch = ref('')
const searchLoading = ref(false)

// 表格数据 - 与Note实体字段对应
const tableData = ref([])

// 分页信息
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

// 上传模式管理
const uploadMode = ref('local') // 默认本地上传

// 表单验证规则 - 确保与后端实体字段校验匹配
const formRules = reactive({
  title: [
    {required: true, message: '请输入笔记标题', trigger: ['blur', 'change']},
    {min: 1, max: 50, message: '标题长度需在1-50个字符之间', trigger: 'blur'}
  ],
  information: [
    {required: true, message: '请输入笔记内容', trigger: ['blur', 'change']},
    {min: 10, max: 2000, message: '内容长度需在10-2000个字符之间', trigger: 'blur'}
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
})

// 加载数据 - 接口与实体类匹配
const loadData = async () => {
  try {
    searchLoading.value = true
    const params = {
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      ...(inputSearch.value.trim() && {title: inputSearch.value.trim()})
    }

    const res = await request.get('/note/page', {params})
    if (res.code === "200") {
      tableData.value = res.data.records || []
      pagination.total = res.data.total || 0
    } else {
      ElMessage.error(`获取数据失败：${res.msg || '未知错误'}`)
    }
  } catch (error) {
    ElMessage.error(`请求错误：${error.message || '网络异常'}`)
  } finally {
    searchLoading.value = false
  }
}

// 处理上传方式切换
const handleUploadModeChange = async (mode) => {
  try {
    // 清除图片相关验证
    if (formRef.value) {
      await formRef.value.clearValidate('picture');
    }

    // 切换模式时的清理工作
    if (mode === 'url') {
      payloadData.value.pictureList = []; // 切换到URL模式时清空上传列表
    } else {
      // 切换到本地模式时无需清空URL，保持数据连贯性
    }
  } catch (error) {
    ElMessage.error(`切换失败: ${error.message || '未知错误'}`);
  }
};

// 图片上传处理 - 确保picture字段正确赋值
const handleAvatarUpload = async (params) => {
  // 文件大小校验
  if (params.file.size > 500 * 1024) {
    ElMessage.error('文件大小不能超过500kb')
    params.onError(new Error('文件过大'))
    return
  }

  // 文件类型校验
  const allowedTypes = ['image/jpeg', 'image/png']
  if (!allowedTypes.includes(params.file.type)) {
    ElMessage.error('仅支持jpg/png格式的图片')
    params.onError(new Error('文件类型错误'))
    return
  }

  try {
    const formData = new FormData()
    formData.append('file', params.file)

    const response = await request.post('/file/upload', formData, {
      headers: {'Content-Type': 'multipart/form-data'},
      responseType: 'text'
    })

    if (response && response.startsWith('http')) {
      payloadData.value.picture = response  // 直接赋值给实体字段
      ElMessage.success('图片上传成功')
      params.onSuccess(response)
    } else {
      throw new Error('上传失败，服务器返回无效URL')
    }
  } catch (error) {
    ElMessage.error(`图片上传失败：${error.message}`)
    params.onError(error)
  }
}

const handleFileRemove = () => {
  payloadData.value.picture = ''
  payloadData.value.pictureList = []
}

// 上传超出限制回调
const handleUploadExceed = () => {
  ElMessage.warning('最多只能上传1张封面图片')
}

// 上传成功回调
const handleUploadSuccess = () => {
}

// 搜索处理（防抖）
const handleSearch = debounce(() => {
  pagination.pageNum = 1
  loadData()
}, 300)

// 分页事件处理
const handleSizeChange = (pageSize) => {
  pagination.pageSize = pageSize
  loadData()
}
const handleCurrentChange = (pageNum) => {
  pagination.pageNum = pageNum
  loadData()
}

// 初始化加载数据
onMounted(() => {
  loadData()
})

// 选中行管理
const selectedRows = ref([])
const selectedCount = computed(() => selectedRows.value.length)
const handleSelectionChange = (rows) => {
  selectedRows.value = rows
}

// 对话框相关状态
const dialogBuildVisible = ref(false)
const batchDeleteVisible = ref(false)
const formRef = ref(null)
const saveLoading = ref(false)
const batchDeleteLoading = ref(false)

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
const tagInput = ref('')
const activeTab = ref('edit')

// 初始化文件列表（用于编辑时回显）
const initFileList = computed(() => {
  return payloadData.value.picture
      ? [{url: payloadData.value.picture, name: 'cover'}]
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
    author: 'POTATO',
    pageview: 0
  }
  tagInput.value = ''
  activeTab.value = 'edit'
  uploadMode.value = 'local' // 重置为默认上传方式
  dialogBuildVisible.value = false
  nextTick(() => {
    formRef.value?.resetFields()
  })
}

// 新建笔记
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
  tagInput.value = ''
}
const removeTag = (index) => {
  payloadData.value.badgeList.splice(index, 1)
}

// 保存/更新操作
const handleSave = async () => {
  try {
    // 表单验证
    await formRef.value.validate();

    saveLoading.value = true

    // 处理标签转换
    payloadData.value.badge = payloadData.value.badgeList.join(',')
    payloadData.value.author = defaultWebName
    let res
    if (payloadData.value.id) {
      // 编辑操作
      res = await request.post('/note/update', payloadData.value)
    } else {
      // 新建操作
      res = await request.post('/note', payloadData.value)
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
      ElMessage.error(`保存失败：${error.message}`)
    }
  } finally {
    saveLoading.value = false
  }
}

// 编辑操作 - 正确回显实体数据
const handleEdit = (row) => {
  // 深拷贝避免直接修改表格数据
  const rowCopy = JSON.parse(JSON.stringify(row))

  payloadData.value = {
    id: rowCopy.id,
    title: rowCopy.title,
    picture: rowCopy.picture || '',
    pictureList: rowCopy.picture ? [{url: rowCopy.picture}] : [],
    information: rowCopy.information || '',
    badge: rowCopy.badge || '',
    badgeList: rowCopy.badge ? rowCopy.badge.split(',') : [],
    author: rowCopy.author || 'POTATO',
    pageview: rowCopy.pageview || 0
  }

  // 根据图片地址自动选择合适的上传模式
  if (rowCopy.picture) {
    uploadMode.value = rowCopy.picture.startsWith('http') ? 'url' : 'local'
  } else {
    uploadMode.value = 'local'
  }

  dialogBuildVisible.value = true
}

// 单个删除
const handleSingleDelete = async (row) => {
  try {
    searchLoading.value = true
    const res = await request.delete(`/note/${row.id}`)
    if (res.code === "200") {
      ElMessage.success('删除成功')
      tableData.value = tableData.value.filter(item => item.id !== row.id)
      pagination.total--
    } else {
      ElMessage.error(`删除失败：${res.msg || '未知错误'}`)
    }
  } catch (error) {
    ElMessage.error(`删除失败：${error.message}`)
  } finally {
    searchLoading.value = false
  }
}

// 批量删除
const handleBatchDelete = async () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请先选择要删除的笔记')
    return
  }

  try {
    batchDeleteLoading.value = true
    const ids = selectedRows.value.map(row => row.id)
    const res = await request.post('/note/del/batch', ids)

    if (res.code === "200") {
      ElMessage.success('批量删除成功')
      tableData.value = tableData.value.filter(
          item => !selectedRows.value.some(row => row.id === item.id)
      )
      pagination.total -= selectedRows.value.length
      selectedRows.value = []
      batchDeleteVisible.value = false
    } else {
      ElMessage.error(`批量删除失败：${res.msg || '未知错误'}`)
    }
  } catch (error) {
    ElMessage.error(`批量删除失败：${error.message}`)
  } finally {
    batchDeleteLoading.value = false
  }
}

// 时间格式化 - 适配LocalDateTime显示
const formatDateTime = (row, column, cellValue) => {
  if (!cellValue) return '-'
  // 处理LocalDateTime格式（如：2023-10-01T12:30:45）
  if (cellValue.includes('T')) {
    return cellValue.replace('T', ' ')
  }
  return cellValue
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

.mb-2 {
  margin-bottom: 8px !important;
}

.font-bold {
  font-weight: bold;
}

.text-gray-400 {
  color: #8c8c8c;
}

.text-gray-500 {
  color: #666;
}

.text-danger {
  color: #ff4d4f;
}

.text-warning {
  color: #faad14;
}

/* 按钮容器 */
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
  width: 100%;
}

.left-group {
  flex-wrap: wrap;
}

.right-group {
  justify-content: flex-end;
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

.pagination-block {
  padding: 10px 0;
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

/* Markdown预览样式 */
.markdown-preview {
  padding: 16px;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  min-height: 500px; /* 与编辑器高度保持一致 */
  line-height: 1.6;
  font-family: 'Helvetica Neue', Arial, sans-serif;
  text-align: left; /* 确保内容左对齐 */
}

.markdown-preview h1,
.markdown-preview h2,
.markdown-preview h3 {
  margin: 1em 0 0.5em;
  font-weight: 600;
  border-bottom: 1px solid #eee;
  padding-bottom: 0.3em;
}

.markdown-preview ul, .markdown-preview ol {
  padding-left: 2em;
  margin: 1em 0;
}

.markdown-preview pre {
  padding: 1em;
  margin: 1em 0;
  background: #f5f5f5;
  border-radius: 4px;
  overflow-x: auto;
}

.markdown-preview code {
  font-family: Consolas, Monaco, monospace;
  font-size: 0.9em;
}

.markdown-preview blockquote {
  margin: 1em 0;
  padding-left: 1em;
  border-left: 4px solid #ddd;
  color: #666;
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
}

/* 移动端适配 */
@media (max-width: 768px) {
  .table-scroll-container {
    max-height: calc(100vh - 250px);
  }

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
  .table-scroll-container {
    max-height: calc(100vh - 220px);
  }

  /* 简化分页显示 */
  :deep(.el-pagination__total) {
    display: none;
  }
}
</style>
