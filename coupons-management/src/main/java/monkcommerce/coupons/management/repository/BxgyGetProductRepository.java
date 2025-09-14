package monkcommerce.coupons.management.repository;

import monkcommerce.coupons.management.entities.BxgyGetProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BxgyGetProductRepository extends JpaRepository<BxgyGetProduct, Long> {
}
