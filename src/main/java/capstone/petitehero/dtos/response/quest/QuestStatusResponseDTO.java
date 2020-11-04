package capstone.petitehero.dtos.response.quest;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class QuestStatusResponseDTO implements Serializable {

    private Long questId;
    private String status;
}
