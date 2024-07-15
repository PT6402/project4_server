/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/springframework/Service.java to edit this template
 */
package fpt.aptech.project4_server.service;

import fpt.aptech.project4_server.entities.book.Book;
import fpt.aptech.project4_server.entities.book.ScheduleBookDeletion;
import fpt.aptech.project4_server.entities.user.Mybook;
import fpt.aptech.project4_server.repository.BookRepo;
import fpt.aptech.project4_server.repository.Mybookrepo;
import fpt.aptech.project4_server.repository.ScheduleDeleteRepository;
import fpt.aptech.project4_server.util.ResultDto;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 *
 * @author macos
 */
@Service
public class ScheduledTaskService {
    @Autowired
    private ScheduleDeleteRepository scheduledBookDeletionRepo;

    @Autowired
    private PdfService pdfService;


    @Scheduled(cron = "0 0 0 * * ?")
    public void scheduleTask() {
        LocalDateTime now = LocalDateTime.now();
        List<ScheduleBookDeletion> scheduledBooksToDelete = scheduledBookDeletionRepo.findByExpiredDateBefore(now);
        
        for (ScheduleBookDeletion scheduledBook : scheduledBooksToDelete) {
            pdfService.deleteBookById(scheduledBook.getBookId());
            scheduledBookDeletionRepo.delete(scheduledBook);
        }
    }
}
