package com.herosoft.configserver.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value="/config")
public class ConfigServerController {

    @GetMapping(value = "/checkstatus")
    public String checkStatus(){
        return "Config server is running";
    }
}
