package fpt.aptech.project4_server.repository;

import fpt.aptech.project4_server.entities.book.Author;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorRepository extends JpaRepository<Author, Integer> {
    
}
