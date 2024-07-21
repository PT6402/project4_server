package fpt.aptech.project4_server.service;

import fpt.aptech.project4_server.dto.statistic.BookStatistic;
import fpt.aptech.project4_server.repository.*;
import fpt.aptech.project4_server.util.ResultDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class StatisticService {
    @Autowired
    BookRepo Brepo;
    @Autowired
    Mybookrepo MBrepo;
    @Autowired
    OrderDetailRepository Orepo;
    @PersistenceContext
    private EntityManager entityManager;

//public List<BookStatistic> getBookStatistics() {
//        return MBrepo.getBookStatistics();
//    }

    public List<BookStatistic> getBookStatistics() {
        String queryStr = "SELECT new fpt.aptech.project4_server.dto.statistic.BookStatistic(b.id, b.name, " +
                "COUNT(m.id), " +
                "SUM(CASE WHEN m.ExpiredDate IS NULL THEN 1 ELSE 0 END), " +
                "SUM(CASE WHEN m.ExpiredDate IS NOT NULL THEN 1 ELSE 0 END), " +
                "SUM(COALESCE(od.Price, 0))) " + // Tính tổng giá của các OrderDetail
                "FROM Mybook m " +
                "JOIN m.book b " +
                "LEFT JOIN OrderDetail od ON b.id = od.book.id " + // Chỉnh sửa để join với Book và OrderDetail
                "GROUP BY b.id, b.name";
        TypedQuery<BookStatistic> query = entityManager.createQuery(queryStr, BookStatistic.class);
        return query.getResultList();
    }
}
