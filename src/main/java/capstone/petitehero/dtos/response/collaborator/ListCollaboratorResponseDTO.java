package capstone.petitehero.dtos.response.collaborator;

import capstone.petitehero.dtos.common.ChildInformation;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Setter
@Getter
public class ListCollaboratorResponseDTO implements Serializable {

    private String phoneNumber;
    private String firstName;
    private String lastName;
    private String gender;
    private List<ChildInformation> confirmChild;
    private List<ChildInformation> notConfirmChild;
}
