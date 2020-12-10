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

    Parent_Child findDistinctFirstByChild_ChildIdAndParent_ParentIdAndCollaboratorIsNull(Long childId, Long parentId);

    Parent_Child findParent_ChildByChild_ChildIdAndCollaborator_Account_UsernameAndIsCollaboratorConfirm(Long childId, String collaboratorPhoneNumber, Boolean isConfirm);

    Parent_Child findParent_ChildByChild_ChildIdAndCollaborator_Account_Username(Long childId, String collaboratorPhoneNumber);

    List<Parent_Child> findParent_ChildrenByParent_Account_UsernameAndCollaboratorNotNull(String phoneNumber);

    List<Parent_Child> findParent_ChildrenByCollaborator_Account_UsernameAndChild_IsDisabled(String phoneNumber, Boolean isDisabled);

    List<Parent_Child> findParent_ChildrenByCollaborator_Account_UsernameAndAndParent_Account_UsernameAndChild_IsDisabled(String collaboratorPhone, String parentPhoneNumber, Boolean isDisabled);

    List<Parent_Child> findParent_ChildrenByCollaborator_Account_UsernameAndParent_Account_Username(String collaboratorUsername, String parentUsername);
}
