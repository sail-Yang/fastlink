package com.progsail.fastlink.admin.controller;

import cn.hutool.core.bean.BeanUtil;
import com.progsail.fastlink.admin.common.convention.result.Result;
import com.progsail.fastlink.admin.common.convention.result.Results;
import com.progsail.fastlink.admin.dto.req.UserLoginReqDTO;
import com.progsail.fastlink.admin.dto.req.UserRegisterReqDTO;
import com.progsail.fastlink.admin.dto.req.UserUpdateReqDTO;
import com.progsail.fastlink.admin.dto.resp.UserActualRespDTO;
import com.progsail.fastlink.admin.dto.resp.UserLoginRespDTO;
import com.progsail.fastlink.admin.dto.resp.UserRespDTO;
import com.progsail.fastlink.admin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * @author yangfan
 * @version 1.0
 * @description: 用户控制管理层
 * @date 2024/2/13 9:45
 */
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    /**
    * 根据用户名查询用户信息（脱敏）
    */
    @GetMapping("/api/fast-link/v1/user/{username}")
    public Result<UserRespDTO> getUserByUsername(@PathVariable("username") String username){
        return Results.success(userService.getUserByUsername(username));
    }

    /**
     * 根据用户名查询用户信息（无脱敏）
     */
    @GetMapping("/api/fast-link/v1/user/actual/{username}")
    public Result<UserActualRespDTO> getActualUserByUsername(@PathVariable("username") String username){
        return Results.success(BeanUtil.toBean(userService.getUserByUsername(username),UserActualRespDTO.class));
    }

    /**
     * 查询用户名是否存在
     */
    @GetMapping("/api/fast-link/v1/user/has-username")
    public Result<Boolean> hasUsername(@RequestParam("username") String username) {
        return Results.success(userService.hasUsername(username));
    }

    /**
     * 用户注册
     */
    @PostMapping("/api/fast-link/v1/user/register")
    public Result<Void> register(@RequestBody UserRegisterReqDTO requestParam) {
        userService.register(requestParam);
        return Results.success();
    }

    /**
     * 用户信息修改
     */
    @PutMapping("/api/fast-link/v1/user/update")
    public Result<Void> updateUser(@RequestBody UserUpdateReqDTO requestParam) {
        userService.updateUser(requestParam);
        return Results.success();
    }

    /**
     * 用户登录
     */
    @PostMapping("/api/fast-link/v1/user/login")
    public Result<UserLoginRespDTO> login(@RequestBody UserLoginReqDTO requestParam) {
        return Results.success(userService.login(requestParam));
    }

    /**
     * 检查用户是否登录
     */
    @GetMapping("/api/fast-link/admin/v1/user/check-login")
    public Result<Boolean> checkLogin(@RequestParam("username") String username, @RequestParam("token") String token) {
        return Results.success(userService.checkLogin(username, token));
    }

    /**
     * 用户登出
     * @param username
     * @param token
     * @return
     */
    @DeleteMapping("/api/fast-link/admin/v1/user/logout")
    public Result<Void> logout(@RequestParam("username") String username, @RequestParam("token") String token) {
        userService.logout(username, token);
        return Results.success();
    }
}
