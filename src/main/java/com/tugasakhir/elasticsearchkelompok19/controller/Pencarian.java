package com.tugasakhir.elasticsearchkelompok19.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class Pencarian {

    @GetMapping(value = "/pencarian")
    public String showPencarian() {
        return "Pencarian";
    }
    
}
