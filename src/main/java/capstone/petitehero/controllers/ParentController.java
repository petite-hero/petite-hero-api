package capstone.petitehero.controllers;

import capstone.petitehero.config.common.Constants;
import capstone.petitehero.dtos.ResponseObject;
import capstone.petitehero.dtos.common.ChildInformation;
import capstone.petitehero.dtos.request.child.AddChildRequestDTO;
import capstone.petitehero.dtos.request.collaborator.AddCollaboratorRequestDTO;
import capstone.petitehero.dtos.request.parent.ParentRegisterRequestDTO;
import capstone.petitehero.dtos.request.parent.ParentUpdateProfileRequestDTO;
import capstone.petitehero.dtos.request.parent.UpdatePushTokenRequestDTO;
import capstone.petitehero.dtos.request.parent.payment.ParentPaymentCreateRequestDTO;
import capstone.petitehero.dtos.response.child.AddChildResponseDTO;
import capstone.petitehero.dtos.response.collaborator.AddCollaboratorResponseDTO;
import capstone.petitehero.dtos.response.collaborator.ListCollaboratorResponseDTO;
import capstone.petitehero.dtos.response.parent.DisableParentResponseDTO;
import capstone.petitehero.dtos.response.parent.ParentProfileRegisterResponseDTO;
import capstone.petitehero.dtos.response.parent.ParentUpdateProfileResponseDTO;
import capstone.petitehero.dtos.response.parent.payment.ListPaymentTransactionResponseDTO;
import capstone.petitehero.dtos.response.parent.payment.ParentPaymentCompledResponseDTO;
import capstone.petitehero.entities.*;
import capstone.petitehero.services.*;
import capstone.petitehero.utilities.Util;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.Calendar;
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
    private ParentPaymentService parentPaymentService;

    @Autowired
    private SubscriptionService subscriptionService;

    @Value("${paypal.currency}")
    private String currency;

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
//        if (!Util.validateName(parentRegisterRequestDTO.getFirstName(), 2, 30)) {
//            responseObject = new ResponseObject(Constants.CODE_400, "First name should between 2 to 30 characters and special characters");
//            return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
//        }
        if (parentRegisterRequestDTO.getLastName() == null || parentRegisterRequestDTO.getLastName().isEmpty()) {
            responseObject = new ResponseObject(Constants.CODE_400, "Last name cannot be missing or be empty");
            return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
        }
//        if (!Util.validateName(parentRegisterRequestDTO.getLastName(), 2, 30)) {
//            responseObject = new ResponseObject(Constants.CODE_400, "Last name should between 2 to 30 characters and special characters");
//            return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
//        }
        if (parentRegisterRequestDTO.getEmail() == null || parentRegisterRequestDTO.getEmail().isEmpty()) {
            responseObject = new ResponseObject(Constants.CODE_400, "Email cannot be missing or be empty");
            return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
        } else {
            if (!Util.validateEmail(parentRegisterRequestDTO.getEmail())) {
                responseObject = new ResponseObject(Constants.CODE_400, "Email is not valid");
                return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
            }
        }
        if (parentRegisterRequestDTO.getPassword() == null || parentRegisterRequestDTO.getPassword().isEmpty()) {
            responseObject = new ResponseObject(Constants.CODE_400, "Password cannot be missing or be empty");
            return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
        }
