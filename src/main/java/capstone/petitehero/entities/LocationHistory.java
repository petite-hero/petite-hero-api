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
}
