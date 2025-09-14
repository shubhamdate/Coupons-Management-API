package monkcommerce.coupons.management.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartWiseCouponResponse {
    private Double thresholdAmount;
    private Double discountValue;
    private String discountType;
}