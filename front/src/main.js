import {createApp} from 'vue';
import App from './App.vue';
import {createPinia} from 'pinia';
import ElementPlus from 'element-plus';
import router from './router';
import 'element-plus/dist/index.css';
import './style.css';
// import Particles from "particles.vue3"; 
// app.use(Particles)
// app.mount('#app')

const app = createApp(App);
const pinia = createPinia();

app.use(pinia);
app.use(ElementPlus);
app.use(router);
// app.use(VueParticles)
app.mount('#app');
