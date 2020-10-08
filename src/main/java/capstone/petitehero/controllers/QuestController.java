package capstone.petitehero.controllers;

import capstone.petitehero.services.QuestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class QuestController {

    @Autowired
    private QuestService questService;
}
