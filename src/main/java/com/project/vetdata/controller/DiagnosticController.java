package com.project.vetdata.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("diagnostics-pages")
public class DiagnosticController {

    @GetMapping("list")
    public String list(Model model){
        model.addAttribute("menuAtivo", "diagnostics");
        return "diagnostic_list";
    }
}
