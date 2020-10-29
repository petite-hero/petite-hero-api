package capstone.petitehero.dtos.response.subscription.type;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;

@Setter
@Getter
public class ListSubscriptionTypeResponseDTO implements Serializable {

    private Long subscriptionTypeId;
    private String name;
    private String description;
    private Double price;
    private Integer maxChildren;
    private Integer maxCollaborator;
}
