package capstone.petitehero.dtos.request.location;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PushSilentNotiSWDTO {
    private String title;
    private String body;
    private Object data;

    public PushSilentNotiSWDTO(String title, String body, Object data) {
        this.title = title;
        this.body = body;
        this.data = data;
    }

    public PushSilentNotiSWDTO() {
    }
}
