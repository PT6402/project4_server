package fpt.aptech.project4_server.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fpt.aptech.project4_server.entities.user.UserDetail;
import fpt.aptech.project4_server.entities.user.User;

@Repository
public interface UserDetailRepo extends JpaRepository<UserDetail, Integer> {
    Optional<UserDetail> findByUser(User user);
}
