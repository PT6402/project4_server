package fpt.aptech.project4_server.repository;

import fpt.aptech.project4_server.entities.user.Order;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderRepository extends JpaRepository<Order, Integer> {
  @Query("Select c from Order c where c.userDetail.id = :userID")
  List<Order> findByUserDetailId(@Param("userID") Integer userID);
}