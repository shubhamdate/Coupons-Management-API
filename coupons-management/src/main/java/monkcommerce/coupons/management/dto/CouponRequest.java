package monkcommerce.coupons.management.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CouponRequest {
    private String type; // CART_WISE, PRODUCT_WISE, BXGY
    private Boolean isActive;
    private LocalDateTime expiresAt;

    // Subtype details
    private CartWiseCouponRequest cartWiseDetails;
    private ProductWiseCouponRequest productWiseDetails;
    private BxgyCouponRequest bxgyDetails;
}
