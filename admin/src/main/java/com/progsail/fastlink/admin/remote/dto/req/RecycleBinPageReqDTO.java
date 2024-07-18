package com.progsail.fastlink.admin.remote.dto.req;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
public class RecycleBinPageReqDTO extends Page {

    // 分组列表
    List<String> gidList;
}
