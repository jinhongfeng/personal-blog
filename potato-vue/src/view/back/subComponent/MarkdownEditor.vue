<template>
  <div class="markdown-editor-container">
    <!-- Markdown编辑器 -->
    <v-md-editor
        v-model="content"
        :disabled="props.disabled"
        :height="props.height"
        :autofocus="props.autofocus"
        @change="handleContentChange"
    />
  </div>
</template>

<script setup>
// 脚本部分保持不变
import {ref, defineProps, defineEmits, watch} from 'vue';
import githubTheme from '@kangc/v-md-editor/lib/theme/github.js';
import VMdEditor from '@kangc/v-md-editor';
import '@kangc/v-md-editor/lib/style/base-editor.css';
import '@kangc/v-md-editor/lib/theme/style/github.css';
import hljs from 'highlight.js';
import '@kangc/v-md-editor/lib/style/preview.css';

VMdEditor.use(githubTheme, {
  Hljs: hljs,
});

const props = defineProps({
  modelValue: { type: String, default: '' },
  disabled: { type: Boolean, default: false },
  height: { type: String, default: '500px' },
  autofocus: { type: Boolean, default: false },
});

const emit = defineEmits(['update:modelValue', 'change']);
const content = ref(String(props.modelValue));

const handleContentChange = (value) => {
  content.value = value;
  emit('update:modelValue', value);
  emit('change', value);
};

watch(() => props.modelValue, (newValue) => {
  content.value = String(newValue || '');
});
</script>

<style scoped>
/* 核心修复：强制容器及所有子元素左对齐，对抗全局居中 */
.markdown-editor-container {
  /* 提高优先级，确保覆盖全局样式 */
  text-align: left !important;
  width: 100%;
}

/* 穿透样式隔离，强制编辑器所有内部元素继承左对齐 */
::v-deep .markdown-editor-container * {
  text-align: inherit !important;
}

/* 编辑器基础样式 */
::v-deep .v-md-editor {
  border: 1px solid #ebeef5;
  border-radius: 4px;
  width: 100%;
  /* 确保编辑器本身不继承居中 */
  text-align: left !important;
}

::v-deep .v-md-editor-toolbar {
  border-bottom: 1px solid #ebeef5;
}

/* 编辑区和预览区强制左对齐 */
::v-deep .v-md-editor-content-wrapper,
::v-deep .v-md-previewer-wrapper,
::v-deep .v-md-editor-content,
::v-deep .v-md-previewer {
  text-align: left !important;
  min-height: 400px;
  padding: 16px;
}

/* 一级标题单独居中 */
::v-deep .v-md-editor-content h1,
::v-deep .v-md-previewer h1 {
  text-align: center !important;
  margin: 1.5em 0 !important;
}

/* 其他标题保持左对齐 */
::v-deep .v-md-editor-content h2,
::v-deep .v-md-editor-content h3,
::v-deep .v-md-editor-content h4,
::v-deep .v-md-editor-content h5,
::v-deep .v-md-editor-content h6,
::v-deep .v-md-previewer h2,
::v-deep .v-md-previewer h3,
::v-deep .v-md-previewer h4,
::v-deep .v-md-previewer h5,
::v-deep .v-md-previewer h6 {
  text-align: left !important;
  margin: 1em 0 !important;
}
</style>
