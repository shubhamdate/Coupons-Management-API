package monkcommerce.coupons.management.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartWiseCouponRequest {
    private Double thresholdAmount;
    private Double discountValue;
    private String discountType; // PERCENT / FIXED
}
