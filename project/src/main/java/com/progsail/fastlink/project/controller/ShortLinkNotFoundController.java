package com.progsail.fastlink.project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author yangfan
 * @version 1.0
 * @description: 找不到短链接跳转
 * @date 2024/7/3 16:39
 */
@Controller
public class ShortLinkNotFoundController {
    @RequestMapping("/page/nofound")
    public String notFound() {
        return "nofound";
    }
}
