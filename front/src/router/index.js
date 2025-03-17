import {createRouter, createWebHistory} from 'vue-router';
import Login from '../components/Login.vue';
import Register from '../components/Register.vue';
import TeacherDashboard from '../components/TeacherDashboard.vue';
import StudentHome from '../components/StudentHome.vue';

const routes = [
    {path: '/', component: Login},
    {path: '/register', component: Register},
    {path: '/teacher', component: TeacherDashboard},
    {path: '/student', component: StudentHome},
];

const router = createRouter({
    history: createWebHistory(),
    routes,
});

export default router;
