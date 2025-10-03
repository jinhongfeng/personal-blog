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
          />
          <el-button
              type="info"
              :class="isSmallScreen ? 'mt-2 w-full' : 'ml-2'"
              @click="handleSearch"
              :size="isSmallScreen ? 'small' : 'default'"
          >
            <el-icon class="mr-1"><Search /></el-icon>
            <span>搜索</span>
          </el-button>
        </div>

        <!-- 右侧区域：新建和批量删除按钮 -->
        <div class="right-group">
          <el-button
              type="primary"
              @click="handleCreate"
              :size="isSmallScreen ? 'small' : 'default'"
          >
            <el-icon class="mr-1"><CirclePlus /></el-icon>
            <span>新建</span>
          </el-button>
          <el-button
              type="warning"
              :disabled="selectedRows.length === 0"
              @click="batchDeleteVisible = true"
              :class="isSmallScreen ? 'mt-2 ml-0' : 'ml-2'"
              :size="isSmallScreen ? 'small' : 'default'"
          >
            <el-icon><Delete /></el-icon>
            <span >批量删除</span>
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
            :cell-style="{ padding: isSmallScreen ? '4px 8px' : '8px 12px' }"
            :header-cell-style="{ padding: isSmallScreen ? '6px 8px' : '10px 12px' }"
        >
          <el-table-column type="selection" width="30" />
          <el-table-column
              prop="title"
              label="标题"
              :width="isSmallScreen ? 120 : 180"
          />
          <el-table-column
              label="封面"
              :width="isSmallScreen ? 120 : 180"
          >
            <template #default="scope">
              <img
                  v-if="scope.row.picture"
                  :src="scope.row.picture"
                  alt="封面"
                  :style="{
                  width: isSmallScreen ? '80px' : '100px',
                  height: isSmallScreen ? '60px' : '80px',
                  objectFit: 'cover'
                }"
              />
              <span v-else>无封面</span>
            </template>
          </el-table-column>
          <el-table-column
              prop="publishTime"
              label="发布时间"
              :width="isSmallScreen ? 120 : 150"
          />
          <el-table-column
              prop="author"
              label="作者"
              :width="isSmallScreen ? 80 : 120"
          />
          <el-table-column
              label="标签"
              :show-overflow-tooltip="true"
          >
            <template #default="scope">
              <el-tag
                  v-for="(tag, idx) in scope.row.badge ? scope.row.badge.split(',') : []"
                  :key="idx"
                  class="mr-1"
                  :size="isSmallScreen ? 'small' : 'default'"
              >
                {{ tag }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column
              prop="pageview"
              label="浏览量"
              :width="isSmallScreen ? 100 : 100"
          />
          <el-table-column
              label="操作"
              :width="isSmallScreen ? 'auto' : 'auto'"
          >
            <template #default="scope">
              <el-button-group>
                <el-button
                    type="success"
                    :icon="Edit"
                    @click="handleEdit(scope.row)"
                    size="small"
                >
                </el-button>
                <el-popconfirm
                    title="是否确认删除？"
                    placement="top"
                    @confirm="handleSingleDelete(scope.row)"
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
      >
        <el-form-item label="标题" prop="title">
          <el-input
              v-model="payloadData.title"
              autocomplete="off"
              style="width: 100%"
              max-length="50"
              show-word-limit
              :disabled="saveLoading"
              :size="isSmallScreen ? 'small' : 'default'"
          />
        </el-form-item>

        <el-form-item label="封面" prop="picture">
          <!-- 切换方式选择 -->
          <el-radio-group
              v-model="coverUploadMode"
              class="mb-3"
              :disabled="saveLoading"
              @change="handleCoverModeChange"
          >
            <el-radio
                label="url"
                :size="isSmallScreen ? 'small' : 'default'"
            >
              输入图片链接
            </el-radio>
            <el-radio
                label="upload"
                :size="isSmallScreen ? 'small' : 'default'"
            >
              上传图片文件
            </el-radio>
          </el-radio-group>

          <!-- 链接输入方式 -->
          <el-form-item
              v-if="coverUploadMode === 'url'"
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
                :size="isSmallScreen ? 'small' : 'default'"
            />
            <el-text size="small" type="info" class="mt-1">
              提示：请输入有效的图片URL地址（http://或https://开头）
            </el-text>

            <!-- 预览区域 -->
            <div v-if="payloadData.picture" class="mt-2">
              <el-image
                  :src="payloadData.picture"
                  alt="封面预览"
                  :style="{
                  width: isSmallScreen ? '120px' : '150px',
                  height: isSmallScreen ? '80px' : '100px',
                  objectFit: 'cover'
                }"
                  fit="cover"
                  :preview-src-list="[payloadData.picture]"
                  preview-teleport="body"
              >
                <template #error>
                  <div class="image-error" :style="{
                  width: isSmallScreen ? '120px' : '150px',
                  height: isSmallScreen ? '80px' : '100px'
                }">
                    图片无法显示
                  </div>
                </template>
                <template #placeholder>
                  <div class="image-placeholder" :style="{
                  width: isSmallScreen ? '120px' : '150px',
                  height: isSmallScreen ? '80px' : '100px'
                }">
                    加载中...
                  </div>
                </template>
              </el-image>
            </div>
          </el-form-item>

          <!-- 上传文件方式 -->
          <el-form-item
              v-if="coverUploadMode === 'upload'"
              class="mb-0"
              key="file-upload"
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
              <el-button
                  type="primary"
                  :style="{ width: isSmallScreen ? '150px' : '200px' }"
                  :size="isSmallScreen ? 'small' : 'default'"
              >
                上传封面
              </el-button>
              <template #tip>
                <div class="el-upload__tip">
                  支持jpg/png/webp格式，文件大小不超过500kb
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
              :disabled="saveLoading"
              :size="isSmallScreen ? 'small' : 'default'"
          >
            <template #append>
              <el-button
                  @click="addTag"
                  :disabled="saveLoading"
                  :size="isSmallScreen ? 'small' : 'default'"
              >
                添加
              </el-button>
            </template>
          </el-input>
          <div class="tag-container" style="margin-top: 10px;">
            <el-tag
                v-for="(tag, index) in payloadData.badgeList"
                :key="index"
                type="warning"
                closable
                @close="removeTag(index)"
                :class="isSmallScreen ? 'mr-1 mt-1' : 'ml-2'"
                :disabled="saveLoading"
                :size="isSmallScreen ? 'small' : 'default'"
            >
              {{ tag }}
            </el-tag>
          </div>
        </el-form-item>

        <el-form-item label="内容" prop="information">
          <div class="markdown-container">
            <MarkdownEditor
                v-model="payloadData.information"
                :disabled="saveLoading"
                :height="isSmallScreen ? '300px' : '500px'"
                :autofocus="false"
                @change="handleContentChange"
            />
          </div>
        </el-form-item>
      </el-form>

      <template #footer>
        <div class="center-content">
          <el-button
              type="danger"
              @click="resetForm"
              :disabled="saveLoading"
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
import { CirclePlus, Delete, Edit, Search } from '@element-plus/icons-vue'
import { ref, computed, onMounted, nextTick, watchEffect } from 'vue'
import { ElMessage } from 'element-plus'
import MarkdownIt from 'markdown-it'
import hljs from 'highlight.js'
import 'highlight.js/styles/github.css'
import request from '@/utils/request';
import { debounce } from 'lodash';
import MarkdownEditor from "@/view/back/subComponent/MarkdownEditor.vue";

