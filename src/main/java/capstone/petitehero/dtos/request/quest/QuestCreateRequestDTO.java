package capstone.petitehero.dtos.request.quest;

import capstone.petitehero.dtos.common.Assignee;
import capstone.petitehero.dtos.common.Assigner;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Setter
@Getter
public class QuestCreateRequestDTO implements Serializable {

    private List<Long> childId;
    private String name;
    private String description;
    private Integer criteria;
    private String rewardName;
    private String rewardPhoto;
    private String questBadge;
}
