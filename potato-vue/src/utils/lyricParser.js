export const parseLrc = (lrcText) => {
    // 校验输入
    if (!lrcText || typeof lrcText !== 'string') {
        return [];
    }

    const lyricArray = [];
    // 匹配 LRC 时间戳的正则：[mm:ss.ms]
    const timeRegex = /\[(\d{2}):(\d{2})\.(\d{3})\]/;
    // 按行分割文本
    const lines = lrcText.split('\n');

    lines.forEach(line => {
        // 跳过空行
        if (!line.trim()) return;

        // 提取时间戳
        const timeMatch = line.match(timeRegex);
        if (!timeMatch) return; // 无时间戳的行跳过（如注释行）

        // 提取时间戳中的分、秒、毫秒
        const [, minute, second, millisecond] = timeMatch;
        // 转换为总毫秒数（用于同步播放进度）
        const time = parseInt(minute) * 60 * 1000 +
            parseInt(second) * 1000 +
            parseInt(millisecond);

        // 提取时间戳后面的歌词内容（去掉时间戳部分）
        const text = line.replace(timeRegex, '').trim();

        // 过滤元信息行（如“作词、作曲”等，只保留实际歌词）
        const isMetaLine = text.includes('作词') || text.includes('作曲') ||
            text.includes('编曲') || text.includes('制作人');
        if (!isMetaLine && text) {
            lyricArray.push({ time, text });
        }
    });

    // 按时间戳排序（确保歌词顺序正确）
    return lyricArray.sort((a, b) => a.time - b.time);
};
