import store from '@/store'
import {ElMessage} from 'element-plus'

export function setupRouteGuards(router) {
    router.beforeEach((to, from, next) => {
        const requiresAuth = to.matched.some(record => record.meta.requiresAuth)
        const requiresTeacher = to.matched.some(record => record.meta.requiresTeacher)

        if (requiresAuth && !store.state.user.isLoggedIn) {
            ElMessage.warning('请先登录')
            next({
                path: '/login',
                query: {redirect: to.fullPath}
            })
            return
        }

        if (requiresTeacher && store.state.user.role !== 'teacher') {
            ElMessage.error('无权访问')
            next('/')
            return
        }

        next()
    })
} 