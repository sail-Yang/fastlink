package com.progsail.fastlink.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.progsail.fastlink.admin.common.convention.exception.ClientException;
import com.progsail.fastlink.admin.common.convention.exception.ServiceException;
import com.progsail.fastlink.admin.common.enums.UserErrorCodeEnum;
import com.progsail.fastlink.admin.dao.entity.UserDO;
import com.progsail.fastlink.admin.dao.mapper.UserMapper;
import com.progsail.fastlink.admin.dto.req.UserRegisterReqDTO;
import com.progsail.fastlink.admin.dto.resp.UserRespDTO;
import com.progsail.fastlink.admin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import static com.progsail.fastlink.admin.common.constant.RedisCacheConstant.LOCK_USER_REGISTER_KEY;

/**
 * @author yangfan
 * @version 1.0
 * @description: 用户接口实现
 * @date 2024/2/13 21:00
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper,UserDO> implements UserService {

    private final RBloomFilter<String> userRegisterCachePenetrationBloomFilter;

    private final RedissonClient redissonClient;

    @Override
    public UserRespDTO getUserByUsername(String username) {
        LambdaQueryWrapper<UserDO> queryWrapper = Wrappers.lambdaQuery(UserDO.class)
                .eq(UserDO::getUsername,username);
        UserDO userDO = baseMapper.selectOne(queryWrapper);
        if(null == userDO) {
            throw  new ServiceException(UserErrorCodeEnum.USER_NULL);
        }
        UserRespDTO result = new UserRespDTO();
        BeanUtils.copyProperties(userDO,result);
        return result;
    }

    @Override
    public Boolean hasUsername(String username) {
        return userRegisterCachePenetrationBloomFilter.contains(username);
    }

    @Override
    public void register(UserRegisterReqDTO requestParam) {
        if(hasUsername(requestParam.getUsername())){
            throw new ClientException(UserErrorCodeEnum.USER_NAME_EXIST);
        }
        RLock lock = redissonClient.getLock(LOCK_USER_REGISTER_KEY + requestParam.getUsername());
        try {
            if(lock.tryLock()) {
                try {
                    int inserted = baseMapper.insert(BeanUtil.toBean(requestParam, UserDO.class));
                    if (inserted < 1) {
                        throw new ClientException(UserErrorCodeEnum.USER_SAVE_ERROR);
                    }
                } catch (DuplicateKeyException ex) {
                    throw new ClientException(UserErrorCodeEnum.USER_EXIST);
                }
            }
            userRegisterCachePenetrationBloomFilter.add(requestParam.getUsername());
            return;
        } catch (Throwable e) {
            throw new ClientException(UserErrorCodeEnum.USER_SAVE_ERROR);
        }
        finally {
            lock.unlock();
        }
    }
}
