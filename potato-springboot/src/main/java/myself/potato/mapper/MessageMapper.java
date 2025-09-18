package myself.potato.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import myself.potato.entity.Diary;
import myself.potato.entity.LeaveMessage;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface MessageMapper extends BaseMapper<LeaveMessage> {

    @Select("select * from sys_message")
    List<LeaveMessage> selectAllDiary();

    @Insert("insert into sys_message(nickname, theme, content, contact) " +
            "values (#{nickname}, #{theme}, #{content}, #{contact})")
    int insert(LeaveMessage leaveMessage);

    @Delete("delete from sys_message where id = #{id}")
    Integer deleteById(Integer id);

    @Select("select * from sys_message " +
            "where (nickname like concat('%', #{nickname}, '%') or #{nickname} is null or #{nickname} = '')" +
            "and (theme like concat('%', #{theme}, '%') or #{theme} is null or #{theme} = '')" +
            "and (content like concat('%', #{content}, '%') or #{content} is null or #{content} = '')" +
            "and (contact like concat('%', #{contact}, '%') or #{contact} is null or #{contact} = '')" +
            "limit #{pageNum}, #{pageSize}")
    List<LeaveMessage> selectPage(@Param("pageNum") Integer pageNum, @Param("pageSize") Integer pageSize,
                                  @Param("nickname") String nickname, @Param("theme") String theme,
                                  @Param("content") String content, @Param("contact") String contact);

    @Select("select count(*) from sys_message " +
            "where (nickname like concat('%', #{nickname}, '%') or #{nickname} is null or #{nickname} = '')" +
            "and (theme like concat('%', #{theme}, '%') or #{theme} is null or #{theme} = '')" +
            "and (content like concat('%', #{content}, '%') or #{content} is null or #{content} = '')" +
            "and (contact like concat('%', #{contact}, '%') or #{contact} is null or #{contact} = '')")
    Integer selectTotal(@Param("nickname") String nickname, @Param("theme") String theme,
                        @Param("content") String content, @Param("contact") String contact);
}
