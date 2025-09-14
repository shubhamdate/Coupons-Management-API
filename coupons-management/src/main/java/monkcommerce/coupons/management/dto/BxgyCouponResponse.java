package monkcommerce.coupons.management.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BxgyCouponResponse {
    private Integer repetitionLimit;
    private List<BxgyBuyProductResponse> buyProducts;
    private List<BxgyGetProductResponse> getProducts;
}