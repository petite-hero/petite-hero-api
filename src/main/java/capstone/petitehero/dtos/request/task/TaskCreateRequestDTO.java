package capstone.petitehero.dtos.request.task;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class TaskCreateRequestDTO implements Serializable {

    private String name;
    private String description;
    private String createdDate;
    private String assignDate;
    private String deadline;
}
