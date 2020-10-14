package capstone.petitehero.controllers;

import capstone.petitehero.config.common.Constants;
import capstone.petitehero.dtos.ResponseObject;
import capstone.petitehero.dtos.request.child.AddChildRequestDTO;
import capstone.petitehero.dtos.request.parent.ParentRegisterRequestDTO;
import capstone.petitehero.dtos.response.child.AddChildResponseDTO;
import capstone.petitehero.dtos.response.parent.ParentProfileRegisterResponseDTO;
import capstone.petitehero.entities.Account;
import capstone.petitehero.entities.Child;
import capstone.petitehero.entities.Parent;
import capstone.petitehero.services.AccountService;
import capstone.petitehero.services.ChildService;
import capstone.petitehero.services.ParentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping(value = "/parent")
public class ParentController {

    @Autowired
    private ParentService parentService;

    @Autowired
    private ChildService childService;

    @Autowired
    private AccountService accountService;

    @RequestMapping(value = "/register-profile", method = RequestMethod.POST)
    @ResponseBody
    // parent input basic information for them profile.
    public ResponseEntity<Object> updateProfileAfterRegister(@RequestBody ParentRegisterRequestDTO parentRegisterRequestDTO) {
        ResponseObject responseObject;

        // validate mandatory fields
        if (parentRegisterRequestDTO.getFirstName() == null || parentRegisterRequestDTO.getFirstName().isEmpty()) {
            responseObject = new ResponseObject(Constants.CODE_400, "First name cannot be missing or be empty");
            return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
        }
        if (parentRegisterRequestDTO.getLastName() == null || parentRegisterRequestDTO.getLastName().isEmpty()) {
            responseObject = new ResponseObject(Constants.CODE_400, "Last name cannot be missing or be empty");
            return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
        }
        if (parentRegisterRequestDTO.getPassword() == null || parentRegisterRequestDTO.getPassword().isEmpty()) {
            responseObject = new ResponseObject(Constants.CODE_400, "Password cannot be missing or be empty");
            return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
        }
        if (parentRegisterRequestDTO.getConfirmPassword() == null || parentRegisterRequestDTO.getConfirmPassword().isEmpty()) {
            responseObject = new ResponseObject(Constants.CODE_400, "Confirm password cannot be missing or be empty");
            return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
        } else {
            if (!parentRegisterRequestDTO.getPassword().equals(parentRegisterRequestDTO.getConfirmPassword())) {
                responseObject = new ResponseObject(Constants.CODE_400, "Password and confirm password is not match");
                return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
            }
        }
        if (parentRegisterRequestDTO.getGender() == null || parentRegisterRequestDTO.getGender().isEmpty()) {
            responseObject = new ResponseObject(Constants.CODE_400, "Gender cannot be missing or be empty");
            return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
        }
        if (parentRegisterRequestDTO.getLanguage() == null || parentRegisterRequestDTO.getLanguage().isEmpty()) {
            responseObject = new ResponseObject(Constants.CODE_400, "Language cannot be missing or be empty");
            return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
        }
        // end validate mandatory fields

        if (parentRegisterRequestDTO.getPhoneNumber() != null && !parentRegisterRequestDTO.getPhoneNumber().isEmpty()) {
            Parent parent = parentService.findParentByPhoneNumber(parentRegisterRequestDTO.getPhoneNumber());
            if (parent != null) {
                // add basic parent information
                parent.setFirstName(parentRegisterRequestDTO.getFirstName());
                parent.setLastName(parentRegisterRequestDTO.getLastName());
                parent.getAccount().setPassword(parentRegisterRequestDTO.getPassword());

                if (parentRegisterRequestDTO.getGender().equalsIgnoreCase("Male")) {
                    parent.setGender(Boolean.TRUE);
                } else {
                    parent.setGender(Boolean.FALSE);
                }

                if (parentRegisterRequestDTO.getLanguage().equalsIgnoreCase("Vietnamese")) {
                    parent.setLanguage(Boolean.TRUE);
                } else {
                    parent.setLanguage(Boolean.FALSE);
                }
                // end add basic parent information

                ParentProfileRegisterResponseDTO result = parentService.saveParentInformationToSystem(parent);
                if (result != null) {
                    responseObject = new ResponseObject(Constants.CODE_200, "OK");
                    responseObject.setData(result);
                    return new ResponseEntity<>(responseObject, HttpStatus.OK);
                } else {
                    responseObject = new ResponseObject(Constants.CODE_500, "Server is down cannot save your profile.");
                    return new ResponseEntity<>(responseObject, HttpStatus.INTERNAL_SERVER_ERROR);
                }
            } else {
                responseObject = new ResponseObject(Constants.CODE_404, "Cannot find your account in the system");
                return new ResponseEntity<>(responseObject, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        responseObject = new ResponseObject(Constants.CODE_500, "Server is down pls come back again");
        return new ResponseEntity<>(responseObject, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @RequestMapping(value = "/{phone}/children", method = RequestMethod.POST)
    @ResponseBody
    // Add child for parent
    public ResponseEntity<Object> addNewChild(@PathVariable("phone") String parentPhoneNumber, @RequestBody AddChildRequestDTO addChildRequestDTO) {
        ResponseObject responseObject;

        // validate mandatory fields
        if (addChildRequestDTO.getFirstName() == null || addChildRequestDTO.getFirstName().isEmpty()) {
            responseObject = new ResponseObject(Constants.CODE_400, "Child's first name cannot be missing or empty");
            return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
        }
        if (addChildRequestDTO.getLastName() == null || addChildRequestDTO.getLastName().isEmpty()) {
            responseObject = new ResponseObject(Constants.CODE_400, "Child's first name cannot be missing or empty");
            return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
        }
        if (addChildRequestDTO.getYob() == null || addChildRequestDTO.getYob().toString().isEmpty()) {
            responseObject = new ResponseObject(Constants.CODE_400, "Child's year of birth cannot be missing or empty");
            return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
        }
        if (addChildRequestDTO.getGender() == null || addChildRequestDTO.getGender().isEmpty()) {
            responseObject = new ResponseObject(Constants.CODE_400, "Gender cannot be missing or be empty");
            return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
        }
        if (addChildRequestDTO.getLanguage() == null || addChildRequestDTO.getLanguage().isEmpty()) {
            responseObject = new ResponseObject(Constants.CODE_400, "Language cannot be missing or be empty");
            return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
        }
        // end validate mandatory fields

        Account parentAccount = accountService.findParentAccountByPhoneNumber(parentPhoneNumber);

        if (parentAccount != null) {
            Child child = new Child();
            child.setFirstName(addChildRequestDTO.getFirstName());
            child.setLastName(addChildRequestDTO.getLastName());
            child.setYob(addChildRequestDTO.getYob());
            if (addChildRequestDTO.getNickName() != null) {
                child.setNickName(addChildRequestDTO.getNickName());
            }
            if (addChildRequestDTO.getPhoto() != null) {
                child.setPhoto(addChildRequestDTO.getPhoto());
            }

            if (addChildRequestDTO.getGender().equalsIgnoreCase("Male")) {
                child.setGender(Boolean.TRUE);
            } else {
                child.setGender(Boolean.FALSE);
            }

            if (addChildRequestDTO.getLanguage().equalsIgnoreCase("Vietnamese")) {
                child.setLanguage(Boolean.TRUE);
            } else {
                child.setLanguage(Boolean.FALSE);
            }
            child.setIsDisable(Boolean.FALSE);
            child.setCreatedDate(new Date());

            AddChildResponseDTO result = childService.addChildForParent(child);
            if (result != null) {
                result.setParentPhoneNumber(parentPhoneNumber);
                responseObject = new ResponseObject(Constants.CODE_200, "OK");
                responseObject.setData(result);
                return new ResponseEntity<>(responseObject, HttpStatus.OK);
            }
            responseObject = new ResponseObject(Constants.CODE_500, "Server is down cannot add your child to the system");
            return new ResponseEntity<>(responseObject, HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            responseObject = new ResponseObject(Constants.CODE_404, "Cannot find your account in system to add child");
            return new ResponseEntity<>(responseObject, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
