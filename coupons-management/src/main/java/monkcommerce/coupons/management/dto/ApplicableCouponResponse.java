package monkcommerce.coupons.management.dto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplicableCouponResponse {
    private Long couponId;
    private String type;
    private double discount;
}
