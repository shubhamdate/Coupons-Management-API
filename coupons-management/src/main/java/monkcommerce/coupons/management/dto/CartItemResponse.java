package monkcommerce.coupons.management.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartItemResponse {
    private Long productId;
    private Integer quantity;
    private Double price;          // unit price
    private Double totalDiscount;  // total discount applied for this item
    private Double discountedPrice; // (price * quantity) - totalDiscount
}