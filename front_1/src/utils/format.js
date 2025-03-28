import dayjs from 'dayjs'

export const formatTime = (time) => {
    return dayjs(time).format('YYYY-MM-DD HH:mm:ss')
}

/**
 * 格式化时长
 * @param {number} seconds 秒数
 * @returns {string} 格式化后的时长
 */
export const formatDuration = (seconds) => {
    if (!seconds && seconds !== 0) return '00:00'
    
    const hours = Math.floor(seconds / 3600)
    const minutes = Math.floor((seconds % 3600) / 60)
    const remainingSeconds = seconds % 60

    if (hours > 0) {
        return `${hours}:${minutes.toString().padStart(2, '0')}:${remainingSeconds.toString().padStart(2, '0')}`
    }
    return `${minutes}:${remainingSeconds.toString().padStart(2, '0')}`
}

/**
 * 格式化数字
 * @param {number} num 数字
 * @returns {string} 格式化后的数字
 */
export const formatNumber = (num) => {
    if (!num && num !== 0) return '0'
    
    if (num >= 10000) {
        return (num / 10000).toFixed(1) + 'w'
    }
    if (num >= 1000) {
        return (num / 1000).toFixed(1) + 'k'
    }
    return num.toString()
}

const padZero = (num) => {
    return num.toString().padStart(2, '0')
} 