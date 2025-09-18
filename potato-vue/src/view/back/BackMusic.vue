<template>
  <!-- 头部 -->
  <el-card :style="{ height: isSmallScreen ? 'auto' : '12%', padding: isSmallScreen ? '10px' : '0' }">
    <div class="button-container">
      <!-- 左侧区域：搜索框和搜索按钮 -->
      <div class="left-group">
        <el-input
            v-model="inputSearch"
            :style="{ width: isSmallScreen ? '100%' : '240px' }"
            placeholder="输入歌名搜索"
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
          <span>新建</span>
        </el-button>
        <el-button
            type="warning"
            :disabled="selectedRows.length === 0"
            @click="batchDeleteVisible = true"
            :class="isSmallScreen ? 'w-full' : 'ml-2'"
            :loading="deleteLoading"
            :size="isSmallScreen ? 'small' : 'default'"
        >
          批量删除
        </el-button>
      </div>
    </div>
  </el-card>

  <!-- 主要内容 -->
  <el-card class="homeMain-box mt-2" :style="{ padding: isSmallScreen ? '10px 5px' : '15px' }">
    <div class="table-scroll-container">
      <el-table
          :data="tableData"
          stripe
          style="width: 100%"
          v-loading="searchLoading"
          @selection-change="handleSelectionChange"
          empty-text="暂无数据"
          :row-key="row => row.id"
          :cell-style="{ padding: isSmallScreen ? '4px 8px' : '8px 12px' }"
          :header-cell-style="{ padding: isSmallScreen ? '6px 8px' : '10px 12px' }"
      >
        <el-table-column type="selection" width="35" />
        <el-table-column prop="songName" label="歌名" :width="isSmallScreen ? 100 : 150" :show-overflow-tooltip="true" />
        <el-table-column prop="singer" label="歌手" :width="isSmallScreen ? 80 : 120" :show-overflow-tooltip="true" />
        <el-table-column prop="album" label="专辑" :width="isSmallScreen ? 100 : 150" :show-overflow-tooltip="true" />
        <el-table-column prop="url" label="链接" :width="isSmallScreen ? 120 : 200">
          <template #default="scope">
            <a
                :href="scope.row.url"
                target="_blank"
                class="link-external"
                v-if="scope.row.url"
            >
              {{ scope.row.url.length > (isSmallScreen ? 10 : 20) ? scope.row.url.slice(0, isSmallScreen ? 10 : 20) + '...' : scope.row.url }}
            </a>
            <span v-else>无链接</span>
          </template>
        </el-table-column>
        <el-table-column label="封面" :width="isSmallScreen ? 100 : 150">
          <template #default="scope">
            <el-image
                v-if="scope.row.cover"
                :src="scope.row.cover"
                alt="封面"
                :style="{
                  width: isSmallScreen ? '60px' : '100px',
                  height: isSmallScreen ? '50px' : '80px',
                  objectFit: 'cover'
                }"
                fit="cover"
                :preview-src-list="[scope.row.cover]"
                preview-teleport="body"
            >
              <template #error>
                <div class="image-error">加载失败</div>
              </template>
            </el-image>
            <span v-else>无封面</span>
          </template>
        </el-table-column>
        <el-table-column prop="lyric" label="歌词" :width="isSmallScreen ? 100 : ''">
          <template #default="scope">
            <el-popover
                placement="top"
                :width="isSmallScreen ? 200 : 300"
                trigger="hover"
            >
              <template #content>
                <pre style="white-space: pre-wrap; word-wrap: break-word; max-height: 200px; overflow-y: auto;">{{ scope.row.lyric || '无歌词' }}</pre>
              </template>
              <template #reference>
                <span>{{ scope.row.lyric ? (scope.row.lyric.length > 8 ? scope.row.lyric.slice(0, 8) + '...' : scope.row.lyric) : '无歌词' }}</span>
              </template>
            </el-popover>
          </template>
        </el-table-column>
        <el-table-column label="操作" :width="isSmallScreen ? 140 : 150">
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
      class="center-content mt-2"
      :style="{
        height: isSmallScreen ? 'auto' : '12%',
        padding: isSmallScreen ? '10px' : '0'
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
      :title="payloadData.id ? '编辑音乐' : '新建音乐'"
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
      <el-form-item label="歌名" prop="songName">
        <el-input
            v-model="payloadData.songName"
            autocomplete="off"
            style="width: 100%"
            max-length="50"
            show-word-limit
            placeholder="请输入歌名"
            :size="isSmallScreen ? 'small' : 'default'"
        />
      </el-form-item>

      <el-form-item label="歌手" prop="singer">
        <el-input
            v-model="payloadData.singer"
            autocomplete="off"
            style="width: 100%"
            max-length="50"
            show-word-limit
            placeholder="请输入歌手名"
            :size="isSmallScreen ? 'small' : 'default'"
        />
      </el-form-item>

      <el-form-item label="专辑" prop="album">
        <el-input
            v-model="payloadData.album"
            autocomplete="off"
            style="width: 100%"
            max-length="50"
            show-word-limit
            placeholder="请输入专辑名"
            :size="isSmallScreen ? 'small' : 'default'"
        />
      </el-form-item>

      <el-form-item label="歌曲链接" prop="url">
        <el-input
            v-model="payloadData.url"
            autocomplete="off"
            style="width: 100%"
            max-length="200"
            show-word-limit
            placeholder="请输入音乐链接，.mp3类型"
            :size="isSmallScreen ? 'small' : 'default'"
        />
        <el-text size="small" type="info" class="mt-1">
          提示：请输入有效的音乐播放URL地址
        </el-text>
      </el-form-item>

      <el-form-item label="歌词">
        <!-- 切换方式选择 -->
        <el-radio-group
            v-model="lyricUploadMode"
            class="mb-3"
            :disabled="saveLoading"
            @change="handleLyricModeChange"
            :size="isSmallScreen ? 'small' : 'default'"
        >
          <el-radio label="url" :class="isSmallScreen ? 'mr-4' : 'mr-6'">输入歌词链接</el-radio>
          <el-radio label="upload">上传歌词文件</el-radio>
        </el-radio-group>

        <!-- 链接输入方式 -->
        <el-form-item
            prop="lyric"
            v-if="lyricUploadMode === 'url'"
            class="mb-0"
            key="lyric-url-upload"
        >
          <el-input
              v-model="payloadData.lyric"
              autocomplete="off"
              style="width: 100%"
              max-length="200"
              show-word-limit
              placeholder="请输入有效的歌词链接，.lrc类型"
              :disabled="saveLoading"
              :size="isSmallScreen ? 'small' : 'default'"
          />
          <el-text size="small" type="info" class="mt-1">
            提示：请输入有效的歌词URL地址（http://或https://开头）
          </el-text>
        </el-form-item>

        <!-- 上传文件方式 -->
        <el-form-item
            prop="lyric"
            v-if="lyricUploadMode === 'upload'"
            class="mb-0"
            key="lyric-file-upload"
        >
          <el-upload
              v-model:file-list="lyricFileList"
              action="#"
              list-type="text"
              :on-success="handleLyricUploadSuccess"
              :on-error="handleLyricUploadError"
              :limit="1"
              :on-exceed="handleLyricUploadExceed"
              :before-upload="beforeLyricUpload"
              :http-request="handleLyricUpload"
              :disabled="saveLoading"
          >
            <el-button
                type="primary"
                :style="{ width: isSmallScreen ? '100%' : '200px' }"
                :size="isSmallScreen ? 'small' : 'default'"
            >
              上传歌词
            </el-button>
            <template #tip>
              <div class="el-upload__tip">
                支持.lrc格式，文件大小不超过200kb
              </div>
            </template>
          </el-upload>
        </el-form-item>
      </el-form-item>

      <el-form-item label="封面">
        <!-- 切换方式选择 -->
        <el-radio-group
            v-model="coverUploadMode"
            class="mb-3"
            :disabled="saveLoading"
            @change="handleCoverModeChange"
            :size="isSmallScreen ? 'small' : 'default'"
        >
          <el-radio label="url" :class="isSmallScreen ? 'mr-4' : 'mr-6'">输入图片链接</el-radio>
          <el-radio label="upload">上传图片文件</el-radio>
        </el-radio-group>

        <!-- 链接输入方式 -->
        <el-form-item
            prop="cover"
            v-if="coverUploadMode === 'url'"
            class="mb-0"
            key="url-upload"
        >
          <el-input
              v-model="payloadData.cover"
              autocomplete="off"
              style="width: 100%"
              max-length="200"
              show-word-limit
              placeholder="请输入有效的图片地址"
              :disabled="saveLoading"
              :size="isSmallScreen ? 'small' : 'default'"
          />
          <el-text size="small" type="info" class="mt-1">
            提示：请输入有效的图片URL地址（http://或https://开头）
          </el-text>

          <!-- 预览区域 -->
          <div v-if="payloadData.cover" class="mt-2">
            <el-image
                :src="payloadData.cover"
                alt="封面预览"
                :style="{
                  width: isSmallScreen ? '120px' : '150px',
                  height: isSmallScreen ? '80px' : '100px',
                  objectFit: cover
                }"
                fit="cover"
                :preview-src-list="[payloadData.cover]"
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

        <!-- 上传文件方式 -->
        <el-form-item
            prop="cover"
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
          >
            <el-button
                type="primary"
                :style="{ width: isSmallScreen ? '100%' : '200px' }"
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
          :loading="deleteLoading"
          :size="isSmallScreen ? 'small' : 'default'"
          class="ml-2"
      >
        确认删除
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import {CirclePlus, Delete, Edit, Search} from '@element-plus/icons-vue'
import {ref, reactive, onMounted, nextTick, watchEffect } from 'vue'
import {ElMessage, ElForm} from 'element-plus'
import request from '@/utils/request';
import {debounce} from 'lodash';

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

