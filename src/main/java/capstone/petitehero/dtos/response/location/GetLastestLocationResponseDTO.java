package capstone.petitehero.dtos.response.location;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetLastestLocationResponseDTO {

    private Double latitude;
    private Double longitude;
    private String status;

    public GetLastestLocationResponseDTO() {
    }

    public GetLastestLocationResponseDTO(Double latitude, Double longitude, String status) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.status = status;
    }

}
