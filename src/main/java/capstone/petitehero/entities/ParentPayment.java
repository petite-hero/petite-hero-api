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
public class ParentPayment implements Serializable {

    @Id
    @GeneratedValue
    private Long transactionId;

    @Column(length = Integer.MAX_VALUE)
    private String content;

    @Column
    private Double amount;

    @Column
    private Long date;

    @Column(length = 30)
    private String status;

    @Column(length = 200)
    private String paymentId;

    @Column(length = 200)
    private String payerId;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonManagedReference
    private Parent parent;
}