package capstone.petitehero.dtos.common;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class SummaryTaskDetail implements Serializable {

    private Long totalTaskAssigned;
    private Long taskAssigned;
    private Long taskHanded;
    private Long taskDone;
    private Long taskFailed;
}
