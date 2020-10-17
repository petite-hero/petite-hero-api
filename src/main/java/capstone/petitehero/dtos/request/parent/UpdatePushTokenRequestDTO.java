package capstone.petitehero.dtos.request.parent;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatePushTokenRequestDTO {
    private String username;
    private String pushToken;

    public UpdatePushTokenRequestDTO() {
    }

    public UpdatePushTokenRequestDTO(String username, String pushToken) {
        this.username = username;
        this.pushToken = pushToken;
    }
}
