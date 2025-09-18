<template>
  <div class="admin-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1>个人信息管理</h1>
      <div class="action-buttons">
        <el-button type="primary" @click="handleSaveAll" :size="isSmallScreen ? 'small' : 'default'">
          <el-icon><CircleCheck /></el-icon>
          <span :class="{ 'hidden-sm': isSmallScreen }">保存全部</span>
        </el-button>
        <el-button @click="handleReset" :size="isSmallScreen ? 'small' : 'default'">
          <el-icon><Refresh /></el-icon>
          <span :class="{ 'hidden-sm': isSmallScreen }">重置</span>
        </el-button>
      </div>
    </div>

    <!-- 主要内容区域 -->
    <div class="content-wrapper">
      <!-- 基本信息卡片 -->
      <el-card class="info-card">
        <div slot="header" class="card-header">
          <el-icon><User /></el-icon>
          <span>基本信息</span>
        </div>
        <div class="card-content">
          <el-row :gutter="isSmallScreen ? 10 : 20">
            <!-- 头像设置 - 在小屏幕上占满一行 -->
            <el-col :span="isSmallScreen ? 24 : 6" :class="{ 'mb-4': isSmallScreen }">
              <el-form-item label="头像">
                <div class="avatar-uploader">
                  <el-upload
                      class="avatar-upload"
                      action="#"
                      :show-file-list="false"
                      :before-upload="beforeAvatarUpload"
                      :http-request="handleAvatarUpload"
                  >
                    <el-image
                        :src="avatarUrl"
                        alt="个人头像"
                        class="avatar-image"
                        :style="{ width: isSmallScreen ? '120px' : '160px', height: isSmallScreen ? '120px' : '160px' }"
                    >
                      <template #placeholder>
                        <div class="avatar-placeholder"
                             :style="{ width: isSmallScreen ? '120px' : '160px', height: isSmallScreen ? '120px' : '160px' }">
                          <el-icon><Plus /></el-icon>
                          <span :class="{ 'hidden-xs': isXsScreen }">上传头像</span>
                        </div>
                      </template>
                    </el-image>
                  </el-upload>
                  <p class="upload-tip" :class="{ 'hidden-xs': isXsScreen }">支持JPG、PNG格式，建议尺寸200x200px</p>
                </div>
              </el-form-item>
            </el-col>

            <!-- 个人信息表单 - 在小屏幕上占满一行 -->
            <el-col :span="isSmallScreen ? 24 : 18">
              <el-form :model="personInfo" :label-width="isSmallScreen ? '80px' : '100px'">
                <el-row :gutter="isSmallScreen ? 10 : 20">
                  <!-- 在小屏幕上每行一个输入框 -->
                  <el-col :span="isSmallScreen ? 24 : 12">
                    <el-form-item label="姓名">
                      <el-input v-model="personInfo.name" placeholder="请输入姓名"></el-input>
                    </el-form-item>
                  </el-col>
                  <el-col :span="isSmallScreen ? 24 : 12">
                    <el-form-item label="年龄">
                      <el-input v-model.number="personInfo.age" type="number" placeholder="请输入年龄"></el-input>
                    </el-form-item>
                  </el-col>
                  <el-col :span="24">
                    <el-form-item label="邮箱">
                      <el-input v-model="personInfo.email" placeholder="请输入邮箱"></el-input>
                    </el-form-item>
                  </el-col>
                  <el-col :span="24">
                    <el-form-item label="爱好">
                      <div class="tag-input-group">
                        <el-tag
                            v-for="(hobby, index) in personInfo.hobby"
                            :key="index"
                            closable
                            @close="removeHobby(index)"
                            :size="isSmallScreen ? 'small' : 'default'"
                        >
                          {{ hobby }}
                        </el-tag>
                        <el-input
                            v-model="newHobby"
                            placeholder="按Enter添加爱好"
                            size="small"
                            @keyup.enter="addHobby"
                            class="hobby-input"
                            :style="{ minWidth: isSmallScreen ? '120px' : '150px' }"
                        ></el-input>
                      </div>
                    </el-form-item>
                  </el-col>
                </el-row>
              </el-form>
            </el-col>
          </el-row>
        </div>
      </el-card>

      <!-- 首页内容卡片 -->
      <el-card class="info-card">
        <div slot="header" class="card-header">
          <font-awesome-icon icon="home" />
          <span>首页内容</span>
        </div>
        <div class="card-content">
          <el-form-item>
            <div class="array-editor">
              <div v-for="(index) in homepageContent" :key="index" class="array-item">
                <el-input
                    v-model="homepageContent[index]"
                    placeholder="请输入内容"
                    class="array-input"
                    :size="isSmallScreen ? 'small' : 'default'"
                ></el-input>
                <el-button
                    type="text"
                    icon="Delete"
                    @click="removeItem(homepageContent, index)"
                    class="delete-btn"
                    :size="isSmallScreen ? 'small' : 'default'"
                ></el-button>
              </div>
              <el-button
                  type="primary"
                  icon="Plus"
                  @click="addItem(homepageContent, '')"
                  class="add-btn"
                  :size="isSmallScreen ? 'small' : 'default'"
              >
                <span :class="{ 'hidden-sm': isSmallScreen }">添加内容</span>
              </el-button>
            </div>
          </el-form-item>
        </div>
      </el-card>

      <!-- 个人简介卡片 -->
      <el-card class="info-card">
        <div slot="header" class="card-header">
          <el-icon><Document /></el-icon>
          <span>个人简介</span>
        </div>
        <div class="card-content">
          <el-form-item>
            <div class="array-editor">
              <div
                  v-for="(item, index) in personalProfile"
                  :key="index"
                  class="array-item"
              >
                <el-input
                    v-model="personalProfile[index]"
                    placeholder="请输入简介内容"
                    class="array-input"
                    :size="isSmallScreen ? 'small' : 'default'"
                ></el-input>
                <el-button
                    type="text"
                    icon="Delete"
                    @click="removeItem(personalProfile, index)"
                    class="delete-btn"
                    :size="isSmallScreen ? 'small' : 'default'"
                ></el-button>
              </div>
              <el-button
                  type="primary"
                  icon="Plus"
                  @click="addItem(personalProfile, '')"
                  class="add-btn"
                  :size="isSmallScreen ? 'small' : 'default'"
              >
                <span :class="{ 'hidden-sm': isSmallScreen }">添加简介</span>
              </el-button>
            </div>
          </el-form-item>
        </div>
      </el-card>

      <!-- 我的散文卡片 -->
      <el-card class="info-card">
        <div slot="header" class="card-header">
          <el-icon><EditPen /></el-icon>
          <span>我的散文</span>
        </div>
        <div class="card-content">
          <el-form-item>
            <div class="array-editor">
              <div
                  v-for="(item, index) in myProse"
                  :key="index"
                  class="array-item"
              >
                <el-input
                    v-model="myProse[index]"
                    placeholder="请输入散文内容"
                    class="array-input"
                    :size="isSmallScreen ? 'small' : 'default'"
                ></el-input>
                <el-button
                    type="text"
                    icon="Delete"
                    @click="removeItem(myProse, index)"
                    class="delete-btn"
                    :size="isSmallScreen ? 'small' : 'default'"
                ></el-button>
              </div>
              <el-button
                  type="primary"
                  icon="Plus"
                  @click="addItem(myProse, '')"
                  class="add-btn"
                  :size="isSmallScreen ? 'small' : 'default'"
              >
                <span :class="{ 'hidden-sm': isSmallScreen }">添加散文</span>
              </el-button>
            </div>
          </el-form-item>
        </div>
      </el-card>

      <!-- 我的技能卡片 -->
      <el-card class="info-card">
        <div slot="header" class="card-header">
          <el-icon><Tools /></el-icon>
          <span>我的技能</span>
        </div>
        <div class="card-content">
          <el-form-item>
            <div class="array-editor">
              <div v-for="(item, index) in mySkill" :key="index" class="array-item">
                <el-input
                    v-model="mySkill[index]"
                    placeholder="请输入技能描述"
                    class="array-input"
                    :size="isSmallScreen ? 'small' : 'default'"
                ></el-input>
                <el-button
                    type="text"
                    icon="Delete"
                    @click="removeItem(mySkill, index)"
                    class="delete-btn"
                    :size="isSmallScreen ? 'small' : 'default'"
                ></el-button>
              </div>
              <el-button
                  type="primary"
                  icon="Plus"
                  @click="addItem(mySkill, '')"
                  class="add-btn"
                  :size="isSmallScreen ? 'small' : 'default'"
              >
                <span :class="{ 'hidden-sm': isSmallScreen }">添加技能</span>
              </el-button>
            </div>
          </el-form-item>
        </div>
      </el-card>

      <!-- 读的书籍卡片 -->
      <el-card class="info-card">
        <div slot="header" class="card-header">
          <font-awesome-icon icon="book" />
          <span>读的书籍</span>
        </div>
        <div class="card-content">
          <div class="media-list">
            <div v-for="(book, index) in readBook" :key="book.id || index" class="media-item">
              <el-row :gutter="isSmallScreen ? 10 : 15">
                <!-- 在小屏幕上书籍封面占满一行 -->
                <el-col :span="isSmallScreen ? 24 : 4" :class="{ 'mb-3': isSmallScreen }">
                  <el-form-item label="封面" :label-width="isSmallScreen ? '60px' : 'auto'">
                    <el-upload
                        class="media-upload"
                        action="#"
                        :show-file-list="false"
                        :before-upload="beforeMediaUpload"
                        :http-request="(params) => handleMediaUpload(params, readBook, index, 'book')"
                    >
                      <el-image
                          :src="book.image"
                          alt="书籍封面"
                          class="media-image"
                      >
                        <template #placeholder>
                          <div class="media-placeholder">
                            <el-icon><Plus /></el-icon>
                          </div>
                        </template>
                      </el-image>
                    </el-upload>
                  </el-form-item>
                </el-col>
                <!-- 在小屏幕上信息占满一行 -->
                <el-col :span="isSmallScreen ? 24 : 14">
                  <el-form-item label="标题" :label-width="isSmallScreen ? '60px' : 'auto'">
                    <el-input v-model="book.title" placeholder="请输入书籍标题" :size="isSmallScreen ? 'small' : 'default'"></el-input>
                  </el-form-item>
                  <el-form-item label="链接" :label-width="isSmallScreen ? '60px' : 'auto'">
                    <el-input v-model="book.url" placeholder="请输入书籍链接" :size="isSmallScreen ? 'small' : 'default'"></el-input>
                  </el-form-item>
                </el-col>
                <!-- 在小屏幕上操作按钮调整位置 -->
                <el-col :span="isSmallScreen ? 24 : 6" class="media-actions">
                  <el-button
                      type="danger"
                      icon="Delete"
                      @click="removeMediaItem(readBook, index, 'book')"
                      class="delete-media-btn"
                      :size="isSmallScreen ? 'small' : 'default'"
                  >
                    <span :class="{ 'hidden-sm': isSmallScreen }">删除</span>
                  </el-button>
                </el-col>
              </el-row>
              <el-divider v-if="index !== readBook.length - 1"></el-divider>
            </div>
            <el-button
                type="primary"
                icon="Plus"
                @click="addBook"
                class="add-media-btn"
                :size="isSmallScreen ? 'small' : 'default'"
            >
              <span :class="{ 'hidden-sm': isSmallScreen }">添加书籍</span>
            </el-button>
          </div>
        </div>
      </el-card>

      <!-- 看的电影卡片 -->
      <el-card class="info-card">
        <div slot="header" class="card-header">
          <el-icon><VideoCamera /></el-icon>
          <span>看的电影</span>
        </div>
        <div class="card-content">
          <div class="media-list">
            <div v-for="(film, index) in readFilm" :key="film.id || index" class="media-item">
              <el-row :gutter="isSmallScreen ? 10 : 15">
                <!-- 在小屏幕上电影封面占满一行 -->
                <el-col :span="isSmallScreen ? 24 : 4" :class="{ 'mb-3': isSmallScreen }">
                  <el-form-item label="封面" :label-width="isSmallScreen ? '60px' : 'auto'">
                    <el-upload
                        class="media-upload"
                        action="#"
                        :show-file-list="false"
                        :before-upload="beforeMediaUpload"
                        :http-request="(params) => handleMediaUpload(params, readFilm, index, 'film')"
                    >
                      <el-image
                          :src="film.image"
                          alt="电影封面"
                          class="media-image"
                      >
                        <template #placeholder>
                          <div class="media-placeholder">
                            <el-icon><Plus /></el-icon>
                          </div>
                        </template>
                      </el-image>
                    </el-upload>
                  </el-form-item>
                </el-col>
                <!-- 在小屏幕上信息占满一行 -->
                <el-col :span="isSmallScreen ? 24 : 14">
                  <el-form-item label="标题" :label-width="isSmallScreen ? '60px' : 'auto'">
                    <el-input v-model="film.title" placeholder="请输入电影标题" :size="isSmallScreen ? 'small' : 'default'"></el-input>
                  </el-form-item>
                  <el-form-item label="链接" :label-width="isSmallScreen ? '60px' : 'auto'">
                    <el-input v-model="film.url" placeholder="请输入电影链接" :size="isSmallScreen ? 'small' : 'default'"></el-input>
                  </el-form-item>
                </el-col>
                <!-- 在小屏幕上操作按钮调整位置 -->
                <el-col :span="isSmallScreen ? 24 : 6" class="media-actions">
                  <el-button
                      type="danger"
                      icon="Delete"
                      @click="removeMediaItem(readFilm, index, 'film')"
                      class="delete-media-btn"
                      :size="isSmallScreen ? 'small' : 'default'"
                  >
                    <span :class="{ 'hidden-sm': isSmallScreen }">删除</span>
                  </el-button>
                </el-col>
              </el-row>
              <el-divider v-if="index !== readFilm.length - 1"></el-divider>
            </div>
            <el-button
                type="primary"
                icon="Plus"
                @click="addFilm"
                class="add-media-btn"
                :size="isSmallScreen ? 'small' : 'default'"
            >
              <span :class="{ 'hidden-sm': isSmallScreen }">添加电影</span>
            </el-button>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 保存成功提示 -->
    <el-toast
        v-model="saveSuccess"
        type="success"
        message="保存成功"
        :duration="3000"
    />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watchEffect } from 'vue'
