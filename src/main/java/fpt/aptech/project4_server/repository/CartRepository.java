package fpt.aptech.project4_server.repository;

import fpt.aptech.project4_server.entities.user.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Integer> {
    
}
