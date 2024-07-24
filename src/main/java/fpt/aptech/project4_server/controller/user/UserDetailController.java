package fpt.aptech.project4_server.controller.user;

import fpt.aptech.project4_server.dto.user.UserAdminDTO;
import fpt.aptech.project4_server.service.UserDetailService;
import fpt.aptech.project4_server.util.ResultDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/users")
public class UserDetailController {

    @Autowired
    UserDetailService userDetailService;

    @GetMapping
    public ResponseEntity<ResultDto<List<UserAdminDTO>>> getAllUsers() {
        return userDetailService.getAllUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResultDto<UserAdminDTO>> getUser(@PathVariable("id") int id) {
        return userDetailService.getUser(id);
    }
}
