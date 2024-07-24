package fpt.aptech.project4_server.service.user;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import fpt.aptech.project4_server.dto.user.UserRes;
import fpt.aptech.project4_server.entities.auth.User;
import fpt.aptech.project4_server.repository.auth.UserRepo;
import fpt.aptech.project4_server.security.UserGlobal;
import fpt.aptech.project4_server.util.ResultDto;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
  private final UserRepo userRepo;

  @Override
  public ResponseEntity<ResultDto<?>> getUser(UserGlobal globalUser) {

    try {
      User user = userRepo.findByEmail(globalUser.getEmail()).orElse(null);
      if (user == null) {
        throw new Exception("user not found");
      }
      ResultDto<?> response = ResultDto.builder().message("ok").status(true)

          .model(UserRes.builder().name(user.getUserDetail().getFullname()).email(user.getEmail()).role(user.getRole())
              .build())
          .build();
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      ResultDto<?> response = ResultDto.builder().message(e.getMessage()).status(false)
          .build();
      return ResponseEntity.badRequest().body(response);
    }
  }

}
