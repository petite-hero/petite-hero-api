package capstone.petitehero.dtos.request.child;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class AddChildRequestDTO implements Serializable {

    private String name;
    private Integer yob;
    private String nickName;
    private String gender;
    private String language;
}
