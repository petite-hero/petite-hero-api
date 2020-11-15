package capstone.petitehero.dtos.common;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class Assignee implements Serializable {

    private Long childId;
    private String name;
    private String nickName;
    private String gender;
}
