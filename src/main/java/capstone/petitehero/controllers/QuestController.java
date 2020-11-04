package capstone.petitehero.controllers;

import capstone.petitehero.config.common.Constants;
import capstone.petitehero.dtos.ResponseObject;
import capstone.petitehero.dtos.response.quest.ListQuestResponseDTO;
import capstone.petitehero.dtos.response.quest.QuestStatusResponseDTO;
import capstone.petitehero.dtos.response.quest.QuestDetailResponseDTO;
import capstone.petitehero.dtos.response.quest.badge.QuestBadgeResponseDTO;
import capstone.petitehero.entities.Quest;
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
    public ResponseEntity<Object> getDetailOfQuestById(@PathVariable("questId") Long questId,
                                                       @RequestParam(value = "role", required = false, defaultValue = Constants.PARENT) String role) {
        ResponseObject responseObject;

        QuestDetailResponseDTO result = questService.getDetailOfQuest(questId, role);

        if (result != null) {
            responseObject = new ResponseObject(Constants.CODE_200, "OK");
            responseObject.setData(result);
            return new ResponseEntity<>(responseObject, HttpStatus.OK);
        }

        responseObject = new ResponseObject(Constants.CODE_404, "Cannot find that quest by that id");
        return new ResponseEntity<>(responseObject, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @RequestMapping(value = "/{questId}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<Object> deleteQuestByQuestId(@PathVariable("questId") Long questId) {
        ResponseObject responseObject;
        
        Quest quest = questService.findQuestById(questId);
        if (quest == null) {
            responseObject = new ResponseObject(Constants.CODE_404, "Cannot find that quest by that id");
            return new ResponseEntity<>(responseObject, HttpStatus.NOT_FOUND);
        }

        QuestStatusResponseDTO result = questService.deleteQuest(quest);

        if (result != null) {
            responseObject = new ResponseObject(Constants.CODE_200, "OK");
            responseObject.setData(result);
            return new ResponseEntity<>(responseObject, HttpStatus.OK);
        }

        responseObject = new ResponseObject(Constants.CODE_500, "Cannot delete quest in the system");
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
        responseObject = new ResponseObject(Constants.CODE_500, "Server is down pls come back again");
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

        responseObject = new ResponseObject(Constants.CODE_500, "Server is down pls come back again");
        return new ResponseEntity<>(responseObject, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @RequestMapping(value = "/{questId}", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<Object> updateStatusOfQuestByParent(@PathVariable("questId") Long questId,
                                                              @RequestParam("status") Boolean isSuccess) {
        ResponseObject responseObject;

        if (isSuccess == null) {
            responseObject = new ResponseObject(Constants.CODE_400, "Quest's status update should not be missing");
            return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
        }

        Quest quest = questService.findQuestById(questId);
        if (quest == null) {
            responseObject = new ResponseObject(Constants.CODE_404, "Cannot find that quest by that id");
            return new ResponseEntity<>(responseObject, HttpStatus.NOT_FOUND);
        }

        QuestStatusResponseDTO result = questService.updateStatusOfQuest(quest, isSuccess);
        if (result != null) {
            responseObject = new ResponseObject(Constants.CODE_200, "OK");
            responseObject.setData(result);
            return new ResponseEntity<>(responseObject, HttpStatus.OK);
        }

        responseObject = new ResponseObject(Constants.CODE_500, "Cannot update status of quest");
        return new ResponseEntity<>(responseObject, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
