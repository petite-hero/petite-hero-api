package capstone.petitehero.services;

import capstone.petitehero.dtos.response.parent.ParentRegisterResponseDTO;
import capstone.petitehero.entities.Parent;
import capstone.petitehero.repositories.ParentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ParentService {

    @Autowired
    private ParentRepository parentRepository;

    public ParentRegisterResponseDTO saveParentToSystem(Parent parentHaveOnlyPhoneNumber) {
        Parent parentResult = parentRepository.save(parentHaveOnlyPhoneNumber);
        if (parentResult != null) {
            ParentRegisterResponseDTO result = new ParentRegisterResponseDTO();

            result.setPhoneNumber(parentResult.getParentPhoneNumber());
            result.setFirstName(parentResult.getFirstName());
            result.setLastName(parentResult.getLastName());
            result.setPhoto(parentResult.getPhoto());
            if (parentResult.getLanguage() != null) {
                if (parentResult.getLanguage().booleanValue()) {
                    if (parentResult.getGender() != null) {
                        if (parentResult.getGender().booleanValue()) {
                            result.setGender("Nam");
                        } else {
                            result.setGender("Nữ");
                        }
                    }
                    result.setLanguage("Vietnamese");
                } else {
                    if (parentResult.getGender() != null) {
                        if (parentResult.getGender().booleanValue()) {
                            result.setGender("Male");
                        } else {
                            result.setGender("Female");
                        }
                    }
                    result.setLanguage("English");
                }
            }
            result.setMaxChildren(parentResult.getMaxChildren());
            result.setMaxParent(parentResult.getMaxParent());

            // TODO format datetime
            result.setExpiredDate(parentResult.getExpiredDate());
            if (parentResult.getIsFreeTrial().booleanValue()){
                result.setIsFreeTrial("Free Trial");
            } else {
                result.setIsFreeTrial("Petite Hero Account");
            }

            return result;
        }
        return null;
    }

    public Parent findParentByPhoneNumber(String phoneNumber) {
        return parentRepository.findParentByParentPhoneNumberEquals(phoneNumber);
    }
}
