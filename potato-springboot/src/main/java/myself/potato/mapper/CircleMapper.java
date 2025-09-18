package myself.potato.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import myself.potato.entity.Circle;
import myself.potato.entity.Concern;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Update;

public interface CircleMapper extends BaseMapper<Circle> {

    @Insert("insert into sys_circle(content, image_list, like_count, has_liked) " +
            "values (#{content}, #{imageList}, #{likeCount}, #{hasLiked})")
    int insert(Circle circle);

    @Delete("delete from sys_circle where id = #{id}")
    Integer delete(Integer id);

    @Update("update sys_circle set like_count = like_count + 1 where id = #{id}")
    int incrementHasCount(Integer id);
}
