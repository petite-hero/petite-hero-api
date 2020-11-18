package capstone.petitehero.dtos.response.account;

import capstone.petitehero.dtos.common.ChildInformation;
import capstone.petitehero.dtos.common.ParentInformation;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Setter
@Getter
public class ParentDetailResponseDTO extends ListParentAccountResponseDTO implements Serializable {

    private String avatar;
    private List<ChildInformation> childInformationList;
    private List<ParentInformation> collaboratorInformationList;
}
