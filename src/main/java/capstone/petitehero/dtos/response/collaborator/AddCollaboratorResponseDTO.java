package capstone.petitehero.dtos.response.collaborator;

import capstone.petitehero.dtos.common.ChildInformation;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Setter
@Getter
public class AddCollaboratorResponseDTO implements Serializable {

    private String parentPhoneNumber;
    private List<ChildInformation> listChildren;
    private String status;
}
