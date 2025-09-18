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
                           @RequestParam(required = false) String nickname,
                           @RequestParam(required = false) String email,
                           @RequestParam(required = false) String avatar,
                           @RequestParam(required = false) String hobby,
                           @RequestParam(required = false) String age,
                           @RequestParam(required = false) String role,
                           @RequestParam(required = false) LocalDateTime createTime) {
        // Mybatis-plus
        IPage<User> page = new Page<>(pageNum, pageSize); // 构建分页
        QueryWrapper<User> queryWrapper = buildQueryWrapper(username, nickname, email, avatar, hobby, age, role, createTime);

        IPage<User> result = userService.page(page, queryWrapper);

        return Result.success(result);

    }

    private QueryWrapper<User> buildQueryWrapper(String username, String nickname,
                                                 String email, String avatar,
                                                 String hobby, String age,
                                                 String role, LocalDateTime createTime) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();

        if (StrUtil.isNotBlank(username)) {
            queryWrapper.like("username", username);
        }
        if (StrUtil.isNotBlank(nickname)){
            queryWrapper.like("nickname", nickname);
        }
        if (email != null && !email.isEmpty()) {
            queryWrapper.like("email", email);
        }
        if (avatar != null && !avatar.isEmpty()) {
            queryWrapper.like("avatar", avatar);
        }
        if (hobby != null && !hobby.isEmpty()) {
            queryWrapper.like("hobby", hobby);
        }
        if (age != null && !age.isEmpty()) {
            queryWrapper.like("age", age);
        }
        if (role != null && !role.isEmpty()) {
            queryWrapper.like("role", role);
        }
        if (createTime != null) {
            queryWrapper.like("username", username);
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

    // 导入接口
    @PostMapping("/import")
    public Result imp(MultipartFile file) throws Exception{
        InputStream inputStream = file.getInputStream();
        ExcelReader reader = ExcelUtil.getReader(inputStream);
        List<User> list = reader.read(0, 1, User.class);
        userService.saveBatch(list);
        return Result.success(true);
    }

    // 导出
    @GetMapping("/export")
    public void export(HttpServletResponse response) {
        // 设置响应头和内容类型
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("UTF-8");

        try {
            // 编码文件名，防止中文乱码
            String fileName = URLEncoder.encode("用户信息", StandardCharsets.UTF_8);
            response.setHeader("Content-disposition",
                    "attachment;filename*=UTF-8''" + fileName + ".xlsx");

            // 获取数据列表
            List<User> userList = userService.list();
            if (userList == null || userList.isEmpty()) {
                // 处理空数据情况
                response.getWriter().write("没有可导出的数据");
                return;
            }

            // 创建ExcelWriter
            ExcelWriter writer = ExcelUtil.getWriter(true);

            // 自定义标题别名
            writer.addHeaderAlias("username", "用户名");
            writer.addHeaderAlias("nickname", "昵称");
            writer.addHeaderAlias("role", "角色");
            writer.addHeaderAlias("email", "邮箱");
            writer.addHeaderAlias("age", "年龄");
            writer.addHeaderAlias("hobby", "爱好");

            // 美化表格 - 设置列宽
            writer.setColumnWidth(0, 20);  // 用户名
            writer.setColumnWidth(1, 20);  // 昵称
            writer.setColumnWidth(2, 20);  // 角色
            writer.setColumnWidth(3, 30);  // 邮箱
            writer.setColumnWidth(4, 20);  // 年龄
            writer.setColumnWidth(5, 20);  // 角色

            // 美化标题行
            writer.getStyleSet().setFont((short) 0, (short) 12, "微软雅黑", true);

            // 写入数据
            writer.write(userList, true);

            // 输出到浏览器
            ServletOutputStream out = response.getOutputStream();
            writer.flush(out, true);

            // 关闭资源
            out.close();
            writer.close();

        } catch (Exception e) {
            // 处理异常，可根据实际情况记录日志
            try {
                response.setContentType("text/plain;charset=UTF-8");
                response.getWriter().write("导出失败：" + e.getMessage());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
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
}
