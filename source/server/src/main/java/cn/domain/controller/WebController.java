package cn.domain.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class WebController {

    // 访问首页
    @RequestMapping("/")
    public String index() {
        return "index"; // 对应 templates/index.html
    }

    @RequestMapping("/login/*")
    public String login() {
        return "index"; // 对应 templates/index.html
    }

    @RequestMapping("/app/*")
    public String app() {
        return "index"; // 对应 templates/index.html
    }
}
