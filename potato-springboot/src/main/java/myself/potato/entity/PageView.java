package myself.potato.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;


@Data
@TableName("page_view")
public class PageView {

    @TableId(type = IdType.AUTO)
    private Integer id;

    // 页面唯一标识，通常使用 URI
    @TableField(insertStrategy = FieldStrategy.NOT_EMPTY, updateStrategy = FieldStrategy.NOT_EMPTY)
    private String pageUri;

    // 页面的标题，可选，便于展示
    private String pageTitle;

    // 当天的访问次数,对于要累加的 viewCount，设置更新时的策略
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Integer viewCount = 0;

    // 统计日期
    @TableField(insertStrategy = FieldStrategy.NOT_EMPTY, updateStrategy = FieldStrategy.NOT_EMPTY)
    private LocalDate statDate;
}
