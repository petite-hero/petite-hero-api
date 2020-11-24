package capstone.petitehero.services;

import capstone.petitehero.config.common.Constants;
import capstone.petitehero.dtos.common.ChildInformation;
import capstone.petitehero.dtos.response.collaborator.AddCollaboratorResponseDTO;
import capstone.petitehero.dtos.response.collaborator.ListCollaboratorResponseDTO;
import capstone.petitehero.entities.Parent;
import capstone.petitehero.entities.Parent_Child;
import capstone.petitehero.repositories.ChildRepository;
import capstone.petitehero.repositories.ParentChildRepository;
import capstone.petitehero.utilities.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ParentChildService {

    @Autowired
    private ParentChildRepository parentChildRepository;

    @Autowired
    private ChildRepository childRepository;

    @Autowired
    private NotificationService notiService;

    public List<ChildInformation> getListChildOfParent(String parentPhoneNumber) {
        // get parent children
        List<Parent_Child> listParentChildrenResult =
                parentChildRepository.findParent_ChildrenByParent_Account_UsernameAndChild_IsDisabled(parentPhoneNumber, Boolean.FALSE)
                        .stream()
                        .filter(Util.distinctByKey(Parent_Child::getChild))
                        .collect(Collectors.toList());

        // get collaborator children
        List<Parent_Child> listCollaboratorChildrenResult =
                parentChildRepository.findParent_ChildrenByCollaborator_Account_UsernameAndChild_IsDisabledAndIsCollaboratorConfirm(parentPhoneNumber, Boolean.FALSE, Boolean.TRUE);
        List<ChildInformation> result = new ArrayList<>();
        if (listParentChildrenResult != null) {
            if (!listParentChildrenResult.isEmpty()) {
                for (Parent_Child data : listParentChildrenResult) {
                    ChildInformation childInformation = new ChildInformation();

                    childInformation.setChildId(data.getChild().getChildId());
                    childInformation.setName(data.getChild().getName());
                    childInformation.setNickName(data.getChild().getNickName());
                    if (data.getChild().getGender().booleanValue()) {
                        childInformation.setGender("Male");
                    } else {
                        childInformation.setGender("Female");
                    }
                    if (data.getChild().getAndroidId() == null || data.getChild().getAndroidId().isEmpty()) {
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
                    if (data.getChild().getPhoto() != null && !data.getChild().getPhoto().isEmpty()) {
                        childInformation.setPhoto(Util.fromImageFileToBase64String(data.getChild().getPhoto()));
                    }
                    childInformation.setIsCollaboratorChild(Boolean.FALSE);
                    result.add(childInformation);
                }
            }
        }
        if (listCollaboratorChildrenResult != null) {
            if (!listCollaboratorChildrenResult.isEmpty()) {
                for (Parent_Child data : listCollaboratorChildrenResult) {
                    ChildInformation childInformation = new ChildInformation();

                    childInformation.setChildId(data.getChild().getChildId());
                    childInformation.setName(data.getChild().getName());
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
                    if (data.getChild().getPhoto() != null && !data.getChild().getPhoto().isEmpty()) {
                        childInformation.setPhoto(Util.fromImageFileToBase64String(data.getChild().getPhoto()));
                    }
                    childInformation.setIsCollaboratorChild(Boolean.TRUE);
                    result.add(childInformation);
                }
            }
        }
        return result;
    }

    public Parent_Child findChildParentByChildId(Long childId) {
        return parentChildRepository.findParent_ChildByChild_ChildIdAndChild_IsDisabled(childId, Boolean.FALSE);
    }

    public AddCollaboratorResponseDTO addNewCollaborator(List<Long> listChildId, Parent parent, Parent collaboratorAccount) {
        AddCollaboratorResponseDTO result = new AddCollaboratorResponseDTO();
        result.setParentPhoneNumber(parent.getAccount().getUsername());
        result.setListChildren(new ArrayList<>());
        Parent_Child parent_child;
        List<Long> childNeedToCollab = new ArrayList<>();

        // checking is this collaborator has collab with this child
        // prevent duplicated record in parent_child table in db
        for (Long childId : listChildId) {
            Parent_Child duplicatedParentChild =
                    parentChildRepository.findParent_ChildByChild_ChildIdAndCollaborator_Account_Username(
                            childId, collaboratorAccount.getAccount().getUsername());

            if (duplicatedParentChild == null) {
                childNeedToCollab.add(childId);
            }
        }

        if (!childNeedToCollab.isEmpty()) {
            for (Long childId : childNeedToCollab) {
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
                    childInformation.setName(parentChildResult.getChild().getName());
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
        if (result.getListChildren() != null) {
            if (!result.getListChildren().isEmpty()) {
                result.setStatus(Constants.status.ADDED.toString());
                if (collaboratorAccount.getPushToken() != null && !collaboratorAccount.getPushToken().isEmpty()) {
                    ArrayList<String> pushToken = new ArrayList<>();
                    pushToken.add(collaboratorAccount.getPushToken());
                    String msg;
                    if (collaboratorAccount.getLanguage().booleanValue()) {
                        msg = parent.getName() + " muốn bạn trở thành người cộng tác.";
                    } else {
                        msg = parent.getName() + " want you to become their collaborator.";
                    }
                    notiService.pushNotificationMobile(msg, result, pushToken);
                }
            }
        }
        return result;
    }

    public AddCollaboratorResponseDTO confirmByCollaborator(Parent collaboratorAccount, List<Long> listChildId, Boolean isConfirm) {
        AddCollaboratorResponseDTO result = new AddCollaboratorResponseDTO();
        result.setListChildren(new ArrayList<>());
        result.setParentPhoneNumber(collaboratorAccount.getAccount().getUsername());

        for (Long childId : listChildId) {
            Parent_Child parentChildResult =
                    parentChildRepository.findParent_ChildByChild_ChildIdAndCollaborator_Account_UsernameAndIsCollaboratorConfirm(
                            childId, collaboratorAccount.getAccount().getUsername(), Boolean.FALSE);
            if (parentChildResult != null) {
                parentChildResult.setIsCollaboratorConfirm(isConfirm);
                Parent_Child parentChildUpdated = parentChildRepository.save(parentChildResult);

                if (parentChildUpdated != null) {
                    ChildInformation childInformation = new ChildInformation();

                    childInformation.setChildId(parentChildResult.getChild().getChildId());
                    childInformation.setName(parentChildResult.getChild().getName());
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
            result.setStatus(Constants.status.CONFIRMED.toString());
            Parent_Child parentChild = collaboratorAccount.getParent_childCollection().stream().findFirst().orElse(null);

            if (parentChild != null) {
                if (parentChild.getParent().getPushToken() != null && !parentChild.getParent().getPushToken().isEmpty()) {
                    ArrayList<String> pushToken = new ArrayList<>();
                    pushToken.add(parentChild.getParent().getPushToken());
                    String msg;
                    if (parentChild.getParent().getLanguage().booleanValue()) {
                        msg = collaboratorAccount.getName() + " đã trở thành người cộng tác của bạn.";
                    } else {
                        msg = collaboratorAccount.getName() + " has become your collaborator.";
                    }
                    notiService.pushNotificationMobile(msg, result, pushToken);
                }
            }
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
            result.setStatus(Constants.status.DELETED.toString());
        }

        return result;
    }

    public List<ListCollaboratorResponseDTO> getParentCollaborator(String phoneNumber) {
        List<Parent_Child> parentChildListResult =
                parentChildRepository.findParent_ChildrenByParent_Account_UsernameAndCollaboratorNotNull(phoneNumber)
                        .stream()
                        .filter(Util.distinctByKey(Parent_Child::getCollaborator))
                        .collect(Collectors.toList());

        if (parentChildListResult != null) {
            List<ListCollaboratorResponseDTO> result = new ArrayList<>();
            if (!parentChildListResult.isEmpty()) {
                for (Parent_Child collaborator : parentChildListResult) {
                    ListCollaboratorResponseDTO collaboratorData = new ListCollaboratorResponseDTO();

                    collaboratorData.setPhoneNumber(collaborator.getCollaborator().getAccount().getUsername());
                    collaboratorData.setName(collaborator.getCollaborator().getName());
                    if (collaborator.getCollaborator().getGender().booleanValue()) {
                        collaboratorData.setGender("Male");
                    } else {
                        collaboratorData.setGender("Female");
                    }

                    result.add(collaboratorData);
                }
            }
            return result;
        }

        return null;
    }
}
