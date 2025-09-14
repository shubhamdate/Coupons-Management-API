package monkcommerce.coupons.management.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BxgyGetProductRequest {
    private Long productId;
    private Integer quantity;
    private String discountType; // FREE / PERCENT / FIXED
    private Double discountValue;
}
