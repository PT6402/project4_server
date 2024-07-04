package fpt.aptech.project4_server.dto.author;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthorAdminCreateRes {
    private MultipartFile fileImage;
    private String name;
}
