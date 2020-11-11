package capstone.petitehero.dtos.response.quest;

import capstone.petitehero.dtos.response.quest.badge.QuestBadgeResponseDTO;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class ListQuestResponseDTO implements Serializable {

    private Long questId;
    private String name;
    private String status;
    private Integer reward;
    private String description;
}
