package com.progsail.fastlink.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.progsail.fastlink.admin.dao.entity.UserDO;
import com.progsail.fastlink.admin.dto.req.UserLoginReqDTO;
import com.progsail.fastlink.admin.dto.req.UserRegisterReqDTO;
import com.progsail.fastlink.admin.dto.req.UserUpdateReqDTO;
import com.progsail.fastlink.admin.dto.resp.UserLoginRespDTO;
import com.progsail.fastlink.admin.dto.resp.UserRespDTO;

/**
 * @author yangfan
 * @version 1.0
 * @description: 用户接口服务
 * @date 2024/2/13 20:48
 */
public interface UserService extends IService<UserDO> {
    /**
     * 根据用户名查询用户信息
     * @param username
     * @return
     */
    UserRespDTO getUserByUsername(String username);

    /**
     * 查询用户名是否存在
     * @param username 用户名
     * @return 用户名存在返回 True，不存在返回 False
     */
    Boolean hasUsername(String username);


    /**
     * 用户注册
     * @param requestParam
     * @return
     */
    void register(UserRegisterReqDTO requestParam);

    /**
     * 用户更新
     * @param requestParam
     * @return
     */
    void updateUser(UserUpdateReqDTO requestParam);

    /**
     * 用户登录
     * @param requestParam
     * @return
     */
    UserLoginRespDTO login(UserLoginReqDTO requestParam);

    /**
     * 检查用户是否登录
     * @param username
     * @param token
     * @return
     */
    Boolean checkLogin(String username, String token);
}
