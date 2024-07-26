package fpt.aptech.project4_server.dto.publisher;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PubSearch {
    private int id;
    private byte[] fileImage;
    private String name;
}
