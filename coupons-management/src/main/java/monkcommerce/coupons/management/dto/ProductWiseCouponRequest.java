package monkcommerce.coupons.management.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductWiseCouponRequest {
    private Long productId;
    private Double discountValue;
    private String discountType; // PERCENT / FIXED
}