// 上传相关
const fileList = ref([])          // 上传文件列表
// 封面上传方式：url-输入链接，upload-上传文件
const coverUploadMode = ref('url'); // 默认使用上传方式
// 添加一个状态标记模式是否正在切换
const isModeChanging = ref(false);
// 处理封面模式切换
const handleCoverModeChange = async (mode) => {
  // 保存当前模式，用于后续比较
  const previousMode = coverUploadMode.value;
  // 先更新模式
  coverUploadMode.value = mode;

  try {
    // 清除验证状态
    if (formRef.value) {
      await formRef.value.clearValidate('cover');
    }

    // 处理数据切换
    if (mode === 'url') {
      // 切换到URL模式
      fileList.value = [];
      // 如果之前是上传模式且没有值，不立即验证
      if (previousMode === 'upload' && !payloadData.value.cover) {
        return; // 暂时不验证，等待用户输入
      }
    } else {
      // 切换到上传模式
      if (previousMode === 'url' && !payloadData.value.cover) {
        payloadData.value.cover = '';
        return; // 暂时不验证，等待用户上传
      }
    }

    // 只有在有值的情况下才进行验证
    if (payloadData.value.cover || fileList.value.length > 0) {
      setTimeout(() => {
        formRef.value?.validateField('cover').catch(err => {
        });
      }, 100);
    }
  } catch (error) {
    ElMessage.error(`切换失败: ${error.message || JSON.stringify(error)}`);
  } finally {
    // 延迟一段时间后再标记为切换完成
    setTimeout(() => {
      isModeChanging.value = false;
    }, 300);
  }
};

