package monkcommerce.coupons.management.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "product_wise_coupons")
public class ProductWiseCoupon {

    @Id
    private Long id; // FK to Coupon

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private Coupon coupon;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "discount_value")
    private Double discountValue;

    @Column(name = "discount_type")
    private String discountType; // PERCENT / FIXED

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
