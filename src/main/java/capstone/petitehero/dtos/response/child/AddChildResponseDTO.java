package capstone.petitehero.dtos.response.child;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class AddChildResponseDTO implements Serializable {

    private Long childId;
    private String firstName;
    private String lastName;
    private String nickName;
    private Integer yob;
    private String gender;
    private String language;
    private String photo;
    private Long token;
    private String parentPhoneNumber;

}
