package monkcommerce.coupons.management.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BxgyCouponRequest {
    private Integer repetitionLimit;
    private List<BxgyBuyProductRequest> buyProducts;
    private List<BxgyGetProductRequest> getProducts;
}
