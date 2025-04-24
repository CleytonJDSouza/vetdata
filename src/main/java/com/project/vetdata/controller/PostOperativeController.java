package com.project.vetdata.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("post-operatives-pages")
public class PostOperativeController {

    @GetMapping("list")
    public String list(Model model){
        model.addAttribute("menuAtivo", "post-operatives");
        return "post_operative_list";
    }
}