import {
  User, Document, EditPen, Tools, VideoCamera,
  Refresh, Plus, CircleCheck
} from '@element-plus/icons-vue'
import { ElMessage, ElLoading } from 'element-plus'
import request from '@/utils/request';

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

// 初始化数据
const avatarUrl = ref('')
const personInfo = reactive({
  id: null,
  name: '',
  age: '',
  hobby: [],
  email: ''
})
const homepageContent = ref([])
const personalProfile = ref([])
const myProse = ref([])
const mySkill = ref([])
const readBook = ref([])
const readFilm = ref([])

// 临时变量
const newHobby = ref('')
const saveSuccess = ref(false)
const originalData = ref(null)
const mediaIdSets = ref({
  readBookIds: [],
  readFilmIds: []
})

// 处理爱好字符串分割
const handleHobbySplit = (hobbyStr) => {
  if (!hobbyStr || typeof hobbyStr !== 'string' || hobbyStr.trim() === '') {
    return [];
  }
  return hobbyStr.split('，').filter(item => item.trim() !== '');
};

// 辅助函数：解析JSON数据
const parseJsonData = (data, defaultVal) => {
  if (!data) return [...defaultVal];
  if (typeof data === 'string') {
    try {
      const parsed = JSON.parse(data.replace(/\\/g, ''));
      return Array.isArray(parsed) ? parsed : [parsed];
    } catch (e) {
      console.warn('JSON解析失败:', e);
      return [data];
    }
  }
  if (Array.isArray(data)) {
    return [...data];
  }
  return [data];
}

