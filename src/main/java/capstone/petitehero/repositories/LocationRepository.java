package capstone.petitehero.repositories;

import capstone.petitehero.entities.LocationHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationRepository extends JpaRepository<LocationHistory, Long> {

}
