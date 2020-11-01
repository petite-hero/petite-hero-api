package capstone.petitehero.controllers;

import capstone.petitehero.config.common.Constants;
import capstone.petitehero.dtos.ResponseObject;
import capstone.petitehero.dtos.request.subscription.type.CreateSubscriptionTypeRequestDTO;
import capstone.petitehero.dtos.response.subscription.type.CreateSubscriptionTypeResponseDTO;
import capstone.petitehero.dtos.response.subscription.type.ListSubscriptionTypeResponseDTO;
import capstone.petitehero.dtos.response.subscription.type.ModifySubscriptionTypeResponseDTO;
import capstone.petitehero.dtos.response.subscription.type.SubscriptionTypeDetailResponseDTO;
import capstone.petitehero.entities.SubscriptionType;
import capstone.petitehero.services.SubscriptionService;
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
            if (createSubscriptionTypeRequestDTO.getMaxChildren().longValue() < 0) {
                responseObject = new ResponseObject(Constants.CODE_400, "Subscription type max children value cannot be a negative number");
                return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
            }
        }
        if (createSubscriptionTypeRequestDTO.getMaxCollaborator() == null || createSubscriptionTypeRequestDTO.getMaxCollaborator().toString().isEmpty()) {
            responseObject = new ResponseObject(Constants.CODE_400, "Subscription type max collaborator value cannot be missing or be empty");
            return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
        } else {
            if (createSubscriptionTypeRequestDTO.getMaxCollaborator().longValue() < 0) {
                responseObject = new ResponseObject(Constants.CODE_400, "Subscription type max collaborator value cannot be a negative number");
                return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
            }
        }
        if (createSubscriptionTypeRequestDTO.getPrice() == null || createSubscriptionTypeRequestDTO.getPrice().toString().isEmpty()){
            responseObject = new ResponseObject(Constants.CODE_400, "Subscription type price cannot be missing or be empty");
            return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
        } else  {
            if (createSubscriptionTypeRequestDTO.getPrice().doubleValue() < 0) {
                responseObject = new ResponseObject(Constants.CODE_400, "Subscription type price cannot be a negative number");
                return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
            }
        }

        SubscriptionType subscriptionType = new SubscriptionType();
        subscriptionType.setName(createSubscriptionTypeRequestDTO.getName());
        subscriptionType.setDescription(createSubscriptionTypeRequestDTO.getDescription());
        subscriptionType.setMaxChildren(createSubscriptionTypeRequestDTO.getMaxChildren());
        subscriptionType.setMaxCollaborator(createSubscriptionTypeRequestDTO.getMaxCollaborator());
        subscriptionType.setPrice(createSubscriptionTypeRequestDTO.getPrice());

        CreateSubscriptionTypeResponseDTO result = subscriptionService.createNewSubscriptionType(subscriptionType);
        if (result != null) {

            responseObject = new ResponseObject(Constants.CODE_200, "OK");
            responseObject.setData(result);
            return new ResponseEntity<>(responseObject, HttpStatus.OK);
        }

        responseObject = new ResponseObject(Constants.CODE_500, "Server is down cannot create new subscription type");
        return new ResponseEntity<>(responseObject, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @RequestMapping(value = "/type/{subscriptionTypeId}", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<Object> modifySubscriptionType(@PathVariable("subscriptionTypeId") Long subscriptionTypeId,
                                                         @RequestBody CreateSubscriptionTypeRequestDTO modifySubscriptionTypeRequestDTO) {
        ResponseObject responseObject;
        SubscriptionType subscriptionType = subscriptionService.findSubscriptionTypeById(subscriptionTypeId);

        if (subscriptionType != null) {
            responseObject = new ResponseObject(Constants.CODE_404, "Cannot found that subscription type in the system");
            return new ResponseEntity<>(responseObject, HttpStatus.NOT_FOUND);
        }
        if (modifySubscriptionTypeRequestDTO.getName() != null && !modifySubscriptionTypeRequestDTO.getName().isEmpty()) {
            subscriptionType.setName(modifySubscriptionTypeRequestDTO.getName());
        }
        if (modifySubscriptionTypeRequestDTO.getDescription() != null && !modifySubscriptionTypeRequestDTO.getDescription().isEmpty()) {
            subscriptionType.setDescription(modifySubscriptionTypeRequestDTO.getDescription());
        }
        if (modifySubscriptionTypeRequestDTO.getMaxCollaborator() != null && !modifySubscriptionTypeRequestDTO.getMaxCollaborator().toString().isEmpty()) {
            subscriptionType.setMaxCollaborator(modifySubscriptionTypeRequestDTO.getMaxCollaborator());
        }
        if (modifySubscriptionTypeRequestDTO.getMaxChildren() != null && !modifySubscriptionTypeRequestDTO.getMaxChildren().toString().isEmpty()) {
            subscriptionType.setMaxChildren(modifySubscriptionTypeRequestDTO.getMaxChildren());
        }
        if (modifySubscriptionTypeRequestDTO.getPrice() != null && !modifySubscriptionTypeRequestDTO.getPrice().toString().isEmpty()) {
            subscriptionType.setPrice(modifySubscriptionTypeRequestDTO.getPrice());
        }

        ModifySubscriptionTypeResponseDTO result = subscriptionService.modifySubscriptionType(subscriptionTypeId, subscriptionType);
        if (result != null) {
            responseObject = new ResponseObject(Constants.CODE_200, "OK");
            responseObject.setData(result);
            return new ResponseEntity<>(responseObject, HttpStatus.OK);
        }

        responseObject = new ResponseObject(Constants.CODE_500, "Cannot update subscription type ");
        return new ResponseEntity<>(responseObject, HttpStatus.INTERNAL_SERVER_ERROR);
    }

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

        responseObject = new ResponseObject(Constants.CODE_404, "Cannot get your subscription type");
        return new ResponseEntity<>(responseObject, HttpStatus.NOT_FOUND);
    }

    @RequestMapping(value = "/type/list", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Object> getListSubscriptionType() {
        ResponseObject responseObject;

        List<ListSubscriptionTypeResponseDTO> result = subscriptionService.getListSubscriptionType();
        if (result != null) {
            if (result.isEmpty()) {
                responseObject = new ResponseObject(Constants.CODE_200, "List subscription type is empty");
            } else {
                responseObject = new ResponseObject(Constants.CODE_200, "OK");
            }
            responseObject.setData(result);
            return new ResponseEntity<>(responseObject, HttpStatus.OK);
        }

        responseObject = new ResponseObject(Constants.CODE_500, "Server is down cannot get list subscription type in the system");
        return new ResponseEntity<>(responseObject, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
