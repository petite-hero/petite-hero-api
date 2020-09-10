package capstone.petitehero.controllers;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "test")
public class HelloController {

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
}
