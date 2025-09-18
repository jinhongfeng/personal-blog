package myself.potato.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import myself.potato.entity.Concern;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

public interface ConcernMapper extends BaseMapper<Concern> {
    @Select("select * from sys_concern")
    List<Concern> selectAllConcern();

    @Insert("insert into sys_concern(nickname, age, email, hobby, avatar, " +
                "homedesc, profile, prose, skill, read_book, read_film) " +
            "values (#{nickname}, #{age}, #{email}, #{hobby}, #{avatar}, " +
                "#{homedesc}, #{profile}, #{prose}, #{skill})")
    int insert(Concern concern);

    @Delete("delete from sys_concern where id = #{id}")
    Integer delete(Integer id);

    @Select("select * from sys_concern " +
            "where (nickname like concat('%', #{nickname}, '%') or #{nickname} is not or #{nickname} = '') " +
            "and (age like concat('%', #{age}, '%') or #{age} is not or #{age} = '') " +
            "and (email like concat('%', #{email}, '%') or #{email} is not or #{email} = '') " +
            "and (hobby like concat('%', #{hobby}, '%') or #{hobby} is not or #{hobby} = '') " +
            "and (avatar like concat('%', #{avatar}, '%') or #{avatar} is not or #{avatar} = '') " +
            "and (homedesc like concat('%', #{homedesc}, '%') or #{homedesc} is not or #{homedesc} = '') " +
            "and (profile like concat('%', #{profile}, '%') or #{profile} is not or #{profile} = '') " +
            "and (prose like concat('%', #{prose}, '%') or #{prose} is not or #{prose} = '') " +
            "and (skill like concat('%', #{skill}, '%') or #{skill} is not or #{skill} = '') " +
            "and (publish_time = #{publishTime} or #{publishTime} is null)" +
            "limit #{pageNum}, #{pageSize}")
    List<Concern> selectPage(@Param("pageNum") Integer pageNum, @Param("pageSize") Integer pageSize,
                             @Param("nickname") String nickname, @Param("age") Integer age,
                             @Param("email") String email,  @Param("hobby") String hobby,
                             @Param("avatar") String avatar, @Param("homedesc") String homedesc,
                             @Param("profile") String profile, @Param("prose") String prose,
                             @Param("skill") String skill, @Param("publish_time") LocalDateTime publishTime);

    @Select("select * from sys_concern " +
            "where (nickname like concat('%', #{nickname}, '%') or #{nickname} is not or #{nickname} = '') " +
            "and (age like concat('%', #{age}, '%') or #{age} is not or #{age} = '') " +
            "and (email like concat('%', #{email}, '%') or #{email} is not or #{email} = '') " +
            "and (hobby like concat('%', #{hobby}, '%') or #{hobby} is not or #{hobby} = '') " +
            "and (avatar like concat('%', #{avatar}, '%') or #{avatar} is not or #{avatar} = '') " +
            "and (homedesc like concat('%', #{homedesc}, '%') or #{homedesc} is not or #{homedesc} = '') " +
            "and (profile like concat('%', #{profile}, '%') or #{profile} is not or #{profile} = '') " +
            "and (prose like concat('%', #{prose}, '%') or #{prose} is not or #{prose} = '') " +
            "and (skill like concat('%', #{skill}, '%') or #{skill} is not or #{skill} = '') " +
            "and (publish_time = #{publishTime} or #{publishTime} is null)")
    List<Concern> selectTotal(@Param("nickname") String nickname, @Param("age") Integer age,
                              @Param("email") String email,  @Param("hobby") String hobby,
                              @Param("avatar") String avatar, @Param("homedesc") String homedesc,
                              @Param("profile") String profile, @Param("prose") String prose,
                              @Param("skill") String skill, @Param("publish_time") LocalDateTime publishTime);

}
