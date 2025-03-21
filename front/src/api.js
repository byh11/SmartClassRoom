import axios from 'axios';

export const mockApi = {

    // 模拟学生注册
    async studentRegister(student) {
        try {
            const response = await axios.post(
                'http://10.0.220.99:12000/student/register',
                student,
                {
                    headers: {
                        'Content-Type': 'application/json', // 显式设置 Content-Type（可选）
                    },
                }
            );
            return response.data; // 返回后端响应数据
        } catch (error) {
            console.error('注册失败:', error);
            throw error;
        }
    },

    // 模拟学生登录
    async studentLogin(studentId, password) {
        try {
            const params = new URLSearchParams({
                studentid: studentId,
                password: password
            });
            const response = await axios.post(
                'http://10.0.220.99:12000/student/login',
                params
            );
            return response.data; // 返回后端响应数据
        } catch (error) {
            console.error('登录失败:', error);
            throw error;
        }
    },

    // 模拟教师注册
    async teacherRegister(teacher) {
        try {
            const response = await axios.post(
                'http://10.0.220.99:12000/teacher/register',
                teacher,
                {
                    headers: {
                        'Content-Type': 'application/json', // 显式设置 Content-Type（可选）
                    },
                }
            );
            return response.data; // 返回后端响应数据
        } catch (error) {
            console.error('注册失败:', error);
            throw error;
        }
    },

    // 模拟教师登录
    async teacherLogin(teacherId, password) {
        try {
            const params = new URLSearchParams({
                teacherid: teacherId,
                password: password
            });
            const response = await axios.post(
                'http://10.0.220.99:12000/teacher/login',
                params
            );
            return response.data; // 返回后端响应数据
        } catch (error) {
            console.error('登录失败:', error);
            throw error;
        }
    },

    // 教师上课
    teacherAttendClazz(teacherId, clazzName) {
        return new Promise((resolve) => {
            setTimeout(() => {
                resolve({code: 200, message: `教师 ${teacherId} 上课成功，班级名称: ${clazzName}`, data: null});
            }, 1000);
        });
    },

    // 教师下课
    teacherFinishClazz(teacherId) {
        return new Promise((resolve) => {
            setTimeout(() => {
                resolve({code: 200, message: `教师 ${teacherId} 下课成功`, data: {teacherId}});
            }, 1000);
        });
    },

    // 查询视频列表
    selectVideoAll(pageSize, pageNumber) {
        return new Promise((resolve) => {
            setTimeout(() => {
                const videos = [
                    {
                        id: 1,
                        teacherid: 'teacher123',
                        url: 'https://www.w3schools.com/html/mov_bbb.mp4',
                        videoname: 'JavaScript 入门'
                    },
                    {
                        id: 2,
                        teacherid: 'teacher123',
                        url: 'https://www.w3schools.com/html/mov_bbb.mp4',
                        videoname: 'Vue.js 教程'
                    },
                    {
                        id: 3,
                        teacherid: 'teacher123',
                        url: 'https://www.w3schools.com/html/mov_bbb.mp4',
                        videoname: '前端开发基础'
                    },
                    {id: 4, teacherid: 't002', url: 'https://www.example.com/video3.mp4', videoname: 'CSS 样式设计'},
                    {
                        id: 5,
                        teacherid: 't002',
                        url: 'https://www.example.com/video4.mp4',
                        videoname: 'React.js 高级技巧'
                    },
                    {id: 6, teacherid: 't003', url: 'https://www.example.com/video5.mp4', videoname: 'Python 数据分析'},
                    {id: 7, teacherid: 't003', url: 'https://www.example.com/video6.mp4', videoname: '机器学习入门'},
                    {
                        id: 8,
                        teacherid: 'teacher123',
                        url: 'https://www.example.com/video7.mp4',
                        videoname: 'Node.js 实战'
                    },
                    {id: 9, teacherid: 't002', url: 'https://www.example.com/video8.mp4', videoname: '全栈开发概论'},
                    {id: 10, teacherid: 't003', url: 'https://www.example.com/video9.mp4', videoname: '数据结构与算法'},
                ];
                const paginatedVideos = videos.slice((pageNumber - 1) * pageSize, pageNumber * pageSize);
                resolve({code: 200, message: '查询成功', data: paginatedVideos});
            }, 1000);
        });
    },

    // 上传视频
    uploadVideo(file, teacherId) {
        const formData = new FormData();
        formData.append('file', file);
        formData.append('teacherId', teacherId);

        return new Promise((resolve) => {
            setTimeout(() => {
                resolve({code: 200, message: `教师 ${teacherId} 视频保存成功`, data: null});
            }, 1000);
        });
    },

    // 查询视频
    selectVideo(videoName) {
        return new Promise((resolve) => {
            setTimeout(() => {
                const videos = [
                    {
                        id: 1,
                        title: 'JavaScript 入门',
                        teacherid: 'teacher123',
                        url: 'https://www.w3schools.com/html/mov_bbb.mp4'
                    },
                    {id: 2, title: 'Vue.js 教程', teacherid: 't001', url: 'https://www.w3schools.com/html/mov_bbb.mp4'},
                    {id: 3, title: 'CSS 样式设计', teacherid: 't002', url: 'https://www.example.com/video3.mp4'},
                    {id: 4, title: 'React.js 高级技巧', teacherid: 't002', url: 'https://www.example.com/video4.mp4'},
                    {id: 5, title: 'Python 数据分析', teacherid: 't003', url: 'https://www.example.com/video5.mp4'},
                    {id: 6, title: '机器学习入门', teacherid: 't003', url: 'https://www.example.com/video6.mp4'},
                ];

                const filteredVideos = videos.filter(video => video.title.includes(videoName));
                resolve({code: 200, message: '查询成功', data: filteredVideos});
            }, 1000);
        });
    },

    // 删除视频
    deleteVideo(id) {
        return new Promise((resolve) => {
            setTimeout(() => {
                resolve({code: 200, message: `视频 ID: ${id} 删除成功`, data: null});
            }, 1000);
        });
    },

    // API接口路径及请求类型
    routes: {
        studentRegister: {path: '/student/register', method: 'POST'},
        studentLogin: {path: '/student/login', method: 'POST'},
        teacherRegister: {path: '/teacher/register', method: 'POST'},
        teacherLogin: {path: '/teacher/login', method: 'POST'},
        teacherAttendClazz: {path: '/teacher/AttendClazz', method: 'POST'},
        teacherFinishClazz: {path: '/teacher/FinishClazz', method: 'POST'},
        selectVideoAll: {path: '/video/SelectVideoAll', method: 'POST'},
        uploadVideo: {path: '/video/UploadVideo', method: 'POST'},
        selectVideo: {path: '/video/SelectVideo', method: 'POST'},
        deleteVideo: {path: '/video/DeleteVideo', method: 'POST'},
    },
};
