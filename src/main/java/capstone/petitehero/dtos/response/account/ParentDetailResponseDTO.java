package capstone.petitehero.dtos.response.account;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class ParentDetailResponseDTO extends ListParentAccountResponseDTO implements Serializable {

    private String avatar;
    private Integer listCollaborator;
    private Integer listActiveChildren;
}
