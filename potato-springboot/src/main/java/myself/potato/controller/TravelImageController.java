package myself.potato.controller;

import myself.potato.common.Result;
import myself.potato.entity.TravelImage;
import myself.potato.service.ITravelImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/travelImage")
public class TravelImageController {

    @Autowired
    private ITravelImageService travelImageService;

    /**
     * 批量保存图片
     */
    @PostMapping("/batchSave")
    public Result batchSave(@RequestBody List<TravelImage> travelImages) {
        if (travelImages == null || travelImages.isEmpty()) {
            return Result.error("图片列表不能为空");
        }
        boolean success = travelImageService.saveBatch(travelImages);
        return success ? Result.success("图片保存成功") : Result.error("图片保存失败");
    }

    /**
     * 根据ID查询图片列表（按上传顺序排序）
     */
    @GetMapping("/listByTravelId")
    public Result listByTravelId(@RequestParam Integer travelId) {
        if (travelId == null) {
            return Result.error("travelId不能为空");
        }
        // 按sort升序排列，保证最先上传的图片先显示
        List<TravelImage> images = travelImageService.listByTravelId(travelId);
        return Result.success(images);
    }

    /**
     * 根据ID删除关联的所有图片
     */
    @DeleteMapping("/deleteByTravelId")
    public Result deleteByTravelId(@RequestParam Integer travelId) {
        if (travelId == null) {
            return Result.error("travelId不能为空");
        }
        boolean success = travelImageService.removeByTravelId(travelId);
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
        boolean success = travelImageService.removeById(id);
        return success ? Result.success("图片删除成功") : Result.error("图片删除失败");
    }

    /**
     * 更新图片排序
     */
    @PutMapping("/updateSort")
    public Result updateSort(@RequestParam Integer travelId, @RequestBody List<Integer> imageIds) {
        if (travelId == null || imageIds == null || imageIds.isEmpty()) {
            return Result.error("参数不能为空");
        }
        boolean success = travelImageService.updateSort(travelId, imageIds);
        return success ? Result.success("排序更新成功") : Result.error("排序更新失败");
    }
}
