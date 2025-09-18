package myself.potato.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_concern")
public class Concern {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    // 个人信息
    private String nickname;
    private String email;
    private String hobby;
    private Integer age;
    private String avatar;

    // 首页内容
    private String homedesc;
    // 个人简介
    private String profile;
    // 我的散文
    private String prose;
    // 我的技能
    private String skill;

    @TableField(value = "publish_time")
    private LocalDateTime publishTime;
}
