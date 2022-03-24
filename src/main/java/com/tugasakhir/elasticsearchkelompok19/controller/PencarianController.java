package com.tugasakhir.elasticsearchkelompok19.controller;

import com.tugasakhir.elasticsearchkelompok19.model.PDFDocument;
import com.tugasakhir.elasticsearchkelompok19.services.DocumentServices;
import com.tugasakhir.elasticsearchkelompok19.services.SearchServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class PencarianController {

    /*Logger*/
    final Logger log = LoggerFactory.getLogger(DocumentServices.class);

    /*List of Documents*/
    List<PDFDocument> listPdf = null;

    @Autowired
    SearchServices services;

    @GetMapping(value = "/pencarian")
    public String showPencarian() {
        return "Pencarian";
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
    
}
