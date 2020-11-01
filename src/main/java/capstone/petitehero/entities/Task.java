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
public class Task implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    private Long createdDate;

    @Column
    private Long assignDate;

    @Column
    private Long submitDate;

    @Column
    @Temporal(value = TemporalType.TIME)
    private Date fromTime;

    @Column
    @Temporal(value = TemporalType.TIME)
    private Date toTime;

    @Column(length = 8)
    private String repeatOn;

    @Column
    private Long isDuplicateTask;

    @Column
    private Boolean isDeleted;

    @Column(length = 30)
    private String type;

    @ManyToOne
    @JoinColumn(name = "assignee_id")
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
