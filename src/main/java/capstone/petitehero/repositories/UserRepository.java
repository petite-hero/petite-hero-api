package capstone.petitehero.repositories;

import capstone.petitehero.entities.User;
import org.hibernate.metamodel.model.convert.spi.JpaAttributeConverter;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaAttributeConverter<User, Long> {

}
