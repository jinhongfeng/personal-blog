<template>
  <!-- 头部 -->
  <el-card :style="{ height: isSmallScreen ? 'auto' : '12%', padding: isSmallScreen ? '10px' : '0' }">
    <div class="button-container">
      <!-- 左侧区域：搜索框和搜索按钮 -->
      <div class="left-group">
        <el-input
            v-model="inputSearch"
            :style="{ width: isSmallScreen ? '100%' : '240px' }"
            placeholder="输入要搜索的主题"
            :prefix-icon="Search"
            clearable
            @keyup.enter="handleSearch"
            :size="isSmallScreen ? 'small' : 'default'"
        />
        <el-button
            type="info"
            :class="isSmallScreen ? 'mt-2 w-full' : 'ml-10'"
            @click="handleSearch"
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
            :size="isSmallScreen ? 'small' : 'default'"
        >
          <el-icon class="mr-1"><CirclePlus /></el-icon>
          <span>新建</span>
        </el-button>
        <el-button
            type="warning"
            :disabled="selectedRows.length === 0"
            @click="batchDeleteVisible = true"
            :class="isSmallScreen ? 'mt-2 ml-0' : 'ml-10'"
            :size="isSmallScreen ? 'small' : 'default'"
        >
          批量删除
        </el-button>
      </div>
    </div>
  </el-card>

  <!-- 主要内容 -->
  <el-card class="homeMain-box" :style="{ padding: isSmallScreen ? '10px 5px' : '15px' }">
    <div class="table-scroll-container">
      <el-table
          :data="tableData"
          stripe
          style="width: 100%"
          v-loading="loading"
          @selection-change="handleSelectionChange"
          :cell-style="{ padding: isSmallScreen ? '4px 8px' : '8px 12px' }"
          :header-cell-style="{ padding: isSmallScreen ? '6px 8px' : '10px 12px' }"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column
            prop="nickname"
            label="昵称"
            :width="isSmallScreen ? 100 : 200"
        />
        <el-table-column
            prop="theme"
            label="主题"
            :width="isSmallScreen ? 100 : 200"
        />
        <el-table-column
            prop="content"
            label="内容"
            :show-overflow-tooltip="true"
        />
        <el-table-column
            prop="contact"
            label="联系"
            :width="isSmallScreen ? 100 : 200"
        />
        <el-table-column
            label="操作"
            :width="isSmallScreen ? 140 : 180"
        >
          <template #default="scope">
            <el-button-group>
              <el-button
                  type="success"
                  @click="handleEdit(scope.row)"
                  :size="isSmallScreen ? 'small' : 'default'"
                  title="编辑"
              >
                <el-icon class="mr-1"><Edit /></el-icon>
                <span>编辑</span>
              </el-button>
              <el-popconfirm
                  title="是否确认删除？"
                  confirm-button-text="确认"
                  cancel-button-text="取消"
                  @confirm="handleSingleDelete(scope.row)"
              >
                <template #reference>
                  <el-button
                      type="danger"
                      :class="isSmallScreen ? '' : 'ml-10'"
                      :size="isSmallScreen ? 'small' : 'default'"
                      title="删除"
                  >
                    <el-icon class="mr-1"><Delete /></el-icon>
                    <span>删除</span>
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
          :small="isSmallScreen"
      />
    </div>
  </el-card>

  <!-- 新建/编辑对话框 -->
  <el-dialog
      v-model="dialogBuildVisible"
      :title="payLoadData.id ? '编辑内容' : '新建内容'"
      :width="isSmallScreen ? '90%' : '800px'"
      :before-close="resetForm"
      max-height="80vh"
      overflow-y="auto"
      :close-on-click-modal="false"
  >
    <el-form
        :model="payLoadData"
        :label-width="isSmallScreen ? '80px' : '120px'"
        ref="formRef"
    >
      <el-form-item label="昵称" prop="nickname">
        <el-input
            v-model="payLoadData.nickname"
            autocomplete="off"
            style="width: 100%"
            :size="isSmallScreen ? 'small' : 'default'"
        />
      </el-form-item>

      <el-form-item label="主题" prop="theme" :rules="[{ required: true, message: '请输入主题', trigger: 'blur' }]">
        <el-input
            v-model="payLoadData.theme"
            autocomplete="off"
            style="width: 100%"
            :size="isSmallScreen ? 'small' : 'default'"
        />
      </el-form-item>

      <el-form-item label="内容" prop="content" :rules="[{ required: true, message: '请输入内容', trigger: 'blur' }]">
        <el-input
            v-model="payLoadData.content"
            autocomplete="off"
            style="width: 100%"
            type="textarea"
            :rows="isSmallScreen ? 3 : 4"
            :size="isSmallScreen ? 'small' : 'default'"
        />
      </el-form-item>

      <el-form-item label="联系" prop="contact">
        <el-input
            v-model="payLoadData.contact"
            autocomplete="off"
            style="width: 100%"
            :size="isSmallScreen ? 'small' : 'default'"
        />
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
            :size="isSmallScreen ? 'small' : 'default'"
            class="ml-2"
        >
          保存
        </el-button>
      </div>
    </template>
  </el-dialog>

  <!-- 批量删除确认框 -->
  <el-dialog
      v-model="batchDeleteVisible"
      title="批量删除确认"
      :width="isSmallScreen ? '90%' : '400px'"
      :close-on-click-modal="false"
  >
    <p>您确定要删除选中的 <span style="color: #ff4d4f">{{ selectedCount }}</span> 条数据吗？</p>
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
          class="ml-2"
      >
        确认删除
      </el-button>
    </template>
  </el-dialog>

