package fpt.aptech.project4_server.controller;

import fpt.aptech.project4_server.dto.statistic.BookStatistic;
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

@RestController
@RequestMapping("/api/v1/statistics")
public class StatisticController {

    @Autowired
    private StatisticService statService;

    @GetMapping("/")
    public ResponseEntity<ResultDto<?>> getBookStatistics() {
        try {
            List<BookStatistic> statistics = statService.getBookStatistics();
            return ResponseEntity.ok(new ResultDto<>(statistics, "Success", true));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResultDto<>(null, "Error: " + e.getMessage(), false));
        }
    }

}
