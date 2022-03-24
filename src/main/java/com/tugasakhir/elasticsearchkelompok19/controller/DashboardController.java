package com.tugasakhir.elasticsearchkelompok19.controller;

import com.tugasakhir.elasticsearchkelompok19.model.PDFDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;



import java.util.ArrayList;
import java.util.List;

@Controller
public class DashboardController {

    Logger log = LoggerFactory.getLogger(DashboardController.class);
    List<PDFDocument> listPdf = new ArrayList<>();

    String username = "Admin";
    String password = "Admin123";

    @GetMapping(value = "/")
    public String showDashboard() {
        log.info("Showing Dashboard Page ...");
        return "DashboardPage";
    }

    @PostMapping(value = "/login")
    public String doLogin(
            @RequestParam("username") String username,
            @RequestParam("password") String password
    ) {

        if (this.username.equals(username) && this.password.equals(password)) {
            return "redirect:/admin/";
        }
        return "redirect:/";
    }


}
