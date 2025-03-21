import {createRouter, createWebHistory} from 'vue-router';
import Home from '../components/Home.vue';
import Login from '../components/Login.vue';
import Register from '../components/Register.vue';
import TeacherDashboard from '../components/TeacherDashboard.vue';
import StudentHome from '../components/StudentHome.vue';

const routes = [
    {
        path: '/',
        name: 'Home',
        component: Home
    },
    {
        path: '/login',
        name: 'Login',
        component: Login
    },
    {
        path: '/register',
        name: 'Register',
        component: Register
    },
    {
        path: '/teacher',
        component: TeacherDashboard,
        children: [
            {
                path: '',
                redirect: '/teacher/courses'
            },
            {
                path: 'courses',
                name: 'TeacherCourses',
                component: () => import('../views/teacher/Courses.vue')
            },
            {
                path: 'upload',
                name: 'TeacherUpload',
                component: () => import('../views/teacher/Upload.vue')
            },
            {
                path: 'profile',
                name: 'TeacherProfile',
                component: () => import('../views/teacher/Profile.vue')
            }
        ]
    },
    {
        path: '/student',
        component: StudentHome,
        children: [
            {
                path: '',
                redirect: '/student/videos'
            },
            {
                path: 'videos',
                name: 'StudentVideos',
                component: () => import('../views/student/Videos.vue')
            },
            {
                path: 'profile',
                name: 'StudentProfile',
                component: () => import('../views/student/Profile.vue')
            }
        ]
    }
];

const router = createRouter({
    history: createWebHistory(),
    routes,
});

export default router;
