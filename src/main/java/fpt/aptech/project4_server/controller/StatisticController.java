/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/springframework/Controller.java to edit this template
 */
package fpt.aptech.project4_server.controller;

import fpt.aptech.project4_server.dto.statistic.BookStatistic;
import fpt.aptech.project4_server.dto.statistic.*;
import fpt.aptech.project4_server.service.MyBookService;
import fpt.aptech.project4_server.service.StatisticService;
import fpt.aptech.project4_server.util.ResultDto;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author macos
 */
@RestController
@RequestMapping("/api/v1/statistics")
public class StatisticController {
    
     @Autowired
    private StatisticService statService;

    @GetMapping("/")
    public ResponseEntity<ResultDto<?>> getBookStatistics() {
        try {
            List<BookStatistic> statistics = statService.getBookStatistics() ;
            return ResponseEntity.ok(new ResultDto<>(statistics, "Success", true));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(new ResultDto<>(null, "Error: " + e.getMessage(), false));
        }
    }
    
   @GetMapping("/topbuy")
    public ResponseEntity<ResultDto<List<TopBuy>>> getTopBuy() {
        try {
            List<TopBuy> topBuys = statService.getTopBuy();
            return ResponseEntity.ok(new ResultDto<>(topBuys, "Success", true));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(new ResultDto<>(null, "Error: " + e.getMessage(), false));
        }
    }
    
     @GetMapping("/toprent")
    public ResponseEntity<ResultDto<List<TopRent>>> getTopRent() {
        try {
            List<TopRent> topRents = statService.getTopRent();
            return ResponseEntity.ok(new ResultDto<>(topRents, "Success", true));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(new ResultDto<>(null, "Error: " + e.getMessage(), false));
        }
    }
    
    @GetMapping("/toplike")
    public List<TopLike> getTopLikedBooks() {
        return statService.getTopLike();
    }
}
