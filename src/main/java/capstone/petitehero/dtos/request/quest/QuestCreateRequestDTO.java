package capstone.petitehero.dtos.request.quest;


import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class QuestCreateRequestDTO implements Serializable {

    private Long childId;
    private String creatorPhoneNumber;
    private String name;
    private String title;
    private String description;
    private Integer reward;
}
