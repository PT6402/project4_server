package fpt.aptech.project4_server.repository;

import fpt.aptech.project4_server.entities.user.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
}
