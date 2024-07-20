/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/springframework/Repository.java to edit this template
 */
package fpt.aptech.project4_server.repository;

import fpt.aptech.project4_server.dto.statistic.BookStatistic;
import fpt.aptech.project4_server.entities.book.Book;
import fpt.aptech.project4_server.entities.user.Mybook;
import fpt.aptech.project4_server.entities.user.UserDetail;
import java.time.LocalDateTime;
import java.util.List;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author macos
 */
public interface Mybookrepo extends JpaRepository<Mybook, Integer> {

    @Query
     ("Select b from Mybook b where b.userDetail.id=:userID AND b.book.id=:bookID")       
    Optional<Mybook> findByUserDetailAndBook(@Param("userID") Integer userID,@Param("bookID") Integer bookID);
    
    @Query
    ("Select c from Mybook c where c.userDetail.id=:userID")
    List<Mybook>findByUserDetailId(@Param("userID") Integer userID);
    
    @Query
    ("Select d from Mybook d where d.book.id=:bookID")
    List<Mybook>findByBookId(@Param("bookID") Integer bookID);
    
    
//  @Query("SELECT new fpt.aptech.project4_server.dto.statistic.BookStatistic(b.id, b.name, " +
//           "COUNT(m), " +
//           "SUM(CASE WHEN m.expiredDate IS NULL THEN 1 ELSE 0 END), " +
//           "SUM(CASE WHEN m.expiredDate IS NOT NULL THEN 1 ELSE 0 END)) " +
//           "FROM Mybook m " +
//           "JOIN m.book b " +
//           "GROUP BY b.id, b.name")
//    List<BookStatistic> getBookStatistics();
     
}
