package capstone.petitehero.controllers;

import capstone.petitehero.config.jwt.PetiteHeroUserDetailService;
import capstone.petitehero.dtos.ResponseSuccessDTO;
import capstone.petitehero.entities.Admin;
import capstone.petitehero.utilities.JWTUtil;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping(value = "test")
public class HelloController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PetiteHeroUserDetailService petiteHeroUserDetailService;

    @Getter
    @Setter
    private class entityTest{
        private String id;
        private String name;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/hello")
    public entityTest getHelloWorld() {
        entityTest test = new entityTest();
        test.setId("01");
        test.setName("Hello World");
        return test;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/add")
    public entityTest addEntityTest() {
        entityTest test = new entityTest();
        test.setId("01");
        test.setName("Hello World");
        return test;
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/edit")
    public entityTest editEntityTest() {
        entityTest test = new entityTest();
        test.setId("01");
        test.setName("Hello World");
        return test;
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/delete")
    public entityTest deleteEntityTest() {
        entityTest test = new entityTest();
        test.setId("01");
        test.setName("Hello World");
        return test;
    }
}
