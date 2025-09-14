package monkcommerce.coupons.management.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class CartResponse {
    private List<CartItemResponse> items;
    private Double totalPrice;
    private Double totalDiscount;
    private Double finalPrice;
    private Map<String, Double> discountBreakdown;
}