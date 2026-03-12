/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.DIGIS01.ACardenasProgramacionNCapas.Controller;

import com.DIGIS01.ACardenasProgramacionNCapas.ML.Direccion;
import com.DIGIS01.ACardenasProgramacionNCapas.ML.Pais;
import com.DIGIS01.ACardenasProgramacionNCapas.ML.Result;
import com.DIGIS01.ACardenasProgramacionNCapas.ML.Usuario;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import tools.jackson.databind.ObjectMapper;
//import org.apache.commons.io.FileUtils;

/**
 *
 * @author digis
 */
@Controller
@RequestMapping("usuario")
public class UsuarioController {

//    private static String rutaBase = "http://127.0.0.1:8080";
    private static String rutaBase = "http://localhost:8080";

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

    @GetMapping("/delete/{IdUsuario}")
    public String DeteleUsuario(@PathVariable("IdUsuario") int identificador, RedirectAttributes redirectAttributes) {
        Result result = new Result();

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<Result> responseBorrar = restTemplate.exchange(rutaBase + "/api/usuario/Delete/Usuario/{identificador}",
                HttpMethod.DELETE,
                null,
                new ParameterizedTypeReference<Result>() {
        },
                identificador);

        result.correct = true;

        if (result.correct) {
            redirectAttributes.addFlashAttribute("mensaje", "Borrado exitosamente");
            return "redirect:/usuario";
        } else {
            return "redirect:/usuario";
        }

    }

    @GetMapping("/details/{IdUsuario}")
    public String Details(@PathVariable("IdUsuario") int identificador, Model model) {
        RestTemplate restTemplate = new RestTemplate();
        try {

            ResponseEntity<Result<Usuario>> responseGetById = restTemplate.exchange(rutaBase + "/api/usuario/{identificador}",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<Result<Usuario>>() {
            },
                    identificador);

            if (responseGetById.getStatusCode().value() == 200) { // OK

                Result result = responseGetById.getBody();

                model.addAttribute("usuario", result.object);
                model.addAttribute("direccion", new Direccion());
                //roles
                ResponseEntity<Result> responseRoles = restTemplate.exchange(rutaBase + "/api/usuario/Rol",
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<Result>() {
                });
                model.addAttribute("roles", responseRoles.getBody().objects);

                //roles
                ResponseEntity<Result> responsePaises = restTemplate.exchange(rutaBase + "/api/usuario/Pais",
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<Result>() {
                });
                model.addAttribute("paises", responsePaises.getBody().objects);

                Result<Usuario> usuario = responseGetById.getBody();

//                int idPais = usuario.object.Direcciones.get(0).colonia.municipio.estado.pais.getIdPais();
//                int idEstado = usuario.object.Direcciones.get(0).colonia.municipio.estado.getIdEstado();
//                int idMunicipio = usuario.object.Direcciones.get(0).colonia.municipio.getIdMunicipio();
//                int idColonia = usuario.object.Direcciones.get(0).colonia.getIdColonia();
//
//                if (idEstado != 0) {
//                    //guardo el valor
//                    model.addAttribute("estados", restTemplate.getForObject(rutaBase + "/api/usuario/Estado?identificador=" + idPais, List.class, idPais));
//                    if (idMunicipio != 0) {
//                        //guardo el valor
//                        model.addAttribute("municipios", restTemplate.getForObject(rutaBase + "/api/usuario/Municipio?identificador=" + idEstado, List.class, idEstado));
//                        if (idColonia != 0) {
//                            //guardo el valor
//                            model.addAttribute("colonias", restTemplate.getForObject(rutaBase + "/api/usuario/Colonia?identificador=" + idMunicipio, List.class, idMunicipio));
//
//                        }
//                    }
//
//                }
            }
        } catch (Exception e) {
            System.out.println("Error en details: " + e.getLocalizedMessage());
        }

        return "details";
    }

