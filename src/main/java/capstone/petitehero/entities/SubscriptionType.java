package capstone.petitehero.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;

@Setter
@Getter
@Entity
public class SubscriptionType implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long subscriptionTypeId;

    @Column(length = 50)
    private String name;

    @Column
    private String description;

    @Column
    private Double price;

    @Column
    private Integer maxChildren;

    @Column
    private Integer maxCollaborator;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "subscriptionType")
    @EqualsAndHashCode.Exclude // không sử dụng trường này trong equals và hashcode
    @ToString.Exclude // Không sử dụng trong toString()
    @JsonBackReference
    private Collection<Subscription> subscription;
}
