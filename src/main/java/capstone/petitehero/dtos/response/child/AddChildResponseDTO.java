package capstone.petitehero.dtos.response.child;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class AddChildResponseDTO implements Serializable {

    private String firstName;
    private String lastName;
    private String nickName;
    private String gender;
    private String language;
    private String photo;

}
