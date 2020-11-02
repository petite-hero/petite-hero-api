package capstone.petitehero.dtos.common;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class ChildInformation extends Assignee implements Serializable {

    private Integer age;
    private Boolean hasDevice;
    private Boolean isTrackingActive;
}
