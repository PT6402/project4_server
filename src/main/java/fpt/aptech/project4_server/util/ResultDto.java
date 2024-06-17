package fpt.aptech.project4_server.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResultDto<T> {
    private T model;
    private String message;
    private boolean status;
}
