package capstone.petitehero.dtos.common;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CRONJobChildDTO {
    private Long childId;
    private String pushToken;

    public CRONJobChildDTO() {
    }

    public CRONJobChildDTO(Long childId, String pushToken) {
        this.childId = childId;
        this.pushToken = pushToken;
    }
}
