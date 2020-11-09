package capstone.petitehero.dtos.request.location;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PushNotiSWDTO {
    private String title;
    private String body;
    private Object data;

    public PushNotiSWDTO(String title, String body, Object data) {
        this.title = title;
        this.body = body;
        this.data = data;
    }

    public PushNotiSWDTO() {
    }
}
