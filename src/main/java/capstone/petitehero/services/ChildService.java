package capstone.petitehero.services;

import capstone.petitehero.config.common.Constants;
import capstone.petitehero.dtos.ResponseObject;
import capstone.petitehero.dtos.common.ChildInformation;
import capstone.petitehero.dtos.common.ParentInformation;
import capstone.petitehero.dtos.request.location.PushNotiSWDTO;
import capstone.petitehero.dtos.response.child.*;
import capstone.petitehero.entities.Child;
import capstone.petitehero.entities.Parent;
import capstone.petitehero.entities.Parent_Child;
import capstone.petitehero.repositories.ChildRepository;
import capstone.petitehero.repositories.ParentChildRepository;
import capstone.petitehero.repositories.ParentRepository;
import capstone.petitehero.utilities.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChildService {

    @Autowired
    private ChildRepository childRepository;

    @Autowired
    private ParentChildRepository parentChildRepository;

    @Autowired
    private ParentRepository parentRepository;

    @Autowired
    private NotificationService notiService;

    public AddChildResponseDTO addChildForParent(Child child, Parent parent, MultipartFile childPhoto) {
        Child childResult = childRepository.save(child);
        AddChildResponseDTO result = new AddChildResponseDTO();
        if (childResult != null) {
            result.setChildId(childResult.getChildId());
            result.setName(childResult.getName());
            result.setNickName(childResult.getNickName());

            if (childResult.getYob().toString() != null && !childResult.getYob().toString().isEmpty()) {
                int year = Calendar.getInstance().get(Calendar.YEAR);
                result.setAge(year - childResult.getYob());
            }
            if (childResult.getGender() != null) {
                if (childResult.getGender().booleanValue()) {
                    result.setGender("Male");
                } else {
                    result.setGender("Female");
                }
            }

            // save image for child
            if (childPhoto != null && !childPhoto.isEmpty()) {

                childResult.setPhoto(Util.saveImageToSystem(childResult.getChildId().toString(), "Avatar Added", childPhoto));

            }

            Parent_Child parent_child = new Parent_Child();
            parent_child.setParent(parent);
            parent_child.setChild(childResult);

            parentChildRepository.save(parent_child);
            return result;
        }
        return null;
    }


    public Child findChildByChildId(Long childId, Boolean isDisable) {
        return childRepository.findChildByChildIdEqualsAndIsDisabled(childId, isDisable);
    }

    public VerifyParentResponseDTO verifyParentByScanQRCode(Child child, String parentPhoneNumber) {
        VerifyParentResponseDTO result = new VerifyParentResponseDTO();

        // find child information for adding to db
        if (child != null) {

            // set information for childResult
            ChildInformation childInformation = new ChildInformation();
            childInformation.setChildId(child.getChildId());
            childInformation.setName(child.getName());
            childInformation.setNickName(child.getNickName());
            if (child.getGender() != null) {
                if (child.getGender().booleanValue()) {
                    childInformation.setGender("Male");
                } else {
                    childInformation.setGender("Female");
                }
            }
            if (child.getYob().toString() != null && !child.getYob().toString().isEmpty()) {
                int year = Calendar.getInstance().get(Calendar.YEAR);
                childInformation.setAge(year - child.getYob());
            }
            result.setChildInformation(childInformation);

            // save new child token into db
            childRepository.save(child);
        }

        // find parent information for adding to db
//        Parent parentResult = parentRepository.findParentByAccount_Username(parentPhoneNumber);
        Parent parentResult = parentRepository.findParentByAccount_UsernameAndIsDisabled(parentPhoneNumber, Boolean.FALSE);
        if (parentResult != null) {
            ParentInformation parentInformation = new ParentInformation();

            // set parent information
            parentInformation.setName(parentResult.getName());
            parentInformation.setPhoneNumber(parentResult.getAccount().getUsername());
            if (parentResult.getGender() != null) {
                if (parentResult.getGender().booleanValue()) {
                    parentInformation.setGender("Male");
                } else {
                    parentInformation.setGender("Female");
                }
            }
            result.setParentInformation(parentInformation);
        }

        if (parentResult != null && child != null) {
            return result;
        }
        return null;
    }

    public DeleteChildResponseDTO disableChildById(Child child) {
        child.setIsDisabled(Boolean.TRUE);
        child.setAndroidId(null);
        child.setDeviceName(null);

        List<Parent_Child> parentChildList = new ArrayList<>(child.getChild_parentCollection());
        if (parentChildList != null && !parentChildList.isEmpty()) {
            Parent parent = parentChildList.stream().findAny().orElse(null).getParent();

            List<Parent_Child> collaboratorList = parentChildList
                    .stream()
                    .filter(parent_child -> parent_child.getCollaborator() != null)
                    .filter(Util.distinctByKey(Parent_Child::getCollaborator))
                    .collect(Collectors.toList());
            if (collaboratorList != null && !collaboratorList.isEmpty()) {
                for (Parent_Child parentChild : collaboratorList) {
                    List<Parent_Child> result = parentChildRepository.findParent_ChildrenByCollaborator_Account_UsernameAndParent_Account_Username(
                            parentChild.getCollaborator().getAccount().getUsername(),
                            parent.getAccount().getUsername());
                    if (result != null) {
                        if (result.size() == 1) {
                            parentChild.setCollaborator(null);
                            parentChild.setIsCollaboratorConfirm(null);
                        }
                    }
                }
            }
        }

        Child childResult = childRepository.save(child);
        if (childResult != null) {
            PushNotiSWDTO noti = new PushNotiSWDTO(Constants.SILENT_NOTI, Constants.LOGOUT, new Object());

            if (child.getPushToken() != null && !child.getPushToken().isEmpty()) {
                notiService.pushNotificationSW(noti, child.getPushToken());
            }
            DeleteChildResponseDTO result = new DeleteChildResponseDTO();
            result.setStatus(Constants.status.DELETED.toString());
            return result;
        }

        return null;
    }

    public DeleteChildResponseDTO disableChildIdByCollaborator(Child child, Parent collaborator) {
        Parent_Child parentChild =
                parentChildRepository.findParent_ChildByChild_ChildIdAndCollaborator_Account_Username(child.getChildId(), collaborator.getAccount().getUsername());

        if (parentChild != null) {
            parentChild.setCollaborator(null);
            parentChild.setIsCollaboratorConfirm(null);

            if (parentChildRepository.save(parentChild) != null) {
                DeleteChildResponseDTO result = new DeleteChildResponseDTO();
                result.setStatus(Constants.status.DELETED.toString());

                if (parentChild.getParent().getPushToken() != null && !parentChild.getParent().getPushToken().isEmpty()) {
                    ArrayList<String> pushToken = new ArrayList<>();
                    pushToken.add(parentChild.getParent().getPushToken());
                    String msg;
                    if (parentChild.getParent().getLanguage().booleanValue()) {
                        msg = collaborator.getName() + " không còn là cộng tác cho " + child.getName() + " của bạn nữa.";
                    } else {
                        msg = collaborator.getName() + " isn't your collaborator with child " + child.getName() + " anymore.";
                    }

                    notiService.pushNotificationMobile(msg, result, pushToken);
                }

                return result;
            }
            return null;
        }

        return null;
    }

    public UpdateChildProfileResponseDTO updateChildProfile(Child child) {
        Child childResult = childRepository.save(child);

        if (childResult != null) {
            UpdateChildProfileResponseDTO result = new UpdateChildProfileResponseDTO();

            result.setChildId(childResult.getChildId());
            result.setName(child.getName());
            if (childResult.getLanguage().booleanValue()) {
                result.setLanguage("Vietnamese");
            } else {
                result.setLanguage("English");
            }
            if (childResult.getGender().booleanValue()) {
                result.setGender("Male");
            } else {
                result.setGender("Female");
            }
            if (child.getPhoto() != null && !child.getPhoto().isEmpty()) {
                result.setPhoto(Util.fromImageFileToBase64String(child.getPhoto()));
            }
            result.setStatus(Constants.status.UPDATED.toString());

            return result;
        }
        return null;
    }

    public ChildDetailResponseDTO getDetailOfChild(Long childId) {
        Child childResult = childRepository.findChildByChildIdEqualsAndIsDisabled(childId, Boolean.FALSE);

        if (childResult != null) {
            ChildDetailResponseDTO result = new ChildDetailResponseDTO();

            result.setName(childResult.getName());
            result.setNickName(childResult.getNickName());
            Calendar calendar = Calendar.getInstance();
            int currentYear = calendar.get(Calendar.YEAR);
            result.setAge(currentYear - childResult.getYob());

            if (childResult.getPhoto() != null && !childResult.getPhoto().isEmpty()) {
                result.setPhoto(Util.fromImageFileToBase64String(childResult.getPhoto()));
            }
            if (childResult.getGender() != null) {
                if (childResult.getGender().booleanValue()) {
                    result.setGender("Male");
                } else {
                    result.setGender("Female");
                }
            }
            if (childResult.getLanguage() != null) {
                if (childResult.getLanguage().booleanValue()) {
                    result.setLanguage("Vietnamese");
                } else {
                    result.setLanguage("English");
                }
            }
            if (childResult.getAndroidId() != null && !childResult.getAndroidId().isEmpty()) {
                result.setAndroidId(childResult.getDeviceName());
            }

            return result;
        }
        return null;
    }

    public ResponseObject getSWTrackingStatus(Long childId) {
        ResponseObject result = Util.createResponse();
        try {
            Child child = childRepository.getOne(childId);
            if (child == null) {
                result.setData(null);
                result.setMsg("Bad request - Child doesn't exist");
                result.setCode(Constants.CODE_400);
            } else {
                result.setData(child.getTrackingActive());
                result.setMsg(Constants.NO_ERROR);
            }
        } catch (Exception e) {
            result.setData(null);
            result.setMsg(Constants.SERVER_ERROR + e.toString());
            result.setCode(Constants.CODE_500);
        }
        return result;
    }

    public DeleteChildResponseDTO resetChildDevice(Child child) {
        child.setAndroidId(null);
        child.setDeviceName(null);

        Child childResult = childRepository.save(child);
        if (childResult != null) {
            PushNotiSWDTO noti = new PushNotiSWDTO(Constants.SILENT_NOTI, Constants.LOGOUT, new Object());

            if (child.getPushToken() != null && !child.getPushToken().isEmpty()) {
                notiService.pushNotificationSW(noti, child.getPushToken());
            }
            DeleteChildResponseDTO result = new DeleteChildResponseDTO();
            result.setStatus(Constants.DELETED);

            return result;
        }

        return null;
    }
}
