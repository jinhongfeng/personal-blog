package myself.potato.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("sys_travel")
public class Travel {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String title;
    private String astyle; // 保存标签颜色

}
