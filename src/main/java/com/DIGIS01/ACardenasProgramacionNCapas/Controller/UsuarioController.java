/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.DIGIS01.ACardenasProgramacionNCapas.Controller;

import com.DIGIS01.ACardenasProgramacionNCapas.ML.Result;
import com.DIGIS01.ACardenasProgramacionNCapas.ML.Usuario;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;
//import org.apache.commons.io.FileUtils;


/**
 *
 * @author digis
 */
@Controller
@RequestMapping("usuario")
public class UsuarioController {
 
     private static String rutaBase = "http://127.0.0.1:8080";

    @GetMapping
    public String Index(Model model) {
        
        RestTemplate restTemplate = new RestTemplate();
        
        ResponseEntity<Result<Usuario>> responseEntity = restTemplate.exchange(rutaBase + "/api/usuario", 
                HttpMethod.GET, 
                HttpEntity.EMPTY, 
                new ParameterizedTypeReference<Result<Usuario>>(){});
        
        if (responseEntity.getStatusCode().value() == 200) { // OK
            Result result = responseEntity.getBody();
            model.addAttribute("usuarios", result.objects);
        }

        return "usuario";

    }
    
     

}
