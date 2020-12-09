package capstone.petitehero.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50)
    private String name;

    @Column(length = Integer.MAX_VALUE)
    private String photo;

    @Column
    private Boolean language;

    @Column
    private Boolean gender;

    @Column
    private Boolean isDisabled;

    @Column(length = Integer.MAX_VALUE)
    private String pushToken;

    @Column(length = 50)
    private String email;

    @Column
    private Integer authyId;

    @Column
    private Boolean isVerify;

    @Column
    private String deviceId;

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
    @JsonManagedReference
    private Account account;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "subscription_id")
    @JsonBackReference
    private Subscription subscription;
}
