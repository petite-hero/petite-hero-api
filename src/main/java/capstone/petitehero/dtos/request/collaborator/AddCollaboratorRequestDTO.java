package capstone.petitehero.dtos.request.collaborator;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Setter
@Getter
public class AddCollaboratorRequestDTO implements Serializable {

    private String collaboratorPhoneNumber;
    private List<Long> listChildId;
}
