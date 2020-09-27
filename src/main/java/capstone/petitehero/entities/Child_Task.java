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
public class Child_Task implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "child_id")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Child child;

    @ManyToOne
    @JoinColumn(name = "task_id")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Task task;
}
