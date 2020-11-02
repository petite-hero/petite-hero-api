package capstone.petitehero.services;

import capstone.petitehero.dtos.common.ChildInformation;
import capstone.petitehero.dtos.response.collaborator.AddCollaboratorResponseDTO;
import capstone.petitehero.entities.Parent;
import capstone.petitehero.entities.Parent_Child;
import capstone.petitehero.repositories.ChildRepository;
import capstone.petitehero.repositories.ParentChildRepository;
import capstone.petitehero.repositories.ParentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Service
public class ParentChildService {

    @Autowired
    private ParentChildRepository parentChildRepository;

    @Autowired
    private ParentRepository parentRepository;

    @Autowired
    private ChildRepository childRepository;

    public List<ChildInformation> getListChildOfParent(String parentPhoneNumber) {
        List<Parent_Child> listResult = parentChildRepository.findParent_ChildrenByParent_Account_UsernameAndChild_IsDisabled(parentPhoneNumber, Boolean.FALSE);

        if (listResult != null) {
            List<ChildInformation> result = new ArrayList<>();
            if (!listResult.isEmpty()) {
                for (Parent_Child data : listResult) {
                    ChildInformation childInformation = new ChildInformation();

                    childInformation.setChildId(data.getChild().getChildId());
                    childInformation.setFirstName(data.getChild().getFirstName());
                    childInformation.setLastName(data.getChild().getLastName());
                    childInformation.setNickName(data.getChild().getNickName());
                    if (data.getChild().getGender().booleanValue()) {
                        childInformation.setGender("Male");
                    } else {
                        childInformation.setGender("Female");
                    }
                    if (data.getChild().getPushToken() == null || data.getChild().getPushToken().isEmpty()) {
                        childInformation.setHasDevice(Boolean.FALSE);
                    } else {
                        childInformation.setHasDevice(Boolean.TRUE);
                    }
                    Calendar calendar = Calendar.getInstance();
                    int year = calendar.get(Calendar.YEAR);
                    childInformation.setAge(year - data.getChild().getYob());
                    if (data.getChild().getTrackingActive() != null) {
                        childInformation.setIsTrackingActive(data.getChild().getTrackingActive());
                    }
                    result.add(childInformation);
                }

                return result;
            }
        }
        if (listResult.isEmpty()) {
            return new ArrayList<>();
        }
        return null;
    }

    public Parent_Child findChildParentByChildId(Long childId) {
        return parentChildRepository.findParent_ChildByChild_ChildIdAndChild_IsDisabled(childId, Boolean.FALSE);
    }

    public AddCollaboratorResponseDTO addNewCollaborator(List<Long> listChildId, Parent parent, Parent collaboratorAccount) {
        AddCollaboratorResponseDTO result = new AddCollaboratorResponseDTO();
        result.setListChildren(new ArrayList<>());
        result.setParentPhoneNumber(parent.getAccount().getUsername());
        Parent_Child parent_child;
        for (Long childId : listChildId) {
            parent_child =
                    parentChildRepository.findDistinctFirstByChild_ChildIdAndParent_IdAndCollaboratorIsNull(
                            childId, parent.getId());
            if (parent_child != null) {
                parent_child.setIsCollaboratorConfirm(Boolean.FALSE);
                parent_child.setCollaborator(collaboratorAccount);
            } else {
                parent_child = new Parent_Child();

                parent_child.setParent(parent);
                parent_child.setIsCollaboratorConfirm(Boolean.FALSE);
                parent_child.setCollaborator(collaboratorAccount);
                parent_child.setChild(childRepository.findChildByChildIdEqualsAndIsDisabled(childId, Boolean.FALSE));
            }
            Parent_Child parentChildResult = parentChildRepository.save(parent_child);
            if (parentChildResult != null) {
                ChildInformation childInformation = new ChildInformation();

                childInformation.setChildId(parentChildResult.getChild().getChildId());
                childInformation.setFirstName(parentChildResult.getChild().getFirstName());
                childInformation.setLastName(parentChildResult.getChild().getLastName());
                if (parentChildResult.getChild().getGender() != null) {
                    if (parentChildResult.getChild().getGender().booleanValue()) {
                        childInformation.setGender("Male");
                    } else {
                        childInformation.setGender("Female");
                    }
                }
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                childInformation.setAge(year - parentChildResult.getChild().getYob());

                result.getListChildren().add(childInformation);
            }
        }
        if (!result.getListChildren().isEmpty()) {
            result.setStatus("ADDED");
        }
        return result;
    }

    public AddCollaboratorResponseDTO confirmByCollaborator(Parent collaboratorAccount, List<Long> listChildId) {
        AddCollaboratorResponseDTO result = new AddCollaboratorResponseDTO();
        result.setListChildren(new ArrayList<>());
        result.setParentPhoneNumber(collaboratorAccount.getAccount().getUsername());

        for (Long childId : listChildId) {
            Parent_Child parentChildResult =
                    parentChildRepository.findParent_ChildByChild_ChildIdAndCollaborator_Account_UsernameAndIsCollaboratorConfirm(
                            childId, collaboratorAccount.getAccount().getUsername(), Boolean.FALSE
                    );
            if (parentChildResult != null) {
                parentChildResult.setIsCollaboratorConfirm(Boolean.TRUE);
                Parent_Child parentChildUpdated = parentChildRepository.save(parentChildResult);

                if (parentChildUpdated != null) {
                    ChildInformation childInformation = new ChildInformation();

                    childInformation.setChildId(parentChildResult.getChild().getChildId());
                    childInformation.setFirstName(parentChildResult.getChild().getFirstName());
                    childInformation.setLastName(parentChildResult.getChild().getLastName());
                    if (parentChildResult.getChild().getGender() != null) {
                        if (parentChildResult.getChild().getGender().booleanValue()) {
                            childInformation.setGender("Male");
                        } else {
                            childInformation.setGender("Female");
                        }
                    }
                    Calendar calendar = Calendar.getInstance();
                    int year = calendar.get(Calendar.YEAR);
                    childInformation.setAge(year - parentChildResult.getChild().getYob());

                    result.getListChildren().add(childInformation);
                }
            }
        }
        if (!result.getListChildren().isEmpty()) {
            result.setStatus("CONFIRMED");
        }
        return result;
    }

    public AddCollaboratorResponseDTO deleteCollaboratorByParent(List<Long> listChildId, Parent parent, String collaboratorPhoneNumber) {
        AddCollaboratorResponseDTO result = new AddCollaboratorResponseDTO();
        result.setListChildren(new ArrayList<>());
        result.setParentPhoneNumber(parent.getAccount().getUsername());

        for (Long childId : listChildId) {
            Parent_Child parentChild =
                    parentChildRepository.findParent_ChildByChild_ChildIdAndCollaborator_Account_Username(
                            childId, collaboratorPhoneNumber);

            if (parentChild != null) {
                parentChild.setIsCollaboratorConfirm(null);
                parentChild.setCollaborator(null);

                Parent_Child parentChildResult = parentChildRepository.save(parentChild);
                if (parentChildResult != null) {
                    ChildInformation child = new ChildInformation();
                    result.getListChildren().add(child);
                }
            }
        }
        if (!result.getListChildren().isEmpty()) {
            result.setStatus("DELETED");
        }

        return result;
    }
}
