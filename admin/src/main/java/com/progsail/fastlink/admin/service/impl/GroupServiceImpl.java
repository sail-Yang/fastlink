package com.progsail.fastlink.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.progsail.fastlink.admin.common.biz.user.UserContext;
import com.progsail.fastlink.admin.common.convention.exception.ServiceException;
import com.progsail.fastlink.admin.dao.entity.GroupDO;
import com.progsail.fastlink.admin.dao.mapper.GroupMapper;
import com.progsail.fastlink.admin.dto.resp.ShortLinkGroupRespDTO;
import com.progsail.fastlink.admin.service.GroupService;
import com.progsail.fastlink.admin.util.GIDRandomGeneratorUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author yangfan
 * @version 1.0
 * @description: 短链接分组服务层实现
 * @date 2024/2/17 17:18
 */
@Service
@RequiredArgsConstructor
public class GroupServiceImpl extends ServiceImpl<GroupMapper, GroupDO> implements GroupService{

    @Override
    public void saveGroup(String name) {
        String gid = GIDRandomGeneratorUtil.generateRandomID();
        while(hasGid(gid)){
            gid = GIDRandomGeneratorUtil.generateRandomID();
        }
        //TODO username暂时从UserContext获取，未从网关获取
        GroupDO groupDo = GroupDO.builder()
                .gid(gid)
                .name(name)
                .username(UserContext.getUsername())
                .sortOrder(0)
                .build();
        try {
            baseMapper.insert(groupDo);
        } catch (Exception e) {
            throw new ServiceException("分组插入失败");
        }

    }

    @Override
    public Boolean hasGid(String gid) {
        LambdaQueryWrapper<GroupDO> queryWrapper = Wrappers
                .lambdaQuery(GroupDO.class)
                .eq(GroupDO::getGid, gid);
        GroupDO groupDO = baseMapper.selectOne(queryWrapper);
        return groupDO != null;
    }

    @Override
    public List<ShortLinkGroupRespDTO> sortList() {
        LambdaQueryWrapper<GroupDO> queryWrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getDelFlag, 0)
                .eq(GroupDO::getUsername, UserContext.getUsername())
                .orderByDesc(GroupDO::getSortOrder, GroupDO::getUpdateTime);
        List<GroupDO> groupDOList = baseMapper.selectList(queryWrapper);
        return BeanUtil.copyToList(groupDOList, ShortLinkGroupRespDTO.class);
    }
}
