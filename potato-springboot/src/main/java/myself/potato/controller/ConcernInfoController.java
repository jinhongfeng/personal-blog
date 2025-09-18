package myself.potato.controller;

import myself.potato.common.Result;
import myself.potato.entity.ConcernInfo;
import myself.potato.service.IConcernInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/concern_info")
public class ConcernInfoController {

    @Autowired
    private IConcernInfoService concernInfoService;

    // 查找全部
    @GetMapping
    public Result findAll() {
        return Result.success(concernInfoService.list());
    }

    // 根据ID查询
    @GetMapping("/{id}")
    public Result findById(@PathVariable Integer id) {
        return Result.success(concernInfoService.getById(id));
    }

    // 更新
    @PostMapping("/update")
    public Result updateUser(@RequestBody ConcernInfo concernInfo) {
        concernInfoService.updateById(concernInfo);
        return Result.success(concernInfo);
    }

    // 新增
    @PostMapping("/add")
    public Result add(@RequestBody ConcernInfo concernInfo) {
        concernInfoService.save(concernInfo);
        return Result.success(concernInfo);
    }

    // 批量查询（根据ID集合）
    @PostMapping("/batch")
    public Result batchQuery(@RequestBody Map<String, List<Integer>> params) {
        List<Integer> ids = params.get("ids");
        if (ids == null || ids.isEmpty()) {
            return Result.success();
        }
        return Result.success(concernInfoService.listByIds(ids));
    }

    // 删除
    @PostMapping("/delete")
    public Result delete(@RequestBody Map<String, Integer> params) {
        Integer id = params.get("id");
        if (id == null) {
            return Result.error();
        }
        concernInfoService.removeById(id);
        return Result.success("删除成功");
    }
}
