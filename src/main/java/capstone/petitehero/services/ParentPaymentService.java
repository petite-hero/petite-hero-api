package capstone.petitehero.services;

import capstone.petitehero.config.common.Constants;
import capstone.petitehero.dtos.common.ParentInformation;
import capstone.petitehero.dtos.response.parent.payment.ListPaymentTransactionResponseDTO;
import capstone.petitehero.dtos.response.parent.payment.ParentPaymentCompledResponseDTO;
import capstone.petitehero.dtos.response.parent.payment.ParentPaymentDetailResponseDTO;
import capstone.petitehero.entities.ParentPayment;
import capstone.petitehero.repositories.ParentPaymentRepository;
import capstone.petitehero.utilities.Util;
import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ParentPaymentService {

    @Autowired
    private ParentPaymentRepository parentPaymentRepository;

    @Autowired
    private APIContext apiContext;

    public Payment createPayment(Double total,
                                 String currency,
                                 String method,
                                 String intent,
                                 String description,
                                 String cancelUrl,
                                 String successUrl) throws PayPalRESTException {

        Amount amount = new Amount();
        amount.setCurrency(currency);
        amount.setTotal(String.format("%.2f", total));

        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setDescription(description);

        List<Transaction> transactionList = new ArrayList<>();
        transactionList.add(transaction);

        Payer payer = new Payer();
        payer.setPaymentMethod(method);

        Payment payment = new Payment();
        payment.setIntent(intent);
        payment.setPayer(payer);
        payment.setTransactions(transactionList);

        RedirectUrls redirectUrls =new RedirectUrls();
        redirectUrls.setCancelUrl(cancelUrl);
        redirectUrls.setReturnUrl(successUrl);
//
        payment.setRedirectUrls(redirectUrls);

        apiContext.setMaskRequestId(true);
        return payment.create(apiContext);
    }

    public Payment executePayment(String paymentId, String payerId) throws PayPalRESTException{
        Payment payment = new Payment();
        payment.setId(paymentId);
        PaymentExecution paymentExecute = new PaymentExecution();
        paymentExecute.setPayerId(payerId);
        return payment.execute(apiContext, paymentExecute);
    }

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
        List<ParentPayment> listParentPaymentResult;

        if (status != null) {
            listParentPaymentResult = parentPaymentRepository.findParentPaymentsByParent_Account_UsernameAndStatusOrderByDateDesc(phoneNumber, status);
        } else {
            listParentPaymentResult = parentPaymentRepository.findParentPaymentsByParent_Account_UsernameOrderByDateDesc((phoneNumber));
        }

        if (listParentPaymentResult != null) {
            List<ListPaymentTransactionResponseDTO> result = new ArrayList<>();
            for (ParentPayment payment : listParentPaymentResult) {
                ListPaymentTransactionResponseDTO dataResult = new ListPaymentTransactionResponseDTO();

                dataResult.setTransactionId(payment.getTransactionId());
                dataResult.setPhoneNumber(payment.getParent().getAccount().getUsername());
                dataResult.setStatus(payment.getStatus());
                dataResult.setLink(payment.getLink());
                dataResult.setAmount(payment.getAmount());

                dataResult.setDate(payment.getDate());

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
            if (!listParentPaymentResult.isEmpty()) {
                for (ParentPayment payment : listParentPaymentResult) {
                    ListPaymentTransactionResponseDTO dataResult = new ListPaymentTransactionResponseDTO();

                    dataResult.setTransactionId(payment.getTransactionId());
                    dataResult.setStatus(payment.getStatus());
                    dataResult.setAmount(payment.getAmount());
                    dataResult.setPayerId(payment.getPayerId());
                    dataResult.setPaymentID(payment.getPaymentId());
                    dataResult.setContent(payment.getContent());
                    dataResult.setDate(payment.getDate());
                    dataResult.setLink(payment.getLink());

                    ParentInformation parentInformation = new ParentInformation();
                    parentInformation.setPhoneNumber(payment.getParent().getAccount().getUsername());
                    parentInformation.setName(payment.getParent().getName());
                    parentInformation.setEmail(payment.getParent().getEmail());

                    dataResult.setParentInformation(parentInformation);

                    result.add(dataResult);
                }
            }
            return result;
        }
        return null;
    }

    public ParentPaymentDetailResponseDTO getDetailParentPayment(Long transactionId, String role) {
        ParentPayment paymentResult = parentPaymentRepository.findParentPaymentByTransactionId(transactionId);

        if (paymentResult != null) {
            ParentPaymentDetailResponseDTO result = new ParentPaymentDetailResponseDTO();

            result.setPhoneNumber(paymentResult.getParent().getAccount().getUsername());
            result.setContent(paymentResult.getContent());
            result.setAmount(paymentResult.getAmount());
            result.setStatus(paymentResult.getStatus());
            result.setPaymentId(paymentResult.getPaymentId());
            if (role.equalsIgnoreCase(Constants.PARENT) &&
                    paymentResult.getStatus().equalsIgnoreCase(Constants.status.PENDING.toString())) {
                result.setLink(paymentResult.getLink());
            }

            result.setDate(Util.formatTimestampToDateTime(paymentResult.getDate()));

            return result;
        }

        return null;
    }
}
