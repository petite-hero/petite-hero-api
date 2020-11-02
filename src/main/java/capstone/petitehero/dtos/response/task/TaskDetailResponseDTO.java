package capstone.petitehero.dtos.response.task;

import capstone.petitehero.dtos.common.Assignee;
import capstone.petitehero.dtos.common.Assigner;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class TaskDetailResponseDTO extends TaskCreateResponseDTO implements Serializable{

    private String proofPhoto;
    private Long submitDate;
}
