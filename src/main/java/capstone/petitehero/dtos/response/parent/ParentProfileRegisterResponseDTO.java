package capstone.petitehero.dtos.response.parent;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class ParentProfileRegisterResponseDTO extends ParentRegisterResponseDTO implements Serializable {

    private String name;
    private String gender;
    private String language;
    private String photo;
}

