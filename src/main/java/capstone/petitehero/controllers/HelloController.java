package capstone.petitehero.controllers;

import capstone.petitehero.config.jwt.PetiteHeroUserDetailService;
import capstone.petitehero.utilities.JWTUtil;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

    @RequestMapping(method = RequestMethod.GET, value = "/token")
    public String getToken(@RequestParam("username") String username){
        if (username.isEmpty()) {
            return "dasdasdas";
        } else {
            try {
                //username = test, password = test to get token
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(username, username)
                );
                JWTUtil jwtUtil = new JWTUtil();

                UserDetails userDetails = petiteHeroUserDetailService.loadUserByUsername(username);

                return jwtUtil.generateToken(userDetails);
            } catch (Exception e) {
                e.printStackTrace();
                return "Exception";
            }
        }
    }
}
