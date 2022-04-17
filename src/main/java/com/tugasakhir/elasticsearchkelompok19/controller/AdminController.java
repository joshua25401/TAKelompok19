package com.tugasakhir.elasticsearchkelompok19.controller;

import com.tugasakhir.elasticsearchkelompok19.model.PDFDocument;
import com.tugasakhir.elasticsearchkelompok19.services.DocumentServices;
import com.tugasakhir.elasticsearchkelompok19.services.SearchServices;
import org.elasticsearch.ElasticsearchException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    DocumentServices services;

    @Autowired
    SearchServices dataServices;

    /*Data*/
    List<PDFDocument> listPdf = null;

    /*Logger*/
    final Logger log = LoggerFactory.getLogger(AdminController.class);

    @GetMapping(value = "/")
    public ModelAndView showAdmin(
            HttpServletRequest request,
            ModelMap model
    ) {
        boolean isLogin = request.getSession().getAttribute("userLogin") != null;
        log.info("isLogin : {}", isLogin);
        if (isLogin) {
            try{
                listPdf = dataServices.sarchAll();
                model.addAttribute("listPdf", listPdf);
                log.info("Success Get " + listPdf.size() + " PDF Data!");
                return new ModelAndView("AdminCrud", model);
            }catch (Exception e){
                log.info(e.getMessage());
                return new ModelAndView("AdminCrud");
            }
        } else {
            return new ModelAndView("redirect:/");
        }
    }

    @PostMapping(value = "/upload")
    public String doUpload(
            @ModelAttribute PDFDocument doc,
            @RequestParam("file") MultipartFile file
    ) {
        doc.setAuthors(Arrays.asList(doc.getAuthor().split("\\s*,\\s*")));
        try {
            if(services.upload(doc,file)){
                log.info("Retrieved & Uploaded Doc {}", doc);
                return "redirect:/admin/";
            }
        } catch (IOException | ElasticsearchException e) {
            e.printStackTrace();
        }
        return "redirect:/";
    }

    @GetMapping(value = "/delete/{docId}")
    public String deleteDocument(
            @PathVariable("docId") String documentId
    ){
        try{
            if(services.delete(documentId)){
                log.info("SUCCESS DELETE DOC WITH ID : " + documentId);
                listPdf = dataServices.sarchAll();
                return "redirect:/admin/";
            }
        }catch (IOException | ElasticsearchException e){
            log.info("ERROR : " + e.getMessage());
        }
        return "redirect:/";
    }

    @GetMapping(value = "/logout")
    public String logout(HttpServletRequest request) {
        request.getSession().invalidate();
        log.info("request {}", request.getSession());
        return "redirect:/";
    }
}
