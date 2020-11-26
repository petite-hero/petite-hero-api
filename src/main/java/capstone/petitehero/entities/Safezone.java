package capstone.petitehero.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Time;
import java.time.LocalTime;
import java.util.Date;

@Entity
@Getter
@Setter
public class Safezone implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long safezoneId;

    @Column(length = 20)
    private String name;

    @Column
    private Double latitude;

    @Column
    private Double longitude;

    @Column
    @Temporal(value = TemporalType.TIME)
    private Date fromTime;

    @Column
    @Temporal(value = TemporalType.TIME)
    private Date toTime;

    @Column
    private Long date;

    @Column(length = 20)
    private String repeatOn;

    @Column
    private Integer radius;

    @Column
    private Boolean isDisabled;

    @Column(length = 20)
    private String type;

    @ManyToOne
    @JoinColumn(name = "child_id")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonManagedReference
    private Child child;

    @ManyToOne
    @JoinColumn(name = "creator_id")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonManagedReference
    private Parent parent;
}
