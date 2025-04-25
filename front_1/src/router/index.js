import {createRouter, createWebHistory} from 'vue-router'
import {useStore} from 'vuex'
import VideoDetail from '@/views/VideoDetail.vue'

const routes = [
    {
        path: '/',
        name: 'Home',
        component: () => import('@/views/Home.vue')
    },
    {
        path: '/login',
        name: 'Login',
        component: () => import('@/views/Login.vue')
    },
    {
        path: '/register',
        name: 'Register',
        component: () => import('@/views/Register.vue')
    },
    {
        path: '/video/:id',
        name: 'VideoDetail',
        component: VideoDetail,
        meta: {requiresAuth: true}
    },
    {
        path: '/profile',
        name: 'Profile',
        component: () => import('@/views/Profile.vue'),
        children: [
            {
                path: 'student',
                name: 'StudentProfile',
                component: () => import('@/views/profile/StudentProfile.vue'),
                meta: {requiresAuth: true, role: 'student'}
            },
            {
                path: 'teacher',
                name: 'TeacherProfile',
                component: () => import('@/views/profile/TeacherProfile.vue'),
                meta: {requiresAuth: true, role: 'teacher'}
            }
        ]
    },
    {
        path: '/teacher/videos',
        name: 'TeacherVideos',
        component: () => import('@/views/teacher/VideoManagement.vue'),
        meta: {requiresAuth: true, role: 'teacher'}
    },
    {
        path: '/teacher/class',
        name: 'TeacherClass',
        component: () => import('@/views/teacher/ClassManagement.vue'),
        meta: {requiresAuth: true, role: 'teacher'}
    },
    {
        path: '/video',
        name: 'VideoList',
        component: () => import('@/views/VideoList.vue')
    },
    {
        path: '/learning/stats',
        name: 'LearningStats',
        component: () => import('@/views/LearningStats.vue'),
        meta: {requiresAuth: true, role: 'student'}
    },
    {
        path: '/learning/report',
        name: 'LearningReport',
        component: () => import('@/views/LearningReport.vue'),
        meta: {requiresAuth: true, role: 'student'}
    },
    {
        path: '/liked-videos',
        name: 'LikedVideos',
        component: () => import('@/views/LikedVideos.vue'),
        meta: {requiresAuth: true}
    },
    {
        path: '/collected-videos',
        name: 'CollectedVideos',
        component: () => import('@/views/CollectedVideos.vue'),
        meta: {requiresAuth: true}
    },
    // 404 页面
    {
        path: '/:pathMatch(.*)*',
        name: 'NotFound',
        component: () => import('@/views/NotFound.vue')
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
    const store = useStore()
    const userRole = store.state.user.role
    const requiresAuth = to.matched.some(record => record.meta.requiresAuth)
    const requiredRole = to.meta.role

    if (requiresAuth && !store.state.user.isLoggedIn) {
        next('/login')
    } else if (requiredRole && requiredRole !== userRole) {
        next('/')
    } else {
        next()
    }
})

export default router 