package myself.potato.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("circle_image")
public class CircleImage {
    @TableId(type = IdType.AUTO)
    private Integer id;

    @TableField(value = "circle_id")
    private Integer circleId;

    @TableField(value = "image_url")
    private String imageUrl;

    private Integer sort;
}
