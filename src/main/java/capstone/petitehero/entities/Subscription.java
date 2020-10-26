package capstone.petitehero.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;

@Getter
@Setter
@Entity
public class Subscription implements Serializable {

    @Id
    @GeneratedValue
    private Long subscriptionId;

    @Column(length = 200)
    private String description;

    @Column
    private Double price;

    @Column
    private Integer maxChild;

    @Column
    private Integer maxCollaborator;


}
