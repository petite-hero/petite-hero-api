package capstone.petitehero.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParentChildPushTokenDTO {
    private Long parentId;
    private String parentName;
    private Long childId;
    private String childName;
    private String parentPushToken;
    private String childPushToken;

    public ParentChildPushTokenDTO() {
    }

    public ParentChildPushTokenDTO(Long parentId, String parentName, Long childId, String childName, String parentPushToken, String childPushToken) {
        this.parentId = parentId;
        this.parentName = parentName;
        this.childId = childId;
        this.childName = childName;
        this.parentPushToken = parentPushToken;
        this.childPushToken = childPushToken;
    }
}
