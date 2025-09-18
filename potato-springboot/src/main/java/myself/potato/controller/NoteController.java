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
import myself.potato.entity.Note;
import myself.potato.service.INoteService;
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
@RequestMapping("/note")
public class NoteController {

    @Autowired
    @Qualifier("noteServiceImpl")
    private INoteService noteService;

    @GetMapping
    public Result findAll() {
        QueryWrapper<Note> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        return Result.success(noteService.list(queryWrapper));
    }

    @GetMapping("/{id}")
    public Result findById(@PathVariable Integer id) {
        return Result.success(noteService.getById(id));
    }

    @GetMapping("/page")
    public Result findPage(@RequestParam(required = false) Integer pageNum,
                           @RequestParam(required = false) Integer pageSize,
                           @RequestParam(required = false) String title,
                           @RequestParam(required = false) String picture,
                           @RequestParam(required = false) String information,
                           @RequestParam(required = false) LocalDateTime publishTime,
                           @RequestParam(required = false) String badge,
                           @RequestParam(required = false) String author,
                           @RequestParam(required = false) Integer pageview) {
        IPage<Note> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Note> queryWrapper = buildQueryWrapper(title, picture, information, publishTime, badge, author, pageview);

        IPage<Note> result = noteService.page(page, queryWrapper);

        return Result.success(result);
    }

    private QueryWrapper<Note> buildQueryWrapper(String title, String picture, String information,
                                                  LocalDateTime publishTime, String badge, String author,
                                                  Integer pageview) {
        QueryWrapper<Note> queryWrapper = new QueryWrapper<>();

        if (StrUtil.isNotBlank(title)) {
            queryWrapper.like("title", title);
        }
        if (information != null && !information.isEmpty()) {
            queryWrapper.like("information", information);
        }
        if (picture != null && !picture.isEmpty()) {
            queryWrapper.like("picture", picture);
        }
        if (badge != null && !badge.isEmpty()) {
            queryWrapper.like("badge", badge);
        }
        if (author != null && !author.isEmpty()) {
            queryWrapper.like("author", author);
        }
        if (pageview != null) {
            queryWrapper.like("pageview", pageview);
        }
        queryWrapper.orderByDesc("id");
        return queryWrapper;
    }


    @PostMapping
    public Result save(@RequestBody Note note) {
        String title = note.getTitle();
        if (StrUtil.isBlank(title)) {
            return Result.error(Constants.CODE_400, "参数错误");
        }
        return Result.success(noteService.saveOrUpdate(note));
    }

    @PostMapping("/update")
    public Result update(@RequestBody Note note){
        noteService.updateById(note);
        return Result.success(note);
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        return Result.success(noteService.removeById(id));
    }

    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        return Result.success(noteService.removeByIds(ids));
    }

    @PostMapping("/import")
    public Result imp(MultipartFile file) throws Exception {
        InputStream inputStream = file.getInputStream();
        ExcelReader reader = ExcelUtil.getReader(inputStream);
        List<Note> list = reader.read(0, 1, Note.class);
        noteService.saveBatch(list);
        return Result.success(true);
    }

    @PostMapping("/export")
    public void export(HttpServletResponse response) throws Exception {
        List<Note> list = noteService.list();
        ExcelWriter writer = ExcelUtil.getWriter(true);

        writer.addHeaderAlias("title", "资讯标题");
        writer.addHeaderAlias("picture", "图片");
        writer.addHeaderAlias("information", "内容");
        writer.addHeaderAlias("badge", "标签");
        writer.addHeaderAlias("author", "作者");
        writer.addHeaderAlias("pageview", "浏览量");
        writer.addHeaderAlias("publish_time", "发布时间");

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

    @PutMapping("/{id}/pageview")
    public Result updatePageview(@PathVariable Integer id) {
        noteService.incrementPageview(id);
        return Result.success();
    }

}
