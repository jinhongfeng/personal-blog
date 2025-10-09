package myself.potato.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_apply")
public class Dialogue {
    @TableId(type = IdType.AUTO)
    private Integer id;

    @TableField(value = "session_id")
    private String sessionId;

    @TableField(value = "from_user_id")
    private String fromUserId;

    @TableField(value = "to_user_id")
    private String toUserId;

    private String message;

    @TableField(value = "publish_time")
    private LocalDateTime publishTime;

    @TableField(value = "update_time")
    private LocalDateTime updateTime;
}
