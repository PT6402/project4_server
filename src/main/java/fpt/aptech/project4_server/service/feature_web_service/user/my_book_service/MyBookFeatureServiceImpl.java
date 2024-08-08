package fpt.aptech.project4_server.service.feature_web_service.user.my_book_service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import fpt.aptech.project4_server.entities.user.Mybook;
import fpt.aptech.project4_server.entities.user.UserDetail;
import fpt.aptech.project4_server.repository.Mybookrepo;
import fpt.aptech.project4_server.repository.UserDetailRepo;
import fpt.aptech.project4_server.util.ResultDto;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MyBookFeatureServiceImpl implements MyBookFeatureService {
  private final Mybookrepo mybookrepo;
  private final UserDetailRepo userDetailRepo;

  @Override
  public ResponseEntity<ResultDto<?>> getMyBook(int userId) {
    try {
      UserDetail userDetail = userDetailRepo.findByUserId(userId)
          .orElseThrow(() -> new Exception("UserDetail not found"));
      var mybooks = mybookrepo.findByUserDetailId(userDetail.getId()).stream().map(c -> {
        HashMap<String, Object> myBookMap = new HashMap<>();
        myBookMap.put("id", c.getId());
        myBookMap.put("bookId", c.getBook().getId());
        myBookMap.put("getDate", c.getCreateAt());
        myBookMap.put("getExpiredDate", c.getExpiredDate());
        handleStateAndDayTotal(myBookMap, c);
        return myBookMap;
      }).toList();
      ResultDto<?> response = ResultDto.builder().message("ok").status(true).model(mybooks).build();
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      ResultDto<?> response = ResultDto.builder().message(e.getMessage()).status(false).build();
      return ResponseEntity.badRequest().body(response);
    }
  }

  private void handleStateAndDayTotal(HashMap<String, Object> myBookItem, Mybook mybook) {
    Long daysDif = mybook.getExpiredDate() != null
        ? ChronoUnit.DAYS.between(LocalDateTime.now(), mybook.getExpiredDate())
        : null;
    int caseValue = daysDif == null ? 0 : (daysDif > 3 ? 1 : (daysDif < 0 ? 3 : 2));

    switch (caseValue) {
      case 0:
        myBookItem.put("dayTotal", 0);
        myBookItem.put("status", 0);
        break;
      case 1:
        myBookItem.put("dayTotal", daysDif != null ? Math.abs(daysDif) : 0);
        myBookItem.put("status", 1);
        break;
      case 2:
        myBookItem.put("dayTotal", daysDif != null ? Math.abs(daysDif) : 0);
        myBookItem.put("status", 2);

        break;
      case 3:
        myBookItem.put("dayTotal", 0);
        myBookItem.put("status", 3);
        break;
      default:
        throw new IllegalStateException("Unexpected value: " + caseValue);
    }
  }

}