</template>

<script setup>
import { ref, onMounted, reactive, watchEffect } from "vue";
import { CirclePlus, Delete, Edit, Search } from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";
import request from '@/utils/request';
import { debounce } from "lodash";

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

// 分页配置
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

// 表格数据
const tableData = ref([])
const loading = ref(false)  // 表格加载状态

// 选中数据
const selectedRows = ref([])
const selectedCount = ref(0)

// 对话框相关
const dialogBuildVisible = ref(false)
const batchDeleteVisible = ref(false)
const formRef = ref(null)  // 表单引用
const payLoadData = ref({
  id: null,
  nickname: '',
  theme: '',
  content: '',
  contact: ''
})

// 加载表格数据（核心接口调用）
const loadData = async () => {
  try {
    loading.value = true

    const params = {
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,

      ...(inputSearch.value.trim() && { theme: inputSearch.value.trim() })
    }
    const res = await request.get('/message/page', { params })
    if (res.code === "200") {
      tableData.value = res.data.records || []
      pagination.total = res.data.total || 0
    } else {
      ElMessage.error('获取数据失败：' + (res.msg || '未知错误'))
      tableData.value = []
      pagination.total = 0
    }
  } catch (error) {
    ElMessage.error('请求异常：' + (error.response?.data?.msg || error.message || '网络错误'))
    tableData.value = []
  } finally {
    loading.value = false
  }
}

// 初始化加载数据
onMounted(() => {
  loadData()
})


// 搜索操作（防抖处理）
const handleSearch = debounce(() => {
  pagination.pageNum = 1  // 搜索时重置到第一页
  loadData()
}, 300)

// 分页变化处理
const handleSizeChange = (size) => {
  pagination.pageSize = size
  loadData()
}
const handleCurrentChange = (page) => {
  pagination.pageNum = page
  loadData()
}

// 选中行变化
const handleSelectionChange = (rows) => {
  selectedRows.value = rows
  selectedCount.value = rows.length
}

// 重置表单
const resetForm = () => {
  payLoadData.value = {
    id: null,
    nickname: '',
    theme: '',
    content: '',
    contact: ''
  }
  formRef.value?.resetFields()  // 重置表单验证
  dialogBuildVisible.value = false
}

// 新建操作
const handleCreate = () => {
  resetForm()
  dialogBuildVisible.value = true
}

// 编辑操作
const handleEdit = (row) => {
  // 深拷贝避免直接修改原数据
  payLoadData.value = { ...row }
  dialogBuildVisible.value = true
}

// 保存/更新操作（核心接口调用）
const handleSave = async () => {
  // 表单验证
  if (!formRef.value) return
  const valid = await formRef.value.validate()
  if (!valid) return

  try {
    payLoadData.value.nickname = "陌生人"
    let res
    if (payLoadData.value.id) {
      // 编辑：调用更新接口
      res = await request.post('/message/update', payLoadData.value)
    } else {
      // 新建：调用创建接口
      res = await request.post('/message', payLoadData.value)
    }

    if (res.code === "200") {
      ElMessage.success(payLoadData.value.id ? '更新成功' : '创建成功')
      resetForm()
      await loadData()  // 重新加载数据
    } else {
      ElMessage.error(res.msg || (payLoadData.value.id ? '更新失败' : '创建失败'))
    }
  } catch (error) {
    ElMessage.error('保存失败：' + (error.response?.data?.msg || error.message))
  }
}

// 单个删除（核心接口调用）
const handleSingleDelete = async (row) => {
  try {
    const res = await request.delete(`/message/${row.id}`)
    if (res.code === "200") {
      ElMessage.success('删除成功')
      await loadData()
    } else {
      ElMessage.error('删除失败：' + (res.msg || '未知错误'))
    }
  } catch (error) {
    ElMessage.error('删除异常：' + (error.response?.data?.msg || error.message))
  }
}

// 批量删除（核心接口调用）
const handleBatchDelete = async () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请选择要删除的数据')
    return
  }
  try {
    const ids = selectedRows.value.map(row => row.id)
    const res = await request.post('/message/del/batch', ids)  // 与后端批量删除接口匹配

    if (res.code === "200") {
      ElMessage.success('批量删除成功')
      batchDeleteVisible.value = false
      selectedRows.value = []
      selectedCount.value = 0
      await loadData()  // 重新加载数据
    } else {
      ElMessage.error('批量删除失败：' + (res.msg || '未知错误'))
    }
  } catch (error) {
    ElMessage.error('批量删除异常：' + (error.response?.data?.msg || error.message))
  }
}
</script>

<style scoped>
.button-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 20px;
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

.ml-10 {
  margin-left: 10px !important;
}

.ml-2 {
  margin-left: 8px !important;
}

.center-content {
  display: flex;
  justify-content: center;
  align-items: center;
}

.pagination-block {
  padding: 10px 0;
}

.homeMain-box {
  margin: 15px 0;
  min-height: calc(100vh - 220px);
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
