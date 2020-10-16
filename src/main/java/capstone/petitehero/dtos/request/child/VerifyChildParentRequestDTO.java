package capstone.petitehero.dtos.request.child;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class VerifyChildParentRequestDTO implements Serializable {

    private String firstName;
    private String lastName;
    private String nickname;
    private String gender;
    private String language;
    private String photo;
    private Integer yob;
    private String pushToken;
    private String phoneNumber;
}
