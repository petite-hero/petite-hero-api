package capstone.petitehero.dtos.response.task;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class ListTaskHandedResponseDTO implements Serializable {

    private Long date;
    private Integer count;
}
