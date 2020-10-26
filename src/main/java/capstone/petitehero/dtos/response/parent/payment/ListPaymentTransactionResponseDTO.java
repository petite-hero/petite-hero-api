package capstone.petitehero.dtos.response.parent.payment;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class ListPaymentTransactionResponseDTO implements Serializable {

    private Long transactionId;
    private String phoneNumber;
    private String date;
    private Double amount;
    private String status;
}
