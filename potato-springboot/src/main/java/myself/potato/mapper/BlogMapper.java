package myself.potato.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import myself.potato.entity.Blog;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Update;

public interface BlogMapper extends BaseMapper<Blog> {

    @Insert("insert into sys_blog(title, picture, information, pageview, author, badge) " +
            "values (#{title}, #{picture}, #{information}, #{pageview}, #{author}, #{badge})")
    int insert(Blog blog);

    @Update("update sys_blog set pageview = pageview + 1 where id = #{id}")
    int incrementPageView(Integer id);
}
