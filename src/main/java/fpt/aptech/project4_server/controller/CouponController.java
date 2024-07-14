package fpt.aptech.project4_server.controller;

import fpt.aptech.project4_server.dto.coupon.CouponCreateRequest;
import fpt.aptech.project4_server.service.CouponService;
import fpt.aptech.project4_server.util.ResultDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api/v1/coupon")
public class CouponController {

    @Autowired
    CouponService couponService;

    @GetMapping
    public ResponseEntity<ResultDto<?>> getAllCoupons() {
        return couponService.getAllCoupons();
    }

    @PostMapping
    public ResponseEntity<ResultDto<?>> createCoupon(@RequestBody CouponCreateRequest request) {
        return couponService.createCoupon(request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResultDto<?>> deleteCoupon(@PathVariable int id) {
        return couponService.deleteCoupon(id);
    }
}
