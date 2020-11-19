package capstone.petitehero.repositories;

import capstone.petitehero.entities.SubscriptionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubscriptionTypeRepository extends JpaRepository<SubscriptionType, Long> {

    SubscriptionType findSubscriptionTypeBySubscriptionTypeId(Long subscriptionTypeId);

    SubscriptionType findSubscriptionTypeBySubscriptionTypeIdAndIsDeleted(Long subscriptionTypeId, Boolean isDeleted);

    List<SubscriptionType> findSubscriptionTypesByIsDeletedAndAppliedDateLessThanEqual(Boolean isDeleted, Long currentDayTimeStamp);

    List<SubscriptionType> findSubscriptionTypesByMaxCollaboratorGreaterThanEqualAndMaxChildrenIsGreaterThanEqualAndAppliedDateLessThanEqualAndIsDeletedAndPriceNot(Integer maxCollaborator, Integer maxChildren, Long appliedDayTimeStamp, Boolean isDeleted, Double    price);
}
