/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/springframework/Controller.java to edit this template
 */
package fpt.aptech.project4_server.controller.user;

import fpt.aptech.project4_server.security.CurrentUser;
import fpt.aptech.project4_server.security.UserGlobal;
import fpt.aptech.project4_server.service.MyBookService;
import jakarta.websocket.server.PathParam;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author macos
 */
@RestController
@RequestMapping("api/v1/mybook")
public class MyBookController {
    @Autowired
    MyBookService MBservice;

    @PostMapping("/create")
    public ResponseEntity<?> createMBook(@RequestParam Integer orderId, @RequestParam Integer userDetailId)
            throws IOException {
        return MBservice.createMybook(orderId, userDetailId);
    }

    @GetMapping
    public ResponseEntity<?> getMybooksByUserDetailId(@CurrentUser UserGlobal user) {
        ResponseEntity<?> response = MBservice.ShowMybooklist(user.getId());
        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
    }
}
