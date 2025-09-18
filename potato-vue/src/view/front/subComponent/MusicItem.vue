<template>
  <!-- 整体容器 -->
  <div class="music-player-container">
    <div class="music-background">
      <!-- 左侧歌曲库区域 -->
      <div class="left-section" v-if="!isLyricExpanded"
           @mouseenter="leftSectionHovered = true"
           @mouseleave="leftSectionHovered = false">
        <div class="section-header">
          <h2>音乐库</h2>
        </div>
        <!-- 导航栏 -->
        <div class="tabs">
          <button class="tab-button" :class="{ active: activeTab === 'playlist' }" @click="activeTab = 'playlist'">
            <el-icon><Menu /></el-icon> 全部歌曲
          </button>
          <button class="tab-button" :class="{ active: activeTab === 'playing' }" @click="activeTab = 'playing'">
            <el-icon><Aim /></el-icon>正在播放
          </button>
          <button class="tab-button" :class="{ active: activeTab === 'search' }" @click="activeTab = 'search'">
            <el-icon><Search /></el-icon>搜索
          </button>
        </div>

        <!-- 搜索框 -->
        <div class="search-box" v-if="activeTab === 'search'">
          <input type="text" placeholder="搜索歌曲、歌手或专辑..."
                 v-model="searchKeyword" @keyup.enter="searchSongs"/>
          <button @click="searchSongs"><el-icon><Search /></el-icon></button>
        </div>

        <!-- 歌曲列表容器 -->
        <div class="song-list-container">
          <!-- 加载状态 -->
          <div v-if="isLoading" class="loading-container">
            <el-loading :fullscreen="false" text="加载中..."></el-loading>
          </div>

          <!-- 错误状态 -->
          <div v-else-if="showError" class="error-container">
            <el-icon class="error-icon"><Warning /></el-icon>
            <p class="error-text">{{ errorMessage }}</p>
            <el-button type="primary" @click="fetchSongList">重试</el-button>
          </div>

          <!-- 数据加载完成但为空 -->
          <div v-else-if="songList.length === 0 && activeTab === 'playlist'" class="empty-state">
            <el-empty description="暂无歌曲数据"></el-empty>
          </div>

          <!-- 正常显示歌曲列表 -->
          <table v-else-if="(activeTab === 'playlist' && songList.length > 0) || (activeTab === 'search' && searchResults.length)">
            <thead>
            <tr>
              <th class="序号">#</th>
              <th class="歌曲">歌曲</th>
              <th class="歌手">歌手</th>
              <th class="专辑">专辑</th>
              <th class="操作">操作</th>
            </tr>
            </thead>
            <tbody>
            <tr v-for="(song, index) in activeTab === 'search' ? searchResults : songList" :key="index"
                :class="{ 'playing-now': currentSong && song.id === currentSong.id, 'song-row': true}">
              <!-- 表格内容 -->
              <td>{{ index + 1 }}</td>
              <td>
                <div class="song-info">
                  <img :src="song.cover" :alt="song.songName" class="song-cover" />
                  <span>{{ song.songName }}</span>
                </div>
              </td>
              <td>{{ song.singer }}</td>
              <td>{{ song.album }}</td>
              <td>
                <button @click="playSong(song)" class="play-btn">
                  <font-awesome-icon icon="play" />
                </button>
              </td>
            </tr>
            </tbody>
          </table>

          <!-- 正在播放列表 -->
          <div class="playing-list" v-if="activeTab === 'playing'">
            <h3>正在播放列表</h3>
            <div v-if="playingList.length === 0" class="empty-state">
              <el-empty description="暂无播放歌曲"></el-empty>
            </div>
            <ul v-else>
              <li v-for="(song, idx) in playingList" :key="idx"
                  :class="{'current': currentSong && song.id === currentSong.id,'playlist-item': true}">
                <img :src="song.cover" :alt="song.songName" class="mini-cover" />
                <div class="song-details">
                  <div>{{ song.songName }}</div>
                  <div class="singer-name">{{ song.singer }}</div>
                </div>
                <button @click="playSong(song)"><i class="fas fa-play"></i></button>
              </li>
            </ul>
          </div>

          <!-- 搜索无结果 -->
          <div v-if="activeTab === 'search' && searchResults.length === 0 && searchKeyword" class="empty-state">
            没有找到与 "{{ searchKeyword }}" 相关的歌曲
          </div>
        </div>
      </div>

      <!-- 右侧歌词区域 -->
      <div class="right-section" :class="{ 'lyric-container-expanded': isLyricExpanded }">
        <!-- 未播放时显示动态音符 -->
        <div class="music-placeholder" v-if="!currentSong && !isLoading && !showError">
          <div class="music-note">
            <svg width="100" height="100" viewBox="0 0 100 100" xmlns="http://www.w3.org/2000/svg">
              <path d="M30,10 C40,10 45,20 45,30 C45,40 40,50 30,50 C20,50 15,40 15,30 C15,20 20,10 30,10 Z M30,60 C45,60 50,70 50,80 L20,80 C20,70 25,60 30,60 Z" fill="#3b82f6">
                <animate attributeName="opacity" values="0.7;1;0.7" dur="1s" repeatCount="indefinite" />
              </path>
              <path d="M60,20 C70,20 75,30 75,40 C75,50 70,60 60,60 C50,60 45,50 45,40 C45,30 50,20 60,20 Z M60,70 C75,70 80,80 80,90 L50,90 C50,80 55,70 60,70 Z" fill="#3b82f6">
                <animate attributeName="opacity" values="0.5;0.8;0.5" dur="1.2s" repeatCount="indefinite" />
                <animate attributeName="transform" values="translate(0,0);translate(0,5);translate(0,0)" dur="1.2s" repeatCount="indefinite" />
              </path>
            </svg>
            <p>点击左侧歌曲开始播放</p>
          </div>
        </div>

        <!-- 播放时显示歌词 -->
        <div class="lyric-container" v-if="currentSong && !isLoading && !showError">
          <h2>《{{ currentSong.songName }}》</h2>
          <img class="album-cover" :src="currentSong.cover" :alt="currentSong.songName"
               :class="{ 'paused-rotation': !isPlaying }"/>
          <p class="singer">{{ currentSong.singer }} - {{ currentSong.album }}</p>

          <!-- 歌词滚动容器 - 根据isLyricVisible控制显示 -->
          <div class="lyric-scroll-wrapper"
               v-if="isLyricVisible"
               @mouseenter="handleLyricMouseEnter"
               @mouseleave="handleLyricMouseLeave"
               @wheel="handleLyricScroll"
               style="pointer-events: auto; ">
            <div class="lyric-scroll">
              <div class="lyric-content"
                   :style="{ transform: `translateY(-${scrollOffset}px)` }">
                <p v-for="(line, lIdx) in currentLyric" :key="lIdx"
                   :class="{ 'active-line': lIdx === currentLyricIndex }">
                  {{ line.text }}
                </p>
              </div>
            </div>
          </div>

          <!-- 歌词隐藏时显示提示 -->
          <div class="lyric-hidden-hint" v-else>
            <p>歌词已隐藏</p>
            <p class="hint-small">点击眼睛图标显示歌词</p>
          </div>
        </div>

      </div>

      <!-- 右下角控制歌词组件 -->
      <div class="lyric-controls">
        <button @click="toggleLyricVisibility" class="lyric-control-btn" :title="isLyricVisible ? '隐藏歌词' : '显示歌词'">
          <font-awesome-icon :icon="isLyricVisible ? 'eye-slash' : 'eye'" />
        </button>
        <button @click="toggleLyricExpand" class="lyric-control-btn" :title="isLyricExpanded ? '收缩歌词' : '扩展歌词'">
          <font-awesome-icon :icon="isLyricExpanded ? 'compress' : 'expand'" />
        </button>
        <button @click="showSongInfo" class="lyric-control-btn" title="歌曲信息" >
          <font-awesome-icon icon="ellipsis-v" />
        </button>
      </div>
    </div>
    <!-- 底部播放器区域-->
    <div class="bottom-player">
      <audio ref="audioPlayer" :src="currentSong?.url"  @error="handleAudioError"
             @timeupdate="handleTimeUpdate" @ended="handleEnded"
             @canplay="handleCanPlay" />

      <div class="player-row">
        <!-- 左侧控制按钮组 -->
        <div class="player-controls">
          <!-- 上一首 -->
          <button @click="playPrevious" class="control-btn">
            <el-icon><ArrowLeftBold /></el-icon>
          </button>
          <!-- 播放/暂停 -->
          <button @click="togglePlay" class="play-pause-btn">
            <el-icon v-if="isPlaying"><VideoPause /></el-icon>
            <el-icon v-else><VideoPlay /></el-icon>
          </button>
          <!-- 下一首 -->
          <button @click="playNext" class="control-btn">
            <el-icon><ArrowRightBold /></el-icon>
          </button>
          <!-- 循环切换 -->
          <button @click="toggleRepeatMode" class="control-btn">
            <el-icon v-if="repeatMode === 'none'"><Refresh /></el-icon>
            <font-awesome-icon icon="random" v-else-if="repeatMode === 'all'" />
            <font-awesome-icon icon="sync-alt" v-else />
          </button>
        </div>
        <!-- 进度条 -->
        <div class="progress-container">
          <span class="time-display">{{ formatTime(currentTime) }}</span>
          <input type="range" :value="currentTime" @input="seek" step="0.1"
                 :max="duration" class="progress-bar"/>
          <span class="time-display">{{ formatTime(duration) }}</span>
        </div>
        <!-- 音量控制 -->
        <div class="volume-control">
          <div class="volume-icon-container" @click="toggleMute">
            <font-awesome-icon :icon="volumeIcon" :class="{ 'muted-pulse': isMuted }" />
          </div>
          <div class="volume-slider-wrapper">
            <input type="range" v-model="currentVolume"
                   @input="setVolume" min="0" max="1" step="0.05" class="volume-slider"/>
          </div>
        </div>
      </div>
    </div>
  </div>
  <!-- 对话框 -->
  <el-dialog v-model="isSongInfoShow" :show-close="false" width="500"
             class="song-info-dialog" :append-to-body="true">
    <template #header>
      <div class="dialog-header">
        <el-button @click="closeSongInfo" type="danger">
          <el-icon ><CircleCloseFilled /></el-icon>
        </el-button>
      </div>
    </template>
    <!-- 有选择歌曲 -->
    <el-form :model="currentSong" label-width="80px" v-if="currentSong"
             style="max-width: 500px" >
      <el-form-item label="歌名">
        <el-input v-model="currentSong.songName" disabled class="info-input"/>
      </el-form-item>
      <el-form-item label="歌手">
        <el-input v-model="currentSong.singer" disabled class="info-input"/>
      </el-form-item>
      <el-form-item label="专辑">
        <el-input v-model="currentSong.album" disabled class="info-input"/>
      </el-form-item>
    </el-form>
    <!-- 无选择歌曲 -->
    <div class="no-selection" v-else>
      请先播放歌曲！
    </div>
  </el-dialog>
