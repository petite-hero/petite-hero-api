package capstone.petitehero.controllers;

import capstone.petitehero.dtos.ResponseErrorDTO;
import capstone.petitehero.dtos.ResponseSuccessDTO;
import capstone.petitehero.dtos.request.child.AddChildRequestDTO;
import capstone.petitehero.dtos.request.parent.ParentRegisterDTO;
import capstone.petitehero.dtos.request.parent.ParentRegisterRequestDTO;
import capstone.petitehero.dtos.response.child.AddChildResponseDTO;
import capstone.petitehero.dtos.response.parent.ParentRegisterResponseDTO;
import capstone.petitehero.entities.Child;
import capstone.petitehero.entities.Parent;
import capstone.petitehero.services.ChildService;
import capstone.petitehero.services.ParentService;
import capstone.petitehero.utilities.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@RestController
@RequestMapping(value = "/parent")
public class ParentController {

    @Autowired
    private ParentService parentService;

    @Autowired
    private ChildService childService;

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ResponseBody
    // parent input phone number to get OTP code for verify to access (register) system.
    public ResponseEntity<Object> registerByPhoneNumber(@RequestBody ParentRegisterDTO parentRegisterDTO) {
        ResponseErrorDTO responseErrorDTO;
        Util util = new Util();

        // validate phone number of parent
        if (parentRegisterDTO.getPhoneNumber() == null || parentRegisterDTO.getPhoneNumber().isEmpty()) {
            responseErrorDTO = new ResponseErrorDTO(400, "Parent's phone number cannot be missing or be empty when register");
            return new ResponseEntity<>(responseErrorDTO, HttpStatus.BAD_REQUEST);
        }
        if (!util.validatePhoneNumberParent(parentRegisterDTO.getPhoneNumber())) {
            responseErrorDTO = new ResponseErrorDTO(400, "Phone number is not in right format" +
                    "Phone number should be (1234567890) or (123( |-)456( |-)7890)");
            return new ResponseEntity<>(responseErrorDTO, HttpStatus.BAD_REQUEST);
        }
        // end validate phone number of parent

        // TODO verify phone number of parent using OTP
        // code here
        Parent parent = new Parent();
        parent.setParentPhoneNumber(parentRegisterDTO.getPhoneNumber());

        // add license & policy for parent account
        parent.setIsFreeTrial(Boolean.TRUE);
        parent.setMaxChildren(3);
        parent.setMaxParent(1);
        parent.setIsDisable(Boolean.FALSE);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 30);
        parent.setExpiredDate(calendar.getTime()); // trial 1 month

