package monkcommerce.coupons.management.service;

import monkcommerce.coupons.management.dto.CartRequest;
import monkcommerce.coupons.management.dto.ApplicableCouponResponse;
import monkcommerce.coupons.management.dto.ApplyCouponResponse;

import java.util.List;

public interface CouponCalculationService {
    List<ApplicableCouponResponse> getApplicableCoupons(CartRequest cartRequest);
    ApplyCouponResponse applyCoupon(Long couponId, CartRequest cartRequest);
}