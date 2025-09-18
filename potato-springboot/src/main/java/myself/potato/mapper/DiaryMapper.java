package myself.potato.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import myself.potato.entity.Diary;
import org.apache.ibatis.annotations.*;
import java.time.LocalDateTime;
import java.util.List;

public interface DiaryMapper extends BaseMapper<Diary> {
    @Select("select * from sys_diary")
    List<Diary> selectAllDiary();

    @Insert("insert into sys_diary(title, picture, information, pageview, author, badge) " +
            "values (#{title}, #{picture}, #{information}, #{pageview}, #{author}, #{badge})")
    int insert(Diary diary);

    @Delete("delete from sys_diary where id = #{id}")
    Integer deleteById(Integer id);

    @Select("select * from sys_diary " +
            "where (title like concat('%', #{title}, '%') or #{title} is null or #{title} = '')" +
            "and (picture like concat('%', #{picture}, '%') or #{picture} is null or #{picture} = '')" +
            "and (information like concat('%', #{information}, '%') or #{information} is null or #{information} = '')" +
            "and (publish_time = #{publishTime} or #{publishTime} is null)" +
            "and (pageview like concat('%', #{pageview}, '%') or #{pageview} is null or #{pageview} = '')" +
            "and (author like concat('%', #{author}, '%') or #{author} is null or #{author} = '')" +
            "and (badge like concat('%', #{badge}, '%') or #{badge} is null or #{badge} = '')" +
            "limit #{pageNum}, #{pageSize}")
    List<Diary> selectPage(@Param("pageNum") Integer pageNum, @Param("pageSize") Integer pageSize,
                           @Param("title") String title, @Param("picture") String picture,
                           @Param("information") String information, @Param("publishTime") LocalDateTime publishTime,
                           @Param("pageview") String pageview, @Param("author") String author,
                           @Param("badge") String badge);

    @Select("select count(*) from sys_diary " +
            "where (title like concat('%', #{title}, '%') or #{title} is null or #{title} = '')" +
            "and (picture like concat('%', #{picture}, '%') or #{picture} is null or #{picture} = '')" +
            "and (information like concat('%', #{information}, '%') or #{information} is null or #{information} = '')" +
            "and (publish_time = #{publishTime} or #{publishTime} is null)" +
            "and (pageview like concat('%', #{pageview}, '%') or #{pageview} is null or #{pageview} = '')" +
            "and (author like concat('%', #{author}, '%') or #{author} is null or #{author} = '')" +
            "and (badge like concat('%', #{badge}, '%') or #{badge} is null or #{badge} = '')")
    Integer selectTotal(@Param("title") String title,
                        @Param("picture") String picture,
                        @Param("information") String information,
                        @Param("publishTime") LocalDateTime publishTime,
                        @Param("pageview") String pageview,
                        @Param("author") String author,
                        @Param("badge") String badge);


    @Update("update sys_diary set pageview = pageview + 1 where id = #{id}")
    int incrementPageView(Integer id);

}
