package com.bankmandiri.streamfailedpe.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class controller {

    @GetMapping("/delete") public String delete()
    {
        return "This is the delete request";
    }


    @GetMapping("/details") public String details()
    {
        return "This is the details request";
    }
}