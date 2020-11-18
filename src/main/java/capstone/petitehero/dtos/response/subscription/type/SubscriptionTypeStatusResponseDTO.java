package capstone.petitehero.dtos.response.subscription.type;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Setter
@Getter
public class SubscriptionTypeStatusResponseDTO implements Serializable {

    private Long subscriptionTypeId;
    private String status;
    private List<SubscriptionTypeDetailResponseDTO> subscriptionTypeReplace;
}
