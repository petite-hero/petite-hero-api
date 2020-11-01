package capstone.petitehero.dtos.request.task;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Setter
@Getter
public class TaskCreateRequestDTO implements Serializable {

    private Long childId;
    private String creatorPhoneNumber;
    private String name;
    private String description;
    private Long fromTime;
    private Long toTime;
    private Long assignDate;
    private String type;
    private String repeatOn;
}
