package capstone.petitehero.dtos.common;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class ParentInformation extends Assigner implements Serializable {

    private String email;
}
