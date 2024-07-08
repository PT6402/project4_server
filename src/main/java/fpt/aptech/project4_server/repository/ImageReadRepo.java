/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/springframework/Repository.java to edit this template
 */
package fpt.aptech.project4_server.repository;

import fpt.aptech.project4_server.entities.book.CurrentPage;
import fpt.aptech.project4_server.entities.book.ImageRead;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author macos
 */
public interface ImageReadRepo extends JpaRepository<ImageRead, Integer> {
    
    @Query("SELECT ir FROM ImageRead ir WHERE ir.currentpage.id = :currentPageId")
    List<ImageRead> findByCurrentpage(@Param("currentPageId") int currentPageId);
}
