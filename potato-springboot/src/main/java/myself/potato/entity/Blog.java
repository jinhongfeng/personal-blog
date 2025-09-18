package myself.potato.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_blog")
public class Blog {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String title;
    private String picture;

    private String information;
    private String badge;
    private String author;
    private Integer pageview;
    @TableField(value = "publish_time")
    private LocalDateTime publishTime;
}