// 响应式相关
const isSmallScreen = ref(window.innerWidth < 768)

// 监听窗口大小变化
watchEffect(() => {
  const handleResize = () => {
    isSmallScreen.value = window.innerWidth < 768
  }

  window.addEventListener('resize', handleResize)
  return () => window.removeEventListener('resize', handleResize)
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

    const res = await request.get('/diary/page', { params })

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

const saveLoading = ref(false)    // 保存/编辑加载状态

// Markdown编辑器内容变化处理
const handleContentChange = (value) => {
  payloadData.value.information = value;
}

// 封面上传模式管理
const coverUploadMode = ref('url'); // 默认使用URL方式
const fileList = ref([]); // 上传文件列表

// 处理封面模式切换
const handleCoverModeChange = async (mode) => {
  try {
    // 清除验证状态
    if (formRef.value) {
      await formRef.value.clearValidate('picture');
    }

    // 处理数据切换
    if (mode === 'url') {
      // 切换到URL模式，清空上传列表
      fileList.value = [];
    } else {
      // 切换到上传模式，保留已有的URL
      // 无需额外操作，fileList会在上传成功后自动更新
    }
  } catch (error) {
    console.error('切换上传方式错误:', error);
    ElMessage.error(`切换失败: ${error.message || JSON.stringify(error)}`);
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
      payloadData.value.picture = response; // 上传成功后更新picture字段
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

// 文件上传前验证
const beforeUpload = (file) => {
  // 验证文件类型
  const isJpgOrPng = file.type === 'image/jpeg' || file.type === 'image/png' || file.type === 'image/webp'
  if (!isJpgOrPng) {
    ElMessage.error('只能上传JPG/PNG/WEBP格式的图片')
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

// 表单数据
const payloadData = ref({
  id: null,  // 用于编辑时的唯一标识
  title: '',  // 标题
  picture: '',  // 封面图片URL（后端字段）
  information: '',  // 内容（后端字段）
  badge: '',  // 标签（后端存储的字符串，如"标签1,标签2"）
  badgeList: [],  // 标签数组（前端临时使用）
  author: '',  // 作者
  pageview: 0  // 浏览量
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
        if (coverUploadMode.value === 'url' && value) {
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
    information: '',
    badge: '',
    badgeList: [],
    author: '',
    pageview: 0
  }
  tagInput.value = ''
  fileList.value = []
  coverUploadMode.value = 'url'
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

// Markdown解析
const md = new MarkdownIt({
  html: true,
  linkify: true,
  typographer: true,
  highlight: (str, lang) => {
    if (lang && hljs.getLanguage(lang)) {
      try {
        return hljs.highlight(str, {language: lang}).value
      } catch (e) {
      }
    }
    return ''  // 不高亮未识别的语言
  }
})

// 保存/更新操作
const handleSave = async () => {
  try {
    // 表单验证
    await formRef.value.validate();

    saveLoading.value = true
    // 处理标签：将数组转为字符串
    payloadData.value.badge = payloadData.value.badgeList.join(',')
    payloadData.value.author = "POTATO"
    let res
    if (payloadData.value.id) {
      // 编辑操作：调用更新接口
      res = await request.post('/diary/update', payloadData.value)
    } else {
      // 新建操作：调用保存接口
      res = await request.post('/diary', payloadData.value)
    }

    if (res.code === "200") {
      ElMessage.success(payloadData.value.id ? '更新成功' : '创建成功')
      dialogBuildVisible.value = false  // 关闭对话框
      await loadData()
    } else {
      ElMessage.error(res.msg || '操作失败')
    }
  } catch (error) {
    // 表单验证失败不显示错误提示
    if (!(error instanceof Error && error.name === 'ValidationError')) {
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
    information: row.information || '',
    badge: row.badge || '',
    badgeList: row.badge ? row.badge.split(',') : [],  // 标签字符串转数组
    author: row.author || '',
    pageview: row.pageview || 0
  }

  // 初始化文件列表
  if (row.picture) {
    fileList.value = [{url: row.picture, name: 'cover'}]
    // 如果有图片，根据是否是URL自动切换模式
    coverUploadMode.value = row.picture.startsWith('http') ? 'url' : 'upload'
  } else {
    fileList.value = []
    coverUploadMode.value = 'url'
  }

  dialogBuildVisible.value = true
}

// 单个删除
const handleSingleDelete = async (row) => {
  try {
    const res = await request.delete(`/diary/${row.id}`)
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
    const res = await request.post('/diary/del/batch', ids)  // 匹配后端批量删除接口
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


.mt-2 {
  margin-top: 10px !important;
}

.mt-1 {
  margin-top: 5px !important;
}

.ml-2 {
  margin-left: 8px !important;
}

.mr-1 {
  margin-right: 4px !important;
}

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

.right-group {
  justify-content: flex-end;
}

/* 在大屏幕上调整左右分组宽度 */
@media (min-width: 768px) {
  .left-group, .right-group {
    width: auto;
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

/* Markdown预览样式优化 */
.markdown-preview {
  padding: 16px;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  min-height: 300px;
  line-height: 1.6;
  font-family: 'Helvetica Neue', Arial, sans-serif;
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

.table-scroll-container {
  max-height: calc(100vh - 300px);
  overflow-y: auto;
  overflow-x: hidden;
  cursor: pointer;
}

.table-scroll-container::-webkit-scrollbar {
  width: 6px; /* 滚动条宽度 */
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

/* 文本样式增强 */
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

  /* 小屏幕按钮文字调整 */
  :deep(.el-button) {
    padding: 4px 6px;
    font-size: 12px;
  }

  :deep(.el-button .el-icon) {
    font-size: 14px;
    margin-right: 2px;
  }
}
</style>
