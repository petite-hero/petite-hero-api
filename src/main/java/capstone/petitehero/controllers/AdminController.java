package capstone.petitehero.controllers;

import capstone.petitehero.dtos.ResponseErrorDTO;
import capstone.petitehero.dtos.user.UserLoginDTO;
import capstone.petitehero.entities.Admin;
import capstone.petitehero.services.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Object> register(@RequestBody UserLoginDTO userLoginDTO) {
        ResponseErrorDTO responseErrorDTO;
        if (userLoginDTO.getUsername() == null) {
            responseErrorDTO = new ResponseErrorDTO(400, "Missing username in request body");
            return new ResponseEntity<>(responseErrorDTO, HttpStatus.BAD_REQUEST);
        }
        if (userLoginDTO.getPassword() == null) {
            responseErrorDTO = new ResponseErrorDTO(400, "Missing password in request body");
            return new ResponseEntity<>(responseErrorDTO, HttpStatus.BAD_REQUEST);
        }
        if (userLoginDTO.getUsername().isEmpty()) {
            responseErrorDTO = new ResponseErrorDTO(400, "Username cannot be empty");
            return new ResponseEntity<>(responseErrorDTO, HttpStatus.BAD_REQUEST);
        }
        if (userLoginDTO.getPassword().isEmpty()) {
            responseErrorDTO = new ResponseErrorDTO(400, "Password cannot be empty");
            return new ResponseEntity<>(responseErrorDTO, HttpStatus.BAD_REQUEST);
        }
        Admin admin = new Admin();
        admin.setUsername(userLoginDTO.getUsername());
        admin.setPassword(userLoginDTO.getPassword());
        Admin result = adminService.register(admin);
        if (result != null) {
            return new ResponseEntity<>(result, HttpStatus.OK);
        } else {
            responseErrorDTO = new ResponseErrorDTO(500, "Server is down cannot register");
            return new ResponseEntity<>(responseErrorDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Object> loginByUser(@RequestBody UserLoginDTO userLoginDTO) {
        ResponseErrorDTO responseErrorDTO;
        //validate mandatory fields
        if (userLoginDTO.getUsername() == null) {
            responseErrorDTO = new ResponseErrorDTO(400, "Missing username in request body");
            return new ResponseEntity<>(responseErrorDTO, HttpStatus.BAD_REQUEST);
        }
        if (userLoginDTO.getPassword() == null) {
            responseErrorDTO = new ResponseErrorDTO(400, "Missing password in request body");
            return new ResponseEntity<>(responseErrorDTO, HttpStatus.BAD_REQUEST);
        }
        if (userLoginDTO.getUsername().isEmpty()) {
            responseErrorDTO = new ResponseErrorDTO(400, "Username cannot be empty");
            return new ResponseEntity<>(responseErrorDTO, HttpStatus.BAD_REQUEST);
        }
        if (userLoginDTO.getPassword().isEmpty()) {
            responseErrorDTO = new ResponseErrorDTO(400, "Password cannot be empty");
            return new ResponseEntity<>(responseErrorDTO, HttpStatus.BAD_REQUEST);
        }
        // end validate mandatory fields

        String jwtString = adminService.loginByUser(userLoginDTO);
        if (jwtString != null) {
            return new ResponseEntity<>(jwtString, HttpStatus.OK);
        }
        responseErrorDTO = new ResponseErrorDTO(404, "Wrong username or password. Pls try again");
        return new ResponseEntity<>(responseErrorDTO, HttpStatus.NOT_FOUND);
    }
}
