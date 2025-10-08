package com.stockflow.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    @GetMapping("/")
    public String index() {
        return "index.html";
    }
    
    @GetMapping({"/login", "/register", "/login.html", "/register.html"})
    public String authPages() {
        return "redirect:/";
    }
}