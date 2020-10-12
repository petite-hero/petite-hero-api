package capstone.petitehero.dtos.request.parent;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class ParentRegisterRequestDTO implements Serializable {

    private String phoneNumber;
    private String password;
    private String confirmPassword;
    private String firstName;
    private String lastName;
    private String gender;
    private String language;
}