</template>

<script setup>
import {computed, onMounted, ref, watch} from 'vue';
import {Aim, ArrowLeftBold, ArrowRightBold, CircleCloseFilled,
  Refresh, VideoPause, VideoPlay, Menu, Search, Warning} from "@element-plus/icons-vue";
import request from "@/utils/request";
import { parseLrc } from '@/utils/lyricParser';

import {ElMessage, ElLoading, ElEmpty} from "element-plus";
// 歌曲列表数据
const songList = ref([]);

// 监听歌曲列表变化，初始化索引
watch(songList, (newList) => {
  if (newList.length > 0) {
    currentIndex.value = 0; // 确保索引在有效范围内
    // 初始加载第一首歌的src（避免初始状态src为空）
    if (audioPlayer.value) {
      audioPlayer.value.src = newList[0].url;
    }
  } else {
    currentIndex.value = -1; // 无歌曲时重置索引
  }

  // 重置失败记录
  failedSongIds.value = new Set();
});

const isLoading = ref(false);
const showError = ref(false);
const errorMessage = ref('');

// 从后端接口获取歌曲数据
const fetchSongList = async () => {
  try {
    // 重置状态
    isLoading.value = true;
    showError.value = false;
    errorMessage.value = '';

    // 调用后端接口（替换为你的实际接口地址）
    const response = await request.get('/music');

    // 解析歌词（后端返回的lyric是LRC字符串，需转为数组）
    const songsWithParsedLyric = response.data.map(song => ({
      ...song,
      lyric: parseLrc(song.lyric || '')  // 用parseLrc解析歌词文本
    }));
    // 赋值给songList
    songList.value = songsWithParsedLyric;

    // 重置失败记录
    failedSongIds.value = new Set();
  } catch (err) {
    showError.value = true;
    errorMessage.value = err.message || "获取歌曲列表失败，请稍后重试";
  } finally {
    isLoading.value = false;
  }
};

