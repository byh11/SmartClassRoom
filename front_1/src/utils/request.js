import {ElMessage} from 'element-plus'

// 统一响应处理
export const handleResponse = (response) => {
    if (response.data.code === 200) {
        return response.data
    } else {
        // 显示错误信息
        ElMessage.error(response.data.message || '操作失败')
        return Promise.reject(response.data)
    }
}

// 统一错误处理
export const handleError = (error) => {
    console.error('请求失败:', error)
    ElMessage.error(error.message || '请求失败')
    return Promise.reject(error)
} 