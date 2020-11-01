package capstone.petitehero.repositories;

import capstone.petitehero.entities.Parent;
import capstone.petitehero.entities.Parent_Child;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParentChildRepository extends JpaRepository<Parent_Child, Long> {

    List<Parent_Child> findParent_ChildrenByParent_Account_UsernameAndChild_IsDisabled(String phoneNumber, Boolean isDisabled);

    Parent_Child findParent_ChildByChild_ChildIdAndChild_IsDisabled(Long childId, Boolean isDisabled);

    Parent_Child findParent_ChildByChild_ChildIdAndParent_IdAndCollaboratorIsNull(Long childId, Long parentId);


}
