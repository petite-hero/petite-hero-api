package capstone.petitehero.services;

import capstone.petitehero.config.common.Constants;
import capstone.petitehero.dtos.ResponseObject;
import capstone.petitehero.dtos.request.parent.UpdatePushTokenRequestDTO;
import capstone.petitehero.dtos.response.parent.ParentProfileRegisterResponseDTO;
import capstone.petitehero.dtos.response.parent.ParentRegisterResponseDTO;
import capstone.petitehero.entities.Parent;
import capstone.petitehero.repositories.ParentRepository;
import capstone.petitehero.utilities.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;

@Service
public class ParentService {

    @Autowired
    private ParentRepository parentRepository;

    public ParentRegisterResponseDTO registerByParent(Parent parentHaveOnlyPhoneNumber) {
        Parent parentResult = parentRepository.save(parentHaveOnlyPhoneNumber);
        if (parentResult != null) {
             ParentRegisterResponseDTO result = new ParentRegisterResponseDTO();
             result.setPhoneNumber(parentResult.getAccount().getUsername());

             SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
             result.setExpiredDate(sdf.format(parentResult.getExpiredDate()));

             result.setMaxChildAllow(new Integer(3));
             result.setMaxCollaboratorAllow(new Integer(1));
             if (parentResult.getIsFreeTrial().booleanValue()) {
                 result.setAccountType("Free Trial");
             } else {
                 result.setAccountType("Petite Hero Account");
             }
//             result.setOTP()
            return result;
        }
        return null;
    }

    public ParentProfileRegisterResponseDTO saveParentInformationToSystem(Parent parentProfile) {
        Parent parentResult = parentRepository.save(parentProfile);
        if (parentResult != null) {
            ParentProfileRegisterResponseDTO result = new ParentProfileRegisterResponseDTO();

            result.setPhoneNumber(parentResult.getAccount().getUsername());
            result.setFirstName(parentResult.getFirstName());
            result.setLastName(parentResult.getLastName());
            result.setPhoto(parentResult.getPhoto());
            if (parentResult.getLanguage() != null) {
                if (parentResult.getLanguage().booleanValue()) {
                    result.setLanguage("Vietnamese");
                } else {
                    result.setLanguage("English");
                }
            }
            if (parentResult.getGender() != null) {
                if (parentResult.getGender().booleanValue()) {
                    result.setGender("Male");
                } else {
                    result.setGender("Female");
                }
            }
            result.setMaxChildAllow(parentResult.getMaxChildren());
            result.setMaxCollaboratorAllow(parentResult.getMaxParent());

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
            result.setExpiredDate(sdf.format(parentResult.getExpiredDate()));
            if (parentResult.getIsFreeTrial().booleanValue()){
                result.setAccountType("Free Trial");
            } else {
                result.setAccountType("Petite Hero Account");
            }

            return result;
        }
        return null;
    }

    public Parent findParentByPhoneNumber(String phoneNumber) {
        return parentRepository.findParentByAccount_Username(phoneNumber);
    }


    public ResponseObject updateAccountPushToken(UpdatePushTokenRequestDTO data) {
        ResponseObject result = Util.createResponse();
        try {
            Parent parent = parentRepository.findParentByAccount_Username(data.getUsername());
            if (parent == null) {
                result.setData(null);
                result.setMsg("Bad request - Parent doesn't exist");
                result.setCode(Constants.CODE_400);
            } else {
                parent.setPushToken(data.getPushToken());

                result.setData(parentRepository.save(parent));
                result.setMsg(Constants.NO_ERROR);
            }
        } catch (Exception e) {
            result.setData(null);
            result.setMsg("Server Error: " + e.toString());
            result.setCode(Constants.CODE_500);
        }
        return  result;
    }
}
