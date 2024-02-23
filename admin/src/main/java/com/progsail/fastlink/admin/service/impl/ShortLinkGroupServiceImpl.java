package com.progsail.fastlink.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.progsail.fastlink.admin.common.biz.user.UserContext;
import com.progsail.fastlink.admin.common.convention.exception.ServiceException;
import com.progsail.fastlink.admin.dao.entity.GroupDO;
import com.progsail.fastlink.admin.dao.mapper.GroupMapper;
import com.progsail.fastlink.admin.dto.req.ShortLinkGroupDeleteReqDTO;
import com.progsail.fastlink.admin.dto.req.ShortLinkGroupSortReqDTO;
import com.progsail.fastlink.admin.dto.req.ShortLinkGroupUpdateReqDTO;
import com.progsail.fastlink.admin.dto.resp.ShortLinkGroupRespDTO;
import com.progsail.fastlink.admin.remote.ShortLinkRemoteService;
import com.progsail.fastlink.admin.remote.dto.resp.ShortLinkGroupCountRespDTO;
import com.progsail.fastlink.admin.service.ShortLinkGroupService;
import com.progsail.fastlink.admin.util.GIDRandomGeneratorUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author yangfan
 * @version 1.0
 * @description: 短链接分组服务层实现
 * @date 2024/2/17 17:18
 */
@Service
@RequiredArgsConstructor
public class ShortLinkGroupServiceImpl extends ServiceImpl<GroupMapper, GroupDO> implements ShortLinkGroupService {

    private final ShortLinkRemoteService shortLinkRemoteService;

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
        List<ShortLinkGroupRespDTO> shortLinkGroupRespList = BeanUtil.copyToList(groupDOList, ShortLinkGroupRespDTO.class);

        List<ShortLinkGroupCountRespDTO> countList = shortLinkRemoteService.groupShortLinkCount(
                groupDOList.stream().map(GroupDO::getGid).toList()
        ).getData();
        Map<String, Integer> gidCountMap = countList.stream().collect(Collectors.toMap(ShortLinkGroupCountRespDTO::getGid, ShortLinkGroupCountRespDTO::getShortLinkCount));
        return shortLinkGroupRespList.stream().peek(each -> each.setShortLinkCount(gidCountMap.getOrDefault(each.getGid(), 0))).toList();
    }

    @Override
    public void updateGroup(ShortLinkGroupUpdateReqDTO requestParam) {
        LambdaUpdateWrapper<GroupDO> updateWrapper = Wrappers.lambdaUpdate(GroupDO.class)
                .eq(GroupDO::getGid,requestParam.getGid())
                .eq(GroupDO::getUsername, UserContext.getUsername())
                .eq(GroupDO::getDelFlag, 0);
        GroupDO groupDO = new GroupDO(requestParam.getName());
        baseMapper.update(groupDO,updateWrapper);
    }

    @Override
    public void deleteGroup(ShortLinkGroupDeleteReqDTO requestParam) {
        LambdaUpdateWrapper<GroupDO> deleteWrapper = Wrappers.lambdaUpdate(GroupDO.class)
                .eq(GroupDO::getGid, requestParam.getGid())
                .eq(GroupDO::getUsername, UserContext.getUsername())
                .eq(GroupDO::getDelFlag, 0);
        GroupDO groupDO = new GroupDO();
        groupDO.setDelFlag(1);
        baseMapper.update(groupDO,deleteWrapper);
    }

    @Override
    public void sortGroup(List<ShortLinkGroupSortReqDTO> requestParam) {
        requestParam.forEach(each -> {
            GroupDO groupDO = GroupDO.builder()
                    .sortOrder(each.getSortOrder())
                    .build();
            LambdaUpdateWrapper<GroupDO> updateWrapper = Wrappers.lambdaUpdate(GroupDO.class)
                    .eq(GroupDO::getUsername, UserContext.getUsername())
                    .eq(GroupDO::getGid, each.getGid())
                    .eq(GroupDO::getDelFlag, 0);
            baseMapper.update(groupDO, updateWrapper);
        });
    }

}
