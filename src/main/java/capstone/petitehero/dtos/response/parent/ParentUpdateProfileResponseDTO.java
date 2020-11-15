package capstone.petitehero.dtos.response.parent;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class ParentUpdateProfileResponseDTO implements Serializable {

    private String name;
    private String gender;
    private String language;
    private String email;
    private String photo;
    private String status;
}
