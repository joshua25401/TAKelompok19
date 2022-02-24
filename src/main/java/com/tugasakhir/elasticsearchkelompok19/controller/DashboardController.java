package com.tugasakhir.elasticsearchkelompok19.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @GetMapping(value = { "/index", "/" })
    public String showDashboard() {
        return "DashboardPage";
    }

}
