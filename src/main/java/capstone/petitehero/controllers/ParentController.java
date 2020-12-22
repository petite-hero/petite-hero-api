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
import capstone.petitehero.dtos.response.account.ParentDetailResponseDTO;
import capstone.petitehero.dtos.response.child.AddChildResponseDTO;
import capstone.petitehero.dtos.response.collaborator.AddCollaboratorResponseDTO;
import capstone.petitehero.dtos.response.collaborator.ListCollaboratorResponseDTO;
import capstone.petitehero.dtos.response.parent.DisableParentResponseDTO;
import capstone.petitehero.dtos.response.parent.ParentProfileRegisterResponseDTO;
import capstone.petitehero.dtos.response.parent.ParentUpdateProfileResponseDTO;
import capstone.petitehero.dtos.response.parent.payment.ListPaymentTransactionResponseDTO;
import capstone.petitehero.dtos.response.parent.payment.ParentPaymentCompleteResponseDTO;
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
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

import static org.springframework.http.MediaType.*;


@RestController
@RequestMapping(value = "/parent")
@CrossOrigin(origins = "*", allowedHeaders = "*")
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
        if (parentRegisterRequestDTO.getName() == null || parentRegisterRequestDTO.getName().isEmpty()) {
            responseObject = new ResponseObject(Constants.CODE_400, "First name cannot be missing or be empty");
            return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
        }
        if (parentRegisterRequestDTO.getEmail() == null || parentRegisterRequestDTO.getEmail().isEmpty()) {
            responseObject = new ResponseObject(Constants.CODE_400, "Email cannot be missing or be empty");
            return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
        } else {
            if (!Util.validateEmail(parentRegisterRequestDTO.getEmail())) {
                responseObject = new ResponseObject(Constants.CODE_400, "Email is not valid");
                return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
            }
        }
        if (parentRegisterRequestDTO.getLanguage() == null || parentRegisterRequestDTO.getLanguage().isEmpty()) {
            responseObject = new ResponseObject(Constants.CODE_400, "Language cannot be missing or be empty");
            return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
        }
        // end validate mandatory fields

        if (parentRegisterRequestDTO.getPhoneNumber() != null && !parentRegisterRequestDTO.getPhoneNumber().isEmpty()) {
            Parent parent = parentService.findParentByPhoneNumber(parentRegisterRequestDTO.getPhoneNumber(), Boolean.FALSE);
            if (parent != null) {
                // add basic parent information
                parent.setName(parentRegisterRequestDTO.getName());
//                    parent.getAccount().setPassword(Util.encodePassword(parentRegisterRequestDTO.getPassword()));
                parent.setEmail(parentRegisterRequestDTO.getEmail());

                if (parentRegisterRequestDTO.getGender() != null) {
                    if (parentRegisterRequestDTO.getGender().equalsIgnoreCase("Male")) {
                        parent.setGender(Boolean.TRUE);
                    } else {
                        parent.setGender(Boolean.FALSE);
                    }
                }

                if (parentRegisterRequestDTO.getLanguage() != null) {
                    if (parentRegisterRequestDTO.getLanguage().equalsIgnoreCase("Vietnamese")) {
                        parent.setLanguage(Boolean.TRUE);
                    } else {
                        parent.setLanguage(Boolean.FALSE);
                    }
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

    @RequestMapping(value = "/{phone}", method = RequestMethod.PUT)
    @ResponseBody
    //updated profile for parent
    public ResponseEntity<Object> updateParentProfile(@PathVariable("phone") String parentPhoneNumber,
                                                      @ModelAttribute ParentUpdateProfileRequestDTO parentUpdateProfileRequestDTO,
                                                      @RequestParam(value = "avatar", required = false) MultipartFile parentAvatar) {
        ResponseObject responseObject;

        Parent parent = parentService.findParentByPhoneNumber(parentPhoneNumber, Boolean.FALSE);
        if (parent == null) {
            responseObject = new ResponseObject(Constants.CODE_404, "Cannot found that parent account in the system");
            return new ResponseEntity<>(responseObject, HttpStatus.NOT_FOUND);
        }

        if (parentUpdateProfileRequestDTO.getName() != null && !parentUpdateProfileRequestDTO.getName().isEmpty()) {
            parent.setName(parentUpdateProfileRequestDTO.getName());
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
        if (addChildRequestDTO.getName() == null || addChildRequestDTO.getName().isEmpty()) {
            responseObject = new ResponseObject(Constants.CODE_400, "Child's name cannot be missing or empty");
            return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
        }
        if (addChildRequestDTO.getYob() == null || addChildRequestDTO.getYob().toString().isEmpty()) {
            responseObject = new ResponseObject(Constants.CODE_400, "Child's year of birth cannot be missing or empty");
            return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
        } else {
            if (!Util.checkChildYoB(addChildRequestDTO.getYob())) {
                responseObject = new ResponseObject(Constants.CODE_400, "Child's must be in 4-11 years old");
                return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
            }
        }
        if (addChildRequestDTO.getGender() == null || addChildRequestDTO.getGender().isEmpty()) {
            responseObject = new ResponseObject(Constants.CODE_400, "Gender cannot be missing or be empty");
            return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
        }
        // end validate mandatory fields

        Parent parentAccount = parentService.findParentByPhoneNumber(parentPhoneNumber, Boolean.FALSE);
        if (parentAccount != null) {
            Subscription parentCurrentSubscription = subscriptionService.findParentCurrentSubscription(parentAccount);
            if (parentCurrentSubscription == null) {
                responseObject = new ResponseObject(Constants.CODE_404, "Cannot found parent current subscription in the system");
                return new ResponseEntity<>(responseObject, HttpStatus.NOT_FOUND);
            }

            if (Util.checkSubscriptionWhenParentAddChild(parentAccount, parentCurrentSubscription.getSubscriptionType())) {
                responseObject = new ResponseObject(Constants.CODE_400, "Your subscription only support max "
                        + parentCurrentSubscription.getSubscriptionType().getMaxChildren() +
                        " child and you already full");
                return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
            }

            Child child = new Child();
            child.setName(addChildRequestDTO.getName());
            child.setYob(addChildRequestDTO.getYob());
            if (addChildRequestDTO.getNickName() != null) {
                child.setNickName(addChildRequestDTO.getNickName());
            }

            if (addChildRequestDTO.getGender().equalsIgnoreCase("Male")) {
                child.setGender(Boolean.TRUE);
            } else {
                child.setGender(Boolean.FALSE);
            }

            if (addChildRequestDTO.getLanguage() != null && !addChildRequestDTO.getLanguage().isEmpty()) {
                if (addChildRequestDTO.getLanguage().equalsIgnoreCase("Vietnamese")) {
                    child.setLanguage(Boolean.TRUE);
                } else {
                    child.setLanguage(Boolean.FALSE);
                }
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
        if (parentPhoneNumber.equals(addCollaboratorRequestDTO.getCollaboratorPhoneNumber())) {
            responseObject = new ResponseObject(Constants.CODE_400, "You cannot collaborate with yourself");
            return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
        }
        for (Long childId : addCollaboratorRequestDTO.getListChildId()) {
            Child child = childService.findChildByChildId(childId, Boolean.FALSE);
            if (child == null) {
                responseObject = new ResponseObject(Constants.CODE_404, "Cannot found that child id: " + childId);
                return new ResponseEntity<>(responseObject, HttpStatus.NOT_FOUND);
            }
        }

        Parent parentAccount = parentService.findParentByPhoneNumber(parentPhoneNumber, Boolean.FALSE);
        if (parentAccount != null) {
            Subscription parentCurrentSubscription = subscriptionService.findParentCurrentSubscription(parentAccount);
            if (parentCurrentSubscription == null) {
                responseObject = new ResponseObject(Constants.CODE_404, "Cannot found parent current subscription in the system");
                return new ResponseEntity<>(responseObject, HttpStatus.NOT_FOUND);
            }

            if (Util.checkSubscriptionWhenParentAddCollaborator(parentAccount, parentCurrentSubscription.getSubscriptionType())) {
                responseObject = new ResponseObject(Constants.CODE_400, "Your subscription only support max "
                        + parentCurrentSubscription.getSubscriptionType().getMaxCollaborator() +
                        " collaborator and you already full");
                return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
            }

            Parent collaboratorAccount = parentService.findParentByPhoneNumber(addCollaboratorRequestDTO.getCollaboratorPhoneNumber(), Boolean.FALSE);
            if (collaboratorAccount != null) {
                AddCollaboratorResponseDTO result =
                        parentChildService.addNewCollaborator(addCollaboratorRequestDTO.getListChildId(), parentAccount, collaboratorAccount);

                if (!result.getListChildren().isEmpty()) {
                    responseObject = new ResponseObject(Constants.CODE_200, "OK");
                } else {
                    responseObject = new ResponseObject(Constants.CODE_200, "Collaborator all ready collaborate with all these child");
                }
                responseObject.setData(result);
                return new ResponseEntity<>(responseObject, HttpStatus.OK);
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
        if (addCollaboratorRequestDTO.getIsConfirm() == null || addCollaboratorRequestDTO.getIsConfirm().toString().isEmpty()) {
            responseObject = new ResponseObject(Constants.CODE_400, "Collaborator confirm is empty");
            return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
        }

        Parent collaboratorAccount = parentService.findParentByPhoneNumber(addCollaboratorRequestDTO.getCollaboratorPhoneNumber(), Boolean.FALSE);
        if (collaboratorAccount != null) {
            AddCollaboratorResponseDTO result = parentChildService.confirmByCollaborator(
                    collaboratorAccount, addCollaboratorRequestDTO.getListChildId(), addCollaboratorRequestDTO.getIsConfirm());

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

        Parent parentAccount = parentService.findParentByPhoneNumber(parentPhoneNumber, Boolean.FALSE);
        if (parentAccount != null) {
            AddCollaboratorResponseDTO result = parentChildService.deleteCollaboratorByParent(
                    addCollaboratorRequestDTO.getListChildId(), parentAccount, addCollaboratorRequestDTO.getCollaboratorPhoneNumber());

            if (result.getStatus().equalsIgnoreCase(Constants.DELETED)) {
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
    public ResponseObject updateAccountPushToken(@RequestBody UpdatePushTokenRequestDTO data) {
        return parentService.updateAccountPushToken(data);
    }

    @RequestMapping(value = "/{phone}/collaborator", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Object> getListCollaboratorOfParent(@PathVariable("phone") String phoneNumber) {
        ResponseObject responseObject;

        Parent parentAccount = parentService.findParentByPhoneNumber(phoneNumber, Boolean.FALSE);
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
            return new ResponseEntity<>(responseObject, HttpStatus.OK);
        }

        responseObject = new ResponseObject(Constants.CODE_500, "Cannot get your collaborator in the system");
        return new ResponseEntity<>(responseObject, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @RequestMapping(value = "/{phone}/children", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Object> getListChildrenOfParent(@PathVariable("phone") String phoneNumber) {
        ResponseObject responseObject;

        Parent parentAccount = parentService.findParentByPhoneNumber(phoneNumber, Boolean.FALSE);
        if (parentAccount == null) {
            responseObject = new ResponseObject(Constants.CODE_404, "Cannot found your account in the system");
            return new ResponseEntity<>(responseObject, HttpStatus.NOT_FOUND);
        }

        List<ChildInformation> result = parentChildService.getListChildOfParent(phoneNumber);
//        if (result != null) {
        if (result.isEmpty()) {
            responseObject = new ResponseObject(Constants.CODE_200, "Your children list is empty");
        } else {
            responseObject = new ResponseObject(Constants.CODE_200, "OK");
        }
        responseObject.setData(result);
        return new ResponseEntity<>(responseObject, HttpStatus.OK);
//        }
//        responseObject = new ResponseObject(Constants.CODE_500, "Cannot get your children in the system");
//        return new ResponseEntity<>(responseObject, HttpStatus.INTERNAL_SERVER_ERROR);
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

            // check parent account constraint when buying new subscription
            Parent parent = parentService.findParentByPhoneNumber(phoneNumber, Boolean.FALSE);
            if (parent == null) {
                responseObject = new ResponseObject(Constants.CODE_404, "Cannot found your account in the system");
                return new ResponseEntity<>(responseObject, HttpStatus.NOT_FOUND);
            }

            if (Util.checkSubscriptionWhenParentAddChild(parent, subscriptionType)) {
                responseObject = new ResponseObject(Constants.CODE_400, "You cannot buy this subscription because your account has been more than "
                        + subscriptionType.getMaxChildren() +
                        " child");
                return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
            }

            if (Util.checkSubscriptionWhenParentAddCollaborator(parent, subscriptionType)) {
                responseObject = new ResponseObject(Constants.CODE_400, "You cannot buy this subscription because your account has been more than "
                        + subscriptionType.getMaxCollaborator() +
                        " collaborator");
                return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
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
            for (Links links : payment.getLinks()) {
                // paypal catch the approval of parent
                if (links.getRel().equals("approval_url")) {
                    ParentPayment parentPayment = new ParentPayment();
                    parentPayment.setContent(parentPaymentCreateRequestDTO.getDescription());
                    parentPayment.setAmount(subscriptionType.getPrice());
                    parentPayment.setStatus(Constants.status.PENDING.toString());

                    parentPayment.setLink(links.getHref());
                    Subscription parentCurrentSubscription = subscriptionService.findParentCurrentSubscription(parent);

                    if (parentCurrentSubscription != null) {
                        if (parentCurrentSubscription.getSubscriptionType().getSubscriptionTypeId().longValue() ==
                                subscriptionType.getSubscriptionTypeId().longValue()) {
                            parentPayment.setSubscription(parentCurrentSubscription);

                            parentPayment.setCreateDate(currentTimeStamp);
                            ParentPayment result = parentPaymentService.insertParentPaymentToSystem(parentPayment);
                            if (result != null) {
                                responseObject = new ResponseObject(Constants.CODE_200, "OK");
                                responseObject.setData(links.getHref());
                                return new ResponseEntity<>(responseObject, HttpStatus.OK);
                            } else {
                                responseObject = new ResponseObject(Constants.CODE_500, "Cannot create your payment in the system");
                                return new ResponseEntity<>(responseObject, HttpStatus.INTERNAL_SERVER_ERROR);
                            }
                        }
                    }

                    // create new subscription but no start date and is expired and not active
                    Subscription newSubscription = new Subscription();
                    newSubscription.setParent(parent);
                    newSubscription.setSubscriptionType(subscriptionType);
                    newSubscription.setIsDisabled(Boolean.TRUE);

                    Subscription subscription = subscriptionService.saveSubscriptionForParent(newSubscription);

                    if (subscription != null) {
                        parentPayment.setSubscription(newSubscription);
                        parentPayment.setCreateDate(currentTimeStamp);
                        ParentPayment result = parentPaymentService.insertParentPaymentToSystem(parentPayment);
                        if (result != null) {
                            responseObject = new ResponseObject(Constants.CODE_200, "OK");
                            responseObject.setData(links.getHref());
                            return new ResponseEntity<>(responseObject, HttpStatus.OK);
                        } else {
                            responseObject = new ResponseObject(Constants.CODE_500, "Cannot create your payment in the system");
                            return new ResponseEntity<>(responseObject, HttpStatus.INTERNAL_SERVER_ERROR);
                        }
                    }
                    responseObject = new ResponseObject(Constants.CODE_500, "Cannot create new subscription for parent in the system");
                    return new ResponseEntity<>(responseObject, HttpStatus.INTERNAL_SERVER_ERROR);
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
    public ModelAndView cancelParentPayment(@PathVariable("phone") String parentPhoneNumber,
                                            @RequestParam(value = "createdDate") Long createdDateTimeStamp) {
        ResponseObject responseObject;
        ModelAndView mav = new ModelAndView("status");
        ParentPayment recentParentPayment = parentPaymentService.findParentPaymentToCompletePayment(parentPhoneNumber, createdDateTimeStamp);

        if (recentParentPayment != null) {
            if (recentParentPayment.getStatus().equalsIgnoreCase(Constants.status.PENDING.toString())) {
                recentParentPayment.setStatus(Constants.status.CANCELLED.toString());

                ParentPayment result = parentPaymentService.insertParentPaymentToSystem(recentParentPayment);
                if (result != null) {
                    responseObject = new ResponseObject(Constants.CODE_200, "OK");
                    responseObject.setData(result.getStatus());
                    mav.addObject("response", responseObject);
                    return mav;
                } else {
                    responseObject = new ResponseObject(Constants.CODE_500, "Cannot connect to server pls come back again");
                    mav.addObject("response", responseObject);
                    return mav;
                }
            } else {
                responseObject = new ResponseObject(Constants.CODE_200, "Payment has completed");
                mav.addObject("response", responseObject);
                return mav;
            }
        }
        responseObject = new ResponseObject(Constants.CODE_404, "Cannot found your current payment.");
        mav.addObject("response", responseObject);
        return mav;
    }

    @RequestMapping(value = "/{phone}/payment/success", method = RequestMethod.GET)
    public ModelAndView successParentPayment(@PathVariable("phone") String parentPhoneNumber,
                                             @RequestParam(value = "subscriptionTypeId") Long subscriptionTypeId,
                                             @RequestParam(value = "createdDate") Long createdDateTimeStamp,
                                             @RequestParam(value = "paymentId") String paymentId,
                                             @RequestParam(value = "PayerID") String payerId) {
        ResponseObject responseObject;
        ModelAndView mav = new ModelAndView("status");
        try {
            Payment payment = parentPaymentService.executePayment(paymentId, payerId);

            SubscriptionType subscriptionType = subscriptionService.findSubscriptionTypeById(subscriptionTypeId);

            if (subscriptionType == null) {
                responseObject = new ResponseObject(Constants.CODE_404, "Cannot found that subscription type in the system");
                mav.addObject("response", responseObject);
                return mav;
            }

            Parent parent = parentService.findParentByPhoneNumber(parentPhoneNumber, Boolean.FALSE);

            if (parent == null) {
                responseObject = new ResponseObject(Constants.CODE_404, "Cannot found your account in the system");
                mav.addObject("response", responseObject);
                return mav;
            }

            Subscription parentCurrentSubscription = subscriptionService.findParentCurrentSubscription(parent);
            if (parentCurrentSubscription == null) {
                responseObject = new ResponseObject(Constants.CODE_404, "Cannot found parent current subscription in the system");
                mav.addObject("response", responseObject);
                return mav;
            }

            ParentPayment recentParentPayment = parentPaymentService.findParentPaymentToCompletePayment(parent.getAccount().getUsername(), createdDateTimeStamp);
            if (recentParentPayment != null) {
                recentParentPayment.setStatus(Constants.status.SUCCESS.toString());
                recentParentPayment.setPayerId(payerId);
                recentParentPayment.setPayDate(new Date().getTime());
                recentParentPayment.setPaymentId(paymentId);

                if (payment.getState().equals("approved")) {
                    ParentPaymentCompleteResponseDTO paymentCompleteResponseDTO = parentPaymentService.completedSuccessParentPayment(recentParentPayment);
                    if (paymentCompleteResponseDTO != null) {
                        Parent parentSubscription =
                                subscriptionService.updateParentSubscription(parent, parentCurrentSubscription, subscriptionType);

                        if (parentSubscription != null) {
                            responseObject = new ResponseObject(Constants.CODE_200, "SUCCESS");
                            mav.addObject("response", responseObject);
                            return mav;
                        }

                        responseObject = new ResponseObject(Constants.CODE_500, "Your payment is completed but cannot update your subscription");
                    } else {
                        responseObject = new ResponseObject(Constants.CODE_500, "Cannot complete your payment");
                    }
                    mav.addObject("response", responseObject);
                    return mav;
                } else if (payment.getState().equals("failed")) {
                    ParentPaymentCompleteResponseDTO paymentCompleteResponseDTO = parentPaymentService.completedFailedParentPayment(recentParentPayment);
                    if (paymentCompleteResponseDTO != null) {
                        responseObject = new ResponseObject(Constants.CODE_500, "Your payment is failed");
                    } else {
                        responseObject = new ResponseObject(Constants.CODE_500, "Cannot complete your payment");
                    }
                    mav.addObject("response", responseObject);
                    return mav;
                } else {
                    responseObject = new ResponseObject(Constants.CODE_500, "Cannot complete your payment");
                    mav.addObject("response", responseObject);
                    return mav;
                }
            } else {
                responseObject = new ResponseObject(Constants.CODE_404, "Cannot found your current payment.");
                mav.addObject("response", responseObject);
                return mav;
            }
        } catch (PayPalRESTException e) {
            responseObject = new ResponseObject(Constants.CODE_500, "Cannot connect to paypal. Reason: " + e.getMessage());
            mav.addObject("response", responseObject);
            return mav;
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
                    !status.equalsIgnoreCase(Constants.status.FAILED.toString()) &&
                    !status.equalsIgnoreCase(Constants.status.CANCELLED.toString())) {
                responseObject = new ResponseObject(Constants.CODE_400, "Status should be success, failed, cancelled or pending");
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
    public ResponseEntity<Object> disableParentAccount(@PathVariable("phone") String phoneNumber,
                                                       @RequestParam("isDisable") Boolean isDisable) {
        ResponseObject responseObject;

        Parent parent = parentService.findParentByPhoneNumber(phoneNumber);
        if (parent == null) {
            responseObject = new ResponseObject(Constants.CODE_404, "Cannot found that parent account in the system");
            return new ResponseEntity<>(responseObject, HttpStatus.NOT_FOUND);
        }

        DisableParentResponseDTO result = parentService.disableParentAccount(parent, isDisable);
        if (result != null) {
            responseObject = new ResponseObject(Constants.CODE_200, "OK");
            responseObject.setData(result);
            return new ResponseEntity<>(responseObject, HttpStatus.OK);
        }

        responseObject = new ResponseObject(Constants.CODE_500, "Cannot disable parent account");
        return new ResponseEntity<>(responseObject, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @RequestMapping(value = "/{parentPhone}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Object> getCollaboratorInformation(@PathVariable("parentPhone") String phoneNumber,
                                                             @RequestParam(value = "collaboratorPhone") String collaboratorPhoneNumber) {
        ResponseObject responseObject;

        Parent parentAccount = parentService.findParentByPhoneNumber(phoneNumber, Boolean.FALSE);
        if (parentAccount == null) {
            responseObject = new ResponseObject(Constants.CODE_404, "Cannot found parent account in the system");
            return new ResponseEntity<>(responseObject, HttpStatus.NOT_FOUND);
        }

        Parent collaboratorAccount = parentService.findParentByPhoneNumber(collaboratorPhoneNumber, Boolean.FALSE);
        if (collaboratorAccount == null) {
            responseObject = new ResponseObject(Constants.CODE_404, "Cannot found collaborator account in the system");
            return new ResponseEntity<>(responseObject, HttpStatus.NOT_FOUND);
        }

        ParentDetailResponseDTO result = parentService.getCollaboratorDetails(parentAccount, collaboratorAccount);
        if (result != null) {
            if (!result.getChildInformationList().isEmpty()) {
                responseObject = new ResponseObject(Constants.CODE_200, "OK");
            } else {
                responseObject = new ResponseObject(Constants.CODE_200, "Collaborator doesn't collaborate with any child yet");
            }
            responseObject.setData(result);
            return new ResponseEntity<>(responseObject, HttpStatus.OK);
        }

        responseObject = new ResponseObject(Constants.CODE_500, "Cannot get collaborator information");
        return new ResponseEntity<>(responseObject, HttpStatus.OK);
    }


    @RequestMapping(value = "/reset-device", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<Object> resetParentDevice(@RequestParam("parentPhoneNumber") String parentPhoneNumber) {
        ResponseObject responseObject;

        Parent parent = parentService.findParentByPhoneNumber(parentPhoneNumber, Boolean.FALSE);
        if (parent == null) {
            responseObject = new ResponseObject(Constants.CODE_404, "Cannot found parent account in the system");
            return new ResponseEntity<>(responseObject, HttpStatus.NOT_FOUND);
        }

        DisableParentResponseDTO result = parentService.resetParentDevice(parent);
        if (result != null) {
            responseObject = new ResponseObject(Constants.CODE_200, "OK");
            responseObject.setData(result);
            return new ResponseEntity<>(responseObject, HttpStatus.OK);
        }

        responseObject = new ResponseObject(Constants.CODE_500, "Cannot disable parent device in the system");
        return new ResponseEntity<>(responseObject, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
