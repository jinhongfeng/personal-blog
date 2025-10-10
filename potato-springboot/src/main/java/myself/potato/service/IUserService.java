package myself.potato.service;

import com.baomidou.mybatisplus.extension.service.IService;
import myself.potato.controller.dto.UserDTO;
import myself.potato.controller.dto.UserPasswordDTO;
import myself.potato.entity.User;

public interface IUserService extends IService<User> {
    UserDTO login(UserDTO userDTO);

    void updatePassword(UserPasswordDTO userPasswordDTO);

    void sendVerificationCode(String email);
    UserDTO loginWithCode(String email, String code);
}
