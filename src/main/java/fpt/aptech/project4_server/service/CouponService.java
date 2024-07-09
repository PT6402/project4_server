package fpt.aptech.project4_server.service;

import fpt.aptech.project4_server.dto.coupon.CouponCreateRequest;
import fpt.aptech.project4_server.dto.coupon.CouponResponse;
import fpt.aptech.project4_server.entities.book.Coupon;
import fpt.aptech.project4_server.repository.CouponRepository;
import fpt.aptech.project4_server.util.ResultDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CouponService {

    @Autowired
    private CouponRepository couponRepository;

    public ResponseEntity<ResultDto<?>> createCoupon(CouponCreateRequest request) {
        try {
            Coupon coupon = Coupon.builder()
                    .code(request.getCode())
                    .discountRate(request.getDiscountRate())
                    .build();
            couponRepository.save(coupon);
            return new ResponseEntity<>(ResultDto.builder()
                    .status(true)
                    .message("Coupon created successfully")
                    .build(), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(ResultDto.builder()
                    .status(false)
                    .message(e.getMessage())
                    .build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<ResultDto<?>> getAllCoupons() {
        try {
            List<Coupon> coupons = couponRepository.findAll();
            List<CouponResponse> responseList = coupons.stream()
                    .map(coupon -> new CouponResponse(coupon.getId(), coupon.getCode(), coupon.getDiscountRate()))
                    .collect(Collectors.toList());
            return new ResponseEntity<>(ResultDto.builder()
                    .status(true)
                    .model(responseList)
                    .build(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(ResultDto.builder()
                    .status(false)
                    .message(e.getMessage())
                    .build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<ResultDto<?>> deleteCoupon(int id) {
        try {
            couponRepository.deleteById(id);
            return new ResponseEntity<>(ResultDto.builder()
                    .status(true)
                    .message("Coupon deleted successfully")
                    .build(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(ResultDto.builder()
                    .status(false)
                    .message(e.getMessage())
                    .build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
