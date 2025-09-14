package monkcommerce.coupons.management.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "bxgy_coupons")
public class BxgyCoupon {

    @Id
    private Long id; // FK to Coupon

    @OneToOne
    @MapsId
    @JoinColumn(name = "id", nullable = false)
    private Coupon coupon;

    @Column(name = "repetition_limit")
    private Integer repetitionLimit;

    @OneToMany(mappedBy = "bxgyCoupon", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BxgyBuyProduct> buyProducts = new ArrayList<>();

    @OneToMany(mappedBy = "bxgyCoupon", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BxgyGetProduct> getProducts = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}

