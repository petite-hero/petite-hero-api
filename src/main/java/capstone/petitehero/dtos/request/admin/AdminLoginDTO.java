package capstone.petitehero.dtos.request.admin;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class AdminLoginDTO implements Serializable {

    private String username;
    private String password;
}
