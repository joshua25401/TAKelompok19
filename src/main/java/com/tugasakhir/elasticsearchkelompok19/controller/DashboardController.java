package com.tugasakhir.elasticsearchkelompok19.controller;

import com.tugasakhir.elasticsearchkelompok19.services.DocumentServices;
import org.elasticsearch.ElasticsearchException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
public class DashboardController {

    Logger log = LoggerFactory.getLogger(DashboardController.class);

    @Autowired
    DocumentServices services;

    @GetMapping(value = "/index")
    public String showDashboard() {
        return "DashboardPage";
    }

    @GetMapping(value="/upload")
    public String showUpload() {
        return "UploadPage";
    }

    @PostMapping(value="/upload")
    public String doUpload(
            @RequestParam("file") MultipartFile file
    ) {
        try{
            services.upload(file);
            return "redirect:/upload";
        }catch (IOException | ElasticsearchException e){
            e.printStackTrace();
        }
        return "redirect:/upload";
    }

    @GetMapping(value="/search/q={query}")
    public String search(
            @PathVariable("query") String query
    ) {
        services.fullTextSearch(query);

        return "redirect:/";
    }

}
