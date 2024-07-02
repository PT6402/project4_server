package fpt.aptech.project4_server.dto.category;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CateAdCreateRes {
    private MultipartFile fileImage;
    private String name;
    private String description;

}
