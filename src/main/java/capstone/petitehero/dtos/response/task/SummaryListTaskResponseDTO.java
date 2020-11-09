package capstone.petitehero.dtos.response.task;

import capstone.petitehero.dtos.common.SummaryTaskDetail;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class SummaryListTaskResponseDTO implements Serializable {

    private Long childId;
    private SummaryTaskDetail houseworkTasks;
    private SummaryTaskDetail skillsTasks;
    private SummaryTaskDetail educationTasks;
}
