package capstone.petitehero.dtos.response.subscription.type;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class SubscriptionTypeStatusResponseDTO implements Serializable {

    private Long subscriptionTypeId;
    private String status;
}
