package myself.potato.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import myself.potato.controller.dto.UserPasswordDTO;
import myself.potato.entity.User;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

public interface UserMapper extends BaseMapper<User> {
    @Select("select * from sys_user")
    List<User> selectAllUser();

    @Insert("insert into sys_user(username, password, role) " +
            "values (#{username}, #{password}, #{role})")
    int insert(User user);

    @Delete("delete from sys_user where id = #{id}")
    Integer delete(Integer id);

    @Select("select * from sys_user " +
            "where (username like concat('%', #{username}, '%') or #{username} is not or #{username} = '') " +
            "and (password like concat('%', #{password}, '%') or #{password} is not or #{password} = '') " +
            "and (role like concat('%', #{role}, '%') or #{role} is not or #{role} = '') " +
            "and (publish_time = #{publishTime} or #{publishTime} is null)" +
            "limit #{pageNum}, #{pageSize}")
    List<User> selectPage(@Param("pageNum") Integer pageNum, @Param("pageSize") Integer pageSize,
                           @Param("username") String username, @Param("password") String password,
                           @Param("role") String role, @Param("publish_time") LocalDateTime publishTime);

    @Select("select count(*) from sys_user " +
            "where (username like concat('%', #{username}, '%') or #{username} is not or #{username} = '') " +
            "and (password like concat('%', #{password}, '%') or #{password} is not or #{password} = '') " +
            "and (role like concat('%', #{role}, '%') or #{role} is not or #{role} = '') " +
            "and (publish_time = #{publishTime} or #{publishTime} is null)")
    List<User> selectTotal(@Param("username") String username, @Param("password") String password,
                           @Param("role") String role, @Param("publishTime") LocalDateTime publishTime);

    @Update("update sys_user set password = #{newPassword} where username = #{username} and password = #{password}")
    int updatePassword(UserPasswordDTO userPasswordDTO);

    @Select("select * from sys_user where username = #{username} and password = MD5(#{password})")
    User findByUsernameAndPassword(@Param("username") String username, @Param("password") String password);
}