onMounted(() => {
  fetchSongList();
});

// 状态管理
const activeTab = ref('playlist');
const searchKeyword = ref('');
const searchResults = ref([]);
const playingList = ref([]);
const audioPlayer = ref(null);
const isPlaying = ref(false);
const currentTime = ref(0);
const duration = ref(0);
const volume = ref(0.7); // 原始音量值（0-1范围）
const currentVolume = ref(0.7); // 用于滑块控制
const isMuted = ref(false); // 是否静音
const currentLyric = ref([]);
const currentLyricIndex = ref(-1);
const repeatMode = ref('none'); // none, all, single
const isLyricHovered = ref(false); // 歌词区域是否被鼠标悬停
const scrollOffset = ref(0); // 歌词滚动偏移量

const isManualScrolled = ref(false); // 当用户手动滚动式，立即停止自动滚动
const isLyricVisible = ref(true); // 歌词是否可见
const isLyricExpanded = ref(false); // 歌词是否扩展
const leftSectionHovered = ref(false); //状态管理左侧悬浮
const isSongInfoShow = ref(false); // 对话框
const currentIndex = ref(0);
const isProcessing = ref(false);
const failedSongIds = ref(new Set()); // 记录播放失败的歌曲ID

const LINE_HEIGHT = 35;  // 歌词行高
const VISIBLE_LINES = 5; // 可视区域显示的歌词行数
// 当前播放歌曲
const currentSong = computed(() => songList.value[currentIndex.value]);

// 检查是否所有歌曲都已尝试过且失败
const allSongsFailed = computed(() => {
  // 过滤掉没有URL的歌曲，这些歌曲一开始就不应该被尝试播放
  const validSongs = songList.value.filter(song => song.url);
  return validSongs.length > 0 && failedSongIds.value.size === validSongs.length;
});

const handleLyricMouseEnter = () => {
  isLyricHovered.value = true;
};

const handleLyricMouseLeave = () => {
  isLyricHovered.value = false;
};

// 滚轮事件处理
const handleLyricScroll = (e) => {
  // 阻止事件冒泡，避免与其他滚动冲突
  e.preventDefault();

  // 计算新的偏移量（每次滚动调整一行高度）
  const newOffset = scrollOffset.value + (e.deltaY > 0 ? LINE_HEIGHT : -LINE_HEIGHT);

  // 限制偏移量范围（0 到 最大可滚动距离）
  const maxOffset = Math.max(0, currentLyric.value.length * LINE_HEIGHT - VISIBLE_LINES * LINE_HEIGHT);
  scrollOffset.value = Math.min(Math.max(0, newOffset), maxOffset);
};

// 当歌曲切换时重置滚动位置
watch(currentSong, () => {
  scrollOffset.value = 0;
  currentLyricIndex.value = -1;
});

// 监听当前歌曲变化，更新歌词
watch(currentSong, (newSong, oldSong) => {
  // 只有当歌曲真正切换时才重置（排除同一首歌的暂停/播放切换）
  if (newSong && (!oldSong || newSong.id !== oldSong.id)) {
    resetLyricState();
  }
});

const calculateScrollOffset = (index) => {
  if (index === -1) return 0; // 无歌词时不滚动

  // 核心公式：当前行索引 * 行高 - 可视区域一半高度（实现居中）
  const middleOffset = (VISIBLE_LINES / 2) * LINE_HEIGHT;
  const targetOffset = index * LINE_HEIGHT - middleOffset;

  // 限制最小偏移量为0（避免顶部空白）
  return Math.max(0, targetOffset);
};

