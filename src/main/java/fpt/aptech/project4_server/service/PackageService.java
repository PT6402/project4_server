/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/springframework/Service.java to edit this template
 */
package fpt.aptech.project4_server.service;

import fpt.aptech.project4_server.dto.packageread.PackageAdCreateRes;
import fpt.aptech.project4_server.entities.book.Book;
import fpt.aptech.project4_server.entities.book.PackageRead;
import fpt.aptech.project4_server.repository.BookRepo;
import fpt.aptech.project4_server.repository.PackageReadRepository;

import fpt.aptech.project4_server.util.ResultDto;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 *
 * @author macos
 */
@Service
public class PackageService {
    @Autowired
    PackageReadRepository Prepo;
   
    
    public ResponseEntity<ResultDto<?>> createPackage(PackageAdCreateRes PackRes){
 
        PackageRead newPackage=new PackageRead();
        newPackage.setPackageName(PackRes.getPackageName());
       
        newPackage.setDayQuantity(PackRes.getDayQuantity());
        System.out.println(newPackage);
        
        Prepo.save(newPackage);
        
         ResultDto<?> response = ResultDto.builder().status(true).message("Create successfully").build();
            return new ResponseEntity<ResultDto<?>>(response, HttpStatus.OK);
    }
}