//        if (!Util.validateLengthOfString(parentRegisterRequestDTO.getPassword(), 6, 30)) {
//            responseObject = new ResponseObject(Constants.CODE_400, "Password should between 6 characters to 30 characters");
//            return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
//        }
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
                }
            } else {
                responseObject = new ResponseObject(Constants.CODE_404, "Cannot find your account in the system");
                return new ResponseEntity<>(responseObject, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        responseObject = new ResponseObject(Constants.CODE_500, "Server is down cannot save your profile.");
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
        if (parent != null) {
            responseObject = new ResponseObject(Constants.CODE_404, "Cannot found that parent account in the system");
            return new ResponseEntity<>(responseObject, HttpStatus.NOT_FOUND);
        }

        if (parentUpdateProfileRequestDTO.getFirstName() != null && !parentUpdateProfileRequestDTO.getFirstName().isEmpty()) {
//            if (!Util.validateName(parentUpdateProfileRequestDTO.getFirstName(), 2, 30)) {
//                responseObject = new ResponseObject(Constants.CODE_400, "First name should between 2 to 30 characters and special characters");
//                return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
//            }
            parent.setFirstName(parentUpdateProfileRequestDTO.getFirstName());
        }
        if (parentUpdateProfileRequestDTO.getLastName() != null && !parentUpdateProfileRequestDTO.getLastName().isEmpty()) {
//            if (!Util.validateName(parentUpdateProfileRequestDTO.getLastName(), 2, 30)) {
//                responseObject = new ResponseObject(Constants.CODE_400, "Last name should between 2 to 30 characters and special characters");
//                return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
//            }
            parent.setLastName(parentUpdateProfileRequestDTO.getLastName());
        }
        if (parentUpdateProfileRequestDTO.getEmail() != null && !parentUpdateProfileRequestDTO.getEmail().isEmpty()) {
            if (!Util.validateEmail(parentUpdateProfileRequestDTO.getEmail())) {
                responseObject = new ResponseObject(Constants.CODE_400, "Email is not valid");
                return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
            }
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
//        if (!Util.validateName(addChildRequestDTO.getFirstName(), 2, 30)) {
//            responseObject = new ResponseObject(Constants.CODE_400, "Child's first name should between 2 characters to 30 characters and special characters");
//            return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
//        }
        if (addChildRequestDTO.getLastName() == null || addChildRequestDTO.getLastName().isEmpty()) {
            responseObject = new ResponseObject(Constants.CODE_400, "Child's first name cannot be missing or empty");
            return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
        }
//        if (!Util.validateName(addChildRequestDTO.getLastName(), 2, 30)) {
//            responseObject = new ResponseObject(Constants.CODE_400, "Child's last name should between 2 characters to 30 characters and special characters");
//            return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
//        }
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
            if (Util.checkSubscriptionWhenParentAddChild(parentAccount)) {
                responseObject = new ResponseObject(Constants.CODE_400, "Your subscription only support max "
                        + parentAccount.getSubscription().getSubscriptionType().getMaxChildren() +
                        " child and you already full");
                return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
            }

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
            child.setTrackingActive(Boolean.FALSE);


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

    @RequestMapping(value = "/{parentPhoneNumber}/collaborator", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Object> addNewCollaborator(@PathVariable("parentPhoneNumber") String parentPhoneNumber,
                                                     @RequestBody AddCollaboratorRequestDTO addCollaboratorRequestDTO) {
        ResponseObject responseObject;
        if (addCollaboratorRequestDTO.getCollaboratorPhoneNumber() == null || addCollaboratorRequestDTO.getCollaboratorPhoneNumber().isEmpty()) {
            responseObject = new ResponseObject(Constants.CODE_400, "Collaborator phone number cannot be missing or empty");
            return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
        }
        if (addCollaboratorRequestDTO.getListChildId().isEmpty()) {
            responseObject = new ResponseObject(Constants.CODE_400, "List children for collaborator take care is empty");
            return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
        }

        Parent parentAccount = parentService.findParentByPhoneNumber(parentPhoneNumber);
        if (parentAccount != null) {
            if (Util.checkSubscriptionWhenParentAddCollaborator(parentAccount)) {
                responseObject = new ResponseObject(Constants.CODE_400, "Your subscription only support max "
                        + parentAccount.getSubscription().getSubscriptionType().getMaxCollaborator() +
                        " collaborator and you already full");
                return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
            }

            Parent collaboratorAccount = parentService.findParentByPhoneNumber(addCollaboratorRequestDTO.getCollaboratorPhoneNumber());
            if (collaboratorAccount != null) {
                AddCollaboratorResponseDTO result =
                        parentChildService.addNewCollaborator(addCollaboratorRequestDTO.getListChildId(), parentAccount, collaboratorAccount);

                if (!result.getListChildren().isEmpty()) {
                    responseObject = new ResponseObject(Constants.CODE_200, "OK");
                    responseObject.setData(result);
                    return new ResponseEntity<>(responseObject, HttpStatus.OK);
                }

                responseObject = new ResponseObject(Constants.CODE_500, "Server is down cannot add collaborator to the system");
                return new ResponseEntity<>(responseObject, HttpStatus.INTERNAL_SERVER_ERROR);
            } else {
                responseObject = new ResponseObject(Constants.CODE_404, "Cannot found that collaborator account in the system");
                return new ResponseEntity<>(responseObject, HttpStatus.NOT_FOUND);
            }
        }

        responseObject = new ResponseObject(Constants.CODE_404, "Cannot find your account in the system");
        return new ResponseEntity<>(responseObject, HttpStatus.NOT_FOUND);
    }

    @RequestMapping(value = "/collaborator/confirm", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<Object> confirmByCollaborator(@RequestBody AddCollaboratorRequestDTO addCollaboratorRequestDTO) {
        ResponseObject responseObject;

        if (addCollaboratorRequestDTO.getCollaboratorPhoneNumber() == null || addCollaboratorRequestDTO.getCollaboratorPhoneNumber().isEmpty()) {
            responseObject = new ResponseObject(Constants.CODE_400, "Collaborator phone number cannot be missing or empty");
            return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
        }
        if (addCollaboratorRequestDTO.getListChildId().isEmpty()) {
            responseObject = new ResponseObject(Constants.CODE_400, "List children of collaborator to confirm is empty");
            return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
        }

        Parent collaboratorAccount = parentService.findParentByPhoneNumber(addCollaboratorRequestDTO.getCollaboratorPhoneNumber());
        if (collaboratorAccount != null) {
            AddCollaboratorResponseDTO result = parentChildService.confirmByCollaborator(
                    collaboratorAccount, addCollaboratorRequestDTO.getListChildId());

            if (!result.getListChildren().isEmpty()) {
                responseObject = new ResponseObject(Constants.CODE_200, "OK");
                responseObject.setData(result);
                return new ResponseEntity<>(responseObject, HttpStatus.OK);
            }
            responseObject = new ResponseObject(Constants.CODE_500, "Cannot confirm collaborator's children");
            return new ResponseEntity<>(responseObject, HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            responseObject = new ResponseObject(Constants.CODE_404, "Cannot found your account in the system");
            return new ResponseEntity<>(responseObject, HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/{parentPhoneNumber}/collaborator", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<Object> deleteCollaboratorForParent(@PathVariable("parentPhoneNumber") String parentPhoneNumber,
                                                              @RequestBody AddCollaboratorRequestDTO addCollaboratorRequestDTO) {
        ResponseObject responseObject;
        if (addCollaboratorRequestDTO.getCollaboratorPhoneNumber() == null || addCollaboratorRequestDTO.getCollaboratorPhoneNumber().isEmpty()) {
            responseObject = new ResponseObject(Constants.CODE_400, "Collaborator phone number cannot be missing or empty");
            return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
        }
        if (addCollaboratorRequestDTO.getListChildId().isEmpty()) {
            responseObject = new ResponseObject(Constants.CODE_400, "List children for collaborator take care is empty");
            return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
        }

        Parent parentAccount = parentService.findParentByPhoneNumber(parentPhoneNumber);
        if (parentAccount != null) {
            AddCollaboratorResponseDTO result = parentChildService.deleteCollaboratorByParent(
                    addCollaboratorRequestDTO.getListChildId(), parentAccount, addCollaboratorRequestDTO.getCollaboratorPhoneNumber());

            if (!result.getListChildren().isEmpty()) {
                responseObject = new ResponseObject(Constants.CODE_200, "OK");
                responseObject.setData(result);
                return new ResponseEntity<>(responseObject, HttpStatus.OK);
            }
            responseObject = new ResponseObject(Constants.CODE_500, "Cannot delete collaborator");
            return new ResponseEntity<>(responseObject, HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            responseObject = new ResponseObject(Constants.CODE_404, "Cannot found your account in the system");
            return new ResponseEntity<>(responseObject, HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/token", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseObject updateAccountPushToken (@RequestBody UpdatePushTokenRequestDTO data) {
        return parentService.updateAccountPushToken(data);
    }

    @RequestMapping(value = "/{phone}/collaborator", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Object> getListCollaboratorOfParent(@PathVariable("phone") String phoneNumber) {
        ResponseObject responseObject;

        Parent parentAccount = parentService.findParentByPhoneNumber(phoneNumber);
        if (parentAccount == null) {
            responseObject = new ResponseObject(Constants.CODE_404, "Cannot found your account in the system");
            return new ResponseEntity<>(responseObject, HttpStatus.NOT_FOUND);
        }

        List<ListCollaboratorResponseDTO> result = parentChildService.getParentCollaborator(phoneNumber);
        if (result != null) {
            if (result.isEmpty()) {
                responseObject = new ResponseObject(Constants.CODE_200, "Your collaborator list is empty");
            } else {
                responseObject = new ResponseObject(Constants.CODE_200, "OK");
            }
            responseObject.setData(result);
            return new ResponseEntity<>(responseObject,HttpStatus.OK);
        }

        responseObject = new ResponseObject(Constants.CODE_500, "Cannot get your collaborator in the system");
        return new ResponseEntity<>(responseObject, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @RequestMapping(value = "/{phone}/children", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Object> getListChildrenOfParent(@PathVariable("phone") String phoneNumber) {
        ResponseObject responseObject;

        Parent parentAccount = parentService.findParentByPhoneNumber(phoneNumber);
        if (parentAccount == null) {
            responseObject = new ResponseObject(Constants.CODE_404, "Cannot found your account in the system");
            return new ResponseEntity<>(responseObject, HttpStatus.NOT_FOUND);
        }

        List<ChildInformation> result = parentChildService.getListChildOfParent(phoneNumber);
        if (result != null) {
            if (result.isEmpty()) {
                responseObject = new ResponseObject(Constants.CODE_200, "Your children list is empty");
            } else {
                responseObject = new ResponseObject(Constants.CODE_200, "OK");
            }
            responseObject.setData(result);
            return new ResponseEntity<>(responseObject, HttpStatus.OK);
        }
        responseObject = new ResponseObject(Constants.CODE_500, "Cannot get your children in the system");
        return new ResponseEntity<>(responseObject, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @RequestMapping(value = "/{phone}/payment", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Object> createParentPayment(HttpServletRequest request,
                                             @PathVariable("phone") String phoneNumber,
                                             @RequestBody ParentPaymentCreateRequestDTO parentPaymentCreateRequestDTO) {
        ResponseObject responseObject;
        Long currentTimeStamp = new Date().getTime();
        // url when parent cancel payment (paypal api redirect back)
        String cancelUrl = Util.getBaseURL(request)
                + "/parent/" + phoneNumber
                + "/payment/cancel?createdDate=" + currentTimeStamp;

        // url when parent success payment (paypal api redirect back)
        String successUrl = Util.getBaseURL(request)
                + "/parent/" + phoneNumber
                + "/payment/success?createdDate=" + currentTimeStamp
                + "&subscriptionTypeId=" + parentPaymentCreateRequestDTO.getSubscriptionTypeId();

        try {
            SubscriptionType subscriptionType = subscriptionService.findSubscriptionTypeById(parentPaymentCreateRequestDTO.getSubscriptionTypeId());

            if (subscriptionType == null) {
                responseObject = new ResponseObject(Constants.CODE_404, "Cannot found that subscription type in the system");
                return new ResponseEntity<>(responseObject, HttpStatus.NOT_FOUND);
            }

            // price in database is VND but paypal api doesn't support for VND
            // change VND to USD
            // VND currency is in application.properties;
            Double usdCurrency = subscriptionType.getPrice() / Double.parseDouble(currency);

            // parse 2 digits after digital. Because paypal api only required number has 6 digit and 2 digit after decimal
            Payment payment = parentPaymentService.createPayment(Double.parseDouble(String.format("%.2f", usdCurrency)),
                    "USD",
                    "paypal",
                    "sale",
                    parentPaymentCreateRequestDTO.getDescription(),
                    cancelUrl,
                    successUrl);
            for(Links links : payment.getLinks()){
                // paypal catch the approval of parent
                if(links.getRel().equals("approval_url")){
                    ParentPayment parentPayment = new ParentPayment();
                    parentPayment.setContent(parentPaymentCreateRequestDTO.getDescription());
                    parentPayment.setAmount(subscriptionType.getPrice());
                    parentPayment.setStatus(Constants.status.PENDING.toString());

                    Parent parent = parentService.findParentByPhoneNumber(phoneNumber);
                    if (parent == null) {
                        responseObject = new ResponseObject(Constants.CODE_404, "Cannot found your account in the system");
                        return new ResponseEntity<>(responseObject, HttpStatus.NOT_FOUND);
                    }

                    parentPayment.setLink(links.getHref());
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

    @RequestMapping(value = "/{phone}/payment/cancel", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Object> cancelParentPayment(@PathVariable("phone") String parentPhoneNumber,
                            @RequestParam(value = "createdDate") Long createdDateTimeStamp){
        ResponseObject responseObject;
        ParentPayment recentParentPayment = parentPaymentService.findParentPaymentToCompletePayment(parentPhoneNumber, createdDateTimeStamp);

        if (recentParentPayment != null) {
            recentParentPayment.setStatus(Constants.status.CANCELLED.toString());

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

    @RequestMapping(value = "/{phone}/payment/success", method = RequestMethod.GET)
    public ResponseEntity<Object> successParentPayment(@PathVariable("phone") String parentPhoneNumber,
                             @RequestParam(value = "subscriptionTypeId") Long subscriptionTypeId,
                             @RequestParam(value = "createdDate") Long createdDateTimeStamp,
                             @RequestParam(value = "paymentId") String paymentId,
                             @RequestParam(value = "PayerID") String payerId){
        ResponseObject responseObject;
        try {
            Payment payment = parentPaymentService.executePayment(paymentId, payerId);
            if(payment.getState().equals("approved")){
                SubscriptionType subscriptionType = subscriptionService.findSubscriptionTypeById(subscriptionTypeId);

                if (subscriptionType == null) {
                    responseObject = new ResponseObject(Constants.CODE_404, "Cannot found that subscription type in the system");
                    return new ResponseEntity<>(responseObject, HttpStatus.NOT_FOUND);
                }

                Parent parent = parentService.findParentByPhoneNumber(parentPhoneNumber);

                if (parent == null) {
                    responseObject = new ResponseObject(Constants.CODE_404, "Cannot found your account in the system");
                    return new ResponseEntity<>(responseObject, HttpStatus.NOT_FOUND);
                }

                ParentPayment recentParentPayment = parentPaymentService.findParentPaymentToCompletePayment(parentPhoneNumber, createdDateTimeStamp);

                if (recentParentPayment != null) {
                    recentParentPayment.setStatus(Constants.status.SUCCESS.toString());
                    recentParentPayment.setPayerId(payerId);
                    recentParentPayment.setPaymentId(paymentId);

                    ParentPaymentCompledResponseDTO result = parentPaymentService.completedSuccessParentPayment(recentParentPayment);
                    if (result != null) {
                        // refresh 30 day when parent buy a subscription
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.MONTH, 30);
                        parent.getSubscription().setExpiredDate(calendar.getTime().getTime());
                        // update subscription type
                        parent.getSubscription().setSubscriptionType(subscriptionType);

                        // update parent subscription in the system
                        if (parentService.saveParentInformationToSystem(parent) == null) {
                            responseObject = new ResponseObject(Constants.CODE_500, "Cannot updated your account in the system. Please come back later");
                            return new ResponseEntity<>(responseObject, HttpStatus.INTERNAL_SERVER_ERROR);
                        }

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

    @RequestMapping(value = "/{phone}/payment/list", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Object> getListTransactionForParent(@PathVariable("phone") String phoneNumber,
                                                              @RequestParam(required = false, value = "status") String status) {
        ResponseObject responseObject;
        List<ListPaymentTransactionResponseDTO> result;
        if (status != null) {
            if (!status.equalsIgnoreCase(Constants.status.PENDING.toString()) &&
                    !status.equalsIgnoreCase(Constants.status.SUCCESS.toString()) &&
                    !status.equalsIgnoreCase(Constants.status.FAILED.toString())) {
                responseObject = new ResponseObject(Constants.CODE_400, "Status should be success or failed or pending");
                return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
            }
            result = parentPaymentService.getParentTransaction(phoneNumber, status);
        } else {
            result = parentPaymentService.getParentTransaction(phoneNumber, null);
        }

        if (result != null) {
            if (result.isEmpty()) {
                responseObject = new ResponseObject(Constants.CODE_200, "Didn't have any payment yet");
            } else {
                responseObject = new ResponseObject(Constants.CODE_200, "OK");
            }
            responseObject.setData(result);
            return new ResponseEntity<>(responseObject, HttpStatus.OK);
        }

        responseObject = new ResponseObject(Constants.CODE_500, "Server is down cannot get list transation for parent");
        return new ResponseEntity<>(responseObject, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @RequestMapping(value = "/{phone}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<Object> disableParentAccount(@PathVariable("phone") String phoneNumber) {
        ResponseObject responseObject;

        Parent parent = parentService.findParentByPhoneNumber(phoneNumber);
        if (parent == null) {
            responseObject = new ResponseObject(Constants.CODE_404, "Cannot found that parent account in the system");
            return new ResponseEntity<>(responseObject, HttpStatus.NOT_FOUND);
        }

        DisableParentResponseDTO result = parentService.disableParentAccount(parent);
        if (result != null) {
            responseObject = new ResponseObject(Constants.CODE_200, "OK");
            responseObject.setData(result);
            return new ResponseEntity<>(responseObject, HttpStatus.OK);
        }

        responseObject = new ResponseObject(Constants.CODE_500, "Server cannot disable parent account");
        return new ResponseEntity<>(responseObject, HttpStatus.INTERNAL_SERVER_ERROR);
    }


}
