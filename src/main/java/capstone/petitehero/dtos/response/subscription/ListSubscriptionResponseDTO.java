package capstone.petitehero.dtos.response.subscription;

import capstone.petitehero.dtos.common.ParentInformation;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class ListSubscriptionResponseDTO implements Serializable {

    private Long subscriptionId;
    private String name;
    private Long startDate;
    private Long expiredDate;
    private Boolean isDisabled;
    private ParentInformation parentInformation;
}
