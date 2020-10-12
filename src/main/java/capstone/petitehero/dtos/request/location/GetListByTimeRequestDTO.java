package capstone.petitehero.dtos.request.location;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetListByTimeRequestDTO {
    private Long child;
    private int time;
}