// 加载数据库数据
const loadData = async () => {
  try {
    const loading = ElLoading.service({
      lock: true,
      text: '加载中...',
      background: 'rgba(0, 0, 0, 0.7)'
    });

    // 1. 查询sys_concern获取用户基本信息
    const sysRes = await request.get('/concern')
    if (sysRes.code !== '200') {
      throw new Error('获取用户信息失败: ' + (sysRes.msg || '未知错误'))
    }

    const sysData = Array.isArray(sysRes.data) ? sysRes.data[0] : sysRes.data;
    originalData.value = JSON.parse(JSON.stringify(sysData));

    // 2. 从concern_info查询所有关联数据，通过theme区分类型
    const allConcernsRes = await request.get('/concern_info');
    if (allConcernsRes.code === '200') {
      const allConcerns = allConcernsRes.data || [];

      // 3. 按theme字段筛选出书籍和电影
      readBook.value = allConcerns.filter(item => item.theme === 'book');
      readFilm.value = allConcerns.filter(item => item.theme === 'film');

      // 4. 更新ID集合（从筛选结果中提取ID）
      mediaIdSets.value.readBookIds = readBook.value.map(book => book.id);
      mediaIdSets.value.readFilmIds = readFilm.value.map(film => film.id);
    }

    // 5. 加载其他基本信息
    personInfo.id = sysData.id;
    personInfo.name = sysData.nickname || '未知用户';
    personInfo.age = sysData.age || '';
    personInfo.email = sysData.email || '未填写';
    personInfo.hobby = handleHobbySplit(sysData.hobby || '');
    avatarUrl.value = sysData.avatar || require("@/assets/personAvatar.jpg");

    homepageContent.value = parseJsonData(sysData.homedesc, [
      '欢迎访问我的个人主页，这里展示了我的个人信息和兴趣爱好。',
      '通过左侧导航可以浏览不同分类的内容'
    ]);
    personalProfile.value = parseJsonData(sysData.profile, [
      "'small potato' 意为小人物。通信工程小破硕。",
      "性格介于开朗和沉默之间，热爱生活"
    ]);
    myProse.value = parseJsonData(sysData.prose, [
      '写什么呢？写我不知上进，写我贪玩勿学，写我缺乏志向，写我蹉跎二十余载，也未学一技之长傍身。终日违心陪笑，苟且于市井之间；写我多年八方奔跑，跌跌撞撞一事无成，还是写花有重开日，人无再少年？写春去秋来，鬓边未觉添霜，却已不敢再提"少年"二字；写曾以为山海可平，后来才懂，世间多的是"求不得"与"已失去"。',
      '如果自己还不够强大，就得把自己藏起来不断学习。学历、阅历、能力都是铺垫，在无人问津的地方打磨，在万众瞩目的地方出现。不必急着向世界证明什么，时间会筛选出真正有价值的东西。等风来不如追风去，不必焦虑路远，反正步步都算数。'
    ]);
    mySkill.value = parseJsonData(sysData.skill, [
      '编程语言：JavaScript、Vue、Python、C++',
      '设计工具：PS、PR'
    ]);

    loading.close();
  } catch (error) {
    ElMessage.error('数据加载异常: ' + error.message);
    console.error('加载失败：', error);
    // eslint-disable-next-line no-undef
    loading.close();
  }
};

