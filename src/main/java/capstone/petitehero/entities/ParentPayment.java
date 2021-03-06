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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long parentPaymentId;

    @Column
    private String content;

    @Column
    private Double amount;

    @Column
    private Long createDate;

    @Column
    private Long payDate;

    @Column(length = 20)
    private String status;

    @Column(length = 200)
    private String paymentId;

    @Column(length = 500)
    private String link;

    @Column(length = 200)
    private String payerId;

    @ManyToOne
    @JoinColumn(name = "subscription_id")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonManagedReference
    private Subscription subscription;
}
