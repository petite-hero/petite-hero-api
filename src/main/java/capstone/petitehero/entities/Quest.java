package capstone.petitehero.entities;

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
public class Quest implements Serializable {

    @Id
    @GeneratedValue
    private Long questId;

    @Column(length = 30)
    private String name;

    @Column(length = Integer.MAX_VALUE)
    private String description;

    @Column
    private Integer criteria;

    @Column
    private Integer progress;

    @Column(length = Integer.MAX_VALUE)
    private String rewardPhoto;

    @Column(length = 30)
    private String rewardName;

    @Column(length = Integer.MAX_VALUE)
    private String questBadge;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date modifiedDate;

    @Column(length = 30)
    private String status;

    @Column
    private Boolean isDeleted;

    @ManyToOne
    @JoinColumn(name = "assignee")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Child child;

    @ManyToOne
    @JoinColumn(name = "creator_phone_number")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Parent parent;
}
