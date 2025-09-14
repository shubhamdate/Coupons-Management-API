package monkcommerce.coupons.management.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductWiseCouponResponse {
    private Long productId;
    private Double discountValue;
    private String discountType;
}