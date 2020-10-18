package capstone.petitehero.dtos.request.parent;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class ParentUpdateProfileRequestDTO implements Serializable {

    private String firstName;
    private String lastName;
    private String gender;
    private String language;
    private String email;
}
