package capstone.petitehero.controllers;

import capstone.petitehero.config.common.Constants;
import capstone.petitehero.dtos.ResponseObject;
import capstone.petitehero.dtos.request.parent.AccountChangePasswordRequestDTO;
import capstone.petitehero.dtos.response.account.ListParentAccountResponseDTO;
import capstone.petitehero.dtos.response.account.LoginResponseDTO;
import capstone.petitehero.dtos.request.admin.AccountLoginDTO;
import capstone.petitehero.dtos.request.parent.ParentRegisterDTO;
import capstone.petitehero.dtos.response.account.AccountLoginResponseDTO;
import capstone.petitehero.dtos.response.account.ParentDetailResponseDTO;
import capstone.petitehero.dtos.response.parent.ParentRegisterResponseDTO;
import capstone.petitehero.entities.*;
import capstone.petitehero.exceptions.DuplicateKeyException;
import capstone.petitehero.services.AccountService;
import capstone.petitehero.services.ParentService;
import capstone.petitehero.services.SubscriptionService;
import capstone.petitehero.utilities.Util;
import com.twilio.Twilio;
import com.twilio.rest.verify.v2.service.Verification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(value = "/account")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private ParentService parentService;

    @Autowired
    private SubscriptionService subscriptionService;

    @RequestMapping(value = "/admin/register", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Object> register(@RequestBody AccountLoginDTO accountLoginDTO) {
        ResponseObject responseObject;
        if (accountLoginDTO.getUsername() == null) {
            responseObject = new ResponseObject(Constants.CODE_400, "Missing username in request body");
            return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
        }
        if (accountLoginDTO.getPassword() == null) {
            responseObject = new ResponseObject(Constants.CODE_400, "Missing password in request body");
            return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
        }
        if (accountLoginDTO.getUsername().isEmpty()) {
            responseObject = new ResponseObject(Constants.CODE_400, "Username cannot be empty");
            return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
        }
        if (accountLoginDTO.getPassword().isEmpty()) {
            responseObject = new ResponseObject(Constants.CODE_400, "Password cannot be empty");
            return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
        }
//        if (!Util.validateLengthOfString(accountLoginDTO.getUsername(), 6, 30)) {
//            responseObject = new ResponseObject(Constants.CODE_400, "Username should between 6 characters to 30 characters");
//            return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
//        }
//        if (!Util.validateLengthOfString(accountLoginDTO.getPassword(), 6, 30)) {
//            responseObject = new ResponseObject(Constants.CODE_400, "Password should between 6 characters to 30 characters");
//            return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
//        }
        Account account = new Account();
        account.setUsername(accountLoginDTO.getUsername());
        account.setPassword(accountLoginDTO.getPassword());
        account.setRole(Constants.ADMIN);

        try {
            AccountLoginResponseDTO result = accountService.registerByAdmin(account);
            if (result != null) {
                responseObject = new ResponseObject(Constants.CODE_200, "OK");
                responseObject.setData(result);
                return new ResponseEntity<>(responseObject, HttpStatus.OK);
            }
            responseObject = new ResponseObject(Constants.CODE_500, "Server is down cannot register");
            return new ResponseEntity<>(responseObject, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (DuplicateKeyException duplicateKeyException) {
            responseObject = new ResponseObject(Constants.CODE_400, duplicateKeyException.getMessage());
            return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/parent/register", method = RequestMethod.POST)
    @ResponseBody
    // parent input phone number to get OTP code for verify to access (register) system.
    public ResponseEntity<Object> registerByPhoneNumber(@RequestBody ParentRegisterDTO parentRegisterDTO) {
        ResponseObject responseObject;

        // validate phone number of parent
        if (parentRegisterDTO.getPhoneNumber() == null || parentRegisterDTO.getPhoneNumber().isEmpty()) {
            responseObject = new ResponseObject(Constants.CODE_400, "Parent's phone number cannot be missing or be empty when register");
            return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
        }
        if (!Util.validatePhoneNumberParent(parentRegisterDTO.getPhoneNumber())) {
            responseObject = new ResponseObject(Constants.CODE_400, "Phone number is not in right format" +
                    "Phone number should be (1234567890) or (123( |-)456( |-)7890)");
            return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
        }
        // end validate phone number of parent

        // TODO verify phone number of parent using OTP
        // code here

        SubscriptionType subscriptionType = subscriptionService.findSubscriptionTypeById(Constants.FREE_TRAIL_TYPE);
        if (subscriptionType == null) {
            responseObject = new ResponseObject(Constants.CODE_404, "Cannot found that subscription type in the system");
            return new ResponseEntity<>(responseObject, HttpStatus.NOT_FOUND);
        }

        Parent parent = new Parent();
        // add license & policy for parent account
        parent.setIsDisabled(Boolean.FALSE);

        // create account and save parent information to account
        Account account = new Account();
        account.setUsername(parentRegisterDTO.getPhoneNumber());
        account.setRole(Constants.PARENT);
        // end create account and save parent information to account
        try {
            Account accountResult = accountService.registerByParent(account);
            if (accountResult != null) {
                // create new subscription type free trial for parent account
                Subscription subscription = new Subscription();
                subscription.setSubscriptionType(subscriptionType);

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date());
                calendar.add(Calendar.DATE, subscriptionType.getDurationDay());
                subscription.setExpiredDate(calendar.getTime().getTime());

                Subscription subscriptionResult = subscriptionService.createFreeTrialSubscriptionForParentAccount(subscription);

                if (subscriptionResult != null) {
                    parent.setSubscription(subscriptionResult);
                    parent.setAccount(accountResult);

                    ParentRegisterResponseDTO parentResult = parentService.registerByParent(parent);
                    if (parentResult != null) {
                        responseObject = new ResponseObject(Constants.CODE_200, "OK");
                        responseObject.setData(parentResult);
                        return new ResponseEntity<>(responseObject, HttpStatus.OK);
                    }
                    responseObject = new ResponseObject(Constants.CODE_500, "Server is down cannot save your account to the system");
                    return new ResponseEntity<>(responseObject, HttpStatus.INTERNAL_SERVER_ERROR);
                }
                responseObject = new ResponseObject(Constants.CODE_500, "Server is down cannot save your account to the system");
                return new ResponseEntity<>(responseObject, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (DuplicateKeyException duplicateKeyException) {
            responseObject = new ResponseObject(Constants.CODE_400, duplicateKeyException.getMessage());
            return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
        }
        responseObject = new ResponseObject(Constants.CODE_500, "Server is down cannot save your account to the system");
        return new ResponseEntity<>(responseObject, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Object> loginAccount(@RequestBody AccountLoginDTO accountLoginDTO) {
        ResponseObject responseObject;
        //validate mandatory fields
        if (accountLoginDTO.getUsername() == null) {
            responseObject = new ResponseObject(Constants.CODE_400, "Missing username in request body");
            return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
        }
        if (accountLoginDTO.getPassword() == null) {
            responseObject = new ResponseObject(Constants.CODE_400, "Missing password in request body");
            return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
        }
        if (accountLoginDTO.getUsername().isEmpty()) {
            responseObject = new ResponseObject(Constants.CODE_400, "Username cannot be empty");
            return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
        }
        if (accountLoginDTO.getPassword().isEmpty()) {
            responseObject = new ResponseObject(Constants.CODE_400, "Password cannot be empty");
            return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
        }
//        if (!Util.validateLengthOfString(accountLoginDTO.getPassword(), 6, 30)) {
//            responseObject = new ResponseObject(Constants.CODE_400, "Password should between 6 characters to 30 characters");
//            return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
//        }
        // end validate mandatory fields

        LoginResponseDTO result = accountService.loginAccount(accountLoginDTO);
        if (result != null) {
            responseObject = new ResponseObject(Constants.CODE_200, "OK");
            responseObject.setData(result);
            return new ResponseEntity<>(responseObject, HttpStatus.OK);
        }
        responseObject = new ResponseObject(Constants.CODE_404, "Wrong username or password. Pls try again");
        return new ResponseEntity<>(responseObject, HttpStatus.NOT_FOUND);
    }

    @RequestMapping(value = "/{username}/password", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<Object> changePasswordForAccount(@PathVariable("username") String username,
                                                           @RequestBody AccountChangePasswordRequestDTO accountChangePasswordRequestDTO) {
        ResponseObject responseObject;
        if (accountChangePasswordRequestDTO.getPassword() == null || accountChangePasswordRequestDTO.getPassword().isEmpty()) {
            responseObject = new ResponseObject(Constants.CODE_400, "New password cannot be null");
            return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
        }
//        if (Util.validateLengthOfString(accountChangePasswordRequestDTO.getPassword(), 6, 30)) {
//            responseObject = new ResponseObject(Constants.CODE_400, "Password should between 6 characters to 30 characters");
//            return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
//        }
        if (accountChangePasswordRequestDTO.getConfirmPassword() == null || accountChangePasswordRequestDTO.getConfirmPassword().isEmpty()) {
            responseObject = new ResponseObject(Constants.CODE_400, "Confirm password cannot be null");
            return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
        } else {
            if (!accountChangePasswordRequestDTO.getConfirmPassword().equals(accountChangePasswordRequestDTO.getPassword())) {
                responseObject = new ResponseObject(Constants.CODE_400, "Password and confirm password is not match");
                return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
            }
        }

        Parent parent = parentService.findParentByPhoneNumber(username);
        if (parent != null) {
            parent.getAccount().setPassword(accountChangePasswordRequestDTO.getPassword());

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

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Object> getListActiveParentAccountForAdmin() {
        ResponseObject responseObject;

        List<ListParentAccountResponseDTO> result = accountService.listAllParentAccountForAdmin();
//        if (result != null) {
        if (!result.isEmpty()) {
            responseObject = new ResponseObject(Constants.CODE_200, "OK");
        } else {
            responseObject = new ResponseObject(Constants.CODE_200, "List active parent account is empty in the system");
        }
        responseObject.setData(result);
        return new ResponseEntity<>(responseObject, HttpStatus.OK);
//        }

//        responseObject = new ResponseObject(Constants.CODE_500, "Cannot get all active parent account in the system");
//        return new ResponseEntity<>(responseObject, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @RequestMapping(value = "/{username}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Object> getDetailOfParentAccount(@PathVariable("username") String parentPhoneNumber) {
        ResponseObject responseObject;

        ParentDetailResponseDTO result = accountService.getDetailOfParentAccount(parentPhoneNumber);
        if (result != null) {
            responseObject = new ResponseObject(Constants.CODE_200, "OK");
            responseObject.setData(result);
            return new ResponseEntity<>(responseObject, HttpStatus.OK);
        }

        responseObject = new ResponseObject(Constants.CODE_404, "Cannot found that parent account in the system");
        return new ResponseEntity<>(responseObject, HttpStatus.NOT_FOUND);
    }

    @RequestMapping(value = "/reset-password", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Object> resetPassword(@RequestParam("username") String username) {
        ResponseObject responseObject;
        Parent parent = parentService.findParentByPhoneNumber(username);
        if (parent == null) {
            responseObject = new ResponseObject(Constants.CODE_404, "Cannot found your account in the systen");
            return new ResponseEntity<>(responseObject, HttpStatus.NOT_FOUND);
        }
        if (!username.matches("(\\d{3}(\\s|-)?\\d{3,4}(\\s|-)?\\d{3,4})")) {
            responseObject = new ResponseObject(Constants.CODE_400, "Reset password for parent account only");
            return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
        }

        responseObject = new ResponseObject(Constants.CODE_500, "Cannot not reset password for your account");
        return new ResponseEntity<>(responseObject, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
