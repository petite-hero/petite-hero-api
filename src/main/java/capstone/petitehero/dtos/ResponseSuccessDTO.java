package capstone.petitehero.dtos;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class ResponseSuccessDTO implements Serializable {

    private int code;
    private String msg;
    private List<Object> data;

    public ResponseSuccessDTO(int code, String msg, List<Object> data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public ResponseSuccessDTO() {
    }
}
