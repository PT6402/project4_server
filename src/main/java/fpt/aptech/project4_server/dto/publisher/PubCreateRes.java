package fpt.aptech.project4_server.dto.publisher;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PubCreateRes {
    private String name;
    private String description;
    private MultipartFile fileImage;
}
