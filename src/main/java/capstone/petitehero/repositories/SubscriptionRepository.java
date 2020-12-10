package capstone.petitehero.repositories;

import capstone.petitehero.entities.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    Subscription findSubscriptionByParent_Account_UsernameAndIsDisabledAndAndExpiredDateAfter(String parentPhoneNumber,
                                                                                              Boolean isDisabled,
                                                                                              Long expiredDate);

    List<Subscription> findSubscriptionsByIsDisabledAndExpiredDateBetween(Boolean isDisabled, Long startDay, Long endDay);
}
