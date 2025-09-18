package myself.potato.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import myself.potato.common.Constants;
import myself.potato.common.Result;
import myself.potato.entity.Apply;
import myself.potato.service.IApplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/apply")
public class ApplyController {
    @Autowired
    @Qualifier("applyService")
    private IApplyService applyService;

    @GetMapping
    public Result findAll() {
        return Result.success(applyService.list());
    }

    @GetMapping("/{id}")
    public Result findById(@PathVariable Integer id) {
        return Result.success(applyService.getById(id));
    }

    @GetMapping("/page")
    public Result findPage(@RequestParam(required = false) Integer pageNum,
                           @RequestParam(required = false) Integer pageSize,
                           @RequestParam(required = false) String title,
                           @RequestParam(required = false) String link,
                           @RequestParam(required = false) String icon,
                           @RequestParam(required = false) LocalDateTime publishTime) {
        IPage<Apply> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Apply> queryWrapper = buildQueryWrapper(title, link, icon, publishTime);

        IPage<Apply> result = applyService.page(page, queryWrapper);

        return Result.success(result);
    }

    private QueryWrapper<Apply> buildQueryWrapper(String title, String link, String icon,
                                                 LocalDateTime publishTime) {
        QueryWrapper<Apply> queryWrapper = new QueryWrapper<>();

        if (StrUtil.isNotBlank(title)) {
            queryWrapper.like("title", title);
        }
        if (link != null && !link.isEmpty()) {
            queryWrapper.like("link", link);
        }
        if (icon != null && !icon.isEmpty()) {
            queryWrapper.like("icon", icon);
        }
        if (publishTime != null) {
            queryWrapper.like("publishTime", publishTime);
        }

        return queryWrapper;
    }


    @PostMapping
    public Result save(@RequestBody Apply apply) {
        if (StrUtil.isBlank(apply.getTitle())) {
            return Result.error(Constants.CODE_400, "标题不能为空");
        }
        return Result.success(applyService.saveOrUpdate(apply));
    }

    @PostMapping("/update")
    public Result update(@RequestBody Apply apply){
        applyService.updateById(apply);
        return Result.success(apply);
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        return Result.success(applyService.removeById(id));
    }

    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        return Result.success(applyService.removeByIds(ids));
    }

}
