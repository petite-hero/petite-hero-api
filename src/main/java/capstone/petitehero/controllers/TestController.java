package capstone.petitehero.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @RequestMapping(value = "welcome", method = RequestMethod.GET)
    public static String welcomeAPI() {
        return "You have started Petite Hero server successfully.";
    }
}
