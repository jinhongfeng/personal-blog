package myself.potato.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import myself.potato.entity.TravelImage;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;

public interface TravelImageMapper extends BaseMapper<TravelImage> {

    @Delete("delete from travel_image where travel_id = #{travelId}")
    Integer deleteByCircleId(@Param("travelId") Integer travelId);

}