    @PostMapping("/update/{IdUsuario}")
    public String UpdateUsuario(@ModelAttribute("usuario") Usuario usuario, @PathVariable("IdUsuario") int identificador, Model model) {

        Result result = new Result();
        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<Result<Usuario>> responseGetById = restTemplate.exchange(rutaBase + "/api/usuario/{identificador}",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<Result<Usuario>>() {
            },
                    identificador);

            result = responseGetById.getBody();

            if (result.correct) {
                Usuario usuarioanterior = (Usuario) result.object;
                usuario.setImagen(usuarioanterior.getImagen());
            }

            ResponseEntity<Result> responseUpdate = restTemplate.exchange(rutaBase + "/api/usuario",
                    HttpMethod.PUT,
                    new HttpEntity<>(usuario),
                    new ParameterizedTypeReference<Result>() {
            });

            result = responseUpdate.getBody();

            if (!result.correct) {
                return "redirect:/usuario/details/" + identificador;
            }

        } catch (Exception e) {
            result.correct = false;
            result.errorMessage = e.getLocalizedMessage();
            result.ex = e;
        }

        return "redirect:/usuario/details/" + identificador;
    }

    @PostMapping("update/{IdDireccion}/{IdUsuario}")
    public String UpdateDireccion(@ModelAttribute("direccion") Direccion direccion, @PathVariable("IdDireccion") int IdDireccion, @PathVariable("IdUsuario") int IdUsuario, RedirectAttributes redirectAttributes) {
        Result result = new Result();
        RestTemplate restTemplate = new RestTemplate();
        direccion.setIdDireccion(IdDireccion);
        try {

            ResponseEntity<Result> responseUpdateDireccion = restTemplate.exchange(rutaBase + "/api/usuario/Direccion",
                    HttpMethod.PUT,
                    new HttpEntity<>(direccion),
                    new ParameterizedTypeReference<Result>() {
            });

            result = responseUpdateDireccion.getBody();

            if (result.correct) {
                redirectAttributes.addFlashAttribute("mensaje", "Dirección actualizada correctamente");
            } else {
                redirectAttributes.addFlashAttribute("error", "Error al actualizar: " + result.errorMessage);
            }

        } catch (Exception e) {
            result.correct = false;
            result.errorMessage = e.getLocalizedMessage();
            result.ex = e;
        }

        return "redirect:/usuario/details/" + IdUsuario;
    }

    @PostMapping("/addDireccion")
    public String AddDireccion(@ModelAttribute("direccion") Direccion direccion, @RequestParam("idUsuarioRelacion") int idUsuario, RedirectAttributes redirectAttributes) {
        Result result = new Result();
        RestTemplate restTemplate = new RestTemplate();
        try {

            ResponseEntity<Result> responseAddDireccion = restTemplate.exchange(rutaBase + "/api/usuario/Direccion?identificador=" + idUsuario,
                    HttpMethod.POST,
                    new HttpEntity<>(direccion),
                    Result.class);

            if (result.correct) {
                redirectAttributes.addFlashAttribute("mensaje", "Dirección agregada correctamente");
            } else {
                redirectAttributes.addFlashAttribute("error", "Error al agregar dirección: " + result.errorMessage);
            }

        } catch (Exception e) {
            result.correct = false;
            result.errorMessage = e.getLocalizedMessage();
            result.ex = e;
        }

        return "redirect:/usuario/details/" + idUsuario;
    }

    @PostMapping("/updateImagen")
    public String UpdateImagen(@RequestParam("idUsuario") int idUsuario, @RequestParam("imagenFile") MultipartFile imagen, RedirectAttributes redirectAttributes) {
        Result result = new Result();
        RestTemplate restTemplate = new RestTemplate();
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("imagenFile", imagen.getResource());
            
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            ResponseEntity<Result> responseUpdateImagen = restTemplate.exchange(rutaBase + "/api/usuario/Imagen?identificador=" + idUsuario,
                    HttpMethod.POST,
                    requestEntity,
                    Result.class);

            if (responseUpdateImagen.getBody() != null && responseUpdateImagen.getBody().correct) {
                redirectAttributes.addFlashAttribute("mensaje", "Imagen agregada correctamente");
            } else {
                redirectAttributes.addFlashAttribute("error", "Error al actualizar Imagen: " + result.errorMessage);
            }

        } catch (Exception e) {
            result.correct = false;
            result.errorMessage = e.getLocalizedMessage();
            result.ex = e;
        }
        return "redirect:/usuario/details/" + idUsuario;
    }

    @GetMapping("/cargaMasiva")
    public String CargaMasiva() {
        return "cargaMasiva";
    }
    
    
    
}
