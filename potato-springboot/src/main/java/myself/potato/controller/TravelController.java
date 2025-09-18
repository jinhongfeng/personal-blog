package myself.potato.controller;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import myself.potato.common.Constants;
import myself.potato.common.Result;
import myself.potato.entity.Travel;
import myself.potato.service.ITravelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/travel")
public class TravelController {

    @Autowired
    private ITravelService travelService;

    @GetMapping
    public Result findAll() {
        QueryWrapper<Travel> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        return Result.success(travelService.list(queryWrapper));
    }

    @GetMapping("/{id}")
    public Result findById(@PathVariable Integer id) {
        return Result.success(travelService.getById(id));
    }

    @GetMapping("/page")
    public Result findPage(@RequestParam(required = false) Integer pageNum,
                           @RequestParam(required = false) Integer pageSize,
                           @RequestParam(required = false) String title,
                           @RequestParam(required = false) String astyle) {

        IPage<Travel> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Travel> queryWrapper = buildQueryWrapper(title, astyle);

        IPage<Travel> result = travelService.page(page, queryWrapper);

        return Result.success(result);
    }
    private QueryWrapper<Travel> buildQueryWrapper(String title, String astyle) {
        QueryWrapper<Travel> queryWrapper = new QueryWrapper<>();

        if (title != null && !title.isEmpty()) {
            queryWrapper.like("title", title);
        }
        if (astyle != null && !astyle.isEmpty()) {
            queryWrapper.like("astyle", astyle);
        }

        queryWrapper.orderByDesc("id");
        return queryWrapper;
    }


    @PostMapping
    public Result save(@RequestBody Travel travel) {
        String title = travel.getTitle();
        if (StrUtil.isBlank(title)) {
            return Result.error(Constants.CODE_400, "参数错误");
        }
        return Result.success(travelService.saveOrUpdate(travel));
    }

    @PostMapping("/update")
    public Result update(@RequestBody Travel travel) {
        travelService.updateById(travel);
        return Result.success(travel);
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        return Result.success(travelService.removeById(id));
    }

    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        return Result.success(travelService.removeByIds(ids));
    }
}