// 加载数据
const loadData = async () => {
  try {
    searchLoading.value = true
    // 构造请求参数
    const params = {
      pageNum: pagination.value.pageNum,
      pageSize: pagination.value.pageSize,
      ...(inputSearch.value.trim() && {songName: inputSearch.value.trim()})  // 搜索歌名
    }

    const res = await request.get('/music/page', {params})

    if (res.code === "200") {
      tableData.value = res.data.records || []
      pagination.value.total = res.data.total || 0
    } else {
      ElMessage.error('获取数据失败：' + (res.msg || '未知错误'))
    }
  } catch (error) {
    console.error('数据请求失败:', error)
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
  songName: [
    {required: true, message: '请输入歌名', trigger: ['blur', 'change']},
    {min: 1, max: 50, message: '歌名长度在 1 到 50 个字符', trigger: 'blur'}
  ],
  singer: [
    {required: true, message: '请输入歌手名', trigger: ['blur', 'change']},
    {min: 1, max: 50, message: '歌手名长度在 1 到 50 个字符', trigger: 'blur'}
  ],
  album: [
    {required: true, message: '请输入专辑名', trigger: ['blur', 'change']},
    {min: 1, max: 50, message: '专辑名长度在 1 到 50 个字符', trigger: 'blur'}
  ],
  url: [
    {required: true, message: '请输入音乐链接', trigger: ['blur', 'change']},
    {type: 'url', message: '请输入有效的URL', trigger: 'blur'}
  ],
  cover: [
    {required: false, message: '请上传封面图片', trigger: 'change'}
  ],
  lyric: [
    {required: false, message: '请上传歌词链接，.lrc类型', trigger: 'change'}
  ],
})

