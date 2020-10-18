package capstone.petitehero.dtos.response.child;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class UpdateChildProfileResponseDTO implements Serializable {

    private Long childId;
    private String firstName;
    private String lastName;
    private String nickname;
    private String gender;
    private String language;
    private String photo;
    private String status;
}
