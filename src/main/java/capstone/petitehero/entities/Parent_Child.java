package capstone.petitehero.entities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Getter
@Setter
public class Parent_Child implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "parent_phone_number")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Parent parent;

    @ManyToOne
    @JoinColumn(name = "parent_collaborator")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Parent collaborator;

    @ManyToOne
    @JoinColumn(name = "child_id")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Child child;
}
