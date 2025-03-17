import vue from '@vitejs/plugin-vue'


// https://vitejs.dev/config/
export default {
    plugins: [vue()],
    server: {
        proxy: {
            '/student': {
                target: 'http://localhost:12000', // 后端服务器地址
                changeOrigin: true,
            },
            '/teacher': {
                target: 'http://localhost:12000',
                changeOrigin: true,
            },
            '/video': {
                target: 'http://localhost:12000',
                changeOrigin: true,
            },
        },
    },
};



