package capstone.petitehero.services;

import capstone.petitehero.dtos.response.parent.payment.ParentPaymentCompledResponseDTO;
import capstone.petitehero.entities.ParentPayment;
import capstone.petitehero.repositories.ParentPaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ParentPaymentService {

    @Autowired
    private ParentPaymentRepository parentPaymentRepository;

    public ParentPayment insertParentPaymentToSystem(ParentPayment parentPayment) {
        return parentPaymentRepository.save(parentPayment);
    }

    public ParentPayment findParentPaymentToCompletePayment(String parentPhoneNumber, Long createdDateTimeStamp) {
        return parentPaymentRepository.findParentPaymentByParent_Account_UsernameAndDate(parentPhoneNumber, createdDateTimeStamp);
    }

    public ParentPaymentCompledResponseDTO completedSuccessParentPayment(ParentPayment parentPayment) {
        ParentPayment parentPaymentResult = parentPaymentRepository.save(parentPayment);

        if (parentPaymentResult != null) {
            ParentPaymentCompledResponseDTO result = new ParentPaymentCompledResponseDTO();

            result.setAmount(parentPaymentResult.getAmount());
            result.setDescription(parentPaymentResult.getContent());
            result.setPaymentId(parentPaymentResult.getPaymentId());
            result.setStatus(parentPaymentResult.getStatus());
            result.setPhoneNumber(parentPaymentResult.getParent().getAccount().getUsername());

            return result;
        }
        return null;
    }
}
