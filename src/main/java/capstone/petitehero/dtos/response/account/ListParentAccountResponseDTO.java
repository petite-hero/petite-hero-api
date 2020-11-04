package capstone.petitehero.dtos.response.account;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class ListParentAccountResponseDTO implements Serializable {

    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private Long expiredDate;
    private String subscriptionType;
}
