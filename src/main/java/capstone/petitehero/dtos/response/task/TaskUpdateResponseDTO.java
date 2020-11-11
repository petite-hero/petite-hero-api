package capstone.petitehero.dtos.response.task;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class TaskUpdateResponseDTO implements Serializable {

    private Long taskId;
    private String name;
    private String assignDate;
    private String fromTime;
    private String toTime;
    private String type;
    private String status;
}
