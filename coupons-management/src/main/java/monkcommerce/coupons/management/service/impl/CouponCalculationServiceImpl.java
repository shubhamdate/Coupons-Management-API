package monkcommerce.coupons.management.service.impl;

import lombok.RequiredArgsConstructor;
import monkcommerce.coupons.management.dto.CartItemRequest;
import monkcommerce.coupons.management.dto.CartRequest;
import monkcommerce.coupons.management.dto.*;
import monkcommerce.coupons.management.entities.*;
import monkcommerce.coupons.management.repository.*;
import monkcommerce.coupons.management.service.CouponCalculationService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponCalculationServiceImpl implements CouponCalculationService {

    private final CouponRepository couponRepository;
    private final CartWiseCouponRepository cartWiseCouponRepository;
    private final ProductWiseCouponRepository productWiseCouponRepository;
    private final BxgyCouponRepository bxgyCouponRepository;

    @Override
    public List<ApplicableCouponResponse> getApplicableCoupons(CartRequest cartRequest) {
        double cartTotal = cartRequest.getItems()
                .stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();

        List<ApplicableCouponResponse> applicableCoupons = new ArrayList<>();

        for (Coupon coupon : couponRepository.findAll()) {
            ApplicableCouponResponse response = null;

            switch (coupon.getType()) {
                case "CART_WISE" -> response = checkCartWiseCoupon(coupon, cartTotal);
                case "PRODUCT_WISE" -> response = checkProductWiseCoupon(coupon, cartRequest);
                case "bxgy" -> response = checkBxgyCoupon(coupon, cartRequest);
            }

            if (response != null) {
                applicableCoupons.add(response);
            }
        }
        return applicableCoupons;
    }

    @Override
    public ApplyCouponResponse applyCoupon(Long couponId, CartRequest cartRequest) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new RuntimeException("Coupon not found"));

        double totalPrice = cartRequest.getItems().stream()
                .mapToDouble(i -> i.getPrice() * i.getQuantity())
                .sum();

        double totalDiscount = 0;
        List<CartItemDiscountResponse> itemResponses = new ArrayList<>();

        for (CartItemRequest item : cartRequest.getItems()) {
            CartItemDiscountResponse itemResponse = new CartItemDiscountResponse();
            itemResponse.setProductId(item.getProductId());
            itemResponse.setQuantity(item.getQuantity());
            itemResponse.setPrice(item.getPrice());
            itemResponse.setTotalDiscount(0);

            itemResponses.add(itemResponse);
        }

        switch (coupon.getType()) {
            case "CART_WISE" -> totalDiscount = applyCartWiseDiscount(coupon, totalPrice);
            case "PRODUCT_WISE" -> totalDiscount = applyProductWiseDiscount(coupon, itemResponses);
            case "bxgy" -> totalDiscount = applyBxgyDiscount(coupon, itemResponses);
        }

        ApplyCouponResponse response = new ApplyCouponResponse();
        UpdatedCartResponse updatedCart = new UpdatedCartResponse();
        updatedCart.setItems(itemResponses);
        updatedCart.setTotalPrice(totalPrice);
        updatedCart.setTotalDiscount(totalDiscount);
        updatedCart.setFinalPrice(totalPrice - totalDiscount);

        response.setUpdatedCart(updatedCart);
        return response;
    }

    // ================= HELPER METHODS =================

    private ApplicableCouponResponse checkCartWiseCoupon(Coupon coupon, double cartTotal) {
        CartWiseCoupon cw = cartWiseCouponRepository.findById(coupon.getId()).orElse(null);
        if (cw != null && cartTotal >= cw.getThresholdAmount()) {
            double discount = 0;
            if ("PERCENT".equalsIgnoreCase(cw.getDiscountType())) {
                discount = cartTotal * cw.getDiscountValue() / 100;
            } else if ("FIXED".equalsIgnoreCase(cw.getDiscountType())) {
                discount = cw.getDiscountValue();
            }

            ApplicableCouponResponse res = new ApplicableCouponResponse();
            res.setCouponId(coupon.getId());
            res.setType("CART_WISE");
            res.setDiscount(discount);
            return res;
        }
        return null;
    }

    private double applyCartWiseDiscount(Coupon coupon, double totalPrice) {
        CartWiseCoupon cw = cartWiseCouponRepository.findById(coupon.getId()).orElse(null);
        if (cw != null && totalPrice >= cw.getThresholdAmount()) {
            if ("PERCENT".equalsIgnoreCase(cw.getDiscountType())) {
                return totalPrice * cw.getDiscountValue() / 100;
            } else if ("FIXED".equalsIgnoreCase(cw.getDiscountType())) {
                return cw.getDiscountValue();
            }
        }
        return 0;
    }

    private ApplicableCouponResponse checkProductWiseCoupon(Coupon coupon, CartRequest cart) {
        ProductWiseCoupon pw = productWiseCouponRepository.findById(coupon.getId()).orElse(null);
        if (pw != null) {
            for (CartItemRequest item : cart.getItems()) {
                if (item.getProductId().equals(pw.getProductId())) {
                    double discount = 0;
                    if ("PERCENT".equalsIgnoreCase(pw.getDiscountType())) {
                        discount = item.getPrice() * item.getQuantity() * pw.getDiscountValue() / 100;
                    } else if ("FIXED".equalsIgnoreCase(pw.getDiscountType())) {
                        discount = pw.getDiscountValue();
                    }

                    ApplicableCouponResponse res = new ApplicableCouponResponse();
                    res.setCouponId(coupon.getId());
                    res.setType("PRODUCT_WISE");
                    res.setDiscount(discount);
                    return res;
                }
            }
        }
        return null;
    }

    private double applyProductWiseDiscount(Coupon coupon, List<CartItemDiscountResponse> items) {
        ProductWiseCoupon pw = productWiseCouponRepository.findById(coupon.getId()).orElse(null);
        double discount = 0;
        if (pw != null) {
            for (CartItemDiscountResponse item : items) {
                if (item.getProductId().equals(pw.getProductId())) {
                    double d = 0;
                    if ("PERCENT".equalsIgnoreCase(pw.getDiscountType())) {
                        d = item.getPrice() * item.getQuantity() * pw.getDiscountValue() / 100;
                    } else if ("FIXED".equalsIgnoreCase(pw.getDiscountType())) {
                        d = pw.getDiscountValue();
                    }

                    item.setTotalDiscount(item.getTotalDiscount() + d);
                    discount += d;
                }
            }
        }
        return discount;
    }

    private ApplicableCouponResponse checkBxgyCoupon(Coupon coupon, CartRequest cart) {
        BxgyCoupon bxgy = bxgyCouponRepository.findById(coupon.getId()).orElse(null);
        if (bxgy == null) return null;

        // Step 1: Count how many buy items are in the cart
        int totalBuyRequired = bxgy.getBuyProducts().stream()
                .mapToInt(BxgyBuyProduct::getQuantity)
                .sum();

        int buyItemsInCart = cart.getItems().stream()
                .filter(item -> bxgy.getBuyProducts().stream()
                        .anyMatch(buy -> buy.getProductId().equals(item.getProductId())))
                .mapToInt(CartItemRequest::getQuantity)
                .sum();

        // Step 2: Find how many repetitions are possible
        int repetitions = buyItemsInCart / totalBuyRequired;
        repetitions = Math.min(repetitions, bxgy.getRepetitionLimit());

        if (repetitions <= 0) return null;

        // Step 3: Calculate discount (value of free products)
        double discount = 0;
        for (BxgyGetProduct gp : bxgy.getGetProducts()) {
            // find product price in cart
            double price = cart.getItems().stream()
                    .filter(item -> item.getProductId().equals(gp.getProductId()))
                    .mapToDouble(CartItemRequest::getPrice)
                    .findFirst()
                    .orElse(0); // If not in cart, assume 0 for now

            discount += repetitions * gp.getQuantity() * price;
        }

        ApplicableCouponResponse res = new ApplicableCouponResponse();
        res.setCouponId(coupon.getId());
        res.setType("bxgy");
        res.setDiscount(discount);
        return res;
    }

    private double applyBxgyDiscount(Coupon coupon, List<CartItemDiscountResponse> items) {
        BxgyCoupon bxgy = bxgyCouponRepository.findById(coupon.getId()).orElse(null);
        if (bxgy == null) return 0;

        int totalBuyRequired = bxgy.getBuyProducts().stream()
                .mapToInt(BxgyBuyProduct::getQuantity)
                .sum();

        int buyItemsInCart = items.stream()
                .filter(item -> bxgy.getBuyProducts().stream()
                        .anyMatch(buy -> buy.getProductId().equals(item.getProductId())))
                .mapToInt(CartItemDiscountResponse::getQuantity)
                .sum();

        int repetitions = buyItemsInCart / totalBuyRequired;
        repetitions = Math.min(repetitions, bxgy.getRepetitionLimit());

        if (repetitions <= 0) return 0;

        double discount = 0;

        for (BxgyGetProduct gp : bxgy.getGetProducts()) {
            CartItemDiscountResponse targetItem = items.stream()
                    .filter(item -> item.getProductId().equals(gp.getProductId()))
                    .findFirst()
                    .orElse(null);

            int totalFreeQty = repetitions * gp.getQuantity();
            double productDiscount = 0;

            if (targetItem != null) {
                double price = targetItem.getPrice();

                switch (gp.getDiscountType().toUpperCase()) {
                    case "FREE": // Default case
                        productDiscount = totalFreeQty * price;
                        break;

                    case "PERCENT":
                        productDiscount = totalFreeQty * price * (gp.getDiscountValue() / 100.0);
                        break;

                    case "FIXED":
                        productDiscount = totalFreeQty * gp.getDiscountValue();
                        break;

                    default:
                        productDiscount = 0;
                }

                targetItem.setTotalDiscount(targetItem.getTotalDiscount() + productDiscount);
            } else {
                // If not in cart, add as free product only if type = FREE
                if ("FREE".equalsIgnoreCase(gp.getDiscountType())) {
                    CartItemDiscountResponse newFreeItem = new CartItemDiscountResponse();
                    newFreeItem.setProductId(gp.getProductId());
                    newFreeItem.setQuantity(totalFreeQty);
                    newFreeItem.setPrice(0.0);
                    newFreeItem.setTotalDiscount(0.0);
                    items.add(newFreeItem);
                }
            }

            discount += productDiscount;
        }

        return discount;
    }
}

