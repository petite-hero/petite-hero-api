package capstone.petitehero.dtos.response.account;

import capstone.petitehero.dtos.common.ChildInformation;
import capstone.petitehero.dtos.common.ParentInformation;
import capstone.petitehero.dtos.response.subscription.ListSubscriptionResponseDTO;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Setter
@Getter
public class ParentDetailResponseDTO extends ListParentAccountResponseDTO implements Serializable {

    private String avatar;
    private Integer maxChild;
    private Integer maxCollaborator;
    private List<ChildInformation> childInformationList;
    private List<ParentInformation> collaboratorInformationList;
    private List<ListSubscriptionResponseDTO> subscriptionHistoryList;
}
