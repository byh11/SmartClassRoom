import dayjs from 'dayjs'

export const formatTime = (time) => {
    return dayjs(time).format('YYYY-MM-DD HH:mm:ss')
}

export const formatDuration = (seconds) => {
    const hours = Math.floor(seconds / 3600)
    const minutes = Math.floor((seconds % 3600) / 60)
    const remainingSeconds = seconds % 60

    if (hours > 0) {
        return `${hours}:${padZero(minutes)}:${padZero(remainingSeconds)}`
    }
    return `${minutes}:${padZero(remainingSeconds)}`
}

export const formatNumber = (num) => {
    if (num >= 10000) {
        return (num / 10000).toFixed(1) + '万'
    }
    return num.toString()
}

const padZero = (num) => {
    return num.toString().padStart(2, '0')
} 