package com.project.vetdata.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/hospital-admission-pages")
public class HospitalAdmissionController {

    @GetMapping("form/{id}")
    public String form(@PathVariable Long id, Model model){
        model.addAttribute("id", id);
        return "hospital_admission_form";
    }

    @GetMapping("form_update/{id}")
    public String formUpdate(@PathVariable Long id, Model model){
        return "hospital_admission_form_update";
    }

    @GetMapping("list/{id}")
    public String list(@PathVariable Long id, Model model){
        System.out.println("ID HOSPITAL ADMISSION LIST " + id);
        model.addAttribute("id", id);
        return "hospital_admission_list";
    }
}
