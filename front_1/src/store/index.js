import {createStore} from 'vuex'

// 从 localStorage 获取用户信息
const savedUser = JSON.parse(localStorage.getItem('userInfo')) || {
    isLoggedIn: false,
    role: '',
    studentid: '',
    teacherid: '',
    name: '',
    phone: '',
    email: '',
    birthday: '',
    sex: '',
    clazz: '',
    major: '',
    college: ''
}

export default createStore({
    state: {
        user: {
            isLoggedIn: false,
            role: '',
            studentid: '',  // 学生ID
            teacherid: '',  // 教师ID
            // ... 其他用户信息
        }
    },
    mutations: {
        setUser(state, userData) {
            state.user = {
                ...userData,
                isLoggedIn: true
            }
            // 保存到 localStorage
            localStorage.setItem('userInfo', JSON.stringify(state.user))
        },
        clearUser(state) {
            state.user = {
                isLoggedIn: false,
                role: '',
                studentid: '',
                teacherid: ''
            }
            localStorage.removeItem('userInfo')
        }
    },
    actions: {
        async login({commit}, credentials) {
            try {
                // 模拟登录API调用
                const user = {
                    id: '1',
                    name: credentials.userId,
                    role: credentials.userType,
                    isLoggedIn: true
                }
                commit('setUser', {...user})
                return Promise.resolve(user)
            } catch (error) {
                return Promise.reject(error)
            }
        },
        async logout({commit}) {
            try {
                commit('clearUser')
                return Promise.resolve()
            } catch (error) {
                return Promise.reject(error)
            }
        }
    }
}) 