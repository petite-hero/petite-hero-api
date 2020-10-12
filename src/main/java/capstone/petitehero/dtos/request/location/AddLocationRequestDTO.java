package capstone.petitehero.dtos.request.location;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AddLocationRequestDTO {
    private Double latitude;
    private Double longitude;
    private Long time;
    private String status;
    private Long child;

    public AddLocationRequestDTO() {

    }

    public AddLocationRequestDTO(Double latitude, Double longitude, Long time, String status, Long child) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.time = time;
        this.status = status;
        this.child = child;
    }
}
