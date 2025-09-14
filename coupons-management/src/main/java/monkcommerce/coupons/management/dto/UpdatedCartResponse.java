package monkcommerce.coupons.management.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UpdatedCartResponse {
    private List<CartItemDiscountResponse> items;
    private double totalPrice;
    private double totalDiscount;
    private double finalPrice;
}
