package capstone.petitehero.dtos.response.location;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class GetListByDateResponseDTO {
    private Long safezoneId;
    private String name;
    private Double latitude;
    private Double longitude;
    private Date fromTime;
    private Date toTime;
    private String type;

    public GetListByDateResponseDTO() {

    }

    public GetListByDateResponseDTO(Long safezoneId, String name, Double latitude, Double longitude, Date fromTime, Date toTime, String type) {
        this.safezoneId = safezoneId;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.fromTime = fromTime;
        this.toTime = toTime;
        this.type = type;
    }
}
