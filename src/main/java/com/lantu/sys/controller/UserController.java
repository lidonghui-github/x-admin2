package com.lantu.sys.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lantu.common.vo.Result;
import com.lantu.sys.entity.User;
import com.lantu.sys.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author lidonghui
 * @since 2023-03-16
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    IUserService userService;

    @Autowired
    PasswordEncoder passwordEncoder;


    @GetMapping("/all")
    public Result<List<User>> findAll() {
        List<User> list = userService.list();
        return Result.success(list, "查询成功");
    }

    /**
     * 登录
     *
     * @param user
     * @return
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody User user) {
        Map<String, Object> data = userService.login(user);
        if (data != null) {
            return Result.success(data);
        } else {
            return Result.fail(20002, "用户名或密码不正确");
        }
    }

    /**
     * 获取用户信息
     *
     * @param token
     * @return
     */
    @GetMapping("/info")
    public Result<Map<String, Object>> getUserInfo(@RequestParam("token") String token) {
        //根据token获取用户信息
        Map<String, Object> data = userService.getUserInfo(token);
        if (data != null) {
            return Result.success(data);
        } else {
            return Result.fail(20003, "登录信息无效，清重新登录");
        }

    }

    /**
     * 退出登录
     *
     * @param token
     * @return
     */

    @PostMapping("/logout")
    public Result<?> logout(@RequestHeader("X-Token") String token) {
        userService.logout(token);
        return Result.success();
    }

    @GetMapping("/list")
    public Result<Map<String, Object>> getUserList(@RequestParam(value = "username", required = false) String username,
                                                   @RequestParam(value = "phone", required = false) String phone,
                                                   @RequestParam(value = "pageNo") Integer pageNo,
                                                   @RequestParam(value = "pageSize") Integer pageSize) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.hasLength(username), User::getUsername, username);
        wrapper.eq(StringUtils.hasLength(phone), User::getPhone, phone);
        wrapper.orderByDesc(User::getId);
        Page<User> page = new Page<>(pageNo, pageSize);
        userService.page(page, wrapper);

        Map<String, Object> data = new HashMap<>();
        data.put("total", page.getTotal());
        data.put("rows", page.getRecords());

        return Result.success(data);
    }

    /**
     * 新增用户
     *
     * @param user
     * @return
     */

    @PostMapping
    public Result<?> addUser(@RequestBody User user) {
        //加密
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userService.save(user);

        return Result.success("新增用户成功");
    }

    /**
     * 修改用户信息
     *
     * @param user
     * @return
     */
    @PutMapping
    public Result<?> updateUser(@RequestBody User user) {
        //加密
        user.setPassword(null);
        userService.updateById(user);

        return Result.success("修改用户成功");
    }

    /**
     * 根据 id 查询用户
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<User> getUserById(@PathVariable("id") int id) {
        User user = userService.getById(id);
        return Result.success(user);
    }

    /**
     * 删除用户
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public Result<?> deleteUserById(@PathVariable("id") int id) {
        userService.removeById(id);
        return Result.success("删除用户成功");
    }

}
