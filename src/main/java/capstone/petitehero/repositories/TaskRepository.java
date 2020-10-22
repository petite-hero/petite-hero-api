package capstone.petitehero.repositories;

import capstone.petitehero.entities.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    Task findTaskByTaskId(Long taskId);

    Task findTasksByTaskIdAndIsDeleted(Long taskId, Boolean isDeleted);

    List<Task> findTasksByChildChildIdAndIsDeleted(Long childId, Boolean isDeleted);

    List<Task> findTasksByChildChildIdAndIsDeletedAndAssignDateIsBetween(Long childId, Boolean isDeleted, Long startDate, Long endDate);
}
