package capstone.petitehero.controllers;

import capstone.petitehero.config.common.Constants;
import capstone.petitehero.dtos.ResponseObject;
import capstone.petitehero.dtos.common.ChildInformation;
import capstone.petitehero.dtos.request.child.AddChildRequestDTO;
import capstone.petitehero.dtos.request.parent.ParentChangePasswordRequestDTO;
import capstone.petitehero.dtos.request.parent.ParentRegisterRequestDTO;
import capstone.petitehero.dtos.request.parent.ParentUpdateProfileRequestDTO;
import capstone.petitehero.dtos.request.parent.UpdatePushTokenRequestDTO;
import capstone.petitehero.dtos.request.parent.payment.ParentPaymentCreateRequestDTO;
import capstone.petitehero.dtos.response.child.AddChildResponseDTO;
import capstone.petitehero.dtos.response.parent.ParentProfileRegisterResponseDTO;
import capstone.petitehero.dtos.response.parent.ParentUpdateProfileResponseDTO;
import capstone.petitehero.dtos.response.parent.payment.ParentPaymentCompledResponseDTO;
import capstone.petitehero.entities.Child;
import capstone.petitehero.entities.Parent;
import capstone.petitehero.entities.ParentPayment;
import capstone.petitehero.services.*;
import capstone.petitehero.utilities.PaypalUtil;
import capstone.petitehero.utilities.Util;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

import static org.springframework.http.MediaType.*;


@RestController
@RequestMapping(value = "/parent")
public class ParentController {

    @Autowired
    private ParentService parentService;

    @Autowired
    private ChildService childService;

    @Autowired
    private ParentChildService parentChildService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private PaypalServices paypalServices;

    @Autowired
    private ParentPaymentService parentPaymentService;

