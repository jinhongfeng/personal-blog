package myself.potato.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_apps")
public class Apps {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String category; // 分类字段
    private String title;
    private String description;
    private String link;
    private String icon;
    private String astyle;
    @TableField(value = "publish_time")
    private LocalDateTime publishTime;

}
