package capstone.petitehero.dtos.request.location;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AddLocationRequestDTO {
    private Double latitude;
    private Double longitude;
    private Long time;
    private Boolean status;
    private Long child;
    private String provider;

    public AddLocationRequestDTO() {

    }

    public AddLocationRequestDTO(Double latitude, Double longitude, Long time, Boolean status, Long child, String provider) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.time = time;
        this.status = status;
        this.child = child;
        this.provider = provider;
    }
}
