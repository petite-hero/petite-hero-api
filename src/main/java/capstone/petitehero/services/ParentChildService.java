package capstone.petitehero.services;

import capstone.petitehero.dtos.common.ChildInformation;
import capstone.petitehero.entities.Child;
import capstone.petitehero.entities.Parent_Child;
import capstone.petitehero.repositories.ParentChildRepository;
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


}
