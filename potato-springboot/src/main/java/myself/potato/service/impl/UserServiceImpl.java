package myself.potato.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.log.Log;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import myself.potato.common.Constants;
import myself.potato.controller.dto.UserDTO;
import myself.potato.controller.dto.UserPasswordDTO;
import myself.potato.entity.User;
import myself.potato.exception.ServiceException;
import myself.potato.mapper.UserMapper;
import myself.potato.service.IUserService;
import myself.potato.util.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service("userService")
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    private static final Log LOG = Log.get();

    @Autowired
    private UserMapper userMapper;

    @Override
    public UserDTO login(UserDTO userDTO) {
        try {
            // 直接根据用户名和密码查询用户
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("username", userDTO.getUsername());
            queryWrapper.eq("password", userDTO.getPassword()); // 明文密码匹配

            User user = getOne(queryWrapper);
            if (user == null) {
                // 进一步判断是用户名错误还是密码错误
                QueryWrapper<User> usernameQuery = new QueryWrapper<>();
                usernameQuery.eq("username", userDTO.getUsername());
                if (getOne(usernameQuery) == null) {
                    throw new ServiceException(Constants.CODE_403, "用户名不存在");
                } else {
                    throw new ServiceException(Constants.CODE_402, "密码错误");
                }
            }
            // 生成 Token 并返回用户信息
            UserDTO resultDTO = new UserDTO();
            BeanUtil.copyProperties(user, resultDTO);
            String token = TokenUtils.genToken(user.getId().toString(), user.getPassword());
            resultDTO.setToken(token);
            return resultDTO;
        } catch (Exception e) {
            LOG.error("登录失败", e);
            throw new ServiceException(Constants.CODE_500, "系统异常");
        }
    }

    private User getUserInfo(UserDTO userDTO) {
        // 去数据库中查询必须是实体类，使用Mybatis中的QueryWrapper
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", userDTO.getUsername());
        queryWrapper.eq("password", userDTO.getPassword());
        User one;
        try { // 如果在数据库中
            one = getOne(queryWrapper);
        } catch (Exception e) {
            LOG.error(e);
            throw new ServiceException(Constants.CODE_600, e.getMessage());
        }
        return one;
    }

    @Override
    public void updatePassword(UserPasswordDTO userPasswordDTO) {
        int update = userMapper.updatePassword(userPasswordDTO);
        if (update < 1) {
            throw new ServiceException(Constants.CODE_402, "密码错误");
        }
    }
}
