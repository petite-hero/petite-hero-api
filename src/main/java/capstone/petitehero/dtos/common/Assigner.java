package capstone.petitehero.dtos.common;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class Assigner implements Serializable {

    private String phoneNumber;
    private String name;
    private String gender;
}
