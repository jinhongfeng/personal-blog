package myself.potato.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import myself.potato.common.Loggable;
import myself.potato.common.Result;

import myself.potato.entity.OperationLog;
import myself.potato.service.IOperationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/operationLog")
public class OperationLogController {
    @Autowired
    @Qualifier("operationLogServiceImpl")
    private IOperationLogService operationLogService;

    @GetMapping("/page")
    public Result findPage(@RequestParam(required = false) Integer pageNum,
                           @RequestParam(required = false) Integer pageSize,
                           @RequestParam(required = false) String operator,
                           @RequestParam(required = false) String action,
                           @RequestParam(required = false) String target,
                           @RequestParam(required = false) String content,
                           @RequestParam(required = false) String ip,
                           @RequestParam(required = false) LocalDateTime operationTime) {
        IPage<OperationLog> page = new Page<>(pageNum, pageSize);
        QueryWrapper<OperationLog> queryWrapper = buildQueryWrapper(operator, action, target,
                                                                    content, ip, operationTime);

        IPage<OperationLog> result = operationLogService.page(page, queryWrapper);

        return Result.success(result);
    }

    private QueryWrapper<OperationLog> buildQueryWrapper(String operator, String action, String target,
                                                        String content, String ip, LocalDateTime operationTime) {
        QueryWrapper<OperationLog> queryWrapper = new QueryWrapper<>();

        if (operator != null && !operator.isEmpty()) {
            queryWrapper.like("operator", operator);
        }
        if (action != null && !action.isEmpty()) {
            queryWrapper.like("action", action);
        }
        if (target != null && !target.isEmpty()) {
            queryWrapper.like("target", target);
        }
        if (content != null && !content.isEmpty()) {
            queryWrapper.like("content", content);
        }
        if (ip != null && !ip.isEmpty()) {
            queryWrapper.like("ip", ip);
        }
        if (operationTime != null) {
            queryWrapper.like("operationTime", operationTime);
        }

        return queryWrapper;
    }

    @PostMapping
    @Loggable(action = "新增文章", description = "通过API新增了一篇文章")
    public String createArticle(@RequestParam String title, @RequestParam String content) {
        return "文章创建成功: " + title;
    }

    @PutMapping("/{id}")
    @Loggable(action = "更新文章", description = "更新了指定ID的文章信息")
    public String updateArticle(@PathVariable Long id, @RequestParam String title) {
        return "文章 " + id + " 更新成功: " + title;
    }

    @DeleteMapping("/{id}")
    @Loggable(action = "删除文章", description = "删除了指定ID的文章")
    public String deleteArticle(@PathVariable Long id) {

        return "文章 " + id + " 删除成功";
    }

    @GetMapping
    public String listArticles() {
        return "文章列表";
    }
}
