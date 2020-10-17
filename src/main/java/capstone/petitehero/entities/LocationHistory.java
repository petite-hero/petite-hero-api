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
    private Double latitude;

    @Column
    private Double longitude;

    @Column
    private Long time;

    @Column
    private Boolean status;

    @ManyToOne
    @JoinColumn(name = "child_id")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonManagedReference
    private Child child;

    public LocationHistory() {
    }
}