// 爱好管理方法
const addHobby = () => {
  if (newHobby.value.trim() && !personInfo.hobby.includes(newHobby.value.trim())) {
    personInfo.hobby.push(newHobby.value.trim())
    newHobby.value = ''
  }
}

const removeHobby = (index) => {
  personInfo.hobby.splice(index, 1)
}

// 数组项管理通用方法
const addItem = (array, defaultValue) => {
  array.push(defaultValue)
}

const removeItem = (array, index) => {
  array.splice(index, 1)
}

// 添加书籍和电影方法
const addBook = () => {
  readBook.value.push({ title: '', image: '', url: '' })
}

const addFilm = () => {
  readFilm.value.push({ title: '', image: '', url: '' })
}

// 头像上传相关方法
const beforeAvatarUpload = (file) => {
  const isJPGOrPNG = file.type === 'image/jpeg' || file.type === 'image/png' || file.type === 'image/webp';
  const isLt2M = file.size / 1024 / 1024 < 2;

  if (!isJPGOrPNG) ElMessage.error('上传头像图片只能是 JPG/PNG 格式!')
  if (!isLt2M) ElMessage.error('上传头像图片大小不能超过 2MB!')
  return isJPGOrPNG && isLt2M
}

const handleAvatarUpload = async (params) => {
  const loading = ElLoading.service({
    lock: true,
    text: '上传中...',
    background: 'rgba(0, 0, 0, 0.7)'
  });

  try {
    const formData = new FormData();
    formData.append('file', params.file);
    const response = await request.post('/file/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
      responseType: 'text'
    });

    if (response && response.startsWith('http')) {
      avatarUrl.value = response;
      ElMessage.success('图片上传成功');
      params.onSuccess(response);

    } else {
      throw new Error('上传失败，服务器返回异常: ' + response);
    }
  } catch (error) {
    ElMessage.error('头像上传失败：' + error.message);
    params.onError(error);
    console.error('头像上传错误:', error);
  } finally {
    loading.close();
  }
}

