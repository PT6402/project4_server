package fpt.aptech.project4_server.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.GetMapping;

@RequestMapping("api/demo")
@RestController
public class TestAuthController {

    @GetMapping("/")
    public String getMethodName() {
        return "success request";
    }

}
