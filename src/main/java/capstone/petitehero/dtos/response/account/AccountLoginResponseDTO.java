package capstone.petitehero.dtos.response.account;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class AccountLoginResponseDTO implements Serializable {

    private String role;
}