watch(currentLyricIndex, (newIndex) => {
  // 只有播放状态且歌词存在时才自动滚动
  if (isPlaying.value && currentLyric.value.length > 0) {
    scrollOffset.value = calculateScrollOffset(newIndex);
  }
});

const resetLyricState = () => {
  // 重置歌词索引为初始状态
  currentLyricIndex.value = -1;
  // 重置滚动偏移量，歌词回到最顶部
  scrollOffset.value = 0;
  // 重置手动滚动标记
  isManualScrolled.value = false;
  // 初始化当前歌曲的歌词
  currentLyric.value = currentSong.value?.lyric || [];
};


// 切换歌词可见性
const toggleLyricVisibility = () => {
  isLyricVisible.value = !isLyricVisible.value;
};

// 切换歌词扩展状态
const toggleLyricExpand = () => {
  isLyricExpanded.value = !isLyricExpanded.value;
};

// 显示歌曲信息
const showSongInfo = () => {
  isSongInfoShow.value = true;
};

const closeSongInfo = () => {
  isSongInfoShow.value = false;
};

// 播放歌曲方法
const playSong = (song) => {
  // 如果正在处理中，直接返回
  if (isProcessing.value) return;

  if (currentSong.value && currentSong.value.id === song.id && isPlaying.value) {
    return;
  }

  // 重置失败记录（用户主动选择歌曲时）
  failedSongIds.value = new Set();

  // 设置当前歌曲
  currentSong.value = song;

  // 更新当前索引（用于上/下一首切换）
  const index = songList.value.findIndex(item => item.id === song.id);
  if (index !== -1) {
    currentIndex.value = index;
  }

  // 设置音频源并播放
  if (audioPlayer.value) {
    // 先暂停当前播放
    audioPlayer.value.pause();

    // 若切换了歌曲，更新src并加载
    if (audioPlayer.value.src !== song.url) {
      // 移除之前的事件监听器，避免多次触发
      const loadHandler = () => {
        // 加载完成后播放
        audioPlayer.value.play()
            .then(() => {
              isPlaying.value = true;
            })
            .catch(() => {
              isPlaying.value = false;
              // 记录失败的歌曲
              failedSongIds.value.add(song.id);
              handlePlayError();
            });
        // 移除事件监听，防止重复调用
        audioPlayer.value.removeEventListener('canplay', loadHandler);
      };

      // 监听加载完成事件
      audioPlayer.value.addEventListener('canplay', loadHandler);

      // 设置新的音频源
      audioPlayer.value.src = song.url;
      audioPlayer.value.load(); // 加载新资源
    } else {
      // 同一首歌直接播放
      audioPlayer.value.play()
          .then(() => {
            isPlaying.value = true;
          })
          .catch(err => {
            isPlaying.value = false;
            // 记录失败的歌曲
            failedSongIds.value.add(song.id);
            ElMessage.error('播放失败:' + err.message);
          });
    }
  }
  addToPlayingList(song);
};

// 处理播放错误
const handlePlayError = () => {
  // 检查是否所有歌曲都已尝试且失败
  if (allSongsFailed.value) {
    ElMessage.error('所有歌曲均无法播放，请检查音频文件');
    isPlaying.value = false;
    isProcessing.value = false;
    return;
  }

  // 如果还有可尝试的歌曲
  ElMessage.info('音频不存在，尝试播放下一首');

  if (songList.value.length > 0) {
    // 找到下一首有效的歌曲（跳过已失败的）
    let nextIndex = (currentIndex.value + 1) % songList.value.length;
    const startIndex = nextIndex;

    // 循环查找下一首未失败的歌曲
    while (failedSongIds.value.has(songList.value[nextIndex].id)) {
      nextIndex = (nextIndex + 1) % songList.value.length;

      // 如果回到起点，说明所有歌曲都已尝试
      if (nextIndex === startIndex) {
        ElMessage.error('所有歌曲均无法播放，请检查音频文件');
        isPlaying.value = false;
        isProcessing.value = false;
        return;
      }
    }

    currentIndex.value = nextIndex;
    playCurrentSong();
  } else {
    isProcessing.value = false;
  }
};

// 监听错误
const handleAudioError = () => {
  // 1. 避免无歌曲时报错
  if (songList.value.length === 0) return;

  // 当前歌曲播放失败，记录下来
  const currentSong = songList.value[currentIndex.value];
  if (currentSong) {
    failedSongIds.value.add(currentSong.id);
  }

  handlePlayError();
};

// 将播放的歌曲添加到正在播放的列表
const addToPlayingList = (song) => {
  // 检查歌曲是否已在列表中
  const isExist = playingList.value.some(item => item.id === song.id);
  if (!isExist) {
    playingList.value.push(song); // 不在列表中则添加
  }
};

// 上一曲
const playPrevious = () => {
  if (isProcessing.value || songList.value.length === 0) return;

  // 重置失败计数
  failedSongIds.value = new Set();

  // 使用setTimeout避免UI阻塞
  setTimeout(() => {
    try {
      const currentSong = songList.value[currentIndex.value];
      if (!currentSong || !currentSong.url) {
        currentIndex.value = (currentIndex.value - 1 + songList.value.length) % songList.value.length;
        playCurrentSong();
        return;
      }

      if (repeatMode.value === 'single') {
        // 单曲循环逻辑
        playCurrentSong();
        return;
      }

      // 非单曲循环逻辑
      currentIndex.value = (currentIndex.value - 1 + songList.value.length) % songList.value.length;
      playCurrentSong();
    } catch (err) {
      isProcessing.value = false;
    }
  }, 50); // 短暂延迟确保状态更新
};

