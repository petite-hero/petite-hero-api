package capstone.petitehero.dtos.request.child;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class VerifyParentRequestDTO implements Serializable {

    private Long childId;
    private String pushToken;
//    private String parentPhoneNumber;
//    private Long token;
}
