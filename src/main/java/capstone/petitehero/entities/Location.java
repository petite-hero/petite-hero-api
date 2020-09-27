package capstone.petitehero.entities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

@Entity
@Getter
@Setter
public class Location implements Serializable {

    @Id
    @GeneratedValue
    private Long locationId;

    @Column
    private Double xCoordinate;

    @Column
    private Double yCoordinate;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date time;

    @ManyToOne
    @JoinColumn(name = "child_id")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Child child;
}