// 下一曲
const playNext = () => {
  if (isProcessing.value || songList.value.length === 0) return;

  // 使用setTimeout避免UI阻塞
  setTimeout(() => {
    try {
      const currentSong = songList.value[currentIndex.value];
      if (!currentSong || !currentSong.url) {
        currentIndex.value = (currentIndex.value + 1) % songList.value.length;
        playCurrentSong();
        return;
      }

      if (repeatMode.value === 'single') {
        // 单曲循环逻辑
        playCurrentSong();
        return;
      }

      // 非单曲循环逻辑
      currentIndex.value = (currentIndex.value + 1) % songList.value.length;
      playCurrentSong();
    } catch (err) {
      isProcessing.value = false;
    }
  }, 50); // 短暂延迟确保状态更新
};

// 播放当前索引对应的歌曲
const playCurrentSong = async () => {
  // 如果正在处理中，直接返回
  if (isProcessing.value) return;

  try {
    // 设置处理中状态，防止重复操作
    isProcessing.value = true;

    const currentSong = songList.value[currentIndex.value];
    // 双重校验歌曲有效性
    if (!currentSong) {
      isProcessing.value = false;
      return;
    }
    if (!currentSong.url) {
      // 记录没有URL的歌曲为失败
      failedSongIds.value.add(currentSong.id);
      handlePlayError();
      return;
    }

    // 确保音频元素存在
    if (!audioPlayer.value) {
      isProcessing.value = false;
      return;
    }

    // 关键修复：先暂停当前播放并重置时间
    await audioPlayer.value.pause();
    audioPlayer.value.currentTime = 0;

    // 如果是同一首歌，直接播放
    if (audioPlayer.value.src === currentSong.url) {
      await audioPlayer.value.play();
      isPlaying.value = true;
      isProcessing.value = false;
      return;
    }

    // 移除之前的事件监听器，避免冲突
    const cleanupEvents = () => {
      audioPlayer.value.removeEventListener('canplay', canPlayHandler);
      audioPlayer.value.removeEventListener('error', errorHandler);
    };

    // 定义加载完成处理函数
    const canPlayHandler = async () => {
      try {
        await audioPlayer.value.play();
        isPlaying.value = true;
        // 播放成功，清除该歌曲的失败记录
        failedSongIds.value.delete(currentSong.id);
        cleanupEvents();
        isProcessing.value = false;
      } catch (err) {
        isPlaying.value = false;
        // 记录失败的歌曲
        failedSongIds.value.add(currentSong.id);
        cleanupEvents();
        handlePlayError();
      }
    };

    // 定义错误处理函数
    const errorHandler = () => {
      isPlaying.value = false;
      // 记录失败的歌曲
      failedSongIds.value.add(currentSong.id);
      cleanupEvents();
      handlePlayError();
    };

    // 添加事件监听
    audioPlayer.value.addEventListener('canplay', canPlayHandler);
    audioPlayer.value.addEventListener('error', errorHandler);

    // 设置新的音频源并加载
    audioPlayer.value.src = currentSong.url;
    audioPlayer.value.load();

  } catch (err) {
    isPlaying.value = false;
    // 记录失败的歌曲
    const currentSong = songList.value[currentIndex.value];
    if (currentSong) {
      failedSongIds.value.add(currentSong.id);
    }
    isProcessing.value = false;
    handlePlayError();
  }
};

// 处理歌曲播放结束
const handleEnded = () => {
  if (repeatMode.value === 'single') {
    // 单曲循环：重置时间并播放
    audioPlayer.value.currentTime = 0;
    audioPlayer.value.play().then(() => {
      isPlaying.value = true;
    }).catch(() => {
      isPlaying.value = false;
      // 记录失败的歌曲
      const currentSong = songList.value[currentIndex.value];
      if (currentSong) {
        failedSongIds.value.add(currentSong.id);
      }
      handlePlayError();
    });
  } else {
    // 其他模式：播放下一首
    playNext();
  }
};

// 切换播放/暂停
const togglePlay = () => {
  if (!audioPlayer.value) return;

  if (isPlaying.value) {
    audioPlayer.value.pause();
  } else {
    audioPlayer.value.play().catch(() => {
      isPlaying.value = false;
      // 记录失败的歌曲
      const currentSong = songList.value[currentIndex.value];
      if (currentSong) {
        failedSongIds.value.add(currentSong.id);
      }
      handlePlayError();
    });
  }
  isPlaying.value = !isPlaying.value;
};

// 切换重复模式
const toggleRepeatMode = () => {
  // 循环切换模式：none → all → single → none...
  if (repeatMode.value === 'none') {
    repeatMode.value = 'all'; // 列表循环
    ElMessage.info('列表循环');
  } else if (repeatMode.value === 'all') {
    repeatMode.value = 'single'; // 单曲循环
    ElMessage.info('单曲循环');
  } else {
    repeatMode.value = 'none'; // 不循环
    ElMessage.info('不循环');
  }
};

const handleCanPlay = () => {
  if (audioPlayer.value) {
    duration.value = audioPlayer.value.duration || 0; // 获取总时长（秒）
  }
};

// 监听播放进度，同步歌词
const handleTimeUpdate = () => {
  if (audioPlayer.value) {
    currentTime.value = audioPlayer.value.currentTime || 0;
  }
};

