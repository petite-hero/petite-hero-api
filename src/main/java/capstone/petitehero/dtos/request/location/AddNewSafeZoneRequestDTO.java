package capstone.petitehero.dtos.request.location;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class AddNewSafeZoneRequestDTO {
    private String name;
    private Double latitude;
    private Double longitude;
    private Date fromTime;
    private Date toTime;
    private Long date;
    private String repeatOn;
    private Integer radius;
    private String type;
    private Long childId;
    private String creator;

    private Double latA;
    private Double lngA;
    private Double latB;
    private Double lngB;
    private Double latC;
    private Double lngC;
    private Double latD;
    private Double lngD;

    public AddNewSafeZoneRequestDTO() {

    }

    public AddNewSafeZoneRequestDTO(String name, Double latitude, Double longitude, Date fromTime, Date toTime, Long date, String repeatOn, Integer radius, String type, Long childId, String creator) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.fromTime = fromTime;
        this.toTime = toTime;
        this.date = date;
        this.repeatOn = repeatOn;
        this.radius = radius;
        this.type = type;
        this.childId = childId;
        this.creator = creator;
    }

    public AddNewSafeZoneRequestDTO(String name, Double latitude, Double longitude, Date fromTime, Date toTime, Long date, String repeatOn, Integer radius, String type, Long childId, String creator, Double latA, Double lngA, Double latB, Double lngB, Double latC, Double lngC, Double latD, Double lngD) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.fromTime = fromTime;
        this.toTime = toTime;
        this.date = date;
        this.repeatOn = repeatOn;
        this.radius = radius;
        this.type = type;
        this.childId = childId;
        this.creator = creator;

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
