package capstone.petitehero.entities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;

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

    @Column(columnDefinition = "TINYINT(1)")
    private Boolean languageSetting;

    @Column(columnDefinition = "TINYINT(1)")
    private Boolean gender;

    @Column(length = 30)
    private String subscriptionType;

    @Column(columnDefinition = "TINYINT(1)")
    private Boolean isExpiredDate;

    @Column
    private Integer maxChildrenAllow;

    @Column
    private Integer maxParentAllow;

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
    private Collection<Transaction> parent_transactionCollection;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude // không sử dụng trường này trong equals và hashcode
    @ToString.Exclude // Không sử dụng trong toString()
    private Collection<Quest> parent_questCollection;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude // không sử dụng trường này trong equals và hashcode
    @ToString.Exclude // Không sử dụng trong toString()
    private Collection<Task> parent_taskCollection;
}