// 更新歌词
const updateLyricIndex = () => {
  if (!currentLyric.value.length) return;

  // 转换播放时间为毫秒（与歌词时间戳单位一致）
  const currentTimeMs = currentTime.value * 1000;
  let targetIndex = -1;

  // 遍历歌词匹配当前时间
  for (let i = 0; i < currentLyric.value.length; i++) {
    const lyricTime = currentLyric.value[i].time;
    // 匹配条件：当前时间 >= 歌词时间，且下一句时间 > 当前时间（或是最后一句）
    if (
        currentTimeMs >= lyricTime &&
        (i === currentLyric.value.length - 1 || currentTimeMs < currentLyric.value[i + 1].time)
    ) {
      targetIndex = i;
      break;
    }
  }

  currentLyricIndex.value = targetIndex;
};

watch(currentTime, updateLyricIndex);

// 搜索歌曲方法
const searchSongs = () => {
  if (!searchKeyword.value.trim()) {
    searchResults.value = [];
    return;
  }

  searchResults.value = songList.value.filter(song =>
      song.songName.toLowerCase().includes(searchKeyword.value.toLowerCase()) ||
      song.singer.toLowerCase().includes(searchKeyword.value.toLowerCase()) ||
      song.album.toLowerCase().includes(searchKeyword.value.toLowerCase())
  );
};

// 时间格式化
const formatTime = (seconds) => {
  if (isNaN(seconds)) return '00:00';
  const mins = Math.floor(seconds / 60);
  const secs = Math.floor(seconds % 60);
  return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
};

// 拖动进度条跳转
const seek = (e) => {
  // 1. 获取拖动后的值（进度条的value，单位：秒）
  const newTime = parseFloat(e.target.value);

  // 2. 校验值的有效性（0 <= newTime <= duration）
  if (isNaN(newTime) || newTime < 0 || newTime > duration.value) {
    return;
  }

  // 3. 同步到音频元素和currentTime变量
  if (audioPlayer.value) {
    audioPlayer.value.currentTime = newTime; // 设置音频播放时间
    currentTime.value = newTime; // 同步更新进度条显示
  }
};

// 计算显示的音量图标
const volumeIcon = computed(() => {
  if (isMuted.value || currentVolume.value === 0) {
    return 'volume-off';
  } else if (currentVolume.value < 0.3) {
    return 'volume-down';
  } else {
    return 'volume-up';
  }
});

// 切换静音状态
const toggleMute = () => {
  if (isMuted.value) {
    // 取消静音，恢复之前的音量
    currentVolume.value = volume.value;
  } else {
    // 静音，保存当前音量
    volume.value = currentVolume.value;
    currentVolume.value = 0;
  }
  isMuted.value = !isMuted.value;
  setVolume(); // 应用音量变化
};

// 设置音量
const setVolume = () => {
  if (audioPlayer.value) {
    audioPlayer.value.volume = currentVolume.value;

    // 处理静音状态同步
    if (currentVolume.value === 0 && !isMuted.value) {
      isMuted.value = true;
      volume.value = 0.7; // 保存默认音量
    } else if (currentVolume.value > 0 && isMuted.value) {
      isMuted.value = false;
    }
  }
};

</script>

<style scoped>
/* 基础样式 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

.music-player-container {
  display: flex;
  flex-direction: column;
  height: calc(100vh - 120px); /* 减去底部播放器高度 */
  position: relative;
  overflow: hidden;
  min-height: 90vh;
}
.music-background {
  display: flex;

  background: rgba(171, 209, 225, 0.1);
  margin: 10px 30px 50px;
  border-radius: 10px;
}
/* 左侧歌曲库区域 */
.left-section {
  width: 60%;
  display: flex;
  flex-direction: column;
  max-height: calc(100vh - 180px);
  transition: background-color 0.3s ease;
}

.section-header {
  padding: 16px 20px;
  border-bottom: 1px solid #e0e0e0;
}

.section-header h2 {
  color: #333;
  font-size: 1.4rem;
}

.tabs {
  display: flex;
  font-family: "微软雅黑 Light",serif;
  font-weight: 16;
  margin-left: 20px;
  margin-top: 20px;
}

.tab-button {
  padding: 12px 20px;
  border: none;
  background: none;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 5px;
  margin-left: 5px;
  color: #ffffff;
  border-radius: 10px;
  transition: all 0.3s;
}

.tab-button.active {
  color: #ffffff;
  background: #e52e71;
  font-weight: 500;
}

.tab-button:hover:not(.active) {
  background-color: #f5f5f5;
  color: #333;
}

.search-box {
  padding: 15px 20px;
  display: flex;
  gap: 4px;
  width: 20%;
}

.search-box input {
  flex: 1;
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 0.9rem;
}

.search-box button {
  padding: 8px 12px;
  background-color: #3b82f6;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.search-box button:hover {
  background-color: #2563eb;
}

/* 歌曲列表容器 */
.song-list-container {
  flex: 1;
  overflow-y: auto; /* 只在歌曲列表区域滚动 */
  padding: 10px 0;
  margin-bottom: 10px;

}

/* 加载状态样式 */
.loading-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 200px;
}

/* 错误状态样式 */
.error-container {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: 200px;
  gap: 15px;
  color: #f56c6c;
  text-align: center;
  padding: 0 20px;
}

.error-icon {
  font-size: 2rem;
}

.error-text {
  font-size: 1rem;
  max-width: 400px;
}

/* 空状态样式 */
.empty-state {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 200px;
  color: #999;
}

.song-list-container table {
  width: 100%;
  border-collapse: collapse;
}

.song-list-container th,
.song-list-container td {
  padding: 15px 20px;
  text-align: left;
}

.song-list-container th {
  font-weight: 500;
  color: #666;
  background-color: #f9f9f9;
  position: sticky;
  top: 0;
  z-index: 10;
}


