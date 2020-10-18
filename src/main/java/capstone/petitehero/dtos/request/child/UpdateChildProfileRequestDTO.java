package capstone.petitehero.dtos.request.child;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class UpdateChildProfileRequestDTO implements Serializable {

    private String firstName;
    private String lastName;
    private String nickName;
    private String gender;
    private Integer age;
    private String language;
}
