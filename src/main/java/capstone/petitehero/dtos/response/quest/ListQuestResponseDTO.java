package capstone.petitehero.dtos.response.quest;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class ListQuestResponseDTO implements Serializable {

    private Long questId;
    private String name;
    private String status;
    private String title;
    private Integer reward;
    private String description;
}