// 媒体封面上传方法
const handleMediaUpload = async (params, array, index, theme) => {
  try {
    if (!array || index === undefined || index < 0 || index >= array.length) {
      throw new Error('参数无效或索引超出范围');
    }

    const formData = new FormData();
    formData.append('file', params.file);
    formData.append('theme', theme);

    const response = await request.post('/file/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
      responseType: 'text'
    });

    if (response && response.startsWith('http')) {
      array[index].image = response;
      ElMessage.success('图片上传成功');
      params.onSuccess(response);
    } else {
      throw new Error('上传失败，服务器返回异常: ' + response);
    }
  } catch (error) {
    const errorMsg = `图片上传失败：${error.message}`;
    ElMessage.error(errorMsg);
    params.onError(error);
    console.error('媒体上传错误:', errorMsg);
  }
};

const beforeMediaUpload = (file) => {
  const isJPGOrPNG = file.type === 'image/jpeg' || file.type === 'image/png' || file.type === 'image/webp'
  const isLt5M = file.size / 1024 / 1024 < 5

  if (!isJPGOrPNG) ElMessage.error('上传图片只能是 JPG/PNG/webp 格式!')
  if (!isLt5M) ElMessage.error('上传图片大小不能超过 5MB!')
  return isJPGOrPNG && isLt5M
}

