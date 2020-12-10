package capstone.petitehero.services;

import capstone.petitehero.config.common.Constants;
import capstone.petitehero.dtos.ResponseObject;
import capstone.petitehero.dtos.common.ChildInformation;
import capstone.petitehero.dtos.request.parent.UpdatePushTokenRequestDTO;
import capstone.petitehero.dtos.response.account.ParentDetailResponseDTO;
import capstone.petitehero.dtos.response.parent.DisableParentResponseDTO;
import capstone.petitehero.dtos.response.parent.ParentProfileRegisterResponseDTO;
import capstone.petitehero.dtos.response.parent.ParentUpdateProfileResponseDTO;
import capstone.petitehero.entities.Parent;
import capstone.petitehero.entities.Parent_Child;
import capstone.petitehero.repositories.ParentChildRepository;
import capstone.petitehero.repositories.ParentRepository;
import capstone.petitehero.utilities.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ParentService {

    @Autowired
    private ParentRepository parentRepository;

    @Autowired
    private ParentChildRepository parentChildRepository;

    public Parent saveParentAccount(Parent parent) {
        Parent result = parentRepository.save(parent);
        if (result != null) {
            return result;
        }
        return null;
    }

//    public ParentRegisterResponseDTO registerByParent(Parent parentHaveOnlyPhoneNumber) {
//        Parent parentResult = parentRepository.save(parentHaveOnlyPhoneNumber);
//        if (parentResult != null) {
//            ParentRegisterResponseDTO result = new ParentRegisterResponseDTO();
//            result.setPhoneNumber(parentResult.getAccount().getUsername());
//
//            result.setMaxChildAllow(parentResult.getSubscription().getSubscriptionType().getMaxChildren());
//            result.setMaxCollaboratorAllow(parentResult.getSubscription().getSubscriptionType().getMaxCollaborator());
//            result.setAccountType("Free Trial");
//
//            result.setExpiredDate(Util.formatTimestampToDateTime(parentResult.getSubscription().getExpiredDate()));
//            return result;
//        }
//        return null;
//    }

    public ParentProfileRegisterResponseDTO saveParentInformationToSystem(Parent parentProfile) {
        Parent parentResult = parentRepository.save(parentProfile);
        if (parentResult != null) {
            ParentProfileRegisterResponseDTO result = new ParentProfileRegisterResponseDTO();

            result.setPhoneNumber(parentResult.getAccount().getUsername());
            result.setName(parentResult.getName());

            if (parentResult.getPhoto() != null && !parentResult.getPhoto().isEmpty()) {
                result.setPhoto(Util.fromImageFileToBase64String(parentResult.getPhoto()));
            }
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

            return result;
        }
        return null;
    }

    public Parent findParentByPhoneNumber(String phoneNumber, Boolean isDisable) {
        return parentRepository.findParentByAccount_UsernameAndIsDisabled(phoneNumber, isDisable);
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
        return result;
    }

    public ParentUpdateProfileResponseDTO updateParentProfile(Parent parent) {
        Parent parentResult = parentRepository.save(parent);

        if (parentResult != null) {
            ParentUpdateProfileResponseDTO result = new ParentUpdateProfileResponseDTO();

            result.setName(parentResult.getName());
            result.setEmail(parentResult.getEmail());
            if (parentResult.getGender() != null) {
                if (parentResult.getGender().booleanValue()) {
                    result.setGender("Male");
                } else {
                    result.setGender("Female");
                }
            }
            if (parentResult.getLanguage() != null) {
                if (parentResult.getLanguage().booleanValue()) {
                    result.setLanguage("Vietnamese");
                } else {
                    result.setLanguage("English");
                }
            }
            result.setStatus(Constants.status.UPDATED.toString());

            return result;
        }
        return null;
    }

    public DisableParentResponseDTO disableParentAccount(Parent parentAccount, Boolean isDisable) {
        if (parentAccount != null) {
            parentAccount.setIsDisabled(isDisable);
            ArrayList<Parent_Child> childList = new ArrayList(parentAccount.getParent_childCollection());
            for (Parent_Child parentChild : childList) {
                parentChild.getChild().setIsDisabled(isDisable);
            }
            Parent parentResult = parentRepository.save(parentAccount);
            if (parentResult != null) {
                DisableParentResponseDTO result = new DisableParentResponseDTO();

                result.setPhoneNumber(parentResult.getAccount().getUsername());
                if (isDisable.booleanValue()) {
                    result.setStatus(Constants.status.DEACTIVE.toString());
                } else {
                    result.setStatus(Constants.status.ACTIVE.toString());
                }
                return result;
            }
        }
        return null;
    }

    public String searchCollaboratorName(String phoneNumber, Boolean isDisable) {
        Parent collaborator =
                parentRepository.findParentByAccount_UsernameAndIsDisabled(phoneNumber, isDisable);

        if (collaborator != null) {
            String result = collaborator.getName();

            return result;
        }
        return null;
    }

    public ParentDetailResponseDTO getCollaboratorDetails(Parent parentAccount, Parent collaboratorAccount) {
        List<Parent_Child> parentChildrenResult =
                parentChildRepository.findParent_ChildrenByCollaborator_Account_UsernameAndAndParent_Account_UsernameAndChild_IsDisabled(
                        collaboratorAccount.getAccount().getUsername(),
                        parentAccount.getAccount().getUsername(),
                        Boolean.FALSE);
        ParentDetailResponseDTO result = new ParentDetailResponseDTO();

        if (parentChildrenResult != null) {
            result.setChildInformationList(new ArrayList<>());
            if (!parentChildrenResult.isEmpty()) {
                for (Parent_Child parentChild : parentChildrenResult) {
                    ChildInformation childInformation = new ChildInformation();

                    childInformation.setChildId(parentChild.getChild().getChildId());
                    if (parentChild.getChild().getPhoto() != null && !parentChild.getChild().getPhoto().isEmpty()) {
                        childInformation.setPhoto(Util.fromImageFileToBase64String(parentChild.getChild().getPhoto()));
                    }

                    result.getChildInformationList().add(childInformation);
                }
                result.setName(collaboratorAccount.getName());
            }
        }

        return result;
    }

    public DisableParentResponseDTO resetParentDevice(Parent parent) {
        parent.setDeviceId(null);

        Parent parentResult = parentRepository.save(parent);
        if (parentResult != null) {
            DisableParentResponseDTO result = new DisableParentResponseDTO();

            result.setPhoneNumber(parentResult.getAccount().getUsername());
            result.setStatus(Constants.UPDATED);

            return result;
        }

        return null;
    }
}
