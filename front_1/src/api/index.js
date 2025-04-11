import axios from 'axios'
import {handleError, handleResponse} from '@/utils/request'
import JSONBig from 'json-bigint'

// 创建默认 axios 实例
const instance = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL,
    timeout: 5000,
    headers: {
        'Content-Type': 'application/json'
    },
    transformResponse: [function (data) {
        try {
            return JSONBig.parse(data)
        } catch (err) {
            return data
        }
    }]
})

// 创建AI接口专用的axios实例
const aiInstance = axios.create({
    baseURL: import.meta.env.VITE_AI_API_BASE_URL,
    timeout: 150000, // AI接口可能需要更长的超时时间
    headers: {
        'Content-Type': 'application/json'
    },
    withCredentials: true
})

// 请求拦截器
instance.interceptors.request.use(
    config => {
        const token = localStorage.getItem('token')
        if (token) {
            config.headers.Authorization = `Bearer ${token}`
        }
        return config
    },
    error => Promise.reject(error)
)

// AI实例的请求拦截器
aiInstance.interceptors.request.use(
    config => {
        const token = localStorage.getItem('token')
        if (token) {
            config.headers.Authorization = `Bearer ${token}`
        }
        return config
    },
    error => Promise.reject(error)
)

// 模拟数据
const mockData = {
    videos: Array(20).fill(null).map((_, index) => ({
        id: index + 1,
        title: `示例视频 ${index + 1}`,
        coverUrl: 'https://via.placeholder.com/300x200',
        duration: 300,
        views: 1000,
        likes: 100,
        comments: 50,
        teacher: {
            name: '示例教师',
            avatar: 'https://via.placeholder.com/40'
        }
    }))
}

