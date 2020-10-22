package capstone.petitehero.dtos.response.parent.payment;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class ParentPaymentCompledResponseDTO implements Serializable {

    private String phoneNumber;
    private Double amount;
    private String description;
    private String status;
    private String paymentId;
}
