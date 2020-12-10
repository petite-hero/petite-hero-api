package capstone.petitehero.repositories;

import capstone.petitehero.entities.ParentPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParentPaymentRepository extends JpaRepository<ParentPayment, Long> {

    ParentPayment findParentPaymentBySubscription_Parent_Account_UsernameAndCreateDate(String phoneNumber, Long createdDateTimeStamp);

    List<ParentPayment> findAllByOrderByCreateDateDesc();

    List<ParentPayment> findParentPaymentsBySubscription_Parent_Account_UsernameAndStatusOrderByCreateDateDesc(String phoneNumber, String status);

    List<ParentPayment> findParentPaymentsBySubscription_Parent_Account_UsernameOrderByCreateDateDesc(String phoneNumber);

    ParentPayment findParentPaymentByParentPaymentId(Long transactionId);

    List<ParentPayment> findParentPaymentsBySubscription_SubscriptionId(Long subscriptionId);
}
