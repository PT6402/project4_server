package fpt.aptech.project4_server.repository;

import fpt.aptech.project4_server.entities.book.Author;
import fpt.aptech.project4_server.entities.book.Publisher;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PublisherRepository extends JpaRepository<Publisher, Integer> {
    @Query("SELECT a FROM Publisher a WHERE a.name LIKE %:wordSearch%")
    List<Publisher> searchByNamePub(@Param("wordSearch") String wordSearch);
}
