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
public class Payment implements Serializable {

    @Id
    @GeneratedValue
    private Long transactionId;

    @Column(length = Integer.MAX_VALUE)
    private String content;

    @Column
    private Double money;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    @Column(length = 30)
    private String status;

    @ManyToOne
    @JoinColumn(name = "parent_phone_number")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonManagedReference
    private Parent parent;
}
