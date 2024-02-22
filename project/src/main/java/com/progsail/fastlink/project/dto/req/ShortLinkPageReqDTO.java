package com.progsail.fastlink.project.dto.req;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.progsail.fastlink.project.dao.entity.ShortLinkDO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yangfan
 * @version 1.0
 * @description: 短链接分页请求实体
 * @date 2024/2/22 18:03
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShortLinkPageReqDTO extends Page<ShortLinkDO> {
    /**
     * 分组标识
     */
    private String gid;
}