.song-list-container th.序号 { width: 50px; }
.song-list-container th.歌曲 { width: 40%; }
.song-list-container th.歌手 { width: 20%; }
.song-list-container th.专辑 { width: 20%; }
.song-list-container th.操作 { width: 80px; }

.song-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.song-cover {
  width: 40px;
  height: 40px;
  object-fit: cover;
  border-radius: 4px;
}

.play-btn {
  background: none;
  border: 1px solid #3b82f6;
  color: #3b82f6;
  width: 30px;
  height: 30px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s;
  &:hover {
    background-color: #3b82f6;
    color: white;
  }
}

.playing-now {
  background-color: #e6f0ff;
}
.song-row {
  transition: all 0.2s ease;
  &:hover {
    background-color: #f5f5f5;
    transform: translateX(3px);
  }
}

/* 正在播放列表项悬浮效果 */
.playlist-item {
  transition: all 0.2s ease;
}

.playlist-item:hover {
  background-color: rgba(255, 255, 255, 0.2);
  transform: translateX(3px);
}

/* 确保当前播放行的样式不受hover影响 */
.song-row.playing-now:hover,
.playlist-item.current:hover {
  background-color: rgba(255, 255, 255, 0.8); /* 保持当前播放行的原有颜色 */
}

.playing-list {
  padding: 20px;
}

.playing-list h3 {
  margin-bottom: 15px;
  color: #333;
  font-size: 1.1rem;
}

.playing-list ul {
  list-style: none;
}

.playing-list li {
  display: flex;
  align-items: center;
  padding: 10px;
  border-radius: 4px;
  margin-bottom: 8px;
  transition: background-color 0.3s;
}

.playing-list li:hover {
  background-color: #f5f5f5;
}

.playing-list li.current {
  background-color: #e6f0ff;
}

.mini-cover {
  width: 36px;
  height: 36px;
  object-fit: cover;
  border-radius: 4px;
  margin-right: 12px;
}

.song-details {
  flex: 1;
}

.singer-name {
  font-size: 0.8rem;
  color: #666;
}

.playing-list button {
  background: none;
  border: none;
  color: #666;
  cursor: pointer;
  padding: 5px;
  opacity: 0;
  transition: all 0.3s;
}

.playing-list li:hover button {
  opacity: 1;
}

/* 右侧歌词区域 */
.right-section {
  width: 40%; /* 右侧区域相应调整 */
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 20px;
  position: relative;
  pointer-events: auto;
  user-select: none;
}
.right-lyric-container.expanded {
  width: 100%;
  margin: 0 auto; /* 水平居中 */
  max-width: 800px; /* 限制最大宽度防止过宽 */
}
/* 动态音符占位 */
.music-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #F7F9FE;
}

.music-note {
  text-align: center;
}

.music-note p {
  margin-top: 20px;
  font-size: 1.1rem;
}

/* 歌词容器 */
.lyric-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 100%;
  max-width: 600px;
  height: 100%;

}
.lyric-container-expanded {
  /* 扩展后，让歌词容器居中，并且左侧词库消失，所以调整 margin 等 */
  width: 100%;
  display: flex;
  text-align: center;
  justify-content: center;
}

.lyric-scroll-wrapper {
  height: 100%;
  width: 100%;
  overflow: hidden;
  position: relative;
  min-height: 300px;
  pointer-events: auto;
}
.lyric-scroll-wrapper:hover {
  background-color: rgba(255, 255, 255, 0.03);
}
.album-cover {
  width: 180px;
  height: 180px;
  object-fit: cover;
  border-radius: 50%;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.8);
  margin-bottom: 30px;
  transition: transform 0.3s ease;
  animation: rotate 20s linear infinite;
  transform-origin: center;
  padding: 20px;
}
/* 播放暂停时的旋转状态 */
.album-cover.paused-rotation {
  animation-play-state: paused;
}
/* 旋转动画定义 */
@keyframes rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}
.album-cover::after {
  content: '';
  position: absolute;
  width: 40px;
  height: 40px;
  background-color: rgba(0, 0, 0, 0.8);
  border-radius: 50%;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  box-shadow: 0 0 0 5px rgba(0, 0, 0, 0.1);
}
.lyric-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  position: relative;
  transform: translateY(-50%);
  transition: transform 0.3s ease-out;
}
.lyric-content p {
  height: 35px; /* 与 LINE_HEIGHT 保持一致 */
  line-height: 35px;
  color: #F4E1C0;
  font-size: 0.9rem;
  transition: all 0.3s;
  padding: 2px 0;

}
/* 增加当前歌词亮度，方便定位 */
.lyric-content p.active-line {
  color: #e52e71;
  font-size: 1.3rem;
  font-weight: 600;
  transform: scale(1.05);
}
.lyric-content p {
  color: #F4E1C0;
  font-size: 1.1rem;
  transition: all 0.3s;
}

.lyric-content p.active-line {
  color: #e52e71;
  font-size: 1.3rem;
  font-weight: 600;
  transform: scale(1.05);
}
.lyric-container h2 {
  color: greenyellow;
  font-size: 1.8rem;
  margin-bottom: 8px;
  text-align: center;
}

.singer {
  color: #ffffff;
  margin-bottom: 40px;
  font-size: 1rem;
}
.lyric-scroll {
  width: 100%;
  height: 100%;
  overflow-y: auto; /* 允许纵向滚动 */
  padding: 20px 0;
  scrollbar-width: none; /* Firefox */
  -ms-overflow-style: none; /* IE 和 Edge */
}
/* 确保歌词容器能接收鼠标事件 */
.lyric-scroll-wrapper {
  /* 移除可能存在的 pointer-events: none; */
  pointer-events: auto !important; /* 强制允许鼠标交互 */
  min-height: 200px; /* 确保有足够的点击区域 */
  padding: 10px; /* 扩大交互范围 */
}

