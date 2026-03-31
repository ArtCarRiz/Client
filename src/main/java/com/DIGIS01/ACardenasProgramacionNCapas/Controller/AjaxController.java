/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.DIGIS01.ACardenasProgramacionNCapas.Controller;

import com.DIGIS01.ACardenasProgramacionNCapas.ML.Result;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author artur
 */
@RestController
@RequestMapping("/ajax")
public class AjaxController {

    private static String rutaBase = "http://localhost:8080";

    public HttpEntity<Void> Cabecera(HttpSession session) {
        String token = (String) session.getAttribute("token");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        //entidad de la peticion
        HttpEntity<Void> entity;
        entity = new HttpEntity<>(headers);
        return entity;
    }

    @GetMapping("/estados")
    public Result estados(@RequestParam int idPais, HttpSession session) {
        Result result = new Result();

        try {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Result> response = restTemplate.exchange(
                    rutaBase + "/api/estado?identificador=" + idPais,
                    HttpMethod.GET,
                    Cabecera(session),
                    Result.class
            );

            return response.getBody();
        } catch (Exception e) {
            result.correct = false;
            result.errorMessage = e.getLocalizedMessage();
            result.ex = e;
        }

        return result;
    }

    @GetMapping("/municipios")
    public Result municipios(@RequestParam int idEstado, HttpSession session) {
        Result result = new Result();

        try {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Result> response = restTemplate.exchange(
                    rutaBase + "/api/municipio?identificador=" + idEstado,
                    HttpMethod.GET,
                    Cabecera(session),
                    Result.class
            );

            return response.getBody();
        } catch (Exception e) {
            result.correct = false;
            result.errorMessage = e.getLocalizedMessage();
            result.ex = e;
        }

        return result;
    }

    @GetMapping("/colonias")
    public Result colonias (@RequestParam int idColonia, HttpSession session) {
        Result result = new Result();

        try {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Result> response = restTemplate.exchange(
                    rutaBase + "/api/colonia?identificador=" + idColonia,
                    HttpMethod.GET,
                    Cabecera(session),
                    Result.class
            );

            return response.getBody();
        } catch (Exception e) {
            result.correct = false;
            result.errorMessage = e.getLocalizedMessage();
            result.ex = e;
        }

        return result;
    }

}
