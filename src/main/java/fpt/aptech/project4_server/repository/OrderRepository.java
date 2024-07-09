package fpt.aptech.project4_server.repository;

import fpt.aptech.project4_server.entities.user.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Integer> {
    
}
