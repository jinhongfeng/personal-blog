package myself.potato.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("travel_image")
public class TravelImage {
    @TableId(type = IdType.AUTO)
    private Integer id;

    @TableField(value = "travel_id")
    private Integer travelId;

    @TableField(value = "image_url")
    private String imageUrl;

    private String information;

    private Integer sort;

    @TableField(value = "publish_time")
    private LocalDateTime publishTime;

}
