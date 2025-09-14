package monkcommerce.coupons.management.repository;

import monkcommerce.coupons.management.entities.ProductWiseCoupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductWiseCouponRepository extends JpaRepository<ProductWiseCoupon, Long> {
}
