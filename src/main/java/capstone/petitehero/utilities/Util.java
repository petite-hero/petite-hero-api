package capstone.petitehero.utilities;

import capstone.petitehero.config.common.Constants;
import capstone.petitehero.dtos.ResponseObject;

public class Util {

    public boolean validatePhoneNumberParent(String phoneNumber) {
        // 1234567890
        // 123-456-7890
        // 123 456 7890
        return phoneNumber.matches("(\\d{3}(\\s|\\-)?\\d{3,4}(\\s|\\-)?\\d{3,4})");
    }

    public static ResponseObject createResponse() {
        ResponseObject result = new ResponseObject();
        result.setCode(Constants.CODE_200);
        result.setMsg(Constants.NO_ERROR);
        result.setData(null);
        return result;
    }
}
