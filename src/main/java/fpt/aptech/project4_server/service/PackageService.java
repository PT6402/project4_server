/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/springframework/Service.java to edit this template
 */
package fpt.aptech.project4_server.service;

import fpt.aptech.project4_server.dto.category.PackageAdCreateRes;
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
    @Autowired
    BookRepo Brepo;
    
    public ResponseEntity<ResultDto<?>> createPackage(int bookid,PackageAdCreateRes PackRes){
        Book book =Brepo.findById(bookid).orElse(null);
        if(book==null){
             ResultDto<?> response = ResultDto.builder().status(false).message("Book is not existed").build();
        }
        BigDecimal price = BigDecimal.valueOf(book.getPrice());
        BigDecimal rentPrice = price.divide(BigDecimal.valueOf(45), 2, RoundingMode.HALF_UP)
                                   .multiply(BigDecimal.valueOf(PackRes.getDayQuantity()));
        
        PackageRead newPackage=new PackageRead();
        newPackage.setPackageName(PackRes.getPackageName());
        newPackage.setRentPrice(rentPrice.doubleValue());
        newPackage.setDayQuantity(PackRes.getDayQuantity());
        newPackage.setBook(book);
        
        Prepo.save(newPackage);
        
         ResultDto<?> response = ResultDto.builder().status(true).message("Create successfully").build();
            return new ResponseEntity<ResultDto<?>>(response, HttpStatus.OK);
    }
}