// 删除媒体项
const removeMediaItem = async (array, index) => {
  try {
    const item = array[index];
    // 如果存在id，先删除concern_info中的记录
    if (item.id) {
      const res = await request.post('/concern_info/delete', { id: item.id });
      if (res.code !== '200') {
        throw new Error('删除失败: ' + (res.msg || '未知错误'));
      }
    }
    // 从本地数组中移除
    array.splice(index, 1);
    ElMessage.success('删除成功');
  } catch (error) {
    ElMessage.error('删除失败: ' + error.message);
    console.error('删除媒体项错误:', error);
  }
}

// 保存全部数据到数据库
const handleSaveAll = async () => {
  try {
    const loading = ElLoading.service({
      lock: true,
      text: '保存中...',
      background: 'rgba(0, 0, 0, 0.7)'
    });

    // 1. 保存书籍到concern_info并获取ID集合
    const bookIds = [];
    for (const book of readBook.value) {
      // 强制设置theme为'book'，确保分类正确
      const payload = { ...book, theme: 'book' };
      let res;
      if (book.id) {
        res = await request.post('/concern_info/update', payload);
      } else {
        res = await request.post('/concern_info/add', payload);
      }
      if (res.code === '200' && res.data.id) {
        bookIds.push(res.data.id);
        book.id = res.data.id; // 更新本地id
      }
    }

    // 2. 保存电影到concern_info并获取ID集合
    const filmIds = [];
    for (const film of readFilm.value) {
      // 强制设置theme为'film'，确保分类正确
      const payload = { ...film, theme: 'film' };
      let res;
      if (film.id) {
        res = await request.post('/concern_info/update', payload);
      } else {
        res = await request.post('/concern_info/add', payload);
      }
      if (res.code === '200' && res.data.id) {
        filmIds.push(res.data.id);
        film.id = res.data.id; // 更新本地id
      }
    }

    // 3. 更新sys_concern表（仅存储ID集合）
    const sysData = {
      id: personInfo.id,
      nickname: personInfo.name?.trim() || '',
      age: personInfo.age || '',
      email: personInfo.email?.trim() || '',
      hobby: personInfo.hobby.filter(h => h.trim()).join('，') || '',
      avatar: avatarUrl.value,
      homedesc: JSON.stringify(homepageContent.value.filter(item => item.trim())),
      profile: JSON.stringify(personalProfile.value.filter(item => item.trim())),
      prose: JSON.stringify(myProse.value.filter(item => item.trim())),
      skill: JSON.stringify(mySkill.value.filter(item => item.trim())),
      // 保留ID集合用于关联，但不再有单独的book/film内容字段
      concern_ids: JSON.stringify([...bookIds, ...filmIds])
    };

    const sysRes = await request.post('/concern/update', sysData);
    if (sysRes.code === '200') {
      originalData.value = JSON.parse(JSON.stringify(sysRes.data));
      mediaIdSets.value.readBookIds = bookIds;
      mediaIdSets.value.readFilmIds = filmIds;
      saveSuccess.value = true;
      setTimeout(() => saveSuccess.value = false, 3000);
      ElMessage.success('数据保存成功');
    } else {
      throw new Error('保存用户信息失败: ' + (sysRes.msg || '未知错误'));
    }

    loading.close();
  } catch (error) {
    ElMessage.error('保存数据失败: ' + error.message);
    console.error('保存失败详细信息：', error);
  }
};

