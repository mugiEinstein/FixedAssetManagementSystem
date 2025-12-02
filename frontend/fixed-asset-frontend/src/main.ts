import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

import App from './App.vue'
import router from './router'
import { setupPermissionDirective } from './directives/permission'

// 初始化深色模式
const isDark = localStorage.getItem('darkMode') === 'true'
if (isDark) {
  document.documentElement.classList.add('dark')
}

const app = createApp(App)

// 注册所有图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
}

// 注册 Pinia
app.use(createPinia())

// 注册路由
app.use(router)

// 注册 Element Plus
app.use(ElementPlus, { locale: zhCn })

// 注册权限指令
setupPermissionDirective(app)

app.mount('#app')