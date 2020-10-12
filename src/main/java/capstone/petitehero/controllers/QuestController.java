package capstone.petitehero.controllers;

import capstone.petitehero.dtos.ResponseErrorDTO;
import capstone.petitehero.dtos.ResponseSuccessDTO;
import capstone.petitehero.dtos.response.quest.ListQuestResponseDTO;
import capstone.petitehero.dtos.response.quest.QuestDeleteResponseDTO;
import capstone.petitehero.dtos.response.quest.QuestDetailResponseDTO;
import capstone.petitehero.dtos.response.task.ListTaskResponseDTO;
import capstone.petitehero.services.QuestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/quest")
public class QuestController {

    @Autowired
    private QuestService questService;

    @RequestMapping(value = "/{questId}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Object> getDetailOfQuestById(@PathVariable("questId") Long questId) {
        ResponseErrorDTO responseErrorDTO;

        QuestDetailResponseDTO result = questService.getDetailOfQuest(questId);

        if (result != null) {
            List<Object> listData = new ArrayList<>();
            listData.add(result);
            ResponseSuccessDTO responseSuccessDTO = new ResponseSuccessDTO(200, "OK", listData);
            return new ResponseEntity<>(responseSuccessDTO, HttpStatus.OK);
        }

        responseErrorDTO = new ResponseErrorDTO(404, "Cannot find that quest by that id");
        return new ResponseEntity<>(responseErrorDTO, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @RequestMapping(value = "/{questId}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<Object> deleteQuestByQuestId(@PathVariable("questId") Long questId) {
        ResponseErrorDTO responseErrorDTO;

        QuestDeleteResponseDTO result = questService.deleteQuest(questId);

        if (result != null) {
            List<Object> listData = new ArrayList<>();
            listData.add(result);
            ResponseSuccessDTO responseSuccessDTO = new ResponseSuccessDTO(200, "OK", listData);
            return new ResponseEntity<>(responseSuccessDTO, HttpStatus.OK);
        }

        responseErrorDTO = new ResponseErrorDTO(404, "Cannot find that quest by that id");
        return new ResponseEntity<>(responseErrorDTO, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @RequestMapping(value = "/list/{childId}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Object> getChildListOfQuestByChildId(@PathVariable("childId") Long childId,
                                                               @RequestParam(value = "status", required = false) String status) {
        ResponseErrorDTO responseErrorDTO;

        List<ListQuestResponseDTO> listQuestOfChild;

        if (status != null ) {
            listQuestOfChild = questService.getChildListOfQuest(childId, status);
        } else {
            listQuestOfChild = questService.getChildListOfQuest(childId, null);
        }

        if (listQuestOfChild != null) {
            ResponseSuccessDTO responseSuccessDTO;

            List<Object> result = new ArrayList<>();
            for (ListQuestResponseDTO listData: listQuestOfChild) {
                result.add(listData);
            }

            if (listQuestOfChild.isEmpty()) {
                responseSuccessDTO = new ResponseSuccessDTO(200, "Child's list of quest is empty", result);
                return new ResponseEntity<>(responseSuccessDTO, HttpStatus.OK);
            }

            responseSuccessDTO = new ResponseSuccessDTO(200, "OK", result);
            return new ResponseEntity<>(responseSuccessDTO, HttpStatus.OK);
        }
        responseErrorDTO = new ResponseErrorDTO(500, "Server is down pls come back again");
        return new ResponseEntity<>(responseErrorDTO, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
