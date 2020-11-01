package capstone.petitehero.services;

import capstone.petitehero.dtos.response.parent.payment.ListPaymentTransactionResponseDTO;
import capstone.petitehero.dtos.response.parent.payment.ParentPaymentCompledResponseDTO;
import capstone.petitehero.dtos.response.parent.payment.ParentPaymentDetailResponseDTO;
import capstone.petitehero.entities.ParentPayment;
import capstone.petitehero.repositories.ParentPaymentRepository;
import capstone.petitehero.utilities.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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

    public List<ListPaymentTransactionResponseDTO> getParentTransaction(String phoneNumber, String status) {
        List<ListPaymentTransactionResponseDTO> result = new ArrayList<>();
        List<ParentPayment> listParentPaymentResult;

        if (status != null) {
            listParentPaymentResult = parentPaymentRepository.findParentPaymentsByParent_Account_UsernameAndStatusOrderByDateDesc(phoneNumber, status);
        } else {
            listParentPaymentResult = parentPaymentRepository.findParentPaymentsByParent_Account_UsernameOrderByDateDesc((phoneNumber));
        }

        if (listParentPaymentResult != null) {
            for (ParentPayment payment : listParentPaymentResult) {
                ListPaymentTransactionResponseDTO dataResult = new ListPaymentTransactionResponseDTO();

                dataResult.setTransactionId(payment.getTransactionId());
                dataResult.setPhoneNumber(payment.getParent().getAccount().getUsername());
                dataResult.setStatus(payment.getStatus());
                dataResult.setAmount(payment.getAmount());

                dataResult.setDate(Util.formatTimestampToDateTime(payment.getDate()));

                result.add(dataResult);
            }
            return result;
        }
        return null;
    }

    public List<ListPaymentTransactionResponseDTO> getListParentPaymentForAdmin() {
        List<ParentPayment> listParentPaymentResult = parentPaymentRepository.findAllByOrderByDateDesc();
        if (listParentPaymentResult != null) {
            List<ListPaymentTransactionResponseDTO> result = new ArrayList<>();

            for (ParentPayment payment : listParentPaymentResult) {
                ListPaymentTransactionResponseDTO dataResult = new ListPaymentTransactionResponseDTO();

                dataResult.setTransactionId(payment.getTransactionId());
                dataResult.setPhoneNumber(payment.getParent().getAccount().getUsername());
                dataResult.setStatus(payment.getStatus());
                dataResult.setAmount(payment.getAmount());

                dataResult.setDate(Util.formatTimestampToDateTime(payment.getDate()));

                result.add(dataResult);
            }
            return result;
        }
        return null;
    }

    public ParentPaymentDetailResponseDTO getDetailParentPayment(Long transactionId) {
        ParentPayment paymentResult = parentPaymentRepository.findParentPaymentByTransactionId(transactionId);

        if (paymentResult != null) {
            ParentPaymentDetailResponseDTO result = new ParentPaymentDetailResponseDTO();

            result.setPhoneNumber(paymentResult.getParent().getAccount().getUsername());
            result.setContent(paymentResult.getContent());
            result.setAmount(paymentResult.getAmount());
            result.setStatus(paymentResult.getStatus());
            result.setPaymentId(paymentResult.getPaymentId());

            result.setDate(Util.formatTimestampToDateTime(paymentResult.getDate()));

            return result;
        }

        return null;
    }
}
