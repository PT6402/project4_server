package fpt.aptech.project4_server.dto.author;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthorUserRes {

    private int id;
    private String name;
    private String pathImage;
}
