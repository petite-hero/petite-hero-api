package capstone.petitehero.repositories;

import capstone.petitehero.entities.ParentPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParentPaymentRepository extends JpaRepository<ParentPayment, Long> {

    ParentPayment findParentPaymentByParent_Account_UsernameAndDate(String phoneNumber, Long createdDateTimeStamp);
}
