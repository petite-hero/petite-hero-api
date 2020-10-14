package capstone.petitehero.controllers;

import capstone.petitehero.config.common.Constants;
import capstone.petitehero.dtos.ResponseObject;
import capstone.petitehero.dtos.common.JWTString;
import capstone.petitehero.dtos.request.admin.AccountLoginDTO;
import capstone.petitehero.dtos.request.parent.ParentRegisterDTO;
import capstone.petitehero.dtos.response.account.AccountLoginResponseDTO;
import capstone.petitehero.dtos.response.parent.ParentRegisterResponseDTO;
import capstone.petitehero.entities.Account;
import capstone.petitehero.entities.Parent;
import capstone.petitehero.exceptions.DuplicateKeyException;
import capstone.petitehero.services.AccountService;
import capstone.petitehero.services.ParentService;
import capstone.petitehero.utilities.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

import java.util.Calendar;

@RestController
@RequestMapping(value = "/account")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private ParentService parentService;

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
        account.setPassword(accountLoginDTO.getPassword());
        account.setRole("Admin");

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
        Util util = new Util();

        // validate phone number of parent
        if (parentRegisterDTO.getPhoneNumber() == null || parentRegisterDTO.getPhoneNumber().isEmpty()) {
            responseObject = new ResponseObject(Constants.CODE_400, "Parent's phone number cannot be missing or be empty when register");
            return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
        }
        if (!util.validatePhoneNumberParent(parentRegisterDTO.getPhoneNumber())) {
            responseObject = new ResponseObject(Constants.CODE_400, "Phone number is not in right format" +
                    "Phone number should be (1234567890) or (123( |-)456( |-)7890)");
            return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
        }
        // end validate phone number of parent

        // TODO verify phone number of parent using OTP
        // code here
        Parent parent = new Parent();

        // add license & policy for parent account
        parent.setIsFreeTrial(Boolean.TRUE);
        parent.setMaxChildren(3);
        parent.setMaxParent(1);
        parent.setIsDisable(Boolean.FALSE);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 30);
        parent.setExpiredDate(calendar.getTime()); // trial 1 month

        // create account and save parent information to account
        Account account = new Account();
        account.setUsername(parentRegisterDTO.getPhoneNumber());
        account.setRole("Parent");
        // end create account and save parent information to account

        try {
            Account accountResult = accountService.registerByParent(account);
            if (accountResult != null) {
                parent.setAccount(accountResult);
                ParentRegisterResponseDTO parentResult = parentService.registerByParent(parent);
                if (parentResult != null) {
                    responseObject = new ResponseObject(Constants.CODE_200, "OK");
                    responseObject.setData(parentResult);
                    return new ResponseEntity<>(responseObject, HttpStatus.OK);
                }
            }
            responseObject = new ResponseObject(Constants.CODE_500, "Server is down cannot save your account to the system");
            return new ResponseEntity<>(responseObject, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (DuplicateKeyException duplicateKeyException) {
            responseObject = new ResponseObject(Constants.CODE_400, duplicateKeyException.getMessage());
            return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Object> loginByAccountAdmin(@RequestBody AccountLoginDTO accountLoginDTO) {
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

        String jwtString = accountService.loginAccount(accountLoginDTO);
        if (jwtString != null) {
            JWTString result = new JWTString();
            result.setJwt(jwtString);
            responseObject = new ResponseObject(Constants.CODE_200, "OK");
            responseObject.setData(result);
            return new ResponseEntity<>(responseObject, HttpStatus.OK);
        }
        responseObject = new ResponseObject(Constants.CODE_404, "Wrong username or password. Pls try again");
        return new ResponseEntity<>(responseObject, HttpStatus.NOT_FOUND);
    }
}