// 重置数据方法
const handleReset = () => {
  if (confirm('确定要重置所有数据吗？当前修改将会丢失。')) {
    if (originalData.value) {
      const data = originalData.value;

      // 重置基本信息
      personInfo.id = data.id;
      personInfo.name = data.nickname || '未知用户';
      personInfo.age = data.age || '';
      personInfo.email = data.email || '未填写';
      personInfo.hobby = handleHobbySplit(data.hobby || '');
      avatarUrl.value = data.avatar || require("@/assets/personAvatar.jpg");

      // 重置其他数组
      homepageContent.value = parseJsonData(data.homepageContent, []);
      personalProfile.value = parseJsonData(data.personalProfile, []);
      myProse.value = parseJsonData(data.myProse, []);
      mySkill.value = parseJsonData(data.mySkill, []);

      // 重新加载书籍和电影数据
      mediaIdSets.value.readBookIds = parseJsonData(data.read_book, []);
      mediaIdSets.value.readFilmIds = parseJsonData(data.read_film, []);
      loadMediaDetails(); // 重新加载详情

      ElMessage.success('数据已重置为原始状态');
    } else {
      // 重置为默认值
      avatarUrl.value = require("@/assets/personAvatar.jpg");
      personInfo.name = 'POTATO';
      personInfo.age = 24;
      personInfo.hobby = ['篮球', '健身', '看书', '敲代码'];
      personInfo.email = '2474921543@qq.com';

      homepageContent.value = [
        '欢迎访问我的个人主页，这里展示了我的个人信息和兴趣爱好。'
      ];
      personalProfile.value = [
        "'small potato' 意为小人物。今年24岁，喜欢篮球、健身、看书、敲代码。"
      ];
      myProse.value = [
        '写什么呢？写我不知上进，写我贪玩勿学...'
      ];
      mySkill.value = [
        '编程语言：JavaScript、Vue、Python'
      ];
      readBook.value = [];
      readFilm.value = [];

      ElMessage.success('数据已重置为默认值');
    }
  }
}

