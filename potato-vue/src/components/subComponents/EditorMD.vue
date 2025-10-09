<template>
  <MdEditor v-model="internalValue"
            :disabled="disabled"
            :height="height"
            :autofocus="autofocus" />
</template>

<script setup>
import {ref, watch} from 'vue';
import {MdEditor} from 'md-editor-v3';
import 'md-editor-v3/lib/style.css';

// 定义组件 Props
const props = defineProps({
  modelValue: { type: String, default: ''},
  disabled: { type: Boolean, default: false },
  height: { type: String, default: '500px' },
  autofocus: { type: Boolean, default: false }
})

const emits = defineEmits(['update: modelValue', 'change']);

// 监听外部 modelValue 变化
const internalValue = ref(props.modelValue);

// 监听外部 modelValue 变化
watch(
    () => props.modelValue,
    (newVal) => { internalValue.value = newVal },
    { immediate: true }
)

// 监听内部值变化
watch(
    internalValue,
    (newVal) => {
      emits('update:modelValue', newVal);  // 触发 v-model 双向绑定
      emits('change', newVal);             // 可选：触发 change 事件供父组件使用
    }
);
</script>
