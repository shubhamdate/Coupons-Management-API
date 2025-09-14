package monkcommerce.coupons.management.controller;

import lombok.RequiredArgsConstructor;
import monkcommerce.coupons.management.dto.*;
import monkcommerce.coupons.management.service.CouponCalculationService;
import monkcommerce.coupons.management.service.CouponService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    private final CouponCalculationService couponCalculationService;

    // Create a new coupon
    @PostMapping
    public ResponseEntity<CouponResponse> createCoupon(@RequestBody CouponRequest request) {
        CouponResponse response = couponService.createCoupon(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Get all coupons
    @GetMapping
    public ResponseEntity<List<CouponResponse>> getAllCoupons() {
        return ResponseEntity.ok(couponService.getAllCoupons());
    }

    // Get coupon by ID
    @GetMapping("/{id}")
    public ResponseEntity<CouponResponse> getCouponById(@PathVariable Long id) {
        return ResponseEntity.ok(couponService.getCouponById(id));
    }

    // Delete coupon by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCoupon(@PathVariable Long id) {
        couponService.deleteCoupon(id);
        return ResponseEntity.noContent().build();
    }

    // Apply coupon to a cart
    @PostMapping("/{id}/apply")
    public ResponseEntity<CartResponse> applyCouponToCart(
            @PathVariable Long id,
            @RequestBody CartRequest cartRequest
    ) {
        CartResponse response = couponService.applyCouponToCart(id, cartRequest);
        return ResponseEntity.ok(response);
    }

    // Get all applicable coupons for a cart
    @PostMapping("/applicable")
    public ResponseEntity<List<ApplicableCouponResponse>> getApplicableCoupons(
            @RequestBody CartRequest cartRequest
    ) {
        return ResponseEntity.ok(couponCalculationService.getApplicableCoupons(cartRequest));
    }

    // Apply a coupon to a cart and get updated cart response
    @PostMapping("/{id}/calculate")
    public ResponseEntity<ApplyCouponResponse> applyCoupon(
            @PathVariable Long id,
            @RequestBody CartRequest cartRequest
    ) {
        return ResponseEntity.ok(couponCalculationService.applyCoupon(id, cartRequest));
    }
}
