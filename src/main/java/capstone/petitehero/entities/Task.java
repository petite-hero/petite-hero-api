package capstone.petitehero.entities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

@Entity
@Getter
@Setter
public class Task implements Serializable {

    @Id
    @GeneratedValue
    private Long taskId;

    @Column(length = 30)
    private String name;

    @Column(length = Integer.MAX_VALUE)
    private String description;

    @Column(length = 30)
    private String status;

    @Column(length = Integer.MAX_VALUE)
    private String proofPhoto;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date deadLine;

    @Column(columnDefinition = "TINYINT(1)")
    private Boolean isDisable;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude // không sử dụng trường này trong equals và hashcode
    @ToString.Exclude // không sử dụng trong toString()
    private Collection<Child_Task> task_childCollection;

    @ManyToOne
    @JoinColumn(name = "creator_phone_number")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Parent parent;
}
