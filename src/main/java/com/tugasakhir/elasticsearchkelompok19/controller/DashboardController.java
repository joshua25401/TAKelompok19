package com.tugasakhir.elasticsearchkelompok19.controller;

import com.tugasakhir.elasticsearchkelompok19.model.PDFDocument;
import com.tugasakhir.elasticsearchkelompok19.services.DocumentServices;
import org.elasticsearch.ElasticsearchException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class DashboardController {

    Logger log = LoggerFactory.getLogger(DashboardController.class);
    List<PDFDocument> listPdf = new ArrayList<>();

    String username = "Admin";
    String password = "Admin123";

    @Autowired
    DocumentServices services;

    @GetMapping(value = "/index")
    public String showDashboard() {
        log.info("Showing Dashboard Page ...");
        return "DashboardPage";
    }

    @GetMapping(value = "/upload")
    public String showUpload() {
        log.info("Showing Upload Page ...");
        return "UploadPage";
    }

    @GetMapping(value = "/search")
    public String search(
            @RequestParam("keyword") String keyword
    ) {
        log.info("Searching for " + keyword);
        listPdf = services.fullTextSearch(keyword);

        if (listPdf != null) {
            return "redirect:/admin";
        }
        return "redirect:/";
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

    @PostMapping(value = "/login")
    public String doLogin(
            @RequestParam("username") String username,
            @RequestParam("password") String password
    ) {

        if (this.username.equals(username) && this.password.equals(password)) {
            return "redirect:/admin";
        }
        return "redirect:/";
    }


}
