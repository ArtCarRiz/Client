/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.DIGIS01.ACardenasProgramacionNCapas.Controller;

import com.DIGIS01.ACardenasProgramacionNCapas.ML.Pais;
import com.DIGIS01.ACardenasProgramacionNCapas.ML.Result;
import com.DIGIS01.ACardenasProgramacionNCapas.ML.Usuario;
import java.util.List;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
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
                new ParameterizedTypeReference<Result<Usuario>>() {
        });

        if (responseEntity.getStatusCode().value() == 200) { // OK
            Result result = responseEntity.getBody();
            model.addAttribute("usuarios", result.objects);
            model.addAttribute("usuario", new Usuario());
        }

        return "usuario";

    }

    @GetMapping("form")
    public String Accion(Model model) {
        Result result = new Result();

        RestTemplate restTemplate = new RestTemplate();

        model.addAttribute("usuario", new Usuario());

        try {
            //paises
            ResponseEntity<Result> responsePaises = restTemplate.exchange(rutaBase + "/api/usuario/Pais",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<Result>() {
            });
            model.addAttribute("paises", responsePaises.getBody().objects);

            //roles
            ResponseEntity<Result> responseRoles = restTemplate.exchange(rutaBase + "/api/usuario/Rol",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<Result>() {
            });
            model.addAttribute("roles", responseRoles.getBody().objects);
        } catch (Exception e) {
            System.err.println("Error en RestTemplate: " + e.getMessage());
            model.addAttribute("error", "Error de conexión: " + e.getLocalizedMessage());
        }

        return "Form";
    }

    @PostMapping("form")                                                                                                        //del model vienen todas las modificaciones
    public String Accion(@ModelAttribute("usuario") Usuario usuario, BindingResult bindingResult, @RequestParam("imagenFile") MultipartFile imagenFile, Model model) {
        Result result = new Result();
        RestTemplate restTemplate = new RestTemplate();
        try {

            if (bindingResult.hasErrors()) {
                model.addAttribute("usuario", usuario);
                model.addAttribute("paises", restTemplate.getForObject(rutaBase + "/api/usuario/Pais", List.class));
                model.addAttribute("roles", restTemplate.getForObject(rutaBase + "/api/usuario/Rol", List.class));

                //guardo en variables los id
                int idPais = usuario.Direcciones.get(0).colonia.municipio.estado.pais.getIdPais();
                int idEstado = usuario.Direcciones.get(0).colonia.municipio.estado.getIdEstado();
                int idMunicipio = usuario.Direcciones.get(0).colonia.municipio.getIdMunicipio();
                int idColonia = usuario.Direcciones.get(0).colonia.getIdColonia();

                if (idEstado != 0) {
                    //guardo el valor
                    model.addAttribute("estados", restTemplate.getForObject(rutaBase + "/api/usuario/Estado?identificador=" + idEstado, List.class));
                    if (idMunicipio != 0) {
                        //guardo el valor
                        model.addAttribute("municipios", restTemplate.getForObject(rutaBase + "/api/usuario/Municipio?identificador=" + idMunicipio, List.class));
                        if (idColonia != 0) {
                            //guardo el valor
                            model.addAttribute("colonias", restTemplate.getForObject(rutaBase + "/api/usuario/Colonia?identificador=" + idColonia, List.class));

                        }
                    }

                }
                return "form";
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            usuario.setImagen(null);
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

            body.add("datos", usuario);

            if (!imagenFile.isEmpty()) {
                body.add("imagen", imagenFile.getResource());
            }

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            
            ResponseEntity<Result> response = restTemplate.exchange(rutaBase + "/api/usuario",
                    HttpMethod.POST,
                    requestEntity,
                    Result.class);
            
            result.correct = true;
            if (result.correct) {
                return "redirect:/usuario";
            } else {
                model.addAttribute("error", response.getBody().errorMessage);
                return "form";
            }

        } catch (Exception e) {
            result.correct = false;
            result.errorMessage = e.getLocalizedMessage();
            result.ex = e;
        }
        return "redirect:/usuario";
    }

}
