package capstone.petitehero.dtos.response.task;

import capstone.petitehero.dtos.common.Assignee;
import capstone.petitehero.dtos.common.Assigner;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class TaskCreateResponseDTO implements Serializable {

    private Long taskId;
    private String name;
    private String description;
    private Long createdDate;
    private Long assignDate;
    private String fromTime;
    private String toTime;
    private String status;
    private String type;
    private Assignee assignee;
    private Assigner assigner;
    private Boolean isOverlap;
}
