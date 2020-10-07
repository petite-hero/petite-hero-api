package capstone.petitehero.dtos.parent;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class ParentRegisterDTO implements Serializable {

    private String phoneNumber;
    private String password;
    private String confirmPassword;
    private String firstName;
    private String lastName;
    private String gender;
    private String photo;
    private String languageSetting;
}
