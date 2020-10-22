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
public class Parent implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

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
    private Long expiredDate;

    @Column
    private Integer maxChildren;

    @Column
    private Integer maxParent;

    @Column
    private Boolean isDisabled;

    @Column
    private String OTP;

    @Column(length = Integer.MAX_VALUE)
    private String pushToken;

    @Column(length = 100)
    private String email;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude // không sử dụng trường này trong equals và hashcode
    @ToString.Exclude // Không sử dụng trong toString()
    @JsonBackReference
    private Collection<Parent_Child> parent_childCollection;

    @OneToMany(mappedBy = "collaborator", cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude // không sử dụng trường này trong equals và hashcode
    @ToString.Exclude // Không sử dụng trong toString()
    @JsonBackReference
    private Collection<Parent_Child> parent_collaboratorCollection;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude // không sử dụng trường này trong equals và hashcode
    @ToString.Exclude // Không sử dụng trong toString()
    @JsonBackReference
    private Collection<ParentPayment> parent_Parent_paymentCollection;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude // không sử dụng trường này trong equals và hashcode
    @ToString.Exclude // Không sử dụng trong toString()
    @JsonBackReference
    private Collection<Quest> parent_questCollection;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude // không sử dụng trường này trong equals và hashcode
    @ToString.Exclude // Không sử dụng trong toString()
    @JsonBackReference
    private Collection<Task> parent_taskCollection;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "phone_number")
    private Account account;
}
