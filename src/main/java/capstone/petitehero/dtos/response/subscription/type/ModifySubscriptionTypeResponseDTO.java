package capstone.petitehero.dtos.response.subscription.type;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class ModifySubscriptionTypeResponseDTO extends SubscriptionTypeDetailResponseDTO implements Serializable {

    private String status;
}
