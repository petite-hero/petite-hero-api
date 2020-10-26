package capstone.petitehero.dtos.response.parent.payment;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class ParentPaymentDetailResponseDTO implements Serializable {

    private String content;
    private Double amount;
    private String date;
    private String status;
    private String phoneNumber;
    private String paymentId;
}
