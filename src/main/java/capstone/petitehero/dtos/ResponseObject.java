package capstone.petitehero.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ResponseObject {

    private int code;
    private String msg;
    private String errorMsg;
    private Object data;

    public ResponseObject(int code, String msg, String errorMsg, Object data) {
        this.code = code;
        this.msg = msg;
        this.errorMsg = errorMsg;
        this.data = data;
    }

    public ResponseObject() {
    }
}
