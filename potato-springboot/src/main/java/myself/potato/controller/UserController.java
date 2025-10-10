package myself.potato.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import myself.potato.common.Constants;
import myself.potato.common.Result;
import myself.potato.controller.dto.UserDTO;
import myself.potato.controller.dto.UserPasswordDTO;
import myself.potato.entity.User;
import myself.potato.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Qualifier("userService")
    @Autowired
    private IUserService userService;

    // 查询数据
    @GetMapping
    public Result findAll() {
        return Result.success(userService.list());
    }
    // 查询单行数据
    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        return Result.success(userService.getById(id));
    }

    // 个人数据查询修改
    @GetMapping("/username/{username}")
    public Result findOne(@PathVariable String username) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        return Result.success(userService.getOne(queryWrapper));
    }
    // 分页查询
    @GetMapping("/page")
    public Result findPage(@RequestParam(required = false) Integer pageNum,
                           @RequestParam(required = false) Integer pageSize,
                           @RequestParam(required = false) String username,
                           @RequestParam(required = false) String password,
                           @RequestParam(required = false) String email,
                           @RequestParam(required = false) String role,
                           @RequestParam(required = false) LocalDateTime publishTime) {
        IPage<User> page = new Page<>(pageNum, pageSize); // 构建分页
        QueryWrapper<User> queryWrapper = buildQueryWrapper(username, password, email, role, publishTime);

        IPage<User> result = userService.page(page, queryWrapper);

        return Result.success(result);

    }

    private QueryWrapper<User> buildQueryWrapper(String username, String password,
                                                 String email, String role,
                                                 LocalDateTime publishTime) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();

        if (StrUtil.isNotBlank(username)) {
            queryWrapper.like("username", username);
        }
        if (password != null && !password.isEmpty()) {
            queryWrapper.like("password", password);
        }
        if (email != null && !email.isEmpty()) {
            queryWrapper.like("email", email);
        }
        if (role != null && !role.isEmpty()) {
            queryWrapper.like("role", role);
        }
        if (publishTime != null) {
            queryWrapper.like("publishTime", publishTime);
        }
        queryWrapper.orderByDesc("id");
        return queryWrapper;
    }

    // 新增或者更新
    @PostMapping
    public Result save(@RequestBody User user) {
        String username = user.getUsername();
        if (StrUtil.isBlank(username)) {
            return Result.error(Constants.CODE_400, "参数错误");
        }
        if (user.getId() != null) {
            user.setPassword(null);
        } else {
            user.setUsername(user.getUsername());
            if (user.getPassword() == null) {
                user.setPassword("123456");
            }
        }

        return Result.success(userService.saveOrUpdate(user));
    }

    @PostMapping("/update")
    public Result updateUser(@RequestBody User user) {
        userService.updateById(user);
        return Result.success(user);
    }

    // 单个删除
    @DeleteMapping({"/{id}"})
    public Result delete(@PathVariable Integer id) {
        return Result.success(userService.removeById(id));
    }
    // 批量删除
    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        return Result.success(userService.removeByIds(ids));
    }

    @PostMapping("/login")
    public Result login(@RequestBody UserDTO userDTO) {

        String username = userDTO.getUsername();
        String password = userDTO.getPassword();
        if(StrUtil.isBlank(username) || StrUtil.isBlank(password)) {
            return Result.error(Constants.CODE_400, "参数错误");
        }
        UserDTO dto = userService.login(userDTO);

        return Result.success(dto);
    }

    @PostMapping("/password")
    public Result password(@RequestBody UserPasswordDTO userPasswordDTO) {
        userService.updatePassword(userPasswordDTO);
        return Result.success();
    }

    @PostMapping("/send-code")
    public Result sendCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        if (StrUtil.isBlank(email) || !email.contains("@")) {
            return Result.error(Constants.CODE_400, "邮箱格式不正确");
        }
        userService.sendVerificationCode(email);
        return Result.success("验证码已发送至您的邮箱，有效期5分钟");
    }
    @PostMapping("/login-email")
    public Result loginWithEmail(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String code = request.get("code");
        if (StrUtil.isBlank(email) || StrUtil.isBlank(code)) {
            return Result.error(Constants.CODE_400, "邮箱或验证码不能为空");
        }
        UserDTO dto = userService.loginWithCode(email, code);
        return Result.success(dto);
    }
}
