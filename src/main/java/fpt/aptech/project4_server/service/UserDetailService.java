package fpt.aptech.project4_server.service;

import fpt.aptech.project4_server.dto.user.UserAdminDTO;
import fpt.aptech.project4_server.entities.user.UserDetail;
import fpt.aptech.project4_server.repository.UserDetailRepo;
import fpt.aptech.project4_server.util.ResultDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserDetailService {

    @Autowired
    UserDetailRepo userDetailRepo;

    public ResponseEntity<ResultDto<List<UserAdminDTO>>> getAllUsers() {
        try {
            List<UserAdminDTO> users = userDetailRepo.findAll().stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

            ResultDto<List<UserAdminDTO>> response = ResultDto.<List<UserAdminDTO>>builder()
                    .status(true)
                    .message("Success")
                    .model(users)
                    .build();
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            ResultDto<List<UserAdminDTO>> response = ResultDto.<List<UserAdminDTO>>builder()
                    .status(false)
                    .message("Failed to retrieve users")
                    .build();
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<ResultDto<UserAdminDTO>> getUser(int id) {
        try {
            Optional<UserDetail> userDetailOptional = userDetailRepo.findById(id);
            if (userDetailOptional.isPresent()) {
                UserAdminDTO userAdminDTO = convertToDTO(userDetailOptional.get());
                ResultDto<UserAdminDTO> response = ResultDto.<UserAdminDTO>builder()
                        .status(true)
                        .message("Success")
                        .model(userAdminDTO)
                        .build();
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                ResultDto<UserAdminDTO> response = ResultDto.<UserAdminDTO>builder()
                        .status(false)
                        .message("User not found")
                        .build();
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            ResultDto<UserAdminDTO> response = ResultDto.<UserAdminDTO>builder()
                    .status(false)
                    .message("Failed to retrieve user")
                    .build();
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private UserAdminDTO convertToDTO(UserDetail userDetail) {
        return UserAdminDTO.builder()
                .name(userDetail.getFullname())
                .email(userDetail.getUser().getEmail())
                .typeLogin(userDetail.getUser().getTypeLogin())
                .role(userDetail.getUser().getRole())
                .build();
    }
}
