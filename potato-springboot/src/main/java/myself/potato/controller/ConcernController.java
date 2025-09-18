package myself.potato.controller;

import myself.potato.common.Result;
import myself.potato.entity.Concern;
import myself.potato.service.IConcernService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/concern")
public class ConcernController {

    @Qualifier("concernService")
    @Autowired
    private IConcernService concernService;

    // 查找全部
    @GetMapping
    public Result findAll() {
        return Result.success(concernService.list());
    }

    // 根据ID查询
    @GetMapping("/{id}")
    public Result findById(@PathVariable Integer id) {
        return Result.success(concernService.getById(id));
    }

    // 更新
    @PostMapping("/update")
    public Result updateUser(@RequestBody Concern concern) {
        concernService.updateById(concern);
        return Result.success(concern);
    }

}
