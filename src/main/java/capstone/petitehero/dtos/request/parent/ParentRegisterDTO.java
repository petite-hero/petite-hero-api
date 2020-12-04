package capstone.petitehero.dtos.request.parent;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class ParentRegisterDTO implements Serializable {

    private String phoneNumber;
    private String password;
}
