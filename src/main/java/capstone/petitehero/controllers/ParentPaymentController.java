package capstone.petitehero.controllers;

import capstone.petitehero.config.common.Constants;
import capstone.petitehero.dtos.ResponseObject;
import capstone.petitehero.dtos.response.parent.payment.ListPaymentTransactionResponseDTO;
import capstone.petitehero.dtos.response.parent.payment.ParentPaymentDetailResponseDTO;
import capstone.petitehero.services.ParentPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payment")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ParentPaymentController {

    @Autowired
    private ParentPaymentService parentPaymentService;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Object> getParentPaymentForAdmin(@RequestParam(value = "subscriptionId", required = false) Long subscriptionId) {
        ResponseObject responseObject;
        List<ListPaymentTransactionResponseDTO> result;

        if (subscriptionId != null && !subscriptionId.toString().isEmpty()) {
            result = parentPaymentService.getListParentPaymentForAdmin(subscriptionId);
        } else {
            result = parentPaymentService.getListParentPaymentForAdmin(null);
        }

        if (result != null) {
            if (result.isEmpty()) {
                responseObject = new ResponseObject(Constants.CODE_200, "List payment is empty in the system");
            } else {
                responseObject = new ResponseObject(Constants.CODE_200, "OK");
            }
            responseObject.setData(result);
            return new ResponseEntity<>(responseObject, HttpStatus.OK);
        }

        responseObject = new ResponseObject(Constants.CODE_500, "Cannot get payment list in the system");
        return new ResponseEntity<>(responseObject, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @RequestMapping(value = "/{transactionId}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Object> getPaymentDetails(@PathVariable("transactionId") Long transactionId,
                                                    @RequestParam(value = "role", required = false, defaultValue = Constants.ADMIN) String role) {
        ResponseObject responseObject;

        ParentPaymentDetailResponseDTO result = parentPaymentService.getDetailParentPayment(transactionId, role);

        if (result != null) {
            responseObject = new ResponseObject(Constants.CODE_200, "OK");
            responseObject.setData(result);
            return new ResponseEntity<>(responseObject, HttpStatus.OK);
        }

        responseObject = new ResponseObject(Constants.CODE_404, "Cannot get detail of payment");
        return new ResponseEntity<>(responseObject, HttpStatus.NOT_FOUND);
    }
}
