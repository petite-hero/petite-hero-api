package capstone.petitehero.controllers;

import capstone.petitehero.services.ChildService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChildController {

    @Autowired
    private ChildService childService;
}
