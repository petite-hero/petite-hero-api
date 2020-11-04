package capstone.petitehero.dtos.response.quest.badge;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class QuestBadgeResponseDTO implements Serializable {

    private Integer questCompletedNumber;
    private Integer questBadgeId;
}
