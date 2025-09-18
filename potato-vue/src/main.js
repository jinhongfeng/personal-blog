import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import VueQuillEditor from 'vue-quill-editor';
import { FontAwesomeIcon } from '@/utils/fontawesome'

import request from '@/utils/request'

import '@/assets/globe.css'
import '@/assets/backGlobe.css'
import '@/assets/color.css'

// 编辑器样式
import  'quill/dist/quill.core.css';
import 'quill/dist/quill.snow.css';
import 'quill/dist/quill.bubble.css';

const app = createApp(App)

// 注册 Element Plus 图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
}

// 注册插件/库
app.use(router)
app.component('font-awesome-icon', FontAwesomeIcon)
app.use(ElementPlus)
app.use(VueQuillEditor)

app.config.globalProperties.$request = request

app.mount('#app')
