package com.progsail.fastlink.project.dto.req;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.progsail.fastlink.project.dao.entity.ShortLinkDO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author yangfan
 * @version 1.0
 * @description: 回收站分页请求实体
 * @date 2024/7/18 20:09
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecycleBinPageReqDTO extends Page<ShortLinkDO> {

    // 分组列表
    List<String> gidList;
}
