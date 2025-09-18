package myself.potato.controller;

import myself.potato.entity.CircleImage;
import myself.potato.service.ICircleImageService;
import myself.potato.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/circleImage")
public class CircleImageController {

    @Autowired
    private ICircleImageService circleImageService;

    /**
     * 批量保存圈子图片
     */
    @PostMapping("/batchSave")
    public Result batchSave(@RequestBody List<CircleImage> circleImages) {
        if (circleImages == null || circleImages.isEmpty()) {
            return Result.error("图片列表不能为空");
        }
        boolean success = circleImageService.saveBatch(circleImages);
        return success ? Result.success("图片保存成功") : Result.error("图片保存失败");
    }

    /**
     * 根据圈子ID查询图片列表（按上传顺序排序）
     */
    @GetMapping("/listByCircleId")
    public Result listByCircleId(@RequestParam Integer circleId) {
        if (circleId == null) {
            return Result.error("圈子ID不能为空");
        }
        // 按sort升序排列，保证最先上传的图片先显示
        List<CircleImage> images = circleImageService.listByCircleId(circleId);
        return Result.success(images);
    }

    /**
     * 根据圈子ID删除关联的所有图片
     */
    @DeleteMapping("/deleteByCircleId")
    public Result deleteByCircleId(@RequestParam Integer circleId) {
        if (circleId == null) {
            return Result.error("圈子ID不能为空");
        }
        boolean success = circleImageService.removeByCircleId(circleId);
        return success ? Result.success("图片删除成功") : Result.error("图片删除失败");
    }

    /**
     * 单个删除图片
     */
    @DeleteMapping("/{id}")
    public Result deleteById(@PathVariable Long id) {
        if (id == null) {
            return Result.error("图片ID不能为空");
        }
        boolean success = circleImageService.removeById(id);
        return success ? Result.success("图片删除成功") : Result.error("图片删除失败");
    }

    /**
     * 更新图片排序
     */
    @PutMapping("/updateSort")
    public Result updateSort(@RequestParam Integer circleId, @RequestBody List<Integer> imageIds) {
        if (circleId == null || imageIds == null || imageIds.isEmpty()) {
            return Result.error("参数不能为空");
        }
        boolean success = circleImageService.updateSort(circleId, imageIds);
        return success ? Result.success("排序更新成功") : Result.error("排序更新失败");
    }
}
