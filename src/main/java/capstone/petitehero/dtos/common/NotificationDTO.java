package capstone.petitehero.dtos.common;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class NotificationDTO implements Serializable {

    private Assigner assigner;
    private Assignee assignee;
}
