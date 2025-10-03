import dayjs from "dayjs";


/**
 * 默认图片，当图片地址错误时显示
 */
export const defaultImage = require('@/assets/defaultImage.jpeg')

/**
 * 用户默认头像
 */
export const defaultAvatar = require('@/assets/personAvatar.jpg')

/**
 * 工具类默认图片，当工具类图片失效或不存在时显示
 */
export const defaultToolImg = require('@/assets/tool/default.svg')

/**
 * 格式化时间显示，将时间显示转换成符合的样式
 */
export const formatTime = (timeStr) => {
    if (!timeStr) return '';
    return dayjs(timeStr).format('YYYY-MM-DD HH:mm');
};
