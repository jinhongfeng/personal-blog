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
import myself.potato.entity.Circle;

import myself.potato.service.ICircleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;


@RestController
@RequestMapping("/circle")
public class CircleController {

    @Autowired
    private ICircleService circleService;

    @GetMapping
    public Result findAll() {
        QueryWrapper<Circle> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        return Result.success(circleService.list(queryWrapper));
    }

    @GetMapping("/{id}")
    public Result findById(@PathVariable Integer id) {
        return Result.success(circleService.getById(id));
    }

    @GetMapping("/page")
    public Result findPage(@RequestParam(required = false) Integer pageNum,
                           @RequestParam(required = false) Integer pageSize,
                           @RequestParam(required = false) String content,
                           @RequestParam(required = false) String imageList,
                           @RequestParam(required = false) Integer lkeCount,
                           @RequestParam(required = false) Boolean hasLiked,
                           @RequestParam(required = false) LocalDateTime publishTime) {

        IPage<Circle> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Circle> queryWrapper = buildQueryWrapper(content, imageList, lkeCount, hasLiked, publishTime);

        IPage<Circle> result = circleService.page(page, queryWrapper);

        return Result.success(result);
    }
    private QueryWrapper<Circle> buildQueryWrapper(String content, String imageList, Integer lkeCount,
                                                  Boolean hasLiked, LocalDateTime publishTime) {
        QueryWrapper<Circle> queryWrapper = new QueryWrapper<>();

        if (StrUtil.isNotBlank(content)) {
            queryWrapper.like("content", content);
        }
        if (imageList != null && !imageList.isEmpty()) {
            queryWrapper.like("imageList", imageList);
        }
        if (lkeCount != null) {
            queryWrapper.like("lkeCount", lkeCount);
        }
        if (hasLiked != null) {
            queryWrapper.like("hasLiked", hasLiked);
        }
        if (publishTime != null) {
            queryWrapper.like("publishTime", publishTime);
        }

        queryWrapper.orderByDesc("id");
        return queryWrapper;
    }


    @PostMapping
    public Result save(@RequestBody Circle circle) {
        String content = circle.getContent();
        if (StrUtil.isBlank(content)) {
            return Result.error(Constants.CODE_400, "参数错误");
        }
        return Result.success(circleService.saveOrUpdate(circle));
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        return Result.success(circleService.removeById(id));
    }

    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        return Result.success(circleService.removeByIds(ids));
    }

    @PostMapping("/update")
    public Result update(@RequestBody Circle circle) {
        circleService.updateById(circle);
        return Result.success(circle);
    }

    @PostMapping("/import")
    public Result imp(MultipartFile file) throws Exception {
        InputStream inputStream = file.getInputStream();
        ExcelReader reader = ExcelUtil.getReader(inputStream);
        List<Circle> list = reader.read(0, 1, Circle.class);
        circleService.saveBatch(list);
        return Result.success(true);
    }
    @GetMapping("/export")
    public void export(HttpServletResponse response) throws Exception {
        List<Circle> list = circleService.list();
        ExcelWriter writer = ExcelUtil.getWriter(true);

        writer.addHeaderAlias("content", "内容");
        writer.addHeaderAlias("imageList", "图片");
        writer.addHeaderAlias("likeCount", "点赞数量");
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

    // 控制浏览量添加
    @PostMapping("/{id}/hasCount")
    public Result updatePageview(@PathVariable Integer id) {
        circleService.incrementHasCount(id);
        return Result.success();
    }


}
