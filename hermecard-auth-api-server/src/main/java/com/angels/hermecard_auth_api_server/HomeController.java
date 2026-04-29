package com.angels.hermecard_auth_api_server;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "index";
    }

    /**
     * FIX: Este método serve o template Thymeleaf do formulário de login customizado.
     *
     * IMPORTANTE: NÃO mapeie POST /login aqui. O Spring Security intercepta o
     * POST /login automaticamente via UsernamePasswordAuthenticationFilter com base
     * no loginProcessingUrl configurado no SecurityConfig.
     *
     * O parâmetro "error" é detectado pelo Thymeleaf via th:if="${param.error}"
     * e o parâmetro "logout" via th:if="${param.logout}" — ambos já estão no login.html.
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    /**
     * Callback para receber o código de autorização OAuth2 após o fluxo de autorização.
     */
    @GetMapping("/callback")
    @ResponseBody
    public String callback(@RequestParam String code) {
        return "Authorization code received: " + code;
    }
}