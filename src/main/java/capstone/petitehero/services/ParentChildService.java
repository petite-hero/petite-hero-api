package capstone.petitehero.services;

import capstone.petitehero.dtos.common.ChildInformation;
import capstone.petitehero.entities.Child;
import capstone.petitehero.entities.Parent;
import capstone.petitehero.entities.Parent_Child;
import capstone.petitehero.repositories.ChildRepository;
import capstone.petitehero.repositories.ParentChildRepository;
import capstone.petitehero.repositories.ParentRepository;
import capstone.petitehero.utilities.Util;
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
                    childInformation.setPhoto(Util.fromImageFileToBase64String(data.getChild().getPhoto()));
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

    public Parent_Child addNewCollaborator(List<Long> listChildId, Parent parent, Parent collaboratorAccount) {
        Parent_Child parent_child;
        for (Long childId: listChildId) {
            parent_child =
                    parentChildRepository.findParent_ChildByChild_ChildIdAndParent_IdAndCollaboratorIsNull(childId, parent.getId());
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
        }
        return null;
    }
}
