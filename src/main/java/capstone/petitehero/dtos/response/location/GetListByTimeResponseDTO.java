package capstone.petitehero.dtos.response.location;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetListByTimeResponseDTO {
    private Double latitude;
    private Double longitude;
    private Long time;
    private String status;

    public GetListByTimeResponseDTO() {
    }

    public GetListByTimeResponseDTO(Double latitude, Double longitude, Long time, String status) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.time = time;
        this.status = status;
    }
}
