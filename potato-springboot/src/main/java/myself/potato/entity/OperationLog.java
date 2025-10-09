package myself.potato.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Data
@TableName("operation_log")
public class OperationLog {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String operator;     // 操作人用户名
    private String operatorId;     // 操作人ID
    private String ip;     // 操作IP地址
    // 请求的URI
    private String uri;
    private String method;     // 请求的HTTP方法 (GET, POST, PUT, DELETE等)
    private String action;     // 操作类型
    private String description;     // 操作描述
    private String params; // 请求参数 (JSON格式)

    @TableField(value = "operate_time")
    private LocalDateTime operateTime;
}
