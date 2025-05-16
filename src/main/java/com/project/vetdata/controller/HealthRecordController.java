package com.project.vetdata.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/health-records-pages")
public class HealthRecordController {

    @GetMapping("form")
    public String form(Model model){
        model.addAttribute("menuAtivo", "health-record");
        model.addAttribute("submenuHRAtivo", "form");
        return "health_record_form";
    }

    @GetMapping("form_update/{id}")
    public String formUpdate(@PathVariable Long id, Model model){
        model.addAttribute("id", id);
        model.addAttribute("menuAtivo", "health-record");
        return "health_record_form_update";
    }

    @GetMapping("list")
    public String list(Model model){
        model.addAttribute("menuAtivo", "health-record");
        model.addAttribute("submenuHRAtivo", "list");

        return "health_record_list";
    }

}
