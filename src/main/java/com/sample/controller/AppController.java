package com.sample.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/")
public class AppController {

    @GetMapping("/healthy")
    public String healthy() {
        return "Server Status OK";
    }
}