// 重新加载媒体详情
const loadMediaDetails = async () => {
  try {
    if (mediaIdSets.value.readBookIds.length > 0) {
      const bookRes = await request.post('/concern_info/batch', {
        ids: mediaIdSets.value.readBookIds,
        theme: 'book'
      });
      if (bookRes.code === '200') {
        readBook.value = bookRes.data;
      }
    } else {
      readBook.value = [];
    }

    if (mediaIdSets.value.readFilmIds.length > 0) {
      const filmRes = await request.post('/concern_info/batch', {
        ids: mediaIdSets.value.readFilmIds,
        theme: 'film'
      });
      if (filmRes.code === '200') {
        readFilm.value = filmRes.data;
      }
    } else {
      readFilm.value = [];
    }
  } catch (error) {
    console.error('重置媒体数据失败:', error);
  }
}

// 页面加载时获取数据
onMounted(() => {
  loadData();
})
</script>

<style scoped>
.admin-container {
  padding: 15px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  flex-wrap: wrap;
  gap: 10px;
}

.page-header h1 {
  font-size: 24px;
  margin: 0;
  color: #1f2329;
}

.action-buttons {
  display: flex;
  gap: 10px;
}

.content-wrapper {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.info-card {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  border-radius: 8px;
  overflow: hidden;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 500;
}

.card-content {
  padding: 20px;
}

/* 头像上传样式 */
.avatar-uploader {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.avatar-image {
  border-radius: 50%;
  object-fit: cover;
  cursor: pointer;
  border: 2px dashed #ddd;
}

.avatar-placeholder {
  border-radius: 50%;
  background-color: #f5f7fa;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  border: 2px dashed #ddd;
  cursor: pointer;
  color: #666;
}

.upload-tip {
  margin-top: 10px;
  font-size: 12px;
  color: #999;
}

/* 标签输入组样式 */
.tag-input-group {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  align-items: center;
  padding: 5px 0;
}

.hobby-input {
  flex-grow: 0;
}

/* 数组编辑器样式 */
.array-editor {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.array-item {
  display: flex;
  align-items: center;
  gap: 10px;
  width: 100%;
}

.array-input {
  flex-grow: 1;
}

.delete-btn {
  color: #ff4d4f;
}

.add-btn {
  margin-top: 10px;
  align-self: flex-start;
}

/* 媒体列表样式 */
.media-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.media-item {
  padding: 15px;
  background-color: #fafafa;
  border-radius: 6px;
}

.media-upload {
  width: 100%;
}

.media-image {
  width: 100%;
  height: 120px;
  object-fit: cover;
  border-radius: 4px;
  cursor: pointer;
}

.media-placeholder {
  width: 100%;
  height: 120px;
  background-color: #f0f0f0;
  display: flex;
  justify-content: center;
  align-items: center;
  border-radius: 4px;
  cursor: pointer;
  color: #666;
}

.media-actions {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  height: 100%;
}

.delete-media-btn {
  align-self: center;
}

.add-media-btn {
  margin-top: 10px;
  align-self: flex-start;
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
}

@media (max-width: 768px) {
  .page-header h1 {
    font-size: 20px;
  }

  .card-content {
    padding: 15px;
  }

  .media-actions {
    justify-content: flex-start;
    margin-top: 15px;
  }

  .add-btn, .add-media-btn {
    align-self: stretch;
    width: 100%;
  }
}

@media (max-width: 576px) {
  .admin-container {
    padding: 10px;
  }

  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }

  .action-buttons {
    width: 100%;
    justify-content: space-between;
  }

  .card-header {
    font-size: 14px;
  }

  .array-item {
    width: 100%;
  }

  .media-item {
    padding: 10px;
  }

  .media-image, .media-placeholder {
    height: 100px;
  }
}
</style>
