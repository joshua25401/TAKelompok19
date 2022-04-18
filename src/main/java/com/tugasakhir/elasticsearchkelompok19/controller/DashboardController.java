package com.tugasakhir.elasticsearchkelompok19.controller;

import com.tugasakhir.elasticsearchkelompok19.model.PDFDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
public class DashboardController {

    Logger log = LoggerFactory.getLogger(AdminController.class);
    List<PDFDocument> listPdf = new ArrayList<>();

    String username = "Admin";
    String password = "Admin123";

    @GetMapping(value = "/")
    public String showDashboard(HttpSession session, Model model) {
        log.info("Showing Dashboard Page ...");
        return "DashboardPage";
    }

    @PostMapping(value = "/login")
    public String doLogin(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            HttpSession session
    ) {
        if (this.username.equals(username) && this.password.equals(password)) {
            session.setAttribute("userLogin",true);
            return "redirect:/admin/";
        }
        return "redirect:/";
    }


}