// 表单数据
const payloadData = ref({
  id: null,  // 用于编辑时的唯一标识
  songName: '',
  singer: '',
  album: '',
  url: '',
  cover: '',
  lyric: ''
})

// 重置表单
const resetForm = () => {
  payloadData.value = {
    id: null,
    songName: '',
    singer: '',
    album: '',
    url: '',
    cover: '',
    lyric: ''
  };

  // 清空上传文件列表
  fileList.value = [];
  lyricFileList.value = [];

  // 重置上传模式
  coverUploadMode.value = 'url';
  lyricUploadMode.value = 'url';

  dialogBuildVisible.value = false;
  // 重置表单验证状态
  nextTick(() => {
    formRef.value?.resetFields();
  });
};

// 新建按钮点击
const handleCreate = () => {
  resetForm()
  dialogBuildVisible.value = true
}


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
      payloadData.value.cover = response;
      ElMessage.success('图片上传成功');
      params.onSuccess(response);
    } else {
      throw new Error('上传失败，服务器返回异常: ' + response);
    }

  } catch (error) {
    ElMessage.error('图片上传失败')
    params.onError(error)
  }
}

// 文件上传前验证
const beforeUpload = (file) => {
  // 验证文件类型
  const isJpgOrPng = file.type === 'image/jpeg' || file.type === 'image/png' || file.type === 'image/webp'
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


// 文件上传失败回调
const handleUploadError = (err) => {
  ElMessage.error('上传失败：网络错误，请稍后重试')
  console.error('文件上传错误:', err)
}

// 文件上传超出限制回调
const handleUploadExceed = () => {
  ElMessage.warning('最多只能上传1张封面图片')
}

// 保存/更新操作
const handleSave = async () => {
  // 表单验证
  if (!formRef.value) return;

  try {
    await formRef.value.validate()
  } catch (error) {
    return ElMessage.warning('请完善表单信息后再提交')
  }

  try {
    saveLoading.value = true

    // 构建提交数据
    const submitData = {
      songName: payloadData.value.songName.trim(),
      singer: payloadData.value.singer.trim(),
      album: payloadData.value.album.trim(),
      url: payloadData.value.url.trim(),
      cover: payloadData.value.cover,
      lyric: payloadData.value.lyric.trim()
    }

    // 如果是编辑操作，添加id
    if (payloadData.value.id) {
      submitData.id = payloadData.value.id
    }

    let res
    if (payloadData.value.id) {
      // 编辑操作：调用更新接口
      res = await request.post('/music/update', submitData)
    } else {
      // 新建操作：调用保存接口
      res = await request.post('/music', submitData)
    }

    if (res.code === "200") {
      ElMessage.success(payloadData.value.id ? '更新成功' : '创建成功')
      dialogBuildVisible.value = false  // 关闭对话框
      await loadData() // 重新加载数据
    } else {
      ElMessage.error(res.msg || '操作失败')
    }
  } catch (error) {
    console.error('保存失败:', error)
    ElMessage.error('保存失败：' + error.message)
  } finally {
    saveLoading.value = false
  }
}

// 编辑操作
const handleEdit = (row) => {
  // 深拷贝数据，避免直接修改表格数据
  payloadData.value = {...JSON.parse(JSON.stringify(row))}

  // 如果有封面图，显示在上传组件中
  if (payloadData.value.cover) {
    fileList.value = [{
      url: payloadData.value.cover,
      name: '封面图',
      status: 'success'
    }]
  } else {
    fileList.value = []
  }

  // 如果有歌词，根据类型显示在对应的上传组件中
  if (payloadData.value.lyric) {
    if (payloadData.value.lyric.startsWith('http')) {
      lyricUploadMode.value = 'url';
      lyricFileList.value = [];
    } else {
      lyricUploadMode.value = 'upload';
      lyricFileList.value = [{
        url: payloadData.value.lyric,
        name: '歌词文件',
        status: 'success'
      }];
    }
  } else {
    lyricFileList.value = [];
  }

  dialogBuildVisible.value = true;

  // 重置表单验证状态
  nextTick(() => {
    formRef.value?.clearValidate();
  });
};

// 歌词上传相关新增代码
const lyricUploadMode = ref('url'); // 歌词上传方式：url-输入链接，upload-上传文件
const lyricFileList = ref([]);     // 歌词上传文件列表

// 处理歌词模式切换
const handleLyricModeChange = async (mode) => {
  const previousMode = lyricUploadMode.value;
  lyricUploadMode.value = mode;

  try {
    if (formRef.value) {
      await formRef.value.clearValidate('lyric');
    }

    if (mode === 'url') {
      // 切换到URL模式
      lyricFileList.value = [];
      if (previousMode === 'upload' && !payloadData.value.lyric) {
        return;
      }
    } else {
      // 切换到上传模式
      if (previousMode === 'url' && !payloadData.value.lyric) {
        payloadData.value.lyric = '';
        return;
      }
    }

    if (payloadData.value.lyric || lyricFileList.value.length > 0) {
      setTimeout(() => {
        formRef.value?.validateField('lyric').catch(err => {
          console.log('歌词验证提示:', err);
        });
      }, 100);
    }
  } catch (error) {
    console.error('切换歌词上传方式错误:', error);
    ElMessage.error(`切换失败: ${error.message || JSON.stringify(error)}`);
  }
};


// 歌词文件上传前验证
const beforeLyricUpload = (file) => {
  // 验证文件类型
  const isLrc = file.name.endsWith('.lrc');
  if (!isLrc) {
    ElMessage.error('只能上传LRC格式的歌词文件');
    return false;
  }

  // 验证文件大小
  const isLt200K = file.size / 1024 < 200;
  if (!isLt200K) {
    ElMessage.error('歌词文件大小不能超过200KB');
    return false;
  }

  return true;
};

// 歌词上传
const handleLyricUpload = async (params) => {
  try {
    const formData = new FormData();
    formData.append('file', params.file);
    const response = await request.post('/file/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
      responseType: 'text'
    });

    if (response && (response.startsWith('http') || response.endsWith('.lrc'))) {
      payloadData.value.lyric = response;
      ElMessage.success('歌词上传成功');
      params.onSuccess(response);
    } else {
      throw new Error('上传失败，服务器返回异常: ' + response);
    }

  } catch (error) {
    ElMessage.error('歌词上传失败');
    params.onError(error);
  }
};


