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
    private Long createdDate;

    @Column(length = 30)
    private String status;

    @Column
    private Boolean isDeleted;

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
