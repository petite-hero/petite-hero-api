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
public class Child implements Serializable {

    @Id
    @GeneratedValue
    private Long childId;

    @Column(length = 30)
    private String firstName;

    @Column(length = 30)
    private String lastName;

    @Column(length = Integer.MAX_VALUE)
    private String photo;

    @Column(length = 50)
    private String nickName;

    @Column
    private Boolean language;

    @Column
    private Boolean gender;

    @Column
    private Boolean isDisable;

    @OneToMany(mappedBy = "child", cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude // không sử dụng trường này trong equals và hashcode
    @ToString.Exclude // không sử dụng trong toString()
    private Collection<Parent_Child> child_parentCollection;

    @OneToMany(mappedBy = "child", cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude // không sử dụng trường này trong equals và hashcode
    @ToString.Exclude // không sử dụng trong toString()
    private Collection<Quest> child_questCollection;

    @OneToMany(mappedBy = "child", cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude // không sử dụng trường này trong equals và hashcode
    @ToString.Exclude // không sử dụng trong toString()
    private Collection<Task> child_taskCollection;

    @OneToMany(mappedBy = "child", cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude // không sử dụng trường này trong equals và hashcode
    @ToString.Exclude // không sử dụng trong toString()
    private Collection<Safezone> child_safezoneCollection;

    @OneToMany(mappedBy = "child", cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude // không sử dụng trường này trong equals và hashcode
    @ToString.Exclude // không sử dụng trong toString()
    private Collection<LocationHistory> child_locationHistoryCollection;
}
