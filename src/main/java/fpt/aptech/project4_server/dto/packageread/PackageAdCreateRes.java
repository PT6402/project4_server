package fpt.aptech.project4_server.dto.packageread;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PackageAdCreateRes {
    private String packageName;
    private int dayQuantity;
}
