package com.project.vetdata.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("breeds-pages")
public class DogBreedMVController {

    @GetMapping("form")
    public String form(){
        return "breed_form";
    }
}
