package myself.potato.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import jakarta.annotation.Resource;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;

import myself.potato.common.Result;
import myself.potato.entity.Files;
import myself.potato.mapper.FileMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/file")
public class FileController {
    private static final Logger log = LoggerFactory.getLogger(FileController.class);

    @Value("${files.upload.path}")
    private String fileUploadPath;

    @Value("${fileUrl.upload.address}")
    private String fileBaseUrl;

    @Resource
    private FileMapper fileMapper;

    @PostMapping("/upload")
    public String upload(@RequestParam MultipartFile file) throws IOException {
        // 日志：记录上传开始
        log.info("开始上传文件，原始文件名：{}", file.getOriginalFilename());

        String originalFilename = file.getOriginalFilename();
        String type = FileUtil.extName(originalFilename);
        long size = file.getSize();

        // 生成唯一文件标识
        String UUID = IdUtil.fastSimpleUUID();
        String fileUUID = UUID + StrUtil.DOT + type;

        // 优化路径拼接（使用File构造方法自动处理分隔符）
        File uploadFile = new File(fileUploadPath, fileUUID);

        // 确保上传目录存在
        File parentDir = uploadFile.getParentFile();
        if (!parentDir.exists()) {
            boolean dirsCreated = parentDir.mkdirs();
            if (!dirsCreated) {
                log.error("无法创建上传目录：{}", parentDir.getAbsolutePath());
                throw new IOException("无法创建目录: " + parentDir.getAbsolutePath());
            }
            log.info("已创建上传目录：{}", parentDir.getAbsolutePath());
        }

        String url;
        try {
            // 上传文件到磁盘
            file.transferTo(uploadFile);
            log.info("文件已保存到磁盘，路径：{}", uploadFile.getAbsolutePath());

            // 计算文件MD5（用于去重）
            String md5 = SecureUtil.md5(uploadFile);
            log.debug("文件MD5：{}", md5);

            // 检查是否存在重复文件
            Files dbFiles = getFileByMd5(md5);
            if (dbFiles != null) {
                url = dbFiles.getUrl();
                // 删除重复文件
                if (uploadFile.delete()) {
                    log.info("发现重复文件，已删除：{}，复用URL：{}", uploadFile.getName(), url);
                } else {
                    log.warn("重复文件删除失败：{}", uploadFile.getName());
                }
            } else {
                // 生成公网访问URL
                url = fileBaseUrl + "/file/" + fileUUID;
                // 保存到数据库
                Files saveFile = new Files();
                saveFile.setName(originalFilename);
                saveFile.setType(type);
                saveFile.setSize(size);
                saveFile.setUrl(url);
                saveFile.setMd5(md5);
                saveFile.setIs_delete(false); // 显式设置未删除
                fileMapper.insert(saveFile);
                log.info("文件上传成功，数据库ID：{}，访问URL：{}", saveFile.getId(), url);
            }
            return url;
        } catch (IOException e) {
            log.error("文件上传失败", e);
            throw e; // 抛出异常让前端捕获
        }
    }

    /**
     * 通过MD5查询文件（用于去重）
     */
    private Files getFileByMd5(String md5) {
        QueryWrapper<Files> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("md5", md5)
                .eq("is_delete", false); // 只查询未删除的文件
        List<Files> filesList = fileMapper.selectList(queryWrapper);
        return filesList.isEmpty() ? null : filesList.get(0);
    }

    @PostMapping("/update")
    public Result save(@RequestBody Files files) {
        log.info("更新文件信息，ID：{}", files.getId());
        int rows = fileMapper.updateById(files);
        return Result.success(rows);
    }

    /**
     * 文件下载/预览接口
     * 支持图片预览（自动识别图片类型）和其他文件下载
     */
    @GetMapping("/{fileUUID}")
    public void download(@PathVariable String fileUUID, HttpServletResponse response) throws IOException {
        log.info("请求文件：{}", fileUUID);

        // 构建文件对象
        File uploadFile = new File(fileUploadPath, fileUUID);

        // 1. 检查文件是否存在
        if (!uploadFile.exists()) {
            log.error("文件不存在：{}", uploadFile.getAbsolutePath());
            response.setStatus(HttpServletResponse.SC_NOT_FOUND); // 返回404
            return;
        }

        // 2. 提取文件后缀，动态设置Content-Type
        String fileType = StrUtil.emptyToDefault(FileUtil.extName(fileUUID), "unknown").toLowerCase();
        ServletOutputStream os = response.getOutputStream();

        try {
            // 3. 根据文件类型设置响应头
            if (isImageType(fileType)) {
                // 图片类型：支持预览
                response.setContentType("image/" + (fileType.equals("jpg") ? "jpeg" : fileType));
                log.debug("图片预览，类型：{}", fileType);
            } else {
                // 非图片类型：强制下载
                response.setContentType("application/octet-stream");
                String fileName = URLEncoder.encode(fileUUID, StandardCharsets.UTF_8);
                response.addHeader("Content-Disposition", "attachment;filename=" + fileName);
                log.debug("文件下载，类型：{}", fileType);
            }

            // 4. 写入文件流
            os.write(FileUtil.readBytes(uploadFile));
            os.flush();
        } finally {
            os.close(); // 确保流关闭
        }
    }

    /**
     * 判断是否为图片类型
     */
    private boolean isImageType(String fileType) {
        List<String> imageTypes = List.of("jpg", "jpeg", "png", "gif", "bmp", "webp");
        return imageTypes.contains(fileType);
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        Files files = fileMapper.selectById(id);
        if (files == null) {
            log.warn("删除失败，文件ID不存在：{}", id);
            return Result.error("文件不存在");
        }
        files.setIs_delete(true);
        int rows = fileMapper.updateById(files);
        log.info("逻辑删除文件，ID：{}，影响行数：{}", id, rows);
        return Result.success(rows);
    }

    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        if (ids.isEmpty()) {
            return Result.success(0);
        }
        QueryWrapper<Files> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", ids);
        List<Files> filesList = fileMapper.selectList(queryWrapper);
        int count = 0;
        for (Files file : filesList) {
            file.setIs_delete(true);
            count += fileMapper.updateById(file);
        }
        log.info("批量逻辑删除文件，数量：{}，成功：{}", ids.size(), count);
        return Result.success(count);
    }

    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize,
                           @RequestParam(defaultValue = "") String name) {
        log.info("查询文件分页，页码：{}，每页大小：{}，名称关键词：{}", pageNum, pageSize, name);
        QueryWrapper<Files> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_delete", false)
                .orderByDesc("id");
        if (StrUtil.isNotBlank(name)) {
            queryWrapper.like("name", name);
        }
        Page<Files> page = fileMapper.selectPage(new Page<>(pageNum, pageSize), queryWrapper);
        return Result.success(page);
    }
}
