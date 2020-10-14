package capstone.petitehero.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseObject {

    private int code;
    private String msg;
    private Object data;

    public ResponseObject() {
    }

    public ResponseObject(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }


}
