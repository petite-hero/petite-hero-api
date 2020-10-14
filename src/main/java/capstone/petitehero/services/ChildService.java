package capstone.petitehero.services;

import capstone.petitehero.dtos.common.ChildInformation;
import capstone.petitehero.dtos.common.ParentInformation;
import capstone.petitehero.dtos.response.child.AddChildResponseDTO;
import capstone.petitehero.dtos.response.child.VerifyParentResponseDTO;
import capstone.petitehero.entities.Child;
import capstone.petitehero.entities.Parent;
import capstone.petitehero.entities.Parent_Child;
import capstone.petitehero.repositories.ChildRepository;
import capstone.petitehero.repositories.ParentChildRepository;
import capstone.petitehero.repositories.ParentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;

@Service
public class ChildService {

    @Autowired
    private ChildRepository childRepository;

    @Autowired
    private ParentChildRepository parentChildRepository;

    @Autowired
    private ParentRepository parentRepository;

    public AddChildResponseDTO addChildForParent(Child child) {
        Child childResult = childRepository.save(child);
        AddChildResponseDTO result = new AddChildResponseDTO();
        if (childResult != null) {
            result.setChildId(childResult.getChildId());
            result.setFirstName(childResult.getFirstName());
            result.setLastName(childResult.getLastName());
            result.setNickName(childResult.getNickName());
            result.setPhoto(childResult.getPhoto());
//            if (childResult.getYob().toString() != null && !childResult.getYob().toString().isEmpty()) {
//                int year = Calendar.getInstance().get(Calendar.YEAR);
//                result.setAge(year - childResult.getYob());
//            }
            if (childResult.getLanguage() != null) {
                if (childResult.getLanguage().booleanValue()) {
                    result.setLanguage("Vietnamese");
                } else {
                    result.setLanguage("English");
                }
            }
            if (childResult.getGender() != null) {
                if (childResult.getGender().booleanValue()) {
                    result.setGender("Male");
                } else {
                    result.setGender("Female");
                }
            }
            result.setToken(childResult.getCreatedDate().getTime());

            return result;
        }
        return null;
    }

    public VerifyParentResponseDTO verifyParentByScanQRCode(Long childId, String parentPhoneNumber) {
        VerifyParentResponseDTO result = new VerifyParentResponseDTO();

        // find child information for adding to db
        Child childResult = childRepository.findChildByChildIdEqualsAndIsDisable(childId, Boolean.FALSE);
        if (childResult != null) {

            // set information for childResult
            ChildInformation childInformation = new ChildInformation();
            childInformation.setChildId(childResult.getChildId());
            childInformation.setFirstName(childResult.getFirstName());
            childInformation.setLastName(childResult.getLastName());
            childInformation.setNickName(childResult.getNickName());
            if (childResult.getGender() != null) {
                if (childResult.getGender().booleanValue()) {
                    childInformation.setGender("Male");
                } else {
                    childInformation.setGender("Female");
                }
            }
            childInformation.setPhoto(childResult.getPhoto());
//            if (childResult.getYob().toString() != null && !childResult.getYob().toString().isEmpty()) {
//                int year = Calendar.getInstance().get(Calendar.YEAR);
//                childInformation.setAge(year - childResult.getYob());
//            }
            result.setChildInformation(childInformation);
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

        if (parentResult != null && childResult != null) {
            Parent_Child parent_child = new Parent_Child();
            parent_child.setChild(childResult);
            parent_child.setParent(parentResult);

            parentChildRepository.save(parent_child);
            return result;
        }
        return null;
    }

    public Child findChildByChildId(Long childId, Boolean isDisable) {
        return childRepository.findChildByChildIdEqualsAndIsDisable(childId, isDisable);
    }
}
