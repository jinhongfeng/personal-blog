package myself.potato.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import myself.potato.entity.PageView;
import myself.potato.mapper.PageViewMapper;
import myself.potato.service.IPageViewService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PageViewServiceImpl extends ServiceImpl<PageViewMapper, PageView> implements IPageViewService {

    private final RedisTemplate<String, String> redisTemplate;

    // Redis 中存储访问量的 Key 前缀
    private static final String PV_KEY_PREFIX = "page:view:";

    @Override
    public void recordView(String pageUri) {
        // 生成 Redis Key, 例如: page:view:/blog/index.html
        String redisKey = PV_KEY_PREFIX + pageUri;

        // 使用 INCR 命令原子性地增加访问次数，并设置一个过期时间2天，以防数据永久堆积
        redisTemplate.opsForValue().increment(redisKey);
        redisTemplate.expire(redisKey, 2, TimeUnit.DAYS);
    }

    /**
     * 每天凌晨2点执行一次数据同步
     */
    @Scheduled(cron = "0 0 2 * * ?")
    @Override
    @Transactional // 保证数据库操作的原子性
    public void syncToDatabase() {
        System.out.println("开始同步页面访问量数据到数据库...");

        // 从 Redis 中获取所有页面访问量的 Key
        Set<String> keys = redisTemplate.keys(PV_KEY_PREFIX + "*");
        if (keys == null || keys.isEmpty()) {
            System.out.println("Redis中没有需要同步的数据。");
            return;
        }

        // 批量获取这些 Key 的值
        Map<String, String> pvMap = redisTemplate.opsForValue().multiGet(keys)
                .stream()
                .collect(Collectors.toMap(
                        key -> key.substring(PV_KEY_PREFIX.length()), // 提取 URI
                        redisTemplate.opsForValue()::get
                ));

        LocalDate today = LocalDate.now();

        // 准备要更新或插入的数据
        List<PageView> pageViewsToSave = new ArrayList<>();
        for (Map.Entry<String, String> entry : pvMap.entrySet()) {
            String uri = entry.getKey();
            int count = Integer.parseInt(entry.getValue());

            PageView pageView = new PageView();
            pageView.setPageUri(uri);
            pageView.setStatDate(today);
            pageView.setViewCount(count);

            // TODO: 在这里通过uri获取页面标题，设置到 pageView.setPageTitle()

            pageViewsToSave.add(pageView);
        }

        // 批量更新或插入数据库
        this.saveOrUpdateBatch(pageViewsToSave);
        System.out.println("成功同步 " + pageViewsToSave.size() + " 条页面访问量数据到数据库。");

        // 同步成功后，删除 Redis 中的这些 Key
        redisTemplate.delete(keys);
        this.getBaseMapper().delete(Wrappers.<PageView>lambdaQuery()
                .eq(PageView::getStatDate, today)
                .in(PageView::getPageUri, pageViewsToSave.stream().map(PageView::getPageUri).collect(Collectors.toList()))
        );

        this.saveBatch(pageViewsToSave);

    }
}
