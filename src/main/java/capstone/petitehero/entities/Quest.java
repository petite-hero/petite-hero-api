package capstone.petitehero.entities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;

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
    private String questPhoto;

    @Column(length = 30)
    private String status;

    @Column(columnDefinition = "TINYINT(1)")
    private Boolean isDone;

    @Column(columnDefinition = "TINYINT(1)")
    private Boolean isDisable;

    @OneToMany(mappedBy = "quest", cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude // không sử dụng trường này trong equals và hashcode
    @ToString.Exclude // không sử dụng trong toString()
    private Collection<Child_Quest> quest_childCollection;

    @ManyToOne
    @JoinColumn(name = "creator_phone_number")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Parent parent;
}
