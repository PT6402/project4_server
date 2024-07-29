package fpt.aptech.project4_server.dto.book;

import java.util.HashMap;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class BookPropertiesResultAdmin {
  private List<HashMap<String, String>> categories;
  private List<HashMap<String, String>> authors;
  private List<HashMap<String, String>> publishers;

}
