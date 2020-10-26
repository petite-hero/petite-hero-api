package capstone.petitehero.repositories;

import capstone.petitehero.entities.ParentPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParentPaymentRepository extends JpaRepository<ParentPayment, Long> {

    ParentPayment findParentPaymentByParent_Account_UsernameAndDate(String phoneNumber, Long createdDateTimeStamp);

    List<ParentPayment> findAllByOrderByDateDesc();

    List<ParentPayment> findParentPaymentsByParent_Account_UsernameAndStatusOrderByDateDesc(String phoneNumber, String status);

    List<ParentPayment> findParentPaymentsByParent_Account_UsernameOrderByDateDesc(String phoneNumber);

    ParentPayment findParentPaymentByTransactionId(Long transactionId);
}
