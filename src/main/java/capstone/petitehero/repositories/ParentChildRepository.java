package capstone.petitehero.repositories;

import capstone.petitehero.entities.Parent;
import capstone.petitehero.entities.Parent_Child;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface ParentChildRepository extends JpaRepository<Parent_Child, Long> {

    List<Parent_Child> findParent_ChildrenByParent_Account_UsernameAndChild_IsDisabled(String phoneNumber, Boolean isDisabled);

    Parent_Child findFirstByChild_ChildIdAndChild_IsDisabled(Long childId, Boolean isDisabled);

    Parent_Child findDistinctFirstByChild_ChildIdAndParent_ParentIdAndCollaboratorIsNull(Long childId, Long parentId);

    Parent_Child findParent_ChildByChild_ChildIdAndCollaborator_Account_UsernameAndIsCollaboratorConfirm(Long childId, String collaboratorPhoneNumber, Boolean isConfirm);

    Parent_Child findParent_ChildByChild_ChildIdAndCollaborator_Account_Username(Long childId, String collaboratorPhoneNumber);

    Parent_Child findParent_ChildByChild_ChildIdAndCollaborator_Account_UsernameAndParent_Account_Username(Long childId, String collaboratorPhoneNumber, String parentPhoneNumber);

    List<Parent_Child> findParent_ChildrenByParent_Account_UsernameAndCollaboratorNotNull(String phoneNumber);

    List<Parent_Child> findParent_ChildrenByCollaborator_Account_UsernameAndChild_IsDisabled(String phoneNumber, Boolean isDisabled);

    List<Parent_Child> findParent_ChildrenByCollaborator_Account_UsernameAndAndParent_Account_UsernameAndChild_IsDisabled(String collaboratorPhone, String parentPhoneNumber, Boolean isDisabled);

    List<Parent_Child> findParent_ChildrenByCollaborator_Account_UsernameAndParent_Account_Username(String collaboratorUsername, String parentUsername);

    List<Parent_Child> findParent_ChildrenByChild_ChildIdAndChild_IsDisabled(Long childId, Boolean isDisabled);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM Parent_Child pc WHERE " +
            "pc.parent.parentId = :parent AND " +
            "pc.collaborator.parentId = :collaborator AND " +
            "pc.child.childId = :child")
    void deleteParent_ChildByParentPhoneNumberAndCollaboratorPhoneNumberAndChildId(@Param("parent") Long parentPhoneNumber,
                                                                                   @Param("collaborator") Long collaboratorPhoneNumber,
                                                                                   @Param("child") Long childId);
}
