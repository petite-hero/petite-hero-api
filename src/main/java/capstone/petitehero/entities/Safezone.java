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
@Getter
@Setter
public class Safezone implements Serializable {

    @Id
    @GeneratedValue
    private Long safezoneId;

    @Column
    private Double xCoordinate;

    @Column
    private Double yCoordinate;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date timeIn;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date timeOut;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date timePadding;

    @Column
    private Integer radius;

    @ManyToOne
    @JoinColumn(name = "child_id")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonManagedReference
    private Child child;
}
