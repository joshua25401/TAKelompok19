package com.tugasakhir.elasticsearchkelompok19.controller;

import com.tugasakhir.elasticsearchkelompok19.services.DocumentServices;
import org.elasticsearch.ElasticsearchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    DocumentServices services;

    @GetMapping(value = "/")
    public String showAdmin() {
        return "AdminCrud";
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
}
