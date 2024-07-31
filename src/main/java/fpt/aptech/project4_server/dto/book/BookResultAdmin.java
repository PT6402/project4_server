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
@Builder
@Setter
@Getter
public class BookResultAdmin {
  private int id;
  private String name;
  private byte[] image;
  private double price;
  private List<HashMap<String, String>> authors;
  private HashMap<String, String> publisher;
  private List<HashMap<String, String>> categories;
  private double rating;

}
