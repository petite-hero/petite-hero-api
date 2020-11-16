package capstone.petitehero.dtos.response.quest;

import capstone.petitehero.dtos.common.Assignee;
import capstone.petitehero.dtos.common.Assigner;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class QuestCreateResponseDTO implements Serializable {

    private Long questId;
    private String name;
    private String description;
    private String status;
    private String createdDate;
    private String title;
    private Integer reward;
    private Assignee assignee;
    private Assigner assigner;
}
