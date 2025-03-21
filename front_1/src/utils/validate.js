export const validatePhone = (rule, value, callback) => {
    if (!value) {
        callback(new Error('请输入手机号码'))
    } else if (!/^1[3-9]\d{9}$/.test(value)) {
        callback(new Error('请输入正确的手机号码'))
    } else {
        callback()
    }
}

export const validateEmail = (rule, value, callback) => {
    if (!value) {
        callback(new Error('请输入邮箱'))
    } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value)) {
        callback(new Error('请输入正确的邮箱地址'))
    } else {
        callback()
    }
}

export const validatePassword = (rule, value, callback) => {
    if (!value) {
        callback(new Error('请输入密码'))
    } else if (value.length < 6) {
        callback(new Error('密码长度不能小于6位'))
    } else {
        callback()
    }
} 