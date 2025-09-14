package monkcommerce.coupons.management.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CouponResponse {
    private Long id;
    private String type;
    private Boolean isActive;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private CartWiseCouponResponse cartWiseDetails;
    private ProductWiseCouponResponse productWiseDetails;
    private BxgyCouponResponse bxgyDetails;
}

