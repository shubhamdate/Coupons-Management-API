package monkcommerce.coupons.management.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import monkcommerce.coupons.management.dto.*;
import monkcommerce.coupons.management.entities.*;
import monkcommerce.coupons.management.repository.*;
import monkcommerce.coupons.management.service.CouponService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

    private final CouponRepository couponRepository;
    private final CartWiseCouponRepository cartWiseCouponRepository;
    private final ProductWiseCouponRepository productWiseCouponRepository;
    private final BxgyCouponRepository bxgyCouponRepository;
    private final BxgyBuyProductRepository bxgyBuyProductRepository;
    private final BxgyGetProductRepository bxgyGetProductRepository;

    @Override
    @Transactional
    public CouponResponse createCoupon(CouponRequest request) {
        // 1️⃣ Create base coupon
        Coupon coupon = new Coupon();
        coupon.setType(request.getType());
        coupon.setIsActive(request.getIsActive());
        coupon.setExpiresAt(request.getExpiresAt());
        coupon = couponRepository.save(coupon);

        // 2️⃣ Handle subtypes
        switch (request.getType()) {
            case "CART_WISE" -> {
                CartWiseCoupon cart = new CartWiseCoupon();
                cart.setCoupon(coupon);
                cart.setThresholdAmount(request.getCartWiseDetails().getThresholdAmount());
                cart.setDiscountValue(request.getCartWiseDetails().getDiscountValue());
                cart.setDiscountType(request.getCartWiseDetails().getDiscountType());
                cartWiseCouponRepository.save(cart);
            }
            case "PRODUCT_WISE" -> {
                ProductWiseCoupon productWise = new ProductWiseCoupon();
                productWise.setCoupon(coupon);
                productWise.setProductId(request.getProductWiseDetails().getProductId());
                productWise.setDiscountValue(request.getProductWiseDetails().getDiscountValue());
                productWise.setDiscountType(request.getProductWiseDetails().getDiscountType());
                productWiseCouponRepository.save(productWise);
            }
            case "BXGY" -> {
                BxgyCoupon bxgy = new BxgyCoupon();
                bxgy.setCoupon(coupon);
                bxgy.setRepetitionLimit(request.getBxgyDetails().getRepetitionLimit());
                bxgy = bxgyCouponRepository.save(bxgy);

                // Save Buy products
                for (BxgyBuyProductRequest buyReq : request.getBxgyDetails().getBuyProducts()) {
                    BxgyBuyProduct buy = new BxgyBuyProduct();
                    buy.setBxgyCoupon(bxgy);
                    buy.setProductId(buyReq.getProductId());
                    buy.setQuantity(buyReq.getQuantity());
                    bxgyBuyProductRepository.save(buy);
                }

                // Save Get products
                for (BxgyGetProductRequest getReq : request.getBxgyDetails().getGetProducts()) {
                    BxgyGetProduct get = new BxgyGetProduct();
                    get.setBxgyCoupon(bxgy);
                    get.setProductId(getReq.getProductId());
                    get.setQuantity(getReq.getQuantity());
                    get.setDiscountType(getReq.getDiscountType());
                    get.setDiscountValue(getReq.getDiscountValue());
                    bxgyGetProductRepository.save(get);
                }
            }
        }

        return mapToCouponResponse(coupon);
    }

    @Override
    public CouponResponse getCouponById(Long id) {
        return couponRepository.findById(id)
                .map(this::mapToCouponResponse)
                .orElseThrow(() -> new RuntimeException("Coupon not found"));
    }

    @Override
    public List<CouponResponse> getAllCoupons() {
        return couponRepository.findAll()
                .stream()
                .map(this::mapToCouponResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteCoupon(Long id) {
        bxgyCouponRepository.findById(id).ifPresent(bxgy -> {
            bxgyBuyProductRepository.deleteAll(bxgy.getBuyProducts());
            bxgyGetProductRepository.deleteAll(bxgy.getGetProducts());
            bxgyCouponRepository.delete(bxgy);
        });
        couponRepository.deleteById(id);
    }

    @Override
    public CartResponse applyCouponToCart(Long couponId, CartRequest cartRequest) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new RuntimeException("Coupon not found"));

        switch (coupon.getType()) {
            case "CART_WISE":
                return applyCartWiseCoupon(coupon, cartRequest);
            case "PRODUCT_WISE":
                return applyProductWiseCoupon(coupon, cartRequest);
            case "BXGY":
                return applyBxgyCoupon(coupon, cartRequest);
            default:
                throw new RuntimeException("Unsupported coupon type");
        }
    }

    // ========== PRIVATE HELPERS ==========

    private CouponResponse mapToCouponResponse(Coupon coupon) {
        CouponResponse response = new CouponResponse();
        response.setId(coupon.getId());
        response.setType(coupon.getType());
        response.setIsActive(coupon.getIsActive());
        response.setExpiresAt(coupon.getExpiresAt());
        response.setCreatedAt(coupon.getCreatedAt());
        response.setUpdatedAt(coupon.getUpdatedAt());

        // map subtype details
        switch (coupon.getType()) {
            case "CART_WISE" -> {
                cartWiseCouponRepository.findById(coupon.getId()).ifPresent(cart -> {
                    CartWiseCouponResponse cartRes = new CartWiseCouponResponse();
                    cartRes.setThresholdAmount(cart.getThresholdAmount());
                    cartRes.setDiscountValue(cart.getDiscountValue());
                    cartRes.setDiscountType(cart.getDiscountType());
                    response.setCartWiseDetails(cartRes);
                });
            }
            case "PRODUCT_WISE" -> {
                productWiseCouponRepository.findById(coupon.getId()).ifPresent(pwc -> {
                    ProductWiseCouponResponse pwcRes = new ProductWiseCouponResponse();
                    pwcRes.setProductId(pwc.getProductId());
                    pwcRes.setDiscountValue(pwc.getDiscountValue());
                    pwcRes.setDiscountType(pwc.getDiscountType());
                    response.setProductWiseDetails(pwcRes);
                });
            }
            case "BXGY" -> {
                bxgyCouponRepository.findById(coupon.getId()).ifPresent(bxgy -> {
                    BxgyCouponResponse bxgyRes = new BxgyCouponResponse();
                    bxgyRes.setRepetitionLimit(bxgy.getRepetitionLimit());

                    bxgyRes.setBuyProducts(bxgy.getBuyProducts().stream().map(b -> {
                        BxgyBuyProductResponse br = new BxgyBuyProductResponse();
                        br.setProductId(b.getProductId());
                        br.setQuantity(b.getQuantity());
                        return br;
                    }).collect(Collectors.toList()));

                    bxgyRes.setGetProducts(bxgy.getGetProducts().stream().map(g -> {
                        BxgyGetProductResponse gr = new BxgyGetProductResponse();
                        gr.setProductId(g.getProductId());
                        gr.setQuantity(g.getQuantity());
                        gr.setDiscountType(g.getDiscountType());
                        gr.setDiscountValue(g.getDiscountValue());
                        return gr;
                    }).collect(Collectors.toList()));

                    response.setBxgyDetails(bxgyRes);
                });
            }
        }
        return response;
    }

    private CartResponse applyCartWiseCoupon(Coupon coupon, CartRequest cartRequest) {
        CartWiseCoupon cartCoupon = cartWiseCouponRepository.findById(coupon.getId())
                .orElseThrow(() -> new RuntimeException("Cart-wise coupon not found"));

        double total = cartRequest.getItems().stream()
                .mapToDouble(i -> i.getPrice() * i.getQuantity())
                .sum();

        double discount = total >= cartCoupon.getThresholdAmount()
                ? (cartCoupon.getDiscountType().equals("PERCENT")
                ? total * cartCoupon.getDiscountValue() / 100
                : cartCoupon.getDiscountValue())
                : 0;

        List<CartItemResponse> items = new ArrayList<>();
        for (CartItemRequest item : cartRequest.getItems()) {
            double itemTotal = item.getPrice() * item.getQuantity();
            CartItemResponse cir = new CartItemResponse();
            cir.setProductId(item.getProductId());
            cir.setQuantity(item.getQuantity());
            cir.setPrice(item.getPrice());

            if (discount > 0) {
                double ratio = itemTotal / total;
                double itemDiscount = discount * ratio;
                cir.setTotalDiscount(itemDiscount);
                cir.setDiscountedPrice(itemTotal - itemDiscount);
            } else {
                cir.setTotalDiscount(0.0);
                cir.setDiscountedPrice(itemTotal);
            }
            items.add(cir);
        }

        CartResponse response = new CartResponse();
        response.setItems(items);
        response.setTotalPrice(total);
        response.setTotalDiscount(discount);
        response.setFinalPrice(total - discount);
        response.setDiscountBreakdown(Map.of("CART_WISE", discount));
        return response;
    }

    private CartResponse applyProductWiseCoupon(Coupon coupon, CartRequest cartRequest) {
        ProductWiseCoupon pwc = productWiseCouponRepository.findById(coupon.getId())
                .orElseThrow(() -> new RuntimeException("Product-wise coupon not found"));

        double total = 0;
        double discount = 0;
        List<CartItemResponse> items = new ArrayList<>();

        for (CartItemRequest item : cartRequest.getItems()) {
            double itemTotal = item.getPrice() * item.getQuantity();
            total += itemTotal;

            CartItemResponse cir = new CartItemResponse();
            cir.setProductId(item.getProductId());
            cir.setQuantity(item.getQuantity());
            cir.setPrice(item.getPrice());

            if (item.getProductId().equals(pwc.getProductId())) {
                double itemDiscount = pwc.getDiscountType().equals("PERCENT")
                        ? itemTotal * pwc.getDiscountValue() / 100
                        : pwc.getDiscountValue();
                discount += itemDiscount;
                cir.setTotalDiscount(itemDiscount);
                cir.setDiscountedPrice(itemTotal - itemDiscount);
            } else {
                cir.setTotalDiscount(0.0);
                cir.setDiscountedPrice(itemTotal);
            }
            items.add(cir);
        }

        CartResponse response = new CartResponse();
        response.setItems(items);
        response.setTotalPrice(total);
        response.setTotalDiscount(discount);
        response.setFinalPrice(total - discount);
        response.setDiscountBreakdown(Map.of("PRODUCT_WISE", discount));
        return response;
    }

    private CartResponse applyBxgyCoupon(Coupon coupon, CartRequest cartRequest) {
        BxgyCoupon bxgy = bxgyCouponRepository.findById(coupon.getId())
                .orElseThrow(() -> new RuntimeException("BxGy coupon not found"));

        double total = cartRequest.getItems().stream()
                .mapToDouble(i -> i.getPrice() * i.getQuantity())
                .sum();

        double discount = 0;

        // 🔸 Step 1: Count how many times buy condition is satisfied
        int possibleApplications = Integer.MAX_VALUE;
        for (BxgyBuyProduct buy : bxgy.getBuyProducts()) {
            int inCartQty = cartRequest.getItems().stream()
                    .filter(i -> i.getProductId().equals(buy.getProductId()))
                    .mapToInt(CartItemRequest::getQuantity)
                    .sum();
            int times = inCartQty / buy.getQuantity();
            possibleApplications = Math.min(possibleApplications, times);
        }

        // 🔸 Step 2: Respect repetition limit
        int applyTimes = Math.min(possibleApplications, bxgy.getRepetitionLimit());

        // 🔸 Step 3: Prepare item responses
        List<CartItemResponse> items = new ArrayList<>();
        for (CartItemRequest item : cartRequest.getItems()) {
            double itemTotal = item.getPrice() * item.getQuantity();
            CartItemResponse cir = new CartItemResponse();
            cir.setProductId(item.getProductId());
            cir.setQuantity(item.getQuantity());
            cir.setPrice(item.getPrice());
            cir.setTotalDiscount(0.0);
            cir.setDiscountedPrice(itemTotal);
            items.add(cir);
        }

        // 🔸 Step 4: Apply Get products discount per repetition
        for (int t = 0; t < applyTimes; t++) {
            for (BxgyGetProduct get : bxgy.getGetProducts()) {
                Optional<CartItemResponse> matchedItem = items.stream()
                        .filter(i -> i.getProductId().equals(get.getProductId()))
                        .findFirst();

                if (matchedItem.isPresent()) {
                    CartItemResponse cir = matchedItem.get();
                    double itemPrice = cir.getPrice();

                    double itemDiscount = switch (get.getDiscountType()) {
                        case "FREE" -> itemPrice * get.getQuantity();
                        case "PERCENT" -> (itemPrice * get.getQuantity()) * (get.getDiscountValue() / 100);
                        case "FIXED" -> get.getDiscountValue() * get.getQuantity();
                        default -> 0.0;
                    };

                    cir.setTotalDiscount(cir.getTotalDiscount() + itemDiscount);
                    double originalTotal = cir.getPrice() * cir.getQuantity();
                    cir.setDiscountedPrice(originalTotal - cir.getTotalDiscount());
                    discount += itemDiscount;
                }
            }
        }

        // 🔸 Step 5: Build final response
        CartResponse response = new CartResponse();
        response.setItems(items);
        response.setTotalPrice(total);
        response.setTotalDiscount(discount);
        response.setFinalPrice(total - discount);
        response.setDiscountBreakdown(Map.of("BXGY", discount));

        return response;
    }
}
