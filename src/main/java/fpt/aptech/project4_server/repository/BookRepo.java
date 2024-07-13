package fpt.aptech.project4_server.repository;

import fpt.aptech.project4_server.entities.book.Book;
import fpt.aptech.project4_server.entities.book.FilePdf;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.web.multipart.MultipartFile;

public interface BookRepo extends JpaRepository<Book, Integer> {
    @Query("SELECT b FROM Book b JOIN b.categories c WHERE c.id = :cateId")
    List<Book> findByCateId(@Param("cateId") Integer cateId);
}


    
   

