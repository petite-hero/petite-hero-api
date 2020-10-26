package capstone.petitehero.services;

import capstone.petitehero.dtos.common.ChildInformation;
import capstone.petitehero.dtos.common.ParentInformation;
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

import java.util.Calendar;

@Service
public class ChildService {

    @Autowired
    private ChildRepository childRepository;

    @Autowired
    private ParentChildRepository parentChildRepository;

    @Autowired
    private ParentRepository parentRepository;

    public AddChildResponseDTO addChildForParent(Child child, Parent parent, MultipartFile childPhoto) {
        Child childResult = childRepository.save(child);
        AddChildResponseDTO result = new AddChildResponseDTO();
        if (childResult != null) {
            result.setChildId(childResult.getChildId());
            result.setFirstName(childResult.getFirstName());
            result.setLastName(childResult.getLastName());
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

    public Child saveChildToSystem(Child child) {
        return childRepository.save(child);
    }

    public VerifyParentResponseDTO verifyParentByScanQRCode(Child child, String parentPhoneNumber) {
        VerifyParentResponseDTO result = new VerifyParentResponseDTO();

        // find child information for adding to db
        if (child != null) {

            // set information for childResult
            ChildInformation childInformation = new ChildInformation();
            childInformation.setChildId(child.getChildId());
            childInformation.setFirstName(child.getFirstName());
            childInformation.setLastName(child.getLastName());
            childInformation.setNickName(child.getNickName());
            if (child.getGender() != null) {
                if (child.getGender().booleanValue()) {
                    childInformation.setGender("Male");
                } else {
                    childInformation.setGender("Female");
                }
            }

            if (child.getPhoto() != null && !child.getPhoto().isEmpty()) {
                childInformation.setPhoto(Util.fromImageFileToBase64String(child.getPhoto()));
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
        Parent parentResult = parentRepository.findParentByAccount_Username(parentPhoneNumber);
        if (parentResult != null) {
            ParentInformation parentInformation = new ParentInformation();

            // set parent information
            parentInformation.setFirstName(parentResult.getFirstName());
            parentInformation.setLastName(parentResult.getLastName());
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
//            Parent_Child parent_child = new Parent_Child();
//            parent_child.setChild(child);
//            parent_child.setParent(parentResult);
//
//            parentChildRepository.save(parent_child);
            return result;
        }
        return null;
    }

    public AddChildResponseDTO regenerateQRCodeForChildVerify(Long childId) {
        Child childResult = childRepository.findChildByChildIdEqualsAndIsDisabled(childId, Boolean.FALSE);
        if (childResult != null) {
            AddChildResponseDTO result = new AddChildResponseDTO();

            result.setChildId(childResult.getChildId());
//            result.setToken(childResult.getCreatedDate().longValue());
            return result;
        }
        return null;
    }

    public DeleteChildResponseDTO disableChildById(Long childId) {
        Child child = childRepository.findChildByChildIdEqualsAndIsDisabled(childId, Boolean.FALSE);
        if (child != null) {
            child.setIsDisabled(Boolean.TRUE);
            Child childResult = childRepository.save(child);
            if (childResult != null) {
                DeleteChildResponseDTO result = new DeleteChildResponseDTO();
                result.setStatus("DELETED");
                return result;
            }
        }
        return null;
    }

    public UpdateChildProfileResponseDTO updateChildProfile(Child child) {
        Child childResult = childRepository.save(child);

        if (childResult != null) {
            UpdateChildProfileResponseDTO result = new UpdateChildProfileResponseDTO();

            result.setChildId(childResult.getChildId());
            result.setFirstName(child.getFirstName());
            result.setLastName(child.getLastName());
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
            result.setStatus("Updated");

            return result;
        }
        return null;
    }

    public ChildDetailResponseDTO getDetailOfChild(Long childId) {
        Child childResult = childRepository.findChildByChildIdEqualsAndIsDisabled(childId, Boolean.FALSE);

        if (childResult != null) {
            ChildDetailResponseDTO result = new ChildDetailResponseDTO();

            result.setFirstName(childResult.getFirstName());
            result.setLastName(childResult.getLastName());
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
                    result.setLanguage("Englisg");
                }
            }

            //TODO get list collaborator of child if have
//            result.setCollaborator(null);

            return result;
        }
        return null;
    }
}
