package monkcommerce.coupons.management.service;

import monkcommerce.coupons.management.dto.CartRequest;
import monkcommerce.coupons.management.dto.CartResponse;
import monkcommerce.coupons.management.dto.CouponRequest;
import monkcommerce.coupons.management.dto.CouponResponse;

import java.util.List;

public interface CouponService {
    CouponResponse createCoupon(CouponRequest request);
    CouponResponse getCouponById(Long id);
    List<CouponResponse> getAllCoupons();
    void deleteCoupon(Long id);

    // Apply coupon to cart
    CartResponse applyCouponToCart(Long couponId, CartRequest cartRequest);
}
