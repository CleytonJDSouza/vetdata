package com.project.vetdata.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("breeds-pages")
public class DogBreedMVController {

    @GetMapping("form")
    public String form(){
        return "breed_form";
    }

    @GetMapping("form_update/{id}")
    public String formUpdate(@PathVariable Long id, Model model){

        model.addAttribute("id", id);
        return "breed_form_update";
    }

    @GetMapping("list")
    public String list(){
        return "breed_list";
    }

}
