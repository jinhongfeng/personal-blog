package myself.potato.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_lovemessage")
public class LoveMessage {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String content;
    private String author;

    @TableField(value = "publish_time")
    private LocalDateTime publishTime;
}
