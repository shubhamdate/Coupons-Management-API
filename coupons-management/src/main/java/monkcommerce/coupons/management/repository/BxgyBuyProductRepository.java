package monkcommerce.coupons.management.repository;

import monkcommerce.coupons.management.entities.BxgyBuyProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BxgyBuyProductRepository extends JpaRepository<BxgyBuyProduct, Long> {
}
