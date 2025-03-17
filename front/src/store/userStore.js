import {defineStore} from 'pinia';

export const useUserStore = defineStore('user', {
    state: () => ({
        user: null,
        videos: [], // 用于存储用户的视频
    }),
    actions: {
        login(userInfo) {
            this.user = userInfo; // 存储用户信息
        },
        logout() {
            this.user = null; // 清除用户信息
            this.videos = []; // 清除用户视频
        },
        addVideo(video) {
            this.videos.push(video); // 添加视频
        },
    },
});
