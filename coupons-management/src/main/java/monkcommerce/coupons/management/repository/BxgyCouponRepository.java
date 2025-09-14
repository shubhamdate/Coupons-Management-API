package monkcommerce.coupons.management.repository;

import monkcommerce.coupons.management.entities.BxgyCoupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BxgyCouponRepository extends JpaRepository<BxgyCoupon, Long> {
}
