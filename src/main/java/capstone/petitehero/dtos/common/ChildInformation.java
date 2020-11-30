package capstone.petitehero.dtos.common;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class ChildInformation extends Assignee implements Serializable {

    private Integer age;
    private Integer yob;
    private String photo;
    private String androidId;
    private Boolean isTrackingActive;
    private Boolean isCollaboratorChild;
    private Boolean isConfirm;
}
