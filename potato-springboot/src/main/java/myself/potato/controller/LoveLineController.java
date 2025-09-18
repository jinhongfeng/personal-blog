package myself.potato.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import myself.potato.common.Constants;
import myself.potato.common.Result;
import myself.potato.entity.LoveLine;
import myself.potato.entity.LoveMessage;
import myself.potato.service.ILoveLineService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/loveline")
public class LoveLineController {


    @Autowired
    @Qualifier("lovelineServiceImpl")
    private ILoveLineService loveLineService;

    @GetMapping
    public Result findAll() {
        QueryWrapper<LoveLine> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        return Result.success(loveLineService.list(queryWrapper));
    }

    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        return Result.success(loveLineService.getById(id));
    }

    @GetMapping("/page")
    public Result findPage(@RequestParam(required = false) Integer pageNum,
                           @RequestParam(required = false) Integer pageSize,
                           @RequestParam(required = false) String content,
                           @RequestParam(required = false) String author,
                           @RequestParam(required = false) LocalDateTime publishTime) {

        IPage<LoveLine> page = new Page<>(pageNum, pageSize);
        QueryWrapper<LoveLine> queryWrapper = buildQueryWrapper(content, author, publishTime);

        IPage<LoveLine> result = loveLineService.page(page, queryWrapper);

        return Result.success(result);
    }
    private QueryWrapper<LoveLine> buildQueryWrapper(String content, String author, LocalDateTime publishTime) {
        QueryWrapper<LoveLine> queryWrapper = new QueryWrapper<>();

        if (content != null && !content.isEmpty()) {
            queryWrapper.like("content", content);
        }
        if (author != null && !author.isEmpty()) {
            queryWrapper.like("author", author);
        }
        if (publishTime != null) {
            queryWrapper.like("publishTime", publishTime);
        }
        queryWrapper.orderByDesc("id");
        return queryWrapper;
    }

    @PostMapping
    public Result save(@RequestBody LoveLine loveLine) {
        String content = loveLine.getContent();
        if (StrUtil.isBlank(content)) {
            return Result.error(Constants.CODE_400, "参数错误");
        }
        return Result.success(loveLineService.saveOrUpdate(loveLine));
    }

    @PostMapping("/update")
    public Result update(@RequestBody LoveLine loveLine) {
        loveLineService.updateById(loveLine);
        return Result.success(loveLine);
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        return Result.success(loveLineService.removeById(id));
    }

    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        return Result.success(loveLineService.removeByIds(ids));
    }

    @PostMapping("/import")
    public Result imp(MultipartFile file) throws Exception {
        InputStream inputStream = file.getInputStream();
        ExcelReader reader = ExcelUtil.getReader(inputStream);
        List<LoveLine> list = reader.read(0, 1, LoveLine.class);
        loveLineService.saveBatch(list);
        return Result.success(true);
    }

    @GetMapping("/export")
    public void export(HttpServletResponse response) throws Exception {
        List<LoveLine> list = loveLineService.list();
        ExcelWriter writer = ExcelUtil.getWriter(true);

        writer.addHeaderAlias("content", "内容");
        writer.addHeaderAlias("author", "作者");
        writer.addHeaderAlias("publish_time", "发布时间");

        // 一次性写出list内的对象到excel，使用默认样式，强制输出标题
        writer.write(list, true);

        // 设置浏览器响应的格式
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        String fileName = URLEncoder.encode("用户信息", StandardCharsets.UTF_8);
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");

        ServletOutputStream out = response.getOutputStream();
        writer.flush(out, true);
        out.close();
        writer.close();
    }

}
