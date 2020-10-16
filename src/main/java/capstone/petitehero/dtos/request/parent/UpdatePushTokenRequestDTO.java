package capstone.petitehero.dtos.request.parent;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatePushTokenRequestDTO {
    private Long accountId;
    private String pushToken;

    public UpdatePushTokenRequestDTO() {
    }

    public UpdatePushTokenRequestDTO(Long accountId, String pushToken) {
        this.accountId = accountId;
        this.pushToken = pushToken;
    }
}
