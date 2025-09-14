package monkcommerce.coupons.management.repository;

import monkcommerce.coupons.management.entities.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {
    List<Coupon> findByIsActiveTrue();
    List<Coupon> findByType(String type);

}
