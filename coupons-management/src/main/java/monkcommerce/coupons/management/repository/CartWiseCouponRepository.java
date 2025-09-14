package monkcommerce.coupons.management.repository;

import monkcommerce.coupons.management.entities.CartWiseCoupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartWiseCouponRepository extends JpaRepository<CartWiseCoupon, Long> {
}