    @RequestMapping(value = "/register-profile", method = RequestMethod.POST, consumes = ALL_VALUE)
    @ResponseBody
    // parent input basic information for them profile.
    public ResponseEntity<Object> updateProfileAfterRegister(@ModelAttribute ParentRegisterRequestDTO parentRegisterRequestDTO,
                                                             @RequestParam(value = "avatar", required = false) MultipartFile uploadFile) {
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
        if (parentRegisterRequestDTO.getEmail() == null || parentRegisterRequestDTO.getEmail().isEmpty()) {
            responseObject = new ResponseObject(Constants.CODE_400, "Email cannot be missing or be empty");
            return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
        } else {
            Util util = new Util();
            if (!util.validateEmail(parentRegisterRequestDTO.getEmail())) {
                responseObject = new ResponseObject(Constants.CODE_400, "Email is not valid");
                return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
            }
        }
        if (parentRegisterRequestDTO.getPassword() == null || parentRegisterRequestDTO.getPassword().isEmpty()) {
            responseObject = new ResponseObject(Constants.CODE_400, "Password cannot be missing or be empty");
            return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
        }
        if (!Util.validatePasswordForAllAccount(parentRegisterRequestDTO.getPassword())) {
            responseObject = new ResponseObject(Constants.CODE_400, "Password should between 6-8 characters and no special characters");
            return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
        }
        if (parentRegisterRequestDTO.getConfirmPassword() == null || parentRegisterRequestDTO.getConfirmPassword().isEmpty()) {
            responseObject = new ResponseObject(Constants.CODE_400, "Confirm password cannot be missing or be empty");
            return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
        } else {
            if (!Util.validatePasswordForAllAccount(parentRegisterRequestDTO.getConfirmPassword())) {
                responseObject = new ResponseObject(Constants.CODE_400, "Password should between 6-8 characters and no special characters");
                return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
            }
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
                parent.setEmail(parentRegisterRequestDTO.getEmail());

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

                // save avatar for parent
                if (uploadFile != null && !uploadFile.isEmpty()) {

                    parent.setPhoto(Util.saveImageToSystem(parentRegisterRequestDTO.getPhoneNumber(), "Avatar Added", uploadFile));
                }

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

    @RequestMapping(value = "/{phone}", method = RequestMethod.PUT, consumes = ALL_VALUE)
    @ResponseBody
    //updated profile for parent
    public ResponseEntity<Object> updateParentProfile(@PathVariable("phone") String parentPhoneNumber,
                                                      @ModelAttribute ParentUpdateProfileRequestDTO parentUpdateProfileRequestDTO,
                                                      @RequestParam(value = "avatar", required = false) MultipartFile parentAvatar) {
        ResponseObject responseObject;

        Parent parent = parentService.findParentByPhoneNumber(parentPhoneNumber);

        if (parentUpdateProfileRequestDTO.getFirstName() != null && !parentUpdateProfileRequestDTO.getFirstName().isEmpty()) {
            parent.setFirstName(parentUpdateProfileRequestDTO.getFirstName());
        }
        if (parentUpdateProfileRequestDTO.getLastName() != null && !parentUpdateProfileRequestDTO.getLastName().isEmpty()) {
            parent.setLastName(parentUpdateProfileRequestDTO.getLastName());
        }
        if (parentUpdateProfileRequestDTO.getEmail() != null && !parentUpdateProfileRequestDTO.getEmail().isEmpty()) {
            parent.setEmail(parentUpdateProfileRequestDTO.getEmail());
        }
        if (parentUpdateProfileRequestDTO.getGender() != null && !parentUpdateProfileRequestDTO.getGender().isEmpty()) {
            if (parentUpdateProfileRequestDTO.getGender().equalsIgnoreCase("Male")) {
                parent.setGender(Boolean.TRUE);
            } else {
                parent.setGender(Boolean.FALSE);
            }
        }
        if (parentUpdateProfileRequestDTO.getLanguage() != null && !parentUpdateProfileRequestDTO.getLanguage().isEmpty()) {
            if (parentUpdateProfileRequestDTO.getLanguage().equalsIgnoreCase("Vietnamese")) {
                parent.setLanguage(Boolean.TRUE);
            } else {
                parent.setLanguage(Boolean.FALSE);
            }
        }

        if (parentAvatar != null) {
            parent.setPhoto(Util.saveImageToSystem(parentPhoneNumber, "Avatar Updated", parentAvatar));
        }

        ParentUpdateProfileResponseDTO result = parentService.updateParentProfile(parent);
        if (result != null) {
            responseObject = new ResponseObject(Constants.CODE_200, "OK");
            responseObject.setData(result);
            return new ResponseEntity<>(responseObject, HttpStatus.OK);
        }
        responseObject = new ResponseObject(Constants.CODE_500, "Server is down cannot update your profile");
        return new ResponseEntity<>(responseObject, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @RequestMapping(value = "/{phone}/children", method = RequestMethod.POST, consumes = ALL_VALUE)
    @ResponseBody
    // Add child for parent
    public ResponseEntity<Object> addNewChild(@PathVariable("phone") String parentPhoneNumber,
                                              @ModelAttribute AddChildRequestDTO addChildRequestDTO,
                                              @RequestParam(value = "childAvatar", required = false) MultipartFile childPhoto) {
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

        Parent parentAccount = parentService.findParentByPhoneNumber(parentPhoneNumber);

        if (parentAccount != null) {
            Child child = new Child();
            child.setFirstName(addChildRequestDTO.getFirstName());
            child.setLastName(addChildRequestDTO.getLastName());
            child.setYob(addChildRequestDTO.getYob());
            if (addChildRequestDTO.getNickName() != null) {
                child.setNickName(addChildRequestDTO.getNickName());
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
            child.setIsDisabled(Boolean.FALSE);
            child.setCreatedDate(new Date().getTime());


            AddChildResponseDTO result = childService.addChildForParent(child, parentAccount, childPhoto);
            if (result != null) {
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

    @RequestMapping(value = "/{childId}/regenerate-qrcode", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Object> regenerateQRCodeForChildVerify(@PathVariable("childId") Long childId) {
        ResponseObject responseObject;

        AddChildResponseDTO result = childService.regenerateQRCodeForChildVerify(childId);
        if (result != null) {
            responseObject = new ResponseObject(Constants.CODE_200, "OK");
            responseObject.setData(result);
            return new ResponseEntity<>(responseObject, HttpStatus.OK);
        }
        responseObject = new ResponseObject(Constants.CODE_500, "Cannot regenerate qr code for child to verify");
        return new ResponseEntity<>(responseObject, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @RequestMapping(value = "/token", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseObject updateAccountPushToken (@RequestBody UpdatePushTokenRequestDTO data) {
        return parentService.updateAccountPushToken(data);
    }

    @RequestMapping(value = "/{phone}/password", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<Object> changePasswordForParent(@PathVariable("phone") String parentPhoneNumber,
                                                          @RequestBody ParentChangePasswordRequestDTO parentChangePasswordRequestDTO) {
        ResponseObject responseObject;
        if (parentChangePasswordRequestDTO.getPassword() == null || parentChangePasswordRequestDTO.getPassword().isEmpty()) {
            responseObject = new ResponseObject(Constants.CODE_400, "New password cannot be null");
            return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
        }
        if (!Util.validatePasswordForAllAccount(parentChangePasswordRequestDTO.getPassword())) {
            responseObject = new ResponseObject(Constants.CODE_400, "Password should between 6-8 characters and no special characters");
            return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
        }
        if (parentChangePasswordRequestDTO.getConfirmPassword() == null || parentChangePasswordRequestDTO.getConfirmPassword().isEmpty()) {
            responseObject = new ResponseObject(Constants.CODE_400, "Confirm password cannot be null");
            return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
        } else {
            if (!Util.validatePasswordForAllAccount(parentChangePasswordRequestDTO.getConfirmPassword())) {
                responseObject = new ResponseObject(Constants.CODE_400, "Password should between 6-8 characters and no special characters");
                return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
            }
            if (!parentChangePasswordRequestDTO.getConfirmPassword().equals(parentChangePasswordRequestDTO.getPassword())) {
                responseObject = new ResponseObject(Constants.CODE_400, "Password and confirm password is not match");
                return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
            }
        }

        Parent parent = parentService.findParentByPhoneNumber(parentPhoneNumber);
        if (parent != null) {
            parent.getAccount().setPassword(parentChangePasswordRequestDTO.getPassword());

            String result = parentService.changeParentAccountPassword(parent);
            if (result != null) {
                responseObject = new ResponseObject(Constants.CODE_200, "OK");
                responseObject.setData(result);
                return new ResponseEntity<>(responseObject, HttpStatus.OK);
            }
        } else {
            responseObject = new ResponseObject(Constants.CODE_404, "Cannot found your parent account in the system");
            return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
        }
        responseObject = new ResponseObject(Constants.CODE_500, "Server is down cannot change password");
        return new ResponseEntity<>(responseObject, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @RequestMapping(value = "/{phone}/collaborators", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Object> getListCollaboratorOfParent(@PathVariable("phone") String phoneNumber) {
        return null;
    }

    @RequestMapping(value = "/{phone}/children", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Object> getListChildrenOfParent(@PathVariable("phone") String phoneNumber) {
        ResponseObject responseObject;

        List<ChildInformation> result = parentChildService.getListChildOfParent(phoneNumber);
        if (result != null) {
            if (result.isEmpty()) {
                responseObject = new ResponseObject(Constants.CODE_200, "Your list children is empty");
                return new ResponseEntity<>(responseObject, HttpStatus.OK);
            } else {
                responseObject = new ResponseObject(Constants.CODE_200, "OK");
                responseObject.setData(result);
                return new ResponseEntity<>(responseObject, HttpStatus.OK);
            }
        }
        responseObject = new ResponseObject(Constants.CODE_500, "Cannot get your children in the system");
        return new ResponseEntity<>(responseObject, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @RequestMapping(value = "/{phone}/paypal/payment", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Object> testPaypal(HttpServletRequest request,
                                             @PathVariable("phone") String phoneNumber,
                                             @RequestBody ParentPaymentCreateRequestDTO parentPaymentCreateRequestDTO) {
        ResponseObject responseObject;
        Long currentTimeStamp = new Date().getTime();
        String cancelUrl = PaypalUtil.getBaseURL(request) + "/parent/" + phoneNumber + "/paypal/cancel?createdDate=" + currentTimeStamp;
        String successUrl = PaypalUtil.getBaseURL(request) + "/parent/" + phoneNumber + "/paypal/success?createdDate=" + currentTimeStamp;
        try {
            Payment payment = paypalServices.createPayment(parentPaymentCreateRequestDTO.getAmount(),
                    "USD",
                    "paypal",
                    "sale",
                    parentPaymentCreateRequestDTO.getDescription(),
                    cancelUrl,
                    successUrl);
            for(Links links : payment.getLinks()){
                if(links.getRel().equals("approval_url")){
                    ParentPayment parentPayment = new ParentPayment();
                    parentPayment.setContent(parentPaymentCreateRequestDTO.getDescription());
                    parentPayment.setAmount(parentPaymentCreateRequestDTO.getAmount());
                    parentPayment.setStatus("PENDING");

                    Parent parent = parentService.findParentByPhoneNumber(phoneNumber);

                    parentPayment.setParent(parent);
                    parentPayment.setDate(currentTimeStamp);
                    ParentPayment result = parentPaymentService.insertParentPaymentToSystem(parentPayment);
                    if (result != null) {
                        responseObject = new ResponseObject(Constants.CODE_200, "OK");
                        responseObject.setData(links.getHref());
                        return new ResponseEntity<>(responseObject, HttpStatus.OK);
                    } else {
                        responseObject = new ResponseObject(Constants.CODE_500, "Cannot connect to petite hero pls come back again");
                        return new ResponseEntity<>(responseObject, HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                }
            }
            responseObject = new ResponseObject(Constants.CODE_500, "Cannot connect to paypal");
            return new ResponseEntity<>(responseObject, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            responseObject = new ResponseObject(Constants.CODE_400, "Bad Request for paypal api");
            responseObject.setData(e.getMessage());
            return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/{phone}/paypal/cancel", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Object> cancelPay(@PathVariable("phone") String parentPhoneNumber,
                            @RequestParam(value = "createdDate") Long createdDateTimeStamp){
        ResponseObject responseObject;
        ParentPayment recentParentPayment = parentPaymentService.findParentPaymentToCompletePayment(parentPhoneNumber, createdDateTimeStamp);

        if (recentParentPayment != null) {
            recentParentPayment.setStatus("CANCELLED");

            ParentPayment result = parentPaymentService.insertParentPaymentToSystem(recentParentPayment);
            if (result != null) {
                responseObject = new ResponseObject(Constants.CODE_200, "OK");
                responseObject.setData(result.getStatus());
                return new ResponseEntity<>(responseObject, HttpStatus.OK);
            } else {
                responseObject = new ResponseObject(Constants.CODE_500, "Cannot connect to server pls come back again");
                return new ResponseEntity<>(responseObject, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        responseObject = new ResponseObject(Constants.CODE_404, "Cannot found your current payment.");
        return new ResponseEntity<>(responseObject, HttpStatus.NOT_FOUND);
    }

    @RequestMapping(value = "/{phone}/paypal/success", method = RequestMethod.GET)
    public ResponseEntity<Object> successPay(@PathVariable("phone") String parentPhoneNumber,
                             @RequestParam(value = "createdDate") Long createdDateTimeStamp,
                             @RequestParam(value = "paymentId") String paymentId,
                             @RequestParam(value = "PayerID") String payerId){
        ResponseObject responseObject;
        try {
            Payment payment = paypalServices.executePayment(paymentId, payerId);
            if(payment.getState().equals("approved")){
                ParentPayment recentParentPayment = parentPaymentService.findParentPaymentToCompletePayment(parentPhoneNumber, createdDateTimeStamp);

                if (recentParentPayment != null) {
                    recentParentPayment.setStatus("SUCCESS");
                    recentParentPayment.setPayerId(payerId);
                    recentParentPayment.setPaymentId(paymentId);

                    ParentPaymentCompledResponseDTO result = parentPaymentService.completedSuccessParentPayment(recentParentPayment);
                    if (result != null) {
                        responseObject = new ResponseObject(Constants.CODE_200, "OK");
                        responseObject.setData(result);
                        return new ResponseEntity<>(responseObject, HttpStatus.OK);
                    } else {
                        responseObject = new ResponseObject(Constants.CODE_500, "Cannot connect to server pls come back again");
                        return new ResponseEntity<>(responseObject, HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                } else {
                    responseObject = new ResponseObject(Constants.CODE_404, "Cannot found your current payment.");
                    return new ResponseEntity<>(responseObject, HttpStatus.NOT_FOUND);
                }
            } else {
                responseObject = new ResponseObject(Constants.CODE_500, "Cannot completed your payment because of some reason");
                return new ResponseEntity<>(responseObject, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (PayPalRESTException e) {
            responseObject = new ResponseObject(Constants.CODE_500, "Cannot connect to paypal");
            return new ResponseEntity<>(responseObject, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
