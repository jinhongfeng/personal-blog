package myself.potato.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import myself.potato.entity.Travel;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;

public interface TravelMapper extends BaseMapper<Travel> {

    @Insert("insert into sys_travel(title, astyle) " +
            "values (#{title}, #{astyle})")
    int insert(Travel travel);

    @Delete("delete from sys_travel where id = #{id}")
    Integer delete(Integer id);


}
