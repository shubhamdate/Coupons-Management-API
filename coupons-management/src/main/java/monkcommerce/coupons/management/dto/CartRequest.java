package monkcommerce.coupons.management.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CartRequest {
    private List<CartItemRequest> items;
}

