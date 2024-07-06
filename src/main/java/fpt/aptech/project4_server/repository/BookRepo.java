package fpt.aptech.project4_server.repository;

import fpt.aptech.project4_server.entities.book.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepo extends JpaRepository<Book, Integer> {
    
}
