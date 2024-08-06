package fpt.aptech.project4_server.service.feature_web_service.user.order_service;

import java.util.HashMap;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import fpt.aptech.project4_server.entities.user.UserDetail;
import fpt.aptech.project4_server.repository.OrderRepository;
import fpt.aptech.project4_server.repository.UserDetailRepo;
import fpt.aptech.project4_server.util.ResultDto;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderFeatureServiceImpl implements OrderFeatureService {
  private final OrderRepository orderRepository;
  private final UserDetailRepo userDetailRepo;

  @Override
  public ResponseEntity<ResultDto<?>> getOrder(int userId) {

    try {
      UserDetail userDetail = userDetailRepo.findByUserId(userId)
          .orElseThrow(() -> new Exception("UserDetail not found"));
      var orders = orderRepository.findByUserDetailId(userDetail.getId()).stream().map(c -> {
        var listOrderDetail = c.getOrderDetails().stream().map(d -> {
          HashMap<String, Object> orderDetailMap = new HashMap<>();
          orderDetailMap.put("id", d.getId());
          orderDetailMap.put("dayQuantity", d.getDayQuantity());
          orderDetailMap.put("packageId", d.getPackId());
          orderDetailMap.put("bookId", d.getBook().getId());
          orderDetailMap.put("price", d.getPrice());
          return orderDetailMap;
        }).toList();

        HashMap<String, Object> orderMap = new HashMap<>();
        orderMap.put("id", c.getId());
        orderMap.put("totalPrice", c.getTotalPrice());
        orderMap.put("orderDate", c.getDateOrder());
        orderMap.put("status", c.getPaymentStatus());
        orderMap.put("listOrderDetail", listOrderDetail);
        return orderMap;
      }).toList();
      ResultDto<?> response = ResultDto.builder().message("ok").status(true).model(orders).build();
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      ResultDto<?> response = ResultDto.builder().message(e.getMessage()).status(false).build();
      return ResponseEntity.badRequest().body(response);
    }
  }

}
