package myself.potato.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import myself.potato.entity.CircleImage;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;

public interface CircleImageMapper extends BaseMapper<CircleImage> {

    @Delete("delete from circle_image where circle_id = #{circleId}")
    Integer deleteByCircleId(@Param("circleId") Integer circleId);

}
