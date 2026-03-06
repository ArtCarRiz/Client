/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.DIGIS01.ACardenasProgramacionNCapas.Controller;

import com.DIGIS01.ACardenasProgramacionNCapas.ML.Colonia;
import com.DIGIS01.ACardenasProgramacionNCapas.ML.Direccion;
import com.DIGIS01.ACardenasProgramacionNCapas.ML.ErroresArchivo;
import com.DIGIS01.ACardenasProgramacionNCapas.ML.Estado;
import com.DIGIS01.ACardenasProgramacionNCapas.ML.Result;
import com.DIGIS01.ACardenasProgramacionNCapas.ML.Rol;
import com.DIGIS01.ACardenasProgramacionNCapas.ML.Usuario;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.io.BufferedReader;
import java.io.File;
import java.sql.CallableStatement;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import java.util.UUID;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
//import org.apache.commons.io.FileUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 *
 * @author digis
 */
@Controller
@RequestMapping("usuario")
public class UsuarioController {


    @GetMapping
    public String Index(Model model) {
//        Result result = usuarioDAOJPAImplementation.GetAll();
////        Result result = usuarioDAOImplementation.GetAll();
//
//        model.addAttribute("usuarios", result.objects);
//        model.addAttribute("usuario", new Usuario());
//        model.addAttribute("roles", rolDAOImplementation.GetAll().objects);
////        model.addAttribute("paises", paisDAOImplementation.GetAll().objects);
//
//        model.addAttribute("usuariobusqueda", new Usuario());

        return "usuario";

    }



}
