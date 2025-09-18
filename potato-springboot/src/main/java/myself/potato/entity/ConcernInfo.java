package myself.potato.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("concern_info")
public class ConcernInfo {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String title;
    private String image;
    private String url;
    private String theme;
    @TableField(value = "publish_time")
    private LocalDateTime publishTime;
}
