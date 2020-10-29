package capstone.petitehero.dtos.response.subscription.type;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class ModifySubscriptionTypeResponseDTO implements Serializable {

    private Long subscriptionTypeId;
    private String name;
    private String description;
    private Double price;
    private Integer maxChildren;
    private Integer maxCollaborator;
    private String status;
}
