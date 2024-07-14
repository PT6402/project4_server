package fpt.aptech.project4_server.repository;

import fpt.aptech.project4_server.entities.book.Author;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AuthorRepository extends JpaRepository<Author, Integer> {
      @Query("SELECT a FROM Author a WHERE a.name LIKE %:wordSearch%")
    List<Author> searchByNameAuthor(@Param("wordSearch") String wordSearch);
}
