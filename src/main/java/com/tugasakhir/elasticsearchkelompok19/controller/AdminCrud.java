package com.tugasakhir.elasticsearchkelompok19.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminCrud {
    @GetMapping(value = { "/admin", "/" })
    public String showAdmin() {
        return "AdminCrud";
    }
}
