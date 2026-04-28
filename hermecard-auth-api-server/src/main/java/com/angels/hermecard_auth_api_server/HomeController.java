package com.angels.hermecard_auth_api_server;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomeController {

    // ✅ Descomentado: rota raiz agora funciona
    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // ✅ Callback para receber o código de autorização OAuth2
    @GetMapping("/callback")
    @ResponseBody
    public String callback(@RequestParam String code) {
        return "Authorization code received: " + code;
    }
}
