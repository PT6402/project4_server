package fpt.aptech.project4_server.service.user;

import org.springframework.http.ResponseEntity;

import fpt.aptech.project4_server.security.UserGlobal;
import fpt.aptech.project4_server.util.ResultDto;

public interface UserService {
  ResponseEntity<ResultDto<?>> getUser(UserGlobal user);
}
