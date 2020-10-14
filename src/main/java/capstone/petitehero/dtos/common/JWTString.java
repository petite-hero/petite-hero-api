package capstone.petitehero.dtos.common;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class JWTString implements Serializable {

    private String jwt;
}
