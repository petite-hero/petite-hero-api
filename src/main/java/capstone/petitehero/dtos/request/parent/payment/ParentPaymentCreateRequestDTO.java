package capstone.petitehero.dtos.request.parent.payment;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class ParentPaymentCreateRequestDTO implements Serializable {

    private Long subscriptionTypeId;
    private String description;
}
