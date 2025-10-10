package myself.potato.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.log.Log;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import myself.potato.common.Constants;
import myself.potato.controller.dto.UserDTO;
import myself.potato.controller.dto.UserPasswordDTO;
import myself.potato.entity.User;
import myself.potato.exception.ServiceException;
import myself.potato.mapper.UserMapper;
import myself.potato.service.IUserService;
import myself.potato.util.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;


@Service("userService")
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    private static final Log LOG = Log.get();
    private static final String VERIFY_CODE_PREFIX = "verify_code:";
    private static final int CODE_EXPIRE_MINUTES = 5;
    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private UserMapper userMapper;

    @Value("${spring.mail.username}")
    private String fromEmail;

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

    @Override
    public void sendVerificationCode(String email) {
        try {
            // 生成6位随机验证码
            String code = RandomUtil.randomNumbers(6);
            LOG.info("向邮箱 {} 发送验证码: {}", email, code);

            // 存入 Redis，并设置过期时间
            String redisKey = VERIFY_CODE_PREFIX + email;
            redisTemplate.opsForValue().set(redisKey, code, CODE_EXPIRE_MINUTES, TimeUnit.MINUTES);

            // 使用 JavaMailSender 发送 HTML 邮件
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(email);
            helper.setSubject("您有一封【POTATO】的验证码");

            // HTML 邮件内容
            String content = String.format(
                    "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px;'>" +
                    "<h2 style='color: #333;'>POTATO登录验证</h2>" +
                    "<p>您好！您的登录验证码是：</p>" +
                    "<div style='font-size: 28px; font-weight: bold; color: #409EFF; letter-spacing: 5px; margin: 20px 0;'>%s</div>" +
                    "<p>该验证码有效期为 %d 分钟，请尽快使用。</p>" +
                    "<p style='color: #999; font-size: 12px; margin-top: 30px;'>此邮件为系统自动发送，请勿回复。</p>" +
                    "</div>", code, CODE_EXPIRE_MINUTES);

            helper.setText(content, true); // true 表示发送 HTML 邮件

            javaMailSender.send(mimeMessage);
            LOG.info("验证码邮件发送成功！");

        } catch (MessagingException e) {
            LOG.error("发送验证码邮件失败！", e);
            throw new ServiceException(Constants.CODE_500, "发送验证码失败，请检查邮箱配置或稍后重试");
        }
    }

    @Override
    public UserDTO loginWithCode(String email, String code) {
        // 1. 从 Redis 获取验证码
        String redisKey = VERIFY_CODE_PREFIX + email;
        String storedCode = redisTemplate.opsForValue().get(redisKey);

        // 验证验证码
        if (storedCode == null || !storedCode.equals(code)) {
            throw new ServiceException(Constants.CODE_401, "验证码错误或已过期");
        }

        //  验证通过，删除 Redis 中的验证码
        redisTemplate.delete(redisKey);

        //  检查用户是否存在，不存在则自动注册
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", email);
        User user = getOne(queryWrapper);

        if (user == null) {
            // 用户不存在，执行注册逻辑
            user = new User();
            user.setEmail(email);
            user.setUsername(email.substring(0, email.indexOf("@"))); // 使用邮箱前缀作为用户名
            user.setPassword(""); // 密码为空，后续可让用户自行设置
            user.setRole("user"); // 默认角色为普通用户
            save(user); // 保存新用户
            LOG.info("新用户已自动注册: {}", email);
        }

        // 5. 生成 Token 并返回用户信息
        UserDTO resultDTO = new UserDTO();
        BeanUtil.copyProperties(user, resultDTO);
        String token = TokenUtils.genToken(user.getId().toString(), user.getPassword()); // 使用您现有的 TokenUtils
        resultDTO.setToken(token);

        return resultDTO;
    }
}
