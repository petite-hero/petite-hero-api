package capstone.petitehero.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Getter
@Setter
public class Account implements Serializable {

    @Id
    @Column(length = 30)
    private String username;

    @Column(length = 100)
    private String password;

    @Column(length = 20)
    private String role;

    @OneToOne(fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            mappedBy = "account")
    @JsonBackReference
    private Parent parent;
}
