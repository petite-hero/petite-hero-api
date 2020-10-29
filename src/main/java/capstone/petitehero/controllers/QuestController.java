package capstone.petitehero.controllers;

import capstone.petitehero.config.common.Constants;
import capstone.petitehero.dtos.ResponseObject;
import capstone.petitehero.dtos.response.quest.ListQuestResponseDTO;
import capstone.petitehero.dtos.response.quest.QuestDeleteResponseDTO;
import capstone.petitehero.dtos.response.quest.QuestDetailResponseDTO;
import capstone.petitehero.dtos.response.quest.badge.QuestBadgeResponseDTO;
import capstone.petitehero.services.QuestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/quest")
public class QuestController {

    @Autowired
    private QuestService questService;

    @RequestMapping(value = "/{questId}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Object> getDetailOfQuestById(@PathVariable("questId") Long questId) {
        ResponseObject responseObject;

        QuestDetailResponseDTO result = questService.getDetailOfQuest(questId);

        if (result != null) {
            responseObject = new ResponseObject(200, "OK");
            responseObject.setData(result);
            return new ResponseEntity<>(responseObject, HttpStatus.OK);
        }

        responseObject = new ResponseObject(404, "Cannot find that quest by that id");
        return new ResponseEntity<>(responseObject, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @RequestMapping(value = "/{questId}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<Object> deleteQuestByQuestId(@PathVariable("questId") Long questId) {
        ResponseObject responseObject;

        QuestDeleteResponseDTO result = questService.deleteQuest(questId);

        if (result != null) {
            responseObject = new ResponseObject(200, "OK");
            responseObject.setData(result);
            return new ResponseEntity<>(responseObject, HttpStatus.OK);
        }

        responseObject = new ResponseObject(404, "Cannot find that quest by that id");
        return new ResponseEntity<>(responseObject, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @RequestMapping(value = "/list/{childId}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Object> getChildListOfQuestByChildId(@PathVariable("childId") Long childId,
                                                               @RequestParam(value = "status", required = false) String status) {
        ResponseObject responseObject;
        List<ListQuestResponseDTO> result;

        if (status != null ) {
            result = questService.getChildListOfQuest(childId, status);
        } else {
            result = questService.getChildListOfQuest(childId, null);
        }

        if (result != null) {
            if (result.isEmpty()) {
                responseObject = new ResponseObject(Constants.CODE_200, "Child's list of quest is empty");
            } else {
                responseObject = new ResponseObject(Constants.CODE_200, "OK");
            }

            responseObject.setData(result);
            return new ResponseEntity<>(responseObject, HttpStatus.OK);
        }
        responseObject = new ResponseObject(500, "Server is down pls come back again");
        return new ResponseEntity<>(responseObject, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @RequestMapping(value = "/list/{childId}/badges", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Object> getListBadgeChildArchived(@PathVariable("childId") Long childId) {
        ResponseObject responseObject;

        List<QuestBadgeResponseDTO> listQuestBadgeChildArchived = questService.getBadgeListChildArchived(childId);

        if (listQuestBadgeChildArchived != null) {
            responseObject = new ResponseObject(Constants.CODE_200, "OK");
            responseObject.setData(listQuestBadgeChildArchived);
            return new ResponseEntity<>(responseObject, HttpStatus.OK);
        }

        responseObject = new ResponseObject(500, "Server is down pls come back again");
        return new ResponseEntity<>(responseObject, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
