package capstone.petitehero.dtos.response.child;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class ChildDetailResponseDTO implements Serializable {

    private Long childId;
    private String name;
    private String nickName;
    private Integer age;
    private String gender;
    private String photo;
    private String language;
    private Boolean hasDevice;
}
