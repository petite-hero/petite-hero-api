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
import com.authy.AuthyApiClient;
import com.authy.AuthyException;
import com.authy.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(value = "/account")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private ParentService parentService;

    @Autowired
    private SubscriptionService subscriptionService;

    @Value("${twilio.authy.key}")
    private String AUTHY_KEY;

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
        Account account = new Account();
        account.setUsername(accountLoginDTO.getUsername());
        account.setRole(Constants.ADMIN);

        try {
            account.setPassword(Util.encodePassword(accountLoginDTO.getPassword()));
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
        } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
            responseObject = new ResponseObject(Constants.CODE_500,
                    "Has something wrong in encoded password. Reason: "
                            + noSuchAlgorithmException.getMessage());
            return new ResponseEntity<>(responseObject, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (InvalidKeySpecException invalidKeySpecException) {
            responseObject = new ResponseObject(Constants.CODE_500,
                    "Has something wrong in encoded password. Reason: "
                            + invalidKeySpecException.getMessage());
            return new ResponseEntity<>(responseObject, HttpStatus.INTERNAL_SERVER_ERROR);
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
        if (parentRegisterDTO.getPassword() == null || parentRegisterDTO.getPassword().isEmpty()) {
            responseObject = new ResponseObject(Constants.CODE_400, "Parent's password cannot be missing or be empty when register");
            return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
        }
        // end validate phone number of parent

        SubscriptionType subscriptionType = subscriptionService.findSubscriptionTypeById(Constants.FREE_TRAIL_TYPE);
        if (subscriptionType == null) {
            responseObject = new ResponseObject(Constants.CODE_404, "Cannot found that subscription type in the system");
            return new ResponseEntity<>(responseObject, HttpStatus.NOT_FOUND);
        }

        Parent parent = new Parent();
        // add license & policy for parent account
        parent.setIsDisabled(Boolean.FALSE);

        // create account and save parent information to account
        // end create account and save parent information to account
        try {
            Account account = new Account();
            account.setPassword(Util.encodePassword(parentRegisterDTO.getPassword()));
            account.setUsername(parentRegisterDTO.getPhoneNumber());
            account.setRole(Constants.PARENT);

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
                    // create user for authy application for sending otp
                    AuthyApiClient authyApiClient = new AuthyApiClient(AUTHY_KEY);
                    Users users = authyApiClient.getUsers();
                    User user;

                    try {
                        user = users.createUser(
                                "petite-hero-clone@gmail.com",
                                parentRegisterDTO.getPhoneNumber(),
                                "84");
                        if (user == null) {
                            responseObject = new ResponseObject(Constants.CODE_500, "Cannot create authy account for you. " +
                                    "Please contact with petite hero supporter");
                            return new ResponseEntity<>(responseObject, HttpStatus.INTERNAL_SERVER_ERROR);
                        }
                    } catch (AuthyException authyException) {
                        responseObject = new ResponseObject(Constants.CODE_400,
                                "Cannot create your account because of " + authyException.getMessage());
                        return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
                    }

                    parent.setAuthyId(user.getId());
                    parent.setSubscription(subscriptionResult);
                    parent.setAccount(accountResult);

                    ParentRegisterResponseDTO parentResult = parentService.registerByParent(parent);
                    if (parentResult != null) {
                        try {
                            Hash response = users.requestSms(user.getId());
                            if (response.isOk()) {
                                responseObject = new ResponseObject(Constants.CODE_200, "An OTP token has sent to your phone number");
                                responseObject.setData(parentResult);
                                return new ResponseEntity<>(responseObject, HttpStatus.OK);
                            } else {
                                responseObject = new ResponseObject(Constants.CODE_200,
                                        "Create account success but has problem when sending OTP. " +
                                                "Please connect with petite-hero supporter");
                                responseObject.setData(parentResult);
                                return new ResponseEntity<>(responseObject, HttpStatus.OK);
                            }
                        } catch (AuthyException authyException) {
                            responseObject = new ResponseObject(Constants.CODE_500,
                                    "Reason: " + authyException.getMessage() +
                                            "Please connect with petite-hero supporter.");
                            return new ResponseEntity<>(responseObject, HttpStatus.INTERNAL_SERVER_ERROR);
                        }
                    }
                    try {
                        Hash response = users.deleteUser(user.getId());
                        if (response.isOk()) {
                            responseObject = new ResponseObject(Constants.CODE_200,
                                    "Cannot create petite hero account for you and we have" +
                                            "delete authy account successfully because petite hero cannot save your account in the system");
                            return new ResponseEntity<>(responseObject, HttpStatus.OK);
                        } else {
                            responseObject = new ResponseObject(Constants.CODE_200,
                                    "Cannot create petite hero account for you and we have" +
                                            "deleted authy account failed contact with petite hero supporter to delete authy account for you");
                            return new ResponseEntity<>(responseObject, HttpStatus.OK);
                        }
                    } catch (AuthyException authyException) {
                        responseObject = new ResponseObject(Constants.CODE_500, "Cannot save your account to the system");
                        return new ResponseEntity<>(responseObject, HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                }
                responseObject = new ResponseObject(Constants.CODE_500, "Cannot save your account to the system");
                return new ResponseEntity<>(responseObject, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (DuplicateKeyException duplicateKeyException) {
            responseObject = new ResponseObject(Constants.CODE_400, duplicateKeyException.getMessage());
            return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
        } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
            responseObject = new ResponseObject(Constants.CODE_500,
                    "Has something wrong in encoded password. Reason: "
                            + noSuchAlgorithmException.getMessage());
            return new ResponseEntity<>(responseObject, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (InvalidKeySpecException invalidKeySpecException) {
            responseObject = new ResponseObject(Constants.CODE_500,
                    "Has something wrong in encoded password. Reason: "
                            + invalidKeySpecException.getMessage());
            return new ResponseEntity<>(responseObject, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        responseObject = new ResponseObject(Constants.CODE_500, "Cannot save your account to the system");
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
        // end validate mandatory fields
        try {
            accountLoginDTO.setPassword(Util.encodePassword(accountLoginDTO.getPassword()));

            LoginResponseDTO result = accountService.loginAccount(accountLoginDTO);
            if (result != null) {
                responseObject = new ResponseObject(Constants.CODE_200, "OK");
                responseObject.setData(result);
                return new ResponseEntity<>(responseObject, HttpStatus.OK);
            }
        } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
            responseObject = new ResponseObject(Constants.CODE_500,
                    "Has something wrong in encoded password. Reason: "
                            + noSuchAlgorithmException.getMessage());
            return new ResponseEntity<>(responseObject, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (InvalidKeySpecException invalidKeySpecException) {
            responseObject = new ResponseObject(Constants.CODE_500,
                    "Has something wrong in encoded password. Reason: "
                            + invalidKeySpecException.getMessage());
            return new ResponseEntity<>(responseObject, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        responseObject = new ResponseObject(Constants.CODE_404, "Wrong username or password. Please try again");
        return new ResponseEntity<>(responseObject, HttpStatus.NOT_FOUND);
    }

    @RequestMapping(value = "/{username}/password", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<Object> changePasswordForAccount(@PathVariable("username") String username,
                                                           @RequestBody AccountChangePasswordRequestDTO accountChangePasswordRequestDTO) {
        ResponseObject responseObject;
        if (accountChangePasswordRequestDTO.getOldPassword() == null || accountChangePasswordRequestDTO.getOldPassword().isEmpty()) {
            responseObject = new ResponseObject(Constants.CODE_400, "Old password cannot be null");
            return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
        }
        if (accountChangePasswordRequestDTO.getPassword() == null || accountChangePasswordRequestDTO.getPassword().isEmpty()) {
            responseObject = new ResponseObject(Constants.CODE_400, "New password cannot be null");
            return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
        }
        if (accountChangePasswordRequestDTO.getConfirmPassword() == null || accountChangePasswordRequestDTO.getConfirmPassword().isEmpty()) {
            responseObject = new ResponseObject(Constants.CODE_400, "Confirm password cannot be null");
            return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
        } else {
            if (!accountChangePasswordRequestDTO.getConfirmPassword().equals(accountChangePasswordRequestDTO.getPassword())) {
                responseObject = new ResponseObject(Constants.CODE_400, "Password and confirm password is not match");
                return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
            }
        }

        Account account = accountService.findAccountByUsername(username);
        if (account != null) {
            try {
                String encodeNewPwd = Util.encodePassword(accountChangePasswordRequestDTO.getPassword());
                String encodeOldPwd = Util.encodePassword(accountChangePasswordRequestDTO.getOldPassword());
                if (!encodeOldPwd.equals(account.getPassword())) {
                    responseObject = new ResponseObject(Constants.CODE_400, "Your old password is not match. Please check again");
                    return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
                }
                account.setPassword(encodeNewPwd);

                String result = accountService.changeAccountPassword(account);
                if (result != null) {
                    responseObject = new ResponseObject(Constants.CODE_200, "OK");
                    responseObject.setData(result);
                    return new ResponseEntity<>(responseObject, HttpStatus.OK);
                }
            } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
                responseObject = new ResponseObject(Constants.CODE_500,
                        "Has something wrong in encoded password. Reason: "
                                + noSuchAlgorithmException.getMessage());
                return new ResponseEntity<>(responseObject, HttpStatus.INTERNAL_SERVER_ERROR);
            } catch (InvalidKeySpecException invalidKeySpecException) {
                responseObject = new ResponseObject(Constants.CODE_500,
                        "Has something wrong in encoded password. Reason: "
                                + invalidKeySpecException.getMessage());
                return new ResponseEntity<>(responseObject, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            responseObject = new ResponseObject(Constants.CODE_404, "Cannot found your account in the system");
            return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
        }
        responseObject = new ResponseObject(Constants.CODE_500, "Cannot change password for your account");
        return new ResponseEntity<>(responseObject, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Object> getListParentAccountForAdmin() {
        ResponseObject responseObject;
        List<ListParentAccountResponseDTO> result = accountService.listAllParentAccountForAdmin();

        if (!result.isEmpty()) {
            responseObject = new ResponseObject(Constants.CODE_200, "OK");
        } else {
            responseObject = new ResponseObject(Constants.CODE_200, "List active parent account is empty in the system");
        }

        responseObject.setData(result);
        return new ResponseEntity<>(responseObject, HttpStatus.OK);
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

    @RequestMapping(value = "/send-otp", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Object> sendOTP(@RequestParam("username") String username) {
        ResponseObject responseObject;
        Parent parent = parentService.findParentByPhoneNumber(username, Boolean.FALSE);
        if (parent == null) {
            responseObject = new ResponseObject(Constants.CODE_404, "Cannot found your account in the systen");
            return new ResponseEntity<>(responseObject, HttpStatus.NOT_FOUND);
        }
        if (!username.matches("(\\d{3}(\\s|-)?\\d{3,4}(\\s|-)?\\d{3,4})")) {
            responseObject = new ResponseObject(Constants.CODE_400, "Reset password for parent account only");
            return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
        }

        AuthyApiClient authyApiClient = new AuthyApiClient(AUTHY_KEY);
        Users users = authyApiClient.getUsers();

        if (parent.getAuthyId() != null && !parent.getAuthyId().toString().isEmpty()) {
//        // send the otp code to user base on authy id which is store in db
            try {
                Hash response = users.requestSms(parent.getAuthyId());
                if (response.isOk()) {
                    responseObject = new ResponseObject(Constants.CODE_200,
                            "An OTP token has sent to your phone number");
                    return new ResponseEntity<>(responseObject, HttpStatus.OK);
                } else {
                    responseObject = new ResponseObject(Constants.CODE_200,
                            "Has problem when sending OTP to your phone number. " +
                                    "Please connect with petite-hero supporter");
                    return new ResponseEntity<>(responseObject, HttpStatus.OK);
                }
            } catch (AuthyException authyException) {
                responseObject = new ResponseObject(Constants.CODE_500,
                        "Reason: " + authyException.getMessage() +
                                "Please connect with petite-hero supporter.");
                return new ResponseEntity<>(responseObject, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        responseObject = new ResponseObject(Constants.CODE_500, "Cannot reset password for your account");
        return new ResponseEntity<>(responseObject, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @RequestMapping(value = "verify-otp", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Object> verifyOTPToken(@RequestParam("username") String username,
                                                 @RequestParam("token") Integer token) {
        ResponseObject responseObject;

        Parent parent = parentService.findParentByPhoneNumber(username, Boolean.FALSE);
        if (parent == null) {
            responseObject = new ResponseObject(Constants.CODE_404, "Cannot found your account in the systen");
            return new ResponseEntity<>(responseObject, HttpStatus.NOT_FOUND);
        }

        AuthyApiClient authyApiClient = new AuthyApiClient(AUTHY_KEY);

        if (parent.getAuthyId() != null && !parent.getAuthyId().toString().isEmpty()) {
            Tokens tokens = authyApiClient.getTokens();
            try {
                Token response = tokens.verify(parent.getAuthyId(), token.toString());

                if (response.isOk()) {
                    parent.setIsVerify(Boolean.TRUE);

                    ParentRegisterResponseDTO result = parentService.saveParentInformationToSystem(parent);
                    if (result != null) {
                        responseObject = new ResponseObject(Constants.CODE_200,
                                "Verify OTP for your phone number successfully.");
                        return new ResponseEntity<>(responseObject, HttpStatus.OK);
                    }
                    responseObject = new ResponseObject(Constants.CODE_200,
                            "Has problem when verifying OTP for your phone number. " +
                                    "Please connect with petite-hero supporter");
                    return new ResponseEntity<>(responseObject, HttpStatus.OK);
                } else {
                    responseObject = new ResponseObject(Constants.CODE_200,
                            "Has problem when verifying OTP for your phone number. " +
                                    "Please connect with petite-hero supporter");
                    return new ResponseEntity<>(responseObject, HttpStatus.OK);
                }
            } catch (AuthyException authyException) {
                responseObject = new ResponseObject(Constants.CODE_500,
                        "Reason: " + authyException.getMessage() +
                                "Please connect with petite-hero supporter.");
                return new ResponseEntity<>(responseObject, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        responseObject = new ResponseObject(Constants.CODE_500, "Cannot verify otp token for your account");
        return new ResponseEntity<>(responseObject, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
