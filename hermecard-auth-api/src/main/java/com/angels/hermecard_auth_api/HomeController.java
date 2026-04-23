package com.angels.hermecard_auth_api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomeController {

//    @GetMapping("/")
//    public String home() {
//
//        return "index";
//
//    }

    @GetMapping("/login")
    public String login() {

        return "login";

    }

    @GetMapping("/callback")
    @ResponseBody
    public String callback(@RequestParam String code) {
        return "Authorization code: " + code;
    }

}
