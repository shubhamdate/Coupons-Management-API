package monkcommerce.coupons.management.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartItemDiscountResponse {
    private Long productId;
    private int quantity;
    private double price;
    private double totalDiscount;
}
