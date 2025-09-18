package myself.potato.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("sys_music")
public class Music {

    @TableId(type = IdType.AUTO)
    private Integer id;

    @TableField(value = "song_name")
    private String songName;

    private String singer;
    private String album;
    private String url;
    private String cover;
    private String lyric;
}
