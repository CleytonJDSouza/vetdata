package com.project.vetdata.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("users-pages")
public class UserController {

    @GetMapping("list")
    public String list(Model model){
        model.addAttribute("menuAtivo", "users");
        return "user_list";
    }
}
