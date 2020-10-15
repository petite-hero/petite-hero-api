package capstone.petitehero.dtos.response.parent;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class ParentRegisterResponseDTO implements Serializable {

    private String phoneNumber;
    private String expiredDate;
    private String accountType;
    private Integer maxChildAllow;
    private Integer maxCollaboratorAllow;
}
