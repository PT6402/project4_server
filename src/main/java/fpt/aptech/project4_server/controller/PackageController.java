/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/springframework/Controller.java to edit this template
 */
package fpt.aptech.project4_server.controller;

import fpt.aptech.project4_server.dto.category.PackageAdCreateRes;
import fpt.aptech.project4_server.service.PackageService;
import fpt.aptech.project4_server.util.ResultDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author macos
 */
@RestController
@RequestMapping("/api/v1/package")
public class PackageController {
    @Autowired
    PackageService Pservice;
    
     @PostMapping("/create/{id}")
     public ResponseEntity<ResultDto<?>> createPackage(@PathVariable Integer id, @ModelAttribute PackageAdCreateRes packres){
         return Pservice.createPackage(id, packres);
     }
   
    
}
