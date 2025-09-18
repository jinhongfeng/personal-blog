package myself.potato.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_circle")
public class Circle {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String content;

    @TableField(value = "image_list")
    private String imageList;
    @TableField(value = "like_count")
    private Integer likeCount;
    @TableField(value = "has_liked")
    private Boolean hasLiked;

    @TableField(value = "publish_time")
    private LocalDateTime publishTime;
}
