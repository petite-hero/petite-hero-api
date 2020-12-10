package capstone.petitehero.dtos.response.parent.payment;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class ParentPaymentCompleteResponseDTO implements Serializable {

    private String phoneNumber;
    private Double amount;
    private String description;
    private String status;
    private Long payDate;
    private String paymentId;
}
