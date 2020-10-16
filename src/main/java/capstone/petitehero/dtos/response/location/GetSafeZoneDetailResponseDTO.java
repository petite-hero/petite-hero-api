package capstone.petitehero.dtos.response.location;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class GetSafeZoneDetailResponseDTO {
    private Long safezoneId;
    private String name;
    private Double latitude;
    private Double longitude;
    private Date fromTime;
    private Date toTime;
    private Long date;
    private String repeatOn;
    private Integer radius;
    private Boolean isDisabled;
    private String type;
    private Long child;
    private Long parent;

    public GetSafeZoneDetailResponseDTO() {
    }

    public GetSafeZoneDetailResponseDTO(Long safezoneId, String name, Double latitude, Double longitude, Date fromTime, Date toTime, Long date, String repeatOn, Integer radius, Boolean isDisabled, String type, Long child, Long parent) {
        this.safezoneId = safezoneId;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.fromTime = fromTime;
        this.toTime = toTime;
        this.date = date;
        this.repeatOn = repeatOn;
        this.radius = radius;
        this.isDisabled = isDisabled;
        this.type = type;
        this.child = child;
        this.parent = parent;
    }
}
