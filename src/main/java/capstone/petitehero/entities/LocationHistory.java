package capstone.petitehero.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Setter
@Getter
public class LocationHistory implements Serializable {

    @Id
    @GeneratedValue
    private Long locationHistoryId;

    @Column
    private Double xCoordinate;

    @Column
    private Double yCoordinate;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date time;

    @Column(length = 30)
    private String status;

    @ManyToOne
    @JoinColumn(name = "child_id")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonManagedReference
    private Child child;

    public LocationHistory() {
    }

    public LocationHistory(Double xCoordinate, Double yCoordinate, Date time, String status, Child child) {
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
        this.time = time;
        this.status = status;
        this.child = child;
    }

    public LocationHistory(Long locationHistoryId, Double xCoordinate, Double yCoordinate, Date time, String status, Child child) {
        this.locationHistoryId = locationHistoryId;
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
        this.time = time;
        this.status = status;
        this.child = child;
    }
}
