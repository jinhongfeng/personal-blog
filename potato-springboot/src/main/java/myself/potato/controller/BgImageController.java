package myself.potato.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import myself.potato.common.Constants;
import myself.potato.common.Result;

import myself.potato.entity.BgImage;
import myself.potato.service.IBgImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/bgImage")
public class BgImageController {

    @Autowired
    @Qualifier("bgImageService")
    private IBgImageService bgImageService;

    @GetMapping
    public Result findAll() {
        return Result.success(bgImageService.list());
    }

    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        return Result.success(bgImageService.getById(id));
    }

    @GetMapping("/page")
    public Result findPage(@RequestParam(required = false) Integer pageNum,
                           @RequestParam(required = false) Integer pageSize,
                           @RequestParam(required = false) String title,
                           @RequestParam(required = false) String url,
                           @RequestParam(required = false) String thumbnail) {

        IPage<BgImage> page = new Page<>(pageNum, pageSize);
        QueryWrapper<BgImage> queryWrapper = buildQueryWrapper(title, url, thumbnail);

        IPage<BgImage> result = bgImageService.page(page, queryWrapper);

        return Result.success(result);
    }
    private QueryWrapper<BgImage> buildQueryWrapper(String title, String url, String thumbnail) {
        QueryWrapper<BgImage> queryWrapper = new QueryWrapper<>();

        if (StrUtil.isNotBlank(title)) {
            queryWrapper.like("title", title);
        }
        if (url != null && !url.isEmpty()) {
            queryWrapper.like("url", url);
        }
        if (thumbnail != null && !thumbnail.isEmpty()) {
            queryWrapper.like("thumbnail", thumbnail);
        }

        queryWrapper.orderByDesc("id");
        return queryWrapper;
    }

    @PostMapping
    public Result save(@RequestBody BgImage bgImage) {
        String title = bgImage.getTitle();
        if (StrUtil.isBlank(title)) {
            return Result.error(Constants.CODE_400, "参数错误");
        }
        return Result.success(bgImageService.saveOrUpdate(bgImage));
    }

    @PostMapping("/update")
    public Result update(@RequestBody BgImage bgImage) {
        bgImageService.updateById(bgImage);
        return Result.success(bgImage);
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        return Result.success(bgImageService.removeById(id));
    }

    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        return Result.success(bgImageService.removeByIds(ids));
    }
}
