package myself.potato.controller;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import myself.potato.common.Constants;
import myself.potato.common.Result;
import myself.potato.entity.Music;
import myself.potato.service.IMusicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/music")
public class MusicController {

    @Autowired
    @Qualifier("musicService")
    private IMusicService musicService;

    @GetMapping
    public Result findAll() {
        return Result.success(musicService.list());
    }

    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        return Result.success(musicService.getById(id));
    }

    @GetMapping("/page")
    public Result findPage(@RequestParam(required = false) Integer pageNum,
                           @RequestParam(required = false) Integer pageSize,
                           @RequestParam(required = false) String title,
                           @RequestParam(required = false) String picture,
                           @RequestParam(required = false) String information,
                           @RequestParam(required = false) LocalDateTime publishTime,
                           @RequestParam(required = false) String badge,
                           @RequestParam(required = false) String author,
                           @RequestParam(required = false) Integer pageview) {

        IPage<Music> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Music> queryWrapper = buildQueryWrapper(title, picture, information, publishTime, badge, author, pageview);

        IPage<Music> result = musicService.page(page, queryWrapper);

        return Result.success(result);
    }
    private QueryWrapper<Music> buildQueryWrapper(String title, String picture, String information,
                                                  LocalDateTime publishTime, String badge, String author,
                                                  Integer pageview) {
        QueryWrapper<Music> queryWrapper = new QueryWrapper<>();

        if (StrUtil.isNotBlank(title)) {
            queryWrapper.like("title", title);
        }
        if (information != null && !information.isEmpty()) {
            queryWrapper.like("information", information);
        }
        if (picture != null && !picture.isEmpty()) {
            queryWrapper.like("picture", picture);
        }
        if (publishTime != null) {
            queryWrapper.like("publishTime", publishTime);
        }
        if (badge != null && !badge.isEmpty()) {
            queryWrapper.like("badge", badge);
        }
        if (author != null && !author.isEmpty()) {
            queryWrapper.like("author", author);
        }
        if (pageview != null) {
            queryWrapper.like("pageview", pageview);
        }
        queryWrapper.orderByDesc("id");
        return queryWrapper;
    }

    @PostMapping
    public Result save(@RequestBody Music music) {
        String songName = music.getSongName();
        if (StrUtil.isBlank(songName)) {
            return Result.error(Constants.CODE_400, "参数错误");
        }
        return Result.success(musicService.saveOrUpdate(music));
    }

    @PostMapping("/update")
    public Result update(@RequestBody Music music) {
        musicService.updateById(music);
        return Result.success(music);
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        return Result.success(musicService.removeById(id));
    }

    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        return Result.success(musicService.removeByIds(ids));
    }
}
