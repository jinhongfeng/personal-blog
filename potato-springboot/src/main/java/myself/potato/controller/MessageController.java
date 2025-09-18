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
import myself.potato.entity.Diary;
import myself.potato.entity.LeaveMessage;
import myself.potato.mapper.MessageMapper;
import myself.potato.service.IMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/message")
public class MessageController {

    @Autowired
    @Qualifier("messageServiceImpl")
    private IMessageService messageService;

    @Autowired
    private MessageMapper messageMapper;

    @GetMapping
    public Result findAll() {
        return Result.success(messageService.list());
    }

    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        return Result.success(messageService.getById(id));
    }

    @GetMapping("/page")
    public Result findPage(@RequestParam(required = false) Integer pageNum,
                           @RequestParam(required = false) Integer pageSize,
                           @RequestParam(required = false) String nickname,
                           @RequestParam(required = false) String theme,
                           @RequestParam(required = false) String content,
                           @RequestParam(required = false) String contact) {
        IPage<LeaveMessage> page = new Page<>(pageNum, pageSize);
        QueryWrapper<LeaveMessage> queryWrapper = buildQueryWrapper(nickname, theme, content, contact);
        IPage<LeaveMessage> result = messageService.page(page, queryWrapper);
        return Result.success(result);
    }

    private QueryWrapper<LeaveMessage> buildQueryWrapper(String nickname, String theme,
                                                         String content, String contact) {

        QueryWrapper<LeaveMessage> queryWrapper = new QueryWrapper<>();
        if (StrUtil.isNotBlank(theme)) {
            queryWrapper.like("theme", theme);
        }
        if (nickname != null && !nickname.isEmpty()) {
            queryWrapper.like("nickname", nickname);
        }

        if (content != null && !content.isEmpty()) {
            queryWrapper.like("content", content);
        }
        if (contact != null && !contact.isEmpty()) {
            queryWrapper.like("contact", contact);
        }
        queryWrapper.orderByDesc("id");
        return queryWrapper;
    }


    @PostMapping
    public Result save(@RequestBody LeaveMessage leaveMessage) {
        String theme = leaveMessage.getTheme();
        if (StrUtil.isBlank(theme)) {
            return Result.error(Constants.CODE_400, "参数错误");
        }
        return Result.success(messageService.saveOrUpdate(leaveMessage));
    }

    @PostMapping("/update")
    public Result update(@RequestBody LeaveMessage leavemessage) {
        messageService.updateById(leavemessage);
        return Result.success(leavemessage);
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        return Result.success(messageService.removeById(id));
    }

    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        return Result.success(messageService.removeByIds(ids));
    }

    @PostMapping("/import")
    public Result imp(MultipartFile file) throws Exception {
        InputStream inputStream = file.getInputStream();
        ExcelReader reader = ExcelUtil.getReader(inputStream);
        List<LeaveMessage> list = reader.read(0, 1, LeaveMessage.class);
        messageService.saveBatch(list);
        return Result.success(true);
    }

    @GetMapping("/export")
    public void export(HttpServletResponse response) throws Exception {
        List<LeaveMessage> list = messageService.list();
        ExcelWriter writer = ExcelUtil.getWriter(true);

        writer.addHeaderAlias("title", "资讯标题");
        writer.addHeaderAlias("picture", "图片");
        writer.addHeaderAlias("information", "内容");
        writer.addHeaderAlias("badge", "标签");
        writer.addHeaderAlias("author", "作者");
        writer.addHeaderAlias("pageview", "浏览量");
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
