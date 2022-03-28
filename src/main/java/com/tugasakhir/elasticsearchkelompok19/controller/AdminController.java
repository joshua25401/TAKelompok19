package com.tugasakhir.elasticsearchkelompok19.controller;

import com.tugasakhir.elasticsearchkelompok19.services.DocumentServices;
import org.elasticsearch.ElasticsearchException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    DocumentServices services;

    /*Logger*/
    final Logger log = LoggerFactory.getLogger(AdminController.class);

    @GetMapping(value = "/")
    public String showAdmin(HttpServletRequest request) {
        boolean isLogin = request.getSession().getAttribute("userLogin") != null;
        log.info("isLogin : {}", isLogin);
        if (isLogin) {
            return "AdminCrud";
        }else{
            return "redirect:/";
        }
    }

    @PostMapping(value = "/upload")
    public String doUpload(
            @RequestParam("file") MultipartFile file
    ) {
        try {
            services.upload(file);
            return "redirect:/upload";
        } catch (IOException | ElasticsearchException e) {
            e.printStackTrace();
        }
        return "redirect:/upload";
    }

    @GetMapping(value = "/logout")
    public String logout(HttpServletRequest request) {
        request.getSession().invalidate();
        log.info("request {}", request.getSession());
        return "redirect:/";
    }
}
