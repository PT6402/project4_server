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

    @Query("SELECT d FROM Book d WHERE d.statusMybook= true ")
    List<Book> findBooksToDelete();

    @Query("SELECT b FROM Book b JOIN b.authors a WHERE a.id = :authorId")
    List<Book> findByAuthorId(@Param("authorId") int authorId);
    
  @Query("SELECT b FROM Book b WHERE b.publisher.id = :publisherId")
    List<Book> findBooksByPublisherId(@Param("publisherId") Integer publisherId);

     @Query("SELECT e FROM Book e WHERE e.name LIKE %:name% ")
    List<Book> findByName(@Param("name") String name);

      @Query("SELECT f FROM Book f WHERE f.price BETWEEN :StaPrice AND :EndPrice")
      List<Book> findByPrice (@Param("StaPrice") Integer StaPrice,@Param("EndPrice") Integer EndPrice);
}
