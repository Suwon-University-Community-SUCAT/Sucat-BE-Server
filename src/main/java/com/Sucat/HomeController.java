package com.Sucat;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {
    @GetMapping("/home")
    public String home() {
        return "test";
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/healthcheck")
    public String healthcheck() {
        return "OK";
    }
}
