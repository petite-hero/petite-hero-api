package capstone.petitehero.dtos.response.subscription.type;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class CreateSubscriptionTypeResponseDTO implements Serializable {

    private Long newSubscriptionTypeId;
    private String status;
}