        ParentRegisterResponseDTO result = parentService.saveParentToSystem(parent);
        if (result != null) {
            List<Object> listDataResult = new ArrayList<>();
            listDataResult.add(result);
            ResponseSuccessDTO responseSuccessDTO = new ResponseSuccessDTO(200, "OK", listDataResult);
            return new ResponseEntity<>(responseSuccessDTO, HttpStatus.OK);
        }
        responseErrorDTO = new ResponseErrorDTO(500, "Server is down cannot save your account to the system");
        return new ResponseEntity<>(responseErrorDTO, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @RequestMapping(value = "/register-profile", method = RequestMethod.POST)
    @ResponseBody
    // parent input basic information for them profile.
    public ResponseEntity<Object> updateProfileAfterRegister(@RequestBody ParentRegisterRequestDTO parentRegisterRequestDTO) {
        ResponseErrorDTO responseErrorDTO;

        // validate mandatory fields
        if (parentRegisterRequestDTO.getFirstName() == null || parentRegisterRequestDTO.getFirstName().isEmpty()) {
            responseErrorDTO = new ResponseErrorDTO(400, "First name cannot be missing or be empty");
            return new ResponseEntity<>(responseErrorDTO, HttpStatus.BAD_REQUEST);
        }
        if (parentRegisterRequestDTO.getLastName() == null || parentRegisterRequestDTO.getLastName().isEmpty()) {
            responseErrorDTO = new ResponseErrorDTO(400, "Last name cannot be missing or be empty");
            return new ResponseEntity<>(responseErrorDTO, HttpStatus.BAD_REQUEST);
        }
        if (parentRegisterRequestDTO.getPassword() == null || parentRegisterRequestDTO.getPassword().isEmpty()) {
            responseErrorDTO = new ResponseErrorDTO(400, "Password cannot be missing or be empty");
            return new ResponseEntity<>(responseErrorDTO, HttpStatus.BAD_REQUEST);
        }
        if (parentRegisterRequestDTO.getConfirmPassword() == null || parentRegisterRequestDTO.getConfirmPassword().isEmpty()) {
            responseErrorDTO = new ResponseErrorDTO(400, "Confirm password cannot be missing or be empty");
            return new ResponseEntity<>(responseErrorDTO, HttpStatus.BAD_REQUEST);
        } else {
            if (!parentRegisterRequestDTO.getPassword().equals(parentRegisterRequestDTO.getConfirmPassword())) {
                responseErrorDTO = new ResponseErrorDTO(400, "Password and confirm password is not match");
                return new ResponseEntity<>(responseErrorDTO, HttpStatus.BAD_REQUEST);
            }
        }
        if (parentRegisterRequestDTO.getGender() == null || parentRegisterRequestDTO.getGender().isEmpty()) {
            responseErrorDTO = new ResponseErrorDTO(400, "Gender cannot be missing or be empty");
            return new ResponseEntity<>(responseErrorDTO, HttpStatus.BAD_REQUEST);
        }
        if (parentRegisterRequestDTO.getLanguage() == null || parentRegisterRequestDTO.getLanguage().isEmpty()) {
            responseErrorDTO = new ResponseErrorDTO(400, "Language cannot be missing or be empty");
            return new ResponseEntity<>(responseErrorDTO, HttpStatus.BAD_REQUEST);
        }
        // end validate mandatory fields

        if (parentRegisterRequestDTO.getPhoneNumber() != null && !parentRegisterRequestDTO.getPhoneNumber().isEmpty()) {
            Parent parent = parentService.findParentByPhoneNumber(parentRegisterRequestDTO.getPhoneNumber());
            if (parent != null) {
                // add basic parent information
                parent.setFirstName(parentRegisterRequestDTO.getFirstName());
                parent.setLastName(parentRegisterRequestDTO.getLastName());
                parent.setPassword(parentRegisterRequestDTO.getPassword());
                // TODO add condition gender for vietnamese language
                if (parentRegisterRequestDTO.getGender().equalsIgnoreCase("Male")) {
                    parent.setGender(Boolean.TRUE);
                } else {
                    parent.setGender(Boolean.FALSE);
                }
                // TODO add condition language for vietnamese language
                if (parentRegisterRequestDTO.getLanguage().equalsIgnoreCase("Vietnamese")) {
                    parent.setLanguage(Boolean.TRUE);
                } else {
                    parent.setLanguage(Boolean.FALSE);
                }
                // end add basic parent information

                ParentRegisterResponseDTO result = parentService.saveParentToSystem(parent);
                if (result != null) {
                    List<Object> listDataResult = new ArrayList<>();
                    listDataResult.add(result);
                    ResponseSuccessDTO responseSuccessDTO = new ResponseSuccessDTO(200, "OK", listDataResult);
                    return new ResponseEntity<>(responseSuccessDTO, HttpStatus.OK);
                } else {
                    responseErrorDTO = new ResponseErrorDTO(500, "Server is down cannot save your profile.");
                    return new ResponseEntity<>(responseErrorDTO, HttpStatus.INTERNAL_SERVER_ERROR);
                }
            } else {
                responseErrorDTO = new ResponseErrorDTO(404, "Cannot find your account in the system");
                return new ResponseEntity<>(responseErrorDTO, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        responseErrorDTO = new ResponseErrorDTO(500, "Server is down pls come back again");
        return new ResponseEntity<>(responseErrorDTO, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    @RequestMapping(value = "/{phone}/children", method = RequestMethod.POST)
    @ResponseBody
    // Add child for parent
    public ResponseEntity<Object> addNewChild(@PathVariable("phone") String parentPhoneNumber, @RequestBody AddChildRequestDTO addChildRequestDTO) {
        ResponseErrorDTO responseErrorDTO;

        // validate mandatory fields
        if (addChildRequestDTO.getFirstName() == null || addChildRequestDTO.getFirstName().isEmpty()) {
            responseErrorDTO = new ResponseErrorDTO(400,"Child's first name cannot be missing or empty");
            return new ResponseEntity<>(responseErrorDTO, HttpStatus.BAD_REQUEST);
        }
        if (addChildRequestDTO.getLastName() == null || addChildRequestDTO.getLastName().isEmpty()) {
            responseErrorDTO = new ResponseErrorDTO(400,"Child's first name cannot be missing or empty");
            return new ResponseEntity<>(responseErrorDTO, HttpStatus.BAD_REQUEST);
        }
        if (addChildRequestDTO.getGender() == null || addChildRequestDTO.getGender().isEmpty()) {
            responseErrorDTO = new ResponseErrorDTO(400, "Gender cannot be missing or be empty");
            return new ResponseEntity<>(responseErrorDTO, HttpStatus.BAD_REQUEST);
        }
        if (addChildRequestDTO.getLanguage() == null || addChildRequestDTO.getLanguage().isEmpty()) {
            responseErrorDTO = new ResponseErrorDTO(400, "Language cannot be missing or be empty");
            return new ResponseEntity<>(responseErrorDTO, HttpStatus.BAD_REQUEST);
        }
        // end validate mandatory fields

        Child child = new Child();
        child.setFirstName(addChildRequestDTO.getFirstName());
        child.setLastName(addChildRequestDTO.getLastName());
        if (addChildRequestDTO.getNickName() != null) {
            child.setNickName(addChildRequestDTO.getNickName());
        }
        if (addChildRequestDTO.getPhoto() != null) {
            child.setPhoto(addChildRequestDTO.getPhoto());
        }
        // TODO add condition gender for vietnamese language
        if (addChildRequestDTO.getGender().equalsIgnoreCase("Male")) {
            child.setGender(Boolean.TRUE);
        } else {
            child.setGender(Boolean.FALSE);
        }
        // TODO add condition language  for vietnamese language
        if (addChildRequestDTO.getLanguage().equalsIgnoreCase("Vietnamese")) {
            child.setLanguage(Boolean.TRUE);
        } else {
            child.setLanguage(Boolean.FALSE);
        }
        child.setIsDisable(Boolean.FALSE);

        AddChildResponseDTO result = childService.addChildForParent(child, parentPhoneNumber);
        if (result != null) {
            List<Object> listDataResult = new ArrayList<>();
            listDataResult.add(result);
            ResponseSuccessDTO responseSuccessDTO = new ResponseSuccessDTO(200, "OK", listDataResult);
            return new ResponseEntity<>(responseSuccessDTO, HttpStatus.OK);
        }
        responseErrorDTO = new ResponseErrorDTO(500, "Server is down cannot add your child to the system");
        return new ResponseEntity<>(responseErrorDTO, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
