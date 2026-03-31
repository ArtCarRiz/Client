/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.DIGIS01.ACardenasProgramacionNCapas.Controller;

import com.DIGIS01.ACardenasProgramacionNCapas.ML.Result;
import com.DIGIS01.ACardenasProgramacionNCapas.ML.Usuario;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.websocket.Session;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author artur
 */
@Controller
@RequestMapping("/login")
public class LoginController {

    private static String rutaLogin = "http://localhost:8080/auth/login";

    @GetMapping
    public String login(@RequestParam(value = "error", required = false) String error, Model model) {
        model.addAttribute("usuario", new Usuario());
        if (error != null) {
            model.addAttribute("errorlogin", error);
        }

        return "login";

    }

    @PostMapping
    public String procesar(@ModelAttribute("username") String username, @ModelAttribute("password") String password, HttpSession session, HttpServletResponse response) {
        Result result = new Result();
        try {

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> body = new HashMap<>();
            body.put("username", username);
            body.put("password", password);

            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            ResponseEntity<Result> responseLogin = restTemplate.exchange(rutaLogin,
                    HttpMethod.POST,
                    requestEntity,
                    Result.class);

            if (responseLogin.getBody() != null) {
                Result resultR = responseLogin.getBody();

                String token = (String) resultR.object;
                session.setAttribute("token", token);

                Cookie jwtCookie = new Cookie("jwtToken", token);
                jwtCookie.setHttpOnly(false); // Ponlo en false para que AJAX pueda leerlo
                jwtCookie.setPath("/");
                jwtCookie.setMaxAge(3600);

                response.addCookie(jwtCookie);

                String rolLogeado = resultR.errorMessage;
                if (resultR.correct) {
                    return "redirect:/usuario";
                }else{
                    return null;
                }

            }

        } catch (Exception e) {
        }
        return "/login";
    }

}
