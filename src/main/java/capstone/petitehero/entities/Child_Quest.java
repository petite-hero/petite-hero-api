package capstone.petitehero.entities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Setter
@Getter
public class Child_Quest implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "child_id")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Child child;

    @ManyToOne
    @JoinColumn(name = "quest_id")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Quest quest;
}
