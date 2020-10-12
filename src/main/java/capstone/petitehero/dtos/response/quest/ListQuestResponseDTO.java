package capstone.petitehero.dtos.response.quest;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class ListQuestResponseDTO implements Serializable {

    private Long questId;
    private String name;
    private Integer progress;
    private Integer criteria;
    private String questBadge;
}
