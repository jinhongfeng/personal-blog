package myself.potato.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import myself.potato.common.Constants;
import myself.potato.common.Result;
import myself.potato.entity.Apps;
import myself.potato.service.IAppsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/apps")
public class AppsController {
    @Autowired
    @Qualifier("appsService")
    private IAppsService appsService;

    @GetMapping
    public Result findAll() {
        return Result.success(appsService.list());
    }

    @GetMapping("/{id}")
    public Result findById(@PathVariable Integer id) {
        return Result.success(appsService.getById(id));
    }

    @GetMapping("/page")
    public Result findPage(@RequestParam(required = false) Integer pageNum,
                           @RequestParam(required = false) Integer pageSize,
                           @RequestParam(required = false) String category,
                           @RequestParam(required = false) String title,
                           @RequestParam(required = false) String description,
                           @RequestParam(required = false) String link,
                           @RequestParam(required = false) String icon,
                           @RequestParam(required = false) String astyle,
                           @RequestParam(required = false) LocalDateTime publishTime) {
        IPage<Apps> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Apps> queryWrapper = buildQueryWrapper(category, title, description, link, icon, astyle, publishTime);

        IPage<Apps> result = appsService.page(page, queryWrapper);

        return Result.success(result);
    }

    private QueryWrapper<Apps> buildQueryWrapper(String category, String title, String description, String link, String icon,
                                                 String astyle, LocalDateTime publishTime) {
        QueryWrapper<Apps> queryWrapper = new QueryWrapper<>();
        if (StrUtil.isNotBlank(category)) {
            queryWrapper.like("category", category);
        }
        if (StrUtil.isNotBlank(title)) {
            queryWrapper.like("title", title);
        }
        if (StrUtil.isNotBlank(description)) {
            queryWrapper.like("description", description);
        }
        if (link != null && !link.isEmpty()) {
            queryWrapper.like("link", link);
        }
        if (icon != null && !icon.isEmpty()) {
            queryWrapper.like("icon", icon);
        }
        if (StrUtil.isNotBlank(astyle)) {
            queryWrapper.like("astyle", astyle);
        }
        if (publishTime != null) {
            queryWrapper.like("publishTime", publishTime);
        }

        return queryWrapper;
    }


    @PostMapping
    public Result save(@RequestBody Apps apps) {
        if (StrUtil.isBlank(apps.getCategory())) {
            return Result.error(Constants.CODE_400, "分类字段不能为空");
        }
        if (StrUtil.isBlank(apps.getTitle())) {
            return Result.error(Constants.CODE_400, "标题不能为空");
        }

        return Result.success(appsService.saveOrUpdate(apps));
    }

    @PostMapping("/update")
    public Result update(@RequestBody Apps apps){
        appsService.updateById(apps);
        return Result.success(apps);
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        return Result.success(appsService.removeById(id));
    }

    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        return Result.success(appsService.removeByIds(ids));
    }

}
