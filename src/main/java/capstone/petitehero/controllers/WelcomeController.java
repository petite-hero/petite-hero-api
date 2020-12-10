package capstone.petitehero.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class WelcomeController {

    @RequestMapping(value = "welcome", method = RequestMethod.GET)
    public String welcomeAPI() {
        return "You have started Petite Hero server successfully.";
    }

    @RequestMapping(value = "welcome", method = RequestMethod.POST)
    public String sadasdasdas() {
//        try {
//            String command = String.format("mysqldump -u%s -p%s --add-drop-table --databases %s -r %s",
//                    "leduong", "duonglam", "petite_hero", "outputFile.sql");
//            Process process = Runtime.getRuntime().exec(command);
//            int processComplete = process.waitFor();
//        } catch (IOException ioException) {
//            System.out.println("eee: " + ioException.getMessage());
//        } catch (InterruptedException interruptedException) {
//            System.out.println("aaaaa: " + interruptedException.getMessage());
//        }
        return "aAAa";
    }
}
