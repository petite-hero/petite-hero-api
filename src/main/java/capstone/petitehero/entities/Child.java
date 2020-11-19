package capstone.petitehero.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long childId;

    @Column(length = 50)
    private String name;

    @Column
    private Integer yob;

    @Column(length = Integer.MAX_VALUE)
    private String photo;

    @Column(length = 50)
    private String nickName;

    @Column
    private Boolean language;

    @Column(length = Integer.MAX_VALUE)
    private String androidId;

    @Column(length = Integer.MAX_VALUE)
    private String deviceName;

    @Column
    private Boolean gender;

    @Column
    private Boolean isDisabled;

    @Column
    private Long createdDate;

    @Column(length = Integer.MAX_VALUE)
    private String pushToken;

    @Column
    private Boolean trackingActive;

    @OneToMany(mappedBy = "child", cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude // không sử dụng trường này trong equals và hashcode
    @ToString.Exclude // không sử dụng trong toString()
    @JsonBackReference
    private Collection<Parent_Child> child_parentCollection;

    @OneToMany(mappedBy = "child", cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude // không sử dụng trường này trong equals và hashcode
    @ToString.Exclude // không sử dụng trong toString()
    @JsonBackReference
    private Collection<Quest> child_questCollection;

    @OneToMany(mappedBy = "child", cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude // không sử dụng trường này trong equals và hashcode
    @ToString.Exclude // không sử dụng trong toString()
    @JsonBackReference
    private Collection<Task> child_taskCollection;

    @OneToMany(mappedBy = "child", cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude // không sử dụng trường này trong equals và hashcode
    @ToString.Exclude // không sử dụng trong toString()
    @JsonBackReference
    private Collection<Safezone> child_safezoneCollection;

    @OneToMany(mappedBy = "child", cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude // không sử dụng trường này trong equals và hashcode
    @ToString.Exclude // không sử dụng trong toString()
    @JsonBackReference
    private Collection<LocationHistory> child_locationHistoryCollection;
}
