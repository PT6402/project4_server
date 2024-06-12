package fpt.aptech.project4_server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fpt.aptech.project4_server.entities.user.UserDetail;

@Repository
public interface UserDetailRepo extends JpaRepository<UserDetail, Integer> {

}
