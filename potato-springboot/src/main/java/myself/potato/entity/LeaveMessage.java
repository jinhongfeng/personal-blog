package myself.potato.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_message")
public class LeaveMessage {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String nickname;
    private String theme;
    private String content;
    private String contact;

    @TableField(value = "publish_time")
    private LocalDateTime publishTime;
}
