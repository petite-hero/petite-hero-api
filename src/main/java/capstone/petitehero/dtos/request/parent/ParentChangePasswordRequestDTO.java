package capstone.petitehero.dtos.request.parent;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class ParentChangePasswordRequestDTO implements Serializable {

    private String password;
    private String confirmPassword;
}