// API 接口集合
const api = {
    // =============== 学生认证相关 ===============

    // 学生登录
    studentLogin: (data) => instance.post('/student/login', null, {
        params: {
            studentid: data.username,
            password: data.password
        }
    }),

    // 学生注册
    studentRegister: async (data) => {
        try {
            const response = await instance.post('/student/register', data)
            return handleResponse(response)
        } catch (error) {
            return handleError(error)
        }
    },

    // 获取学生信息
    getStudentInfo: (studentid) => instance.get(`/student/${studentid}/info`),

    // 更新学生信息
    updateStudentInfo: (studentid, data) => instance.post(`/student/${studentid}/info`, data),

    // 学生修改密码
    studentChangePassword: (studentid, data) => instance.post(`/student/${studentid}/change-password`, {
        oldPassword: data.oldPassword,
        newPassword: data.newPassword
    }),

    // =============== 教师认证相关 ===============

    // 教师登录
    teacherLogin: (data) => instance.post('/teacher/login', null, {
        params: {
            teacherid: data.username,
            password: data.password
        }
    }),

    // 教师注册
    teacherRegister: async (data) => {
        try {
            const response = await instance.post('/teacher/register', data)
            return handleResponse(response)
        } catch (error) {
            return handleError(error)
        }
    },

    // 获取教师信息
    getTeacherInfo: (teacherid) => instance.get(`/teacher/${teacherid}/info`),

    // 更新教师信息
    updateTeacherInfo: (teacherid, data) => instance.post(`/teacher/${teacherid}/info`, data),

    // 教师修改密码
    teacherChangePassword: (teacherid, data) => instance.post(`/teacher/${teacherid}/change-password`, {
        oldPassword: data.oldPassword,
        newPassword: data.newPassword
    }),

    // 通用登出接口
    logout: () => instance.post('/auth/logout'),

    // =============== 视频相关 ===============

    // 获取视频列表
    getVideoList: (params) => instance.get('/video/SelectVideoAll', {params}),

    // 获取视频详情
    getVideoDetail: (id) => instance.get(`/video/${id}`),

    // 获取视频列表（教师）
    getTeacherVideos: (teacherid) => instance.get(`/video/SelectVideoByTeacher/${teacherid}`, null),

    // 上传视频
    uploadVideo: (teacherid, data) => instance.post(`/video/${teacherid}/upload`, data, {
        headers: {'Content-Type': 'multipart/form-data'}
    }),

    // 更新视频信息
    updateVideo: (id, data) => instance.post(`/video/update/${id}`, data),

    // 删除视频
    deleteVideo: (teacherid, videoid) => instance.delete(`/teacher/${teacherid}/video/${videoid}`),

    // 获取视频下载链接
    getVideoDownloadUrl: (id) => instance.get(`/video/${id}/download`),

    // 视频点赞
    likeVideo: (userid, videoid, userType) => instance.post(`/video/${userid}/like/${videoid}/${userType}`),

    // 取消点赞
    unlikeVideo: (userid, videoid, userType) => instance.post(`/video/${userid}/unlike/${videoid}/${userType}`),

    // 收藏视频
    collectVideo: (userid, videoid, userType) => instance.post(`/video/${userid}/collections/${videoid}/${userType}`),

    // 取消收藏
    uncollectVideo: (userid, videoid, userType) => instance.post(`/video/${userid}/uncollections/${videoid}/${userType}`),

    // 获取视频点赞和收藏状态
    getVideoStatus: (userid, videoid, userType) => instance.post(`/video/${userid}/status/${videoid}/${userType}`),

    // =============== 评论相关 ===============

    // 获取视频评论
    getComments: (videoid) => instance.get(`/video/${videoid}/comments`),

    // 学生发表评论
    addComment: (studentid, videoid, data, userType) => instance.post(`/video/${studentid}/comments/${videoid}/${userType}`, data, {
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        }
    }),

    // 评论回复相关接口
    addReply: (userId, videoid, parentId, content, userType) => instance.post(`/video/${userId}/reply/${videoid}/${userType}`, {
        content,
        parentId
    }, {
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        }
    }),

    getReplies: (commentId) => instance.get(`/video/comments/${commentId}/replies`),

    // 学生删除评论
    deleteComment: (commentid) => instance.post(`/video/comments/del/${commentid}`),

    // =============== 弹幕相关 ===============

    // 获取视频弹幕
    getDanmaku: (videoid) => instance.get(`/api/video/${videoid}/danmaku`),

    // 发送弹幕
    sendDanmaku: (videoid, data) => instance.post(`/api/video/${videoid}/danmaku`, data),

    // =============== 课程相关 ===============

    // 开始上课
    startClass: (teacherid, className) => instance.post(`/teacher/${teacherid}/class/start`, null, {
        params: {
            className: className
        }
    }),

    // 结束上课
    endClass: (teacherId, formData) => instance.post(`/teacher/${teacherId}/class/end`, formData, {
        headers: {
            'Content-Type': 'multipart/form-data',
            'X-Requested-With': 'XMLHttpRequest'
        },
        timeout: 30000 // 上传超时延长至30秒
    }),

    // 获取课程记录
    getClassRecords: (teacherid, pageNumber) => instance.post(`/teacher/${teacherid}/class/records`, null, {
        params: {
            pageNumber: pageNumber
        }
    }),

    // 获取课程回放
    getClassRecording: (id) => instance.get(`/api/class/recordings/${id}`),

    // 获取学生观看记录
    getStudentWatchHistory: (studentid, params) => instance.get(`/student/${studentid}/watch-history`, {params}),

    // =============== 通知相关 ===============

    // 获取系统通知
    getSystemNotifications: () => instance.get('/api/notifications/system'),

    // 获取课程通知
    getCourseNotifications: () => instance.get('/api/notifications/course'),

    // 标记通知已读
    markNotificationAsRead: (id) => instance.post(`/api/notifications/${id}/read`),

    // 标记所有通知已读
    markAllNotificationsAsRead: () => instance.post('/api/notifications/read-all'),

    // =============== 搜索相关 ===============

    // 获取搜索建议
    getSearchSuggestions: (query) => instance.get('/api/search/suggestions', {params: {q: query}}),

    // 搜索视频
    searchVideos: (params) => instance.get('/api/search/video', {params}),

    // =============== 首页相关 ===============

    // 获取首页轮播图
    getCarouselData: () => instance.get('/api/home/carousel'),

    // 获取校园资讯
    getNewsList: () => instance.get('/api/home/news'),

    // 获取最新技术
    getTechList: () => instance.get('/api/home/tech'),

    // 获取热门视频
    getPopularVideos: () => instance.get('/api/home/popular-video'),

    // 获取学生收藏列表
    getStudentCollections: (studentid, params) => instance.get(`/student/${studentid}/collections`, {params}),

    // 添加播放记录
    addPlayRecord: (videoid) => instance.post(`/video/play/${videoid}`, null),

    // 头像相关接口
    updateStudentAvatar: (studentid, file) => {
        const formData = new FormData()
        formData.append('file', file)
        return instance.post(`/student/${studentid}/avatar`, formData, {
            headers: {
                'Content-Type': 'multipart/form-data'
            }
        })
    },

    updateTeacherAvatar: (teacherid, file) => {
        const formData = new FormData()
        formData.append('file', file)
        return instance.post(`/teacher/${teacherid}/avatar`, formData, {
            headers: {
                'Content-Type': 'multipart/form-data'
            }
        })
    },

    // AI聊天接口
    chatWithAI: (message, user) => aiInstance.get('/chat', {
        params: {
            input: message,
            user: user
        },
        responseType: 'text',
        headers: {
            'Accept': 'text/event-stream',
            'Cache-Control': 'no-cache'
        },
        withCredentials: true
    }),
}

export default api 