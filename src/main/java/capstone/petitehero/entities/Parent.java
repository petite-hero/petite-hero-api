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
@Setter
@Getter
public class Parent implements Serializable {

    @Id
    @Column(length = 15)
    private String parentPhoneNumber;

    @Column(length = 15)
    private String password;

    @Column(length = 30)
    private String firstName;

    @Column(length = 30)
    private String lastName;

    @Column(length = Integer.MAX_VALUE)
    private String photo;

    @Column
    private Boolean language;

    @Column
    private Boolean gender;

    @Column
    private Boolean isFreeTrial;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date expiredDate;

    @Column
    private Integer maxChildren;

    @Column
    private Integer maxParent;

    @Column
    private Boolean isDisable;

    @Column
    private String OTP;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude // không sử dụng trường này trong equals và hashcode
    @ToString.Exclude // Không sử dụng trong toString()
    private Collection<Parent_Child> parent_childCollection;

    @OneToMany(mappedBy = "collaborator", cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude // không sử dụng trường này trong equals và hashcode
    @ToString.Exclude // Không sử dụng trong toString()
    private Collection<Parent_Child> parent_collaboratorCollection;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude // không sử dụng trường này trong equals và hashcode
    @ToString.Exclude // Không sử dụng trong toString()
    private Collection<Payment> parent_paymentCollection;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude // không sử dụng trường này trong equals và hashcode
    @ToString.Exclude // Không sử dụng trong toString()
    private Collection<Quest> parent_questCollection;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude // không sử dụng trường này trong equals và hashcode
    @ToString.Exclude // Không sử dụng trong toString()
    private Collection<Task> parent_taskCollection;
}
