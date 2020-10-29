package capstone.petitehero.dtos.response.parent;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class DisableParentResponseDTO implements Serializable {

    private String phoneNumber;
    private String status;
}
