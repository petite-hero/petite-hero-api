package capstone.petitehero.dtos.request.subscription.type;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class CreateSubscriptionTypeRequestDTO implements Serializable {

    private String name;
    private String description;
    private Double price;
    private Integer maxChildren;
    private Integer maxCollaborator;
    private Integer durationDay;
}
