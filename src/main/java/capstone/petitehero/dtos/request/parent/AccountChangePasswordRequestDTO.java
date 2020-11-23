package capstone.petitehero.dtos.request.parent;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class AccountChangePasswordRequestDTO implements Serializable {

    private String oldPassword;
    private String password;
    private String confirmPassword;
}
