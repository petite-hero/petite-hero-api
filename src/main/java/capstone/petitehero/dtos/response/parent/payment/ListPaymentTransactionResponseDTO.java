package capstone.petitehero.dtos.response.parent.payment;

import capstone.petitehero.dtos.common.ParentInformation;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class ListPaymentTransactionResponseDTO implements Serializable {

    private Long transactionId;
    private String phoneNumber;
    private Long date;
    private Long payDate;
    private Double amount;
    private String content;
    private String link;
    private String status;
    private String payerId;
    private String paymentID;
    private ParentInformation parentInformation;
}
