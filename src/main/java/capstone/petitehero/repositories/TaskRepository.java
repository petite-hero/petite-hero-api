package capstone.petitehero.repositories;

import capstone.petitehero.entities.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    Task findTasksByTaskIdAndIsDeleted(Long taskId, Boolean isDeleted);

    List<Task> findTasksByChildChildIdAndIsDeleted(Long childId, Boolean isDeleted);

    List<Task> findTasksByChildChildIdAndIsDeletedAndAssignDateIsBetween(Long childId, Boolean isDeleted, Long startDate, Long endDate);

    List<Task> findTasksByChildChildIdAndAssignDateIsBetweenAndStatusAndIsDeleted(Long childId, Long startDayOfMonth, Long endDayOfMonth, String status, Boolean isDeleted);

    List<Task> findTasksByIsDeletedAndAssignDateIsBetween(Boolean isDeleted, Long startDay, Long endDay);

    List<Task> findTasksByIsDeletedAndAssignDateIsBetweenAndStatus(Boolean isDeleted, Long startDay, Long endDay, String status);

    @Query(value = "SELECT t.* FROM task t\n" +
            "WHERE t.assignee_id = :childId AND \n" +
            "t.assign_date >= :startDate AND t.assign_date <= :endDate AND\n" +
            "t.is_deleted = :isDeleted AND ((t.from_time <= :fromTime AND t.to_time >= :fromTime)\n" +
            "                       OR (t.from_time <= :toTime AND t.to_time >= :toTime))", nativeQuery = true)
    Task existsTaskInAssignDate(@Param("childId") Long childId,
                                @Param("isDeleted") Boolean isDeleted,
                                @Param("startDate") Long startDate,
                                @Param("endDate") Long endDate,
                                @Param("fromTime") String fromTime,
                                @Param("toTime") String toTime);
}
