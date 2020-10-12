package capstone.petitehero.dtos.response.task;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class TaskDeleteResponseDTO implements Serializable {

    private Long taskId;
    private String status;
}
