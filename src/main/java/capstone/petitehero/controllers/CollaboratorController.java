package capstone.petitehero.controllers;

import capstone.petitehero.services.ParentChildService;
import capstone.petitehero.services.ParentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(value = "/collaborator")
@RestController
public class CollaboratorController {

    @Autowired
    private ParentService parentService;

    @Autowired
    private ParentChildService parentChildService;


}
