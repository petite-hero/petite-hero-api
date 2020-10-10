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
    private Date assignDate;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date modifiedDate;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date deadLine;

    @Column
    private Boolean isDeleted;

    @ManyToOne
    @JoinColumn(name = "assignee")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonManagedReference
    private Child child;

    @ManyToOne
    @JoinColumn(name = "creator_phone_number")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonManagedReference
    private Parent parent;
}
