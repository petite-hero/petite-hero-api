package capstone.petitehero.controllers;

import capstone.petitehero.config.common.Constants;
import capstone.petitehero.dtos.ResponseObject;
import capstone.petitehero.dtos.request.subscription.type.CreateSubscriptionTypeRequestDTO;
import capstone.petitehero.dtos.response.subscription.type.SubscriptionTypeStatusResponseDTO;
import capstone.petitehero.dtos.response.subscription.type.SubscriptionTypeDetailResponseDTO;
import capstone.petitehero.entities.SubscriptionType;
import capstone.petitehero.services.SubscriptionService;
import capstone.petitehero.utilities.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/subscription")
public class SubscriptionController {

    @Autowired
    private SubscriptionService subscriptionService;

    @RequestMapping(value = "/type", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Object> createNewSubscriptionType(@RequestBody CreateSubscriptionTypeRequestDTO createSubscriptionTypeRequestDTO) {
        ResponseObject responseObject;
        if (createSubscriptionTypeRequestDTO.getName() == null || createSubscriptionTypeRequestDTO.getName().isEmpty()) {
            responseObject = new ResponseObject(Constants.CODE_400, "Subscription type name cannot be missing or be empty");
            return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
        }
        if (createSubscriptionTypeRequestDTO.getDescription() == null || createSubscriptionTypeRequestDTO.getDescription().isEmpty()) {
            responseObject = new ResponseObject(Constants.CODE_400, "Subscription type description cannot be missing or be empty");
            return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
        }
        if (createSubscriptionTypeRequestDTO.getMaxChildren() == null || createSubscriptionTypeRequestDTO.getMaxChildren().toString().isEmpty()) {
            responseObject = new ResponseObject(Constants.CODE_400, "Subscription type max children value cannot be missing or be empty");
            return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
        } else {
            if (!Util.validateLongNumber(createSubscriptionTypeRequestDTO.getMaxChildren().toString())) {
                responseObject = new ResponseObject(Constants.CODE_400, "Subscription type max children value cannot be a negative number or a characters");
                return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
            }
        }
        if (createSubscriptionTypeRequestDTO.getMaxCollaborator() == null || createSubscriptionTypeRequestDTO.getMaxCollaborator().toString().isEmpty()) {
            responseObject = new ResponseObject(Constants.CODE_400, "Subscription type max collaborator value cannot be missing or be empty");
            return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
        } else {
            if (!Util.validateLongNumber(createSubscriptionTypeRequestDTO.getMaxCollaborator().toString())) {
                responseObject = new ResponseObject(Constants.CODE_400, "Subscription type max collaborator value cannot be a negative number or a characters");
                return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
            }
        }
        if (createSubscriptionTypeRequestDTO.getPrice() == null || createSubscriptionTypeRequestDTO.getPrice().toString().isEmpty()) {
            responseObject = new ResponseObject(Constants.CODE_400, "Subscription type price cannot be missing or be empty");
            return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
        } else {
            if (!Util.validateFloatNumber(createSubscriptionTypeRequestDTO.getPrice().toString())) {
                responseObject = new ResponseObject(Constants.CODE_400, "Subscription type price cannot be a negative number or a characters");
                return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
            }
        }
        if (createSubscriptionTypeRequestDTO.getDurationDay() == null || createSubscriptionTypeRequestDTO.getDurationDay().toString().isEmpty()) {
            responseObject = new ResponseObject(Constants.CODE_400, "Subscription type duration day cannot be empty or missing");
            return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
        } else {
            if (!Util.validateLongNumber(createSubscriptionTypeRequestDTO.getDurationDay().toString())) {
                responseObject = new ResponseObject(Constants.CODE_400, "Subscription type duration day value cannot be a negative number or a characters");
                return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
            }
        }

        SubscriptionType subscriptionType = new SubscriptionType();
        subscriptionType.setName(createSubscriptionTypeRequestDTO.getName());
        subscriptionType.setDescription(createSubscriptionTypeRequestDTO.getDescription());
        subscriptionType.setMaxChildren(createSubscriptionTypeRequestDTO.getMaxChildren());
        subscriptionType.setMaxCollaborator(createSubscriptionTypeRequestDTO.getMaxCollaborator());
        subscriptionType.setPrice(createSubscriptionTypeRequestDTO.getPrice());
        subscriptionType.setIsDeleted(Boolean.FALSE);
        subscriptionType.setDurationDay(createSubscriptionTypeRequestDTO.getDurationDay());

        SubscriptionTypeStatusResponseDTO result = subscriptionService.createNewSubscriptionType(subscriptionType);
        if (result != null) {

            responseObject = new ResponseObject(Constants.CODE_200, "OK");
            responseObject.setData(result);
            return new ResponseEntity<>(responseObject, HttpStatus.OK);
        }

        responseObject = new ResponseObject(Constants.CODE_500, "Cannot create new subscription type");
        return new ResponseEntity<>(responseObject, HttpStatus.INTERNAL_SERVER_ERROR);
    }

//    @RequestMapping(value = "/type/{subscriptionTypeId}", method = RequestMethod.PUT)
//    @ResponseBody
//    public ResponseEntity<Object> modifySubscriptionType(@PathVariable("subscriptionTypeId") Long subscriptionTypeId,
//                                                         @RequestBody CreateSubscriptionTypeRequestDTO modifySubscriptionTypeRequestDTO) {
//        ResponseObject responseObject;
//        SubscriptionType subscriptionType = subscriptionService.findSubscriptionTypeById(subscriptionTypeId);
//
//        if (subscriptionType != null) {
//            responseObject = new ResponseObject(Constants.CODE_404, "Cannot found that subscription type in the system");
//            return new ResponseEntity<>(responseObject, HttpStatus.NOT_FOUND);
//        }
//        if (modifySubscriptionTypeRequestDTO.getName() != null && !modifySubscriptionTypeRequestDTO.getName().isEmpty()) {
//            subscriptionType.setName(modifySubscriptionTypeRequestDTO.getName());
//        }
//        if (modifySubscriptionTypeRequestDTO.getDescription() != null && !modifySubscriptionTypeRequestDTO.getDescription().isEmpty()) {
//            subscriptionType.setDescription(modifySubscriptionTypeRequestDTO.getDescription());
//        }
//        if (modifySubscriptionTypeRequestDTO.getMaxCollaborator() != null && !modifySubscriptionTypeRequestDTO.getMaxCollaborator().toString().isEmpty()) {
//            if (!Util.validateLongNumber(modifySubscriptionTypeRequestDTO.getMaxCollaborator().toString())) {
//                responseObject = new ResponseObject(Constants.CODE_400, "Subscription type max collaborator cannot be a negative number or a characters");
//                return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
//            }
//            subscriptionType.setMaxCollaborator(modifySubscriptionTypeRequestDTO.getMaxCollaborator());
//        }
//        if (modifySubscriptionTypeRequestDTO.getMaxChildren() != null && !modifySubscriptionTypeRequestDTO.getMaxChildren().toString().isEmpty()) {
//            if (!Util.validateLongNumber(modifySubscriptionTypeRequestDTO.getMaxChildren().toString())) {
//                responseObject = new ResponseObject(Constants.CODE_400, "Subscription type max children cannot be a negative number or a characters");
//                return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
//            }
//            subscriptionType.setMaxChildren(modifySubscriptionTypeRequestDTO.getMaxChildren());
//        }
//        if (modifySubscriptionTypeRequestDTO.getPrice() != null && !modifySubscriptionTypeRequestDTO.getPrice().toString().isEmpty()) {
//            if (!Util.validateFloatNumber(modifySubscriptionTypeRequestDTO.getPrice().toString())) {
//                responseObject = new ResponseObject(Constants.CODE_400, "Subscription type price cannot be a negative number or a characters");
//                return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
//            }
//            subscriptionType.setPrice(modifySubscriptionTypeRequestDTO.getPrice());
//        }
//
//        if (modifySubscriptionTypeRequestDTO.getDurationDay() != null || !modifySubscriptionTypeRequestDTO.getDurationDay().toString().isEmpty()) {
//            if (!Util.validateLongNumber(modifySubscriptionTypeRequestDTO.getDurationDay().toString())) {
//                responseObject = new ResponseObject(Constants.CODE_400, "Subscription type duration day value cannot be a negative number or a characters");
//                return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
//            }
//            subscriptionType.setDurationDay(modifySubscriptionTypeRequestDTO.getDurationDay());
//        }
//
//        ModifySubscriptionTypeResponseDTO result = subscriptionService.modifySubscriptionType(subscriptionType);
//        if (result != null) {
//            responseObject = new ResponseObject(Constants.CODE_200, "OK");
//            responseObject.setData(result);
//            return new ResponseEntity<>(responseObject, HttpStatus.OK);
//        }
//
//        responseObject = new ResponseObject(Constants.CODE_500, "Cannot update subscription type ");
//        return new ResponseEntity<>(responseObject, HttpStatus.INTERNAL_SERVER_ERROR);
//    }

    @RequestMapping(value = "/type/{subscriptionTypeId}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Object> getDetailOfSubscriptionType(@PathVariable("subscriptionTypeId") Long subscriptionTypeId) {
        ResponseObject responseObject;

        SubscriptionTypeDetailResponseDTO result = subscriptionService.getSubscriptionTypeDetail(subscriptionTypeId);
        if (result != null) {
            responseObject = new ResponseObject(Constants.CODE_200, "OK");
            responseObject.setData(result);
            return new ResponseEntity<>(responseObject, HttpStatus.OK);
        }

        responseObject = new ResponseObject(Constants.CODE_404, "Cannot found that subscription type in the system");
        return new ResponseEntity<>(responseObject, HttpStatus.NOT_FOUND);
    }

    @RequestMapping(value = "/type/list", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Object> getListSubscriptionType() {
        ResponseObject responseObject;

        List<SubscriptionTypeDetailResponseDTO> result = subscriptionService.getListSubscriptionType();
        if (result != null) {
            if (result.isEmpty()) {
                responseObject = new ResponseObject(Constants.CODE_200, "List subscription type is empty");
            } else {
                responseObject = new ResponseObject(Constants.CODE_200, "OK");
            }
            responseObject.setData(result);
            return new ResponseEntity<>(responseObject, HttpStatus.OK);
        }

        responseObject = new ResponseObject(Constants.CODE_500, "Cannot get list subscription type in the system");
        return new ResponseEntity<>(responseObject, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @RequestMapping(value = "/type/{subscriptionTypeId}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<Object> deleteSubscriptionType(@PathVariable("subscriptionTypeId") Long subscriptionTypeId) {
        ResponseObject responseObject;

        SubscriptionType subscriptionType = subscriptionService.findSubscriptionTypeById(subscriptionTypeId);
        if (subscriptionType == null) {
            responseObject = new ResponseObject(Constants.CODE_404, "Cannot found that subscription type in the system");
            return new ResponseEntity<>(responseObject, HttpStatus.NOT_FOUND);
        }

        SubscriptionTypeStatusResponseDTO result = subscriptionService.deleteSubscriptionType(subscriptionType);
        if (result != null) {
            responseObject = new ResponseObject(Constants.CODE_200, "OK");
            responseObject.setData(result);
            return new ResponseEntity<>(responseObject, HttpStatus.OK);
        }

        responseObject = new ResponseObject(Constants.CODE_500, "Cannot delete subscription type in the system");
        return new ResponseEntity<>(responseObject, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
