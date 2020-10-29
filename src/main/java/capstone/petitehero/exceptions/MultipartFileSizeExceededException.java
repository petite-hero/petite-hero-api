package capstone.petitehero.exceptions;

import capstone.petitehero.config.common.Constants;
import capstone.petitehero.dtos.ResponseObject;
import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartException;

@ControllerAdvice
public class MultipartFileSizeExceededException implements InitializingBean {

    @Override
    public void afterPropertiesSet() throws Exception {

    }

    @ExceptionHandler(MultipartException.class)
    @ResponseStatus(value = HttpStatus.PAYLOAD_TOO_LARGE)
    public ResponseEntity<ServiceException> handleMultipartFileSizeLimitExceed (MultipartException e) {
        ResponseObject responseObject = new ResponseObject(Constants.CODE_400, "Image was too large, only accept image < 10MB");
        responseObject.setData(e.getMessage());
        return new ResponseEntity(responseObject, HttpStatus.BAD_REQUEST);
    }
}
