package capstone.petitehero.dtos;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class ResponseErrorDTO implements Serializable {

    private int code;
    private String msg;

    public ResponseErrorDTO(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public ResponseErrorDTO() {
    }
}
