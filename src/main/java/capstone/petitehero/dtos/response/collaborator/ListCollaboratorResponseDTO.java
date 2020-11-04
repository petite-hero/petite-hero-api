package capstone.petitehero.dtos.response.collaborator;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class ListCollaboratorResponseDTO implements Serializable {

    private String phoneNumber;
    private String firstName;
    private String lastName;
    private String gender;
    private Boolean isConfirm;
}
