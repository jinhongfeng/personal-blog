package myself.potato.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import myself.potato.controller.dto.UserPasswordDTO;
import myself.potato.entity.User;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

public interface UserMapper extends BaseMapper<User> {


    @Update("update sys_user set password = #{newPassword} where username = #{username} and password = #{password}")
    int updatePassword(UserPasswordDTO userPasswordDTO);

}
