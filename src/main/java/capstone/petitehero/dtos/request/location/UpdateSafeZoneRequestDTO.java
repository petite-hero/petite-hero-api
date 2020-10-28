package capstone.petitehero.dtos.request.location;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class UpdateSafeZoneRequestDTO {
    private Long safezoneId;
    private String name;
    private Double latitude;
    private Double longitude;
    private Date fromTime;
    private Date toTime;
    private Long date;
    private String repeatOn;
    private Integer radius;
    private String type;

    public UpdateSafeZoneRequestDTO() {

    }

    public UpdateSafeZoneRequestDTO(Long safezoneId, String name, Double latitude, Double longitude, Date fromTime, Date toTime, Long date, String repeatOn, Integer radius, String type) {
        this.safezoneId = safezoneId;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.fromTime = fromTime;
        this.toTime = toTime;
        this.date = date;
        this.repeatOn = repeatOn;
        this.radius = radius;
        this.type = type;
    }
}
