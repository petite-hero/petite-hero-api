package capstone.petitehero.dtos.response.child;

import capstone.petitehero.dtos.common.ChildInformation;
import capstone.petitehero.dtos.common.ParentInformation;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class VerifyParentResponseDTO implements Serializable {

    private ParentInformation parentInformation;
    private ChildInformation childInformation;
}
