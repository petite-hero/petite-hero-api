package capstone.petitehero.dtos.common;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class ConfigChangeDTO implements Serializable {

    private Integer outer_radius;
    private Integer report_delay;
}
