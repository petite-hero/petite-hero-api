package capstone.petitehero.dtos.response.parent;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Setter
@Getter
public class ParentRegisterResponseDTO implements Serializable {

    private String phoneNumber;
    private String firstName;
    private String lastName;
    private String gender;
    private String language;
    private String photo;
    private Integer maxChildren;
    private Integer maxParent;
    private Date expiredDate;
    private String isFreeTrial;

}

