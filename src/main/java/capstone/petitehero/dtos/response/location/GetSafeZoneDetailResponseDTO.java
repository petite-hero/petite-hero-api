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

    private Double latA;
    private Double lngA;
    private Double latB;
    private Double lngB;
    private Double latC;
    private Double lngC;
    private Double latD;
    private Double lngD;

    public GetSafeZoneDetailResponseDTO() {
    }

    public GetSafeZoneDetailResponseDTO(Long safezoneId, String name, Double latitude, Double longitude, Date fromTime, Date toTime, Long date, String repeatOn, Integer radius, Boolean isDisabled, String type, Long child, Long parent, Double latA, Double lngA, Double latB, Double lngB, Double latC, Double lngC, Double latD, Double lngD) {
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

        this.latA = latA;
        this.lngA = lngA;
        this.latB = latB;
        this.lngB = lngB;
        this.latC = latC;
        this.lngC = lngC;
        this.latD = latD;
        this.lngD = lngD;
    }
}
