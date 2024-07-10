package fpt.aptech.project4_server.dto.category;

import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CateUserRes {

    private int id;
    private String name;
    private String description;
    @Lob
    private byte[]  Imagedata;
}
