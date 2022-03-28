package com.tugasakhir.elasticsearchkelompok19.controller;

import com.tugasakhir.elasticsearchkelompok19.model.PDFDocument;
import com.tugasakhir.elasticsearchkelompok19.services.DocumentServices;
import com.tugasakhir.elasticsearchkelompok19.services.SearchServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/pencarian")
public class PencarianController {

    /*Logger*/
    final Logger log = LoggerFactory.getLogger(PencarianController.class);

    /*List of Documents*/
    List<PDFDocument> listPdf = null;

    @Autowired
    SearchServices services;

    @GetMapping
    public String showPencarian(){
        return "Pencarian";
    }

    @GetMapping(value = "/search")
    public ModelAndView search(
            @RequestParam("keyword") String keyword,
            ModelMap model
    ) {
        listPdf = services.fullTextSearch(keyword);
        model.addAttribute("keyword", keyword);
        model.addAttribute("listPdf", listPdf != null ? listPdf : "empty");

        log.info("Searching for " + keyword);

//        if (listPdf != null) {
//            return "redirect:/pencarian";
//        }
        return new ModelAndView("forward:/pencarian",model);
    }
    
}