// 歌词文件上传失败回调
const handleLyricUploadError = (err) => {
  ElMessage.error('歌词上传失败：网络错误，请稍后重试');
  console.error('歌词文件上传错误:', err);
};

// 歌词文件上传超出限制回调
const handleLyricUploadExceed = () => {
  ElMessage.warning('最多只能上传1个歌词文件');
};

// 单个删除
const handleSingleDelete = async (row) => {
  try {
    deleteLoading.value = true
    const res = await request.delete(`/music/${row.id}`)
    if (res.code === "200") {
      ElMessage.success('删除成功')
      await loadData() // 重新加载数据，确保数据一致性
    } else {
      ElMessage.error('删除失败：' + (res.msg || '未知错误'))
    }
  } catch (error) {
    console.error('删除失败:', error)
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
    // 确保参数格式正确
    const res = await request.post('/music/del/batch', ids)

    if (res.code === "200") {
      ElMessage.success('批量删除成功')
      await loadData() // 重新加载数据
      selectedRows.value = []
      batchDeleteVisible.value = false
    } else {
      ElMessage.error('批量删除失败：' + (res.msg || '未知错误'))
    }
  } catch (error) {
    console.error('批量删除失败:', error)
    ElMessage.error('批量删除失败：' + error.message)
  } finally {
    deleteLoading.value = false
  }
}
</script>

<style scoped>
/* 调整间距和布局 */
.mt-2 {
  margin-top: 10px !important;
}

.ml-2 {
  margin-left: 8px !important;
}

.mt-1 {
  margin-top: 5px !important;
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
}

.pagination-block {
  padding: 10px 0;
}

.center-content {
  display: flex;
  justify-content: center;
  align-items: center;
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

/* 图标错误状态样式 */
.image-error, .image-placeholder {
  width: 100%;
  height: 100%;
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

.el-upload-list__item {
  max-width: 500px;
}

.el-upload-list__item-name {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
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
    padding: 6px 10px;
    font-size: 12px;
  }

  :deep(.el-button .el-icon) {
    font-size: 14px;
  }
}

@media (max-width: 576px) {
  .table-scroll-container {
    max-height: calc(100vh - 220px);
  }
}
</style>
