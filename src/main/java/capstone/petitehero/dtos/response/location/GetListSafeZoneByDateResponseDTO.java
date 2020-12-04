package capstone.petitehero.dtos.response.location;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class GetListSafeZoneByDateResponseDTO {
    private Long safezoneId;
    private String name;
    private Double latitude;
    private Double longitude;
    private Long date;
    private Integer radius;
    private String repeatOn;
    private Date fromTime;
    private Date toTime;
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

    public GetListSafeZoneByDateResponseDTO() {

    }

    public GetListSafeZoneByDateResponseDTO(Long safezoneId, String name, Double latitude, Double longitude, Long date, Integer radius, String repeatOn, Date fromTime, Date toTime, String type, Long child, Long parent, Double latA, Double lngA, Double latB, Double lngB, Double latC, Double lngC, Double latD, Double lngD) {
        this.safezoneId = safezoneId;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.date = date;
        this.radius = radius;
        this.repeatOn = repeatOn;
        this.fromTime = fromTime;
        this.toTime = toTime;
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

    public GetListSafeZoneByDateResponseDTO(Long safezoneId, String name, Double latitude, Double longitude, Long date, String repeatOn, Date fromTime, Date toTime, String type, Long child, Long parent, Double latA, Double lngA, Double latB, Double lngB, Double latC, Double lngC, Double latD, Double lngD) {
        this.safezoneId = safezoneId;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.date = date;
        this.repeatOn = repeatOn;
        this.fromTime = fromTime;
        this.toTime = toTime;
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