/* 避免父元素遮挡 */
.right-section, .lyric-container {
  pointer-events: auto; /* 确保父容器不阻止事件传递 */
}
.lyric-controls {
  position: absolute;

  right: 20px;
  bottom: 90px;
  display: flex;
  flex-direction: column;
  gap: 10px;
  background-color: rgba(0, 0, 0, 0.3);
  border-radius: 20px;
  padding: 5px;
  backdrop-filter: blur(5px);
  opacity: 0.7;
  transition: opacity 0.3s;
}
.lyric-hidden-hint {
  width: 100%;
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #999;
  padding: 20px;
  text-align: center;
}
.hint-small {
  font-size: 0.8rem;
  margin-top: 8px;
  opacity: 0.7;
}
/* 鼠标悬停在歌词容器上时显示控制按钮 */
.lyric-container:hover .lyric-controls {
  opacity: 1;
}

.lyric-control-btn {
  background: none;
  border: none;
  color: #fff;
  width: 30px;
  height: 30px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s;
  font-size: 0.9rem;
}

.lyric-control-btn:hover {
  background-color: rgba(255, 255, 255, 0.2);
  transform: scale(1.1);
}

/* 歌词扩展状态样式 */
.lyric-container.expanded .lyric-scroll-wrapper {
  flex: 2; /* 扩展时占据更多空间 */
}

/* 歌词隐藏状态样式 */
.lyric-container.hidden-lyric .lyric-scroll-wrapper {
  display: none;
}
/* 底部播放器区域 - 使用图标按钮 */
.bottom-player {
  width: 100%;
  background-color: #2d3748;
  color: white;
  padding: 15px 10px;
  position: fixed;
  bottom: 0;
  left: 0;
  height: 80px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.player-row {
  display: flex;
  align-items: center;
  width: 100%;
  gap: 20px;
}
.player-controls {
  display: flex;
  align-items: center;
  gap: 15px;
  width: 250px;
}

.control-btn {
  background: none;
  border: none;
  color: #cbd5e0;
  font-size: 1.2rem;
  cursor: pointer;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s;
}

.control-btn:hover {
  color: white;
  background-color: rgba(255, 255, 255, 0.2);
}

.play-pause-btn {
  background-color: white;
  color: #2d3748;
  width: 50px;
  height: 50px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  transition: transform 0.2s;
}

.play-pause-btn:hover {
  transform: scale(1.05);
}

.progress-container {
  display: flex;
  align-items: center;
  gap: 15px;
  flex: 1;
  min-width: 300px;
}

.time-display {
  color: #cbd5e0;
  font-size: 0.85rem;
  width: 50px;
  text-align: center;
}

.progress-bar {
  flex: 1;
  height: 4px;
  -webkit-appearance: none;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 2px;
  outline: none;
}

.progress-bar::-webkit-slider-thumb {
  -webkit-appearance: none;
  width: 16px;
  height: 16px;
  border-radius: 50%;
  background: white;
  cursor: pointer;
  transition: all 0.2s;
}

.progress-bar::-webkit-slider-thumb:hover {
  transform: scale(1.2);
}

.volume-control {
  display: flex;
  align-items: center;
  gap: 10px;
  width: 150px;
  padding: 5px 10px;
  position: relative;
}

.volume-slider {
  flex: 1;
  height: 4px;
  -webkit-appearance: none;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 2px;
  outline: none;
}
.volume-icon-container {
  color: #cbd5e0;
  cursor: pointer;
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: color 0.3s;
}

.volume-icon-container:hover {
  color: white;
}
/* 静音状态动画 */
.muted-pulse {
  animation: pulse 1.5s infinite;
}

@keyframes pulse {
  0% { opacity: 1; }
  50% { opacity: 0.6; }
  100% { opacity: 1; }
}

/* 音量滑块容器 */
.volume-slider-wrapper {
  width: 0;
  overflow: hidden;
  transition: width 0.3s ease;
}

/* 鼠标悬停时显示滑块 */
.volume-control:hover .volume-slider-wrapper {
  width: 100px;
}

.volume-slider {
  width: 100px;
  height: 4px;
  -webkit-appearance: none;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 2px;
  outline: none;
  transition: background 0.3s;
}

.volume-slider:hover {
  background: rgba(255, 255, 255, 0.3);
}

.volume-slider::-webkit-slider-thumb {
  -webkit-appearance: none;
  width: 14px;
  height: 14px;
  border-radius: 50%;
  background: white;
  cursor: pointer;
  transition: transform 0.2s;
}

.volume-slider::-webkit-slider-thumb:hover {
  transform: scale(1.2);
}

/* 滚动条美化 */
::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

::-webkit-scrollbar-track {
  background: #f1f1f1;
}

::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

.dialog-header {
  display: flex;
  flex-direction: row;
  justify-content: flex-end;
  gap: 16px;
  padding: 5px 0;
}
.info-input {
  background-color: #f9f9f9;
  cursor: default;
}

.no-selection {
  text-align: center;
  padding: 30px 0;
}
.song-info-dialog {
  --el-dialog-bg-color: transparent;
  background-color: transparent !important;
}

/* 无歌曲选择时的提示样式 */
.no-selection {
  text-align: center;
  padding: 30px 0;
  color: #333;
}
</style>

