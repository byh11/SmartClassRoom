import {createApp} from 'vue'
import ElementPlus from 'element-plus'
import * as ElementPlusIcons from '@element-plus/icons-vue'
import 'element-plus/dist/index.css'
import App from './App.vue'
import router from './router'
import store from './store'
import {setupRouteGuards} from './router/guard'

const app = createApp(App)

// 注册所有图标
for (const [key, component] of Object.entries(ElementPlusIcons)) {
    app.component(key, component)
}

app.use(ElementPlus)
app.use(router)
app.use(store)

// 初始化用户信息
const savedUser = localStorage.getItem('user')
if (savedUser) {
    store.commit('setUser', JSON.parse(savedUser))
}

setupRouteGuards(router)

app.mount('#app') 