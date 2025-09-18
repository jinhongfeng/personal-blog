package myself.potato.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import myself.potato.entity.Note;
import org.apache.ibatis.annotations.*;

public interface NoteMapper extends BaseMapper<Note> {


    @Insert("insert into sys_note(title, picture, information, pageview, author, badge) " +
            "values (#{title}, #{picture}, #{information}, #{pageview}, #{author}, #{badge})")
    int insert(Note note);

    @Update("update sys_note set pageview = pageview + 1 where id = #{id}")
    int incrementPageView(Integer id);
}
