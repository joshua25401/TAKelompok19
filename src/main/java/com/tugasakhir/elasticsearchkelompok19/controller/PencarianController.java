package com.tugasakhir.elasticsearchkelompok19.controller;

import com.tugasakhir.elasticsearchkelompok19.model.PDFDocument;
import com.tugasakhir.elasticsearchkelompok19.services.DocumentServices;
import com.tugasakhir.elasticsearchkelompok19.services.SearchServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/pencarian")
public class PencarianController {

    /*Logger*/
    final Logger log = LoggerFactory.getLogger(PencarianController.class);

    @Autowired
    SearchServices services;

    @Autowired
    DocumentServices documentServices;

    @GetMapping
    public String showPencarian() {
        return "Pencarian";
    }

    @GetMapping(value = "/search")
    public ModelAndView search(
            @RequestParam("keyword") String keyword,
            ModelMap model
    ) {
        /*List of Documents*/
        List<PDFDocument> listPdf = null;
        Map<String,Object> returnVal = null;
        double tookTime = 0.0f;
        float maxScore = 0.0f;

        try {
            log.info("Searching for keyword : " + keyword);
            returnVal = services.fullTextSearch(keyword);

            if (returnVal != null) {
                listPdf = (List<PDFDocument>) returnVal.getOrDefault("listPDF",null);
                tookTime = (double) returnVal.getOrDefault("tookTime",0.0f);
                maxScore = (float) returnVal.getOrDefault("maxScore",0.0f);
            }

            if (listPdf != null) {
                model.addAttribute("keyword", keyword);
                model.addAttribute("listPdf", listPdf);
                model.addAttribute("tookTime",tookTime);
                model.addAttribute("maxScore", maxScore);
                log.info("Got " + listPdf.size() + " PDF Data! within " + tookTime + " seconds");
            } else {
                model.addAttribute("keyword", keyword);
                model.addAttribute("listPdf", "empty");
                log.info("No Data Found!");
            }

            return new ModelAndView("forward:/pencarian", model);
        } catch (Exception e) {
            log.info("ERROR : " + e.getMessage());
        }
        return new ModelAndView("redirect:/");
    }

    @GetMapping(value = "/showFile/{docId}", produces = MediaType.APPLICATION_PDF_VALUE)
    @ResponseBody
    public ResponseEntity<?> showPdf(@PathVariable("docId") String documentId) {
        HttpHeaders headers = new HttpHeaders();
        try {
            File pdfFile = documentServices.getFile(documentId);
            if (pdfFile != null) {
                InputStream fileToOpen = new FileInputStream(pdfFile);
                log.info("Showing PDF File : " + documentId);
                InputStreamResource resource = new InputStreamResource(fileToOpen);
                headers.add("content-disposition", "inline; filename=" + documentId);
                return ResponseEntity.ok()
                        .headers(headers)
                        .body(resource);
            }
        } catch (Exception e) {
            log.info("FILE OPERATION ERROR : " + e.getMessage());
        }
        headers.add("Location", "/");
        return new ResponseEntity<byte[]>(null, headers, HttpStatus.FOUND);
    }

    @GetMapping(value = "/unduhFile/{docId}")
    @ResponseBody
    public ResponseEntity<?> unduhPDF(@PathVariable("docId") String documentId) {
        HttpHeaders headers = new HttpHeaders();

        try {
            File pdfFile = documentServices.getFile(documentId);

            if (pdfFile != null) {
                InputStream fileToDownload = new FileInputStream(pdfFile);
                log.info("Downloading PDF File : " + documentId);
                InputStreamResource downloadResource = new InputStreamResource(fileToDownload);
                headers.add("content-disposition", "attachment; filename=" + documentId);
                return ResponseEntity.ok()
                        .headers(headers)
                        .contentType(MediaType.APPLICATION_PDF)
                        .body(downloadResource);
            }
        } catch (Exception e) {
            log.info("ERROR DOWNLOADING FILE : " + e.getMessage());
        }
        headers.add("Location", "/");
        return new ResponseEntity<byte[]>(null, headers, HttpStatus.FOUND);
    }

}
