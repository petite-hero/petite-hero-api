package capstone.petitehero.utilities;

import capstone.petitehero.config.common.Constants;
import capstone.petitehero.dtos.ResponseObject;
import org.apache.commons.io.FileUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

public class Util {

    public boolean validatePhoneNumberParent(String phoneNumber) {
        // 1234567890
        // 123-456-7890
        // 123 456 7890
        return phoneNumber.matches("(\\d{3}(\\s|-)?\\d{3,4}(\\s|-)?\\d{3,4})");
    }

    public static ResponseObject createResponse() {
        ResponseObject result = new ResponseObject();
        result.setCode(Constants.CODE_200);
        result.setMsg(Constants.NO_ERROR);
        result.setData(null);
        return result;
    }

    public boolean validateEmail(String email) {
        return email.matches("^[a-zA-Z0-9]+@([a-zA-Z]{2,6}\\.)+[a-zA-Z]{2,6}$");
    }

    public static String fromImageFileToBase64String(String photoFileName) {
        try {
            File imageLocation = new File(Constants.UPLOAD_FOLDER + "/" + photoFileName);

            byte[] bytesPhoto = FileUtils.readFileToByteArray(imageLocation);

            return Base64.getEncoder().encodeToString(bytesPhoto);
        } catch (Exception e) {
            return "Cannot get photo of child";
        }
    }

    public static String saveImageToSystem(String idImage, String content, MultipartFile photo) {
        File f = new File(Constants.UPLOAD_FOLDER);

        // photo name
        String fileName = idImage + "-"
                + new Date().getTime() + "-"
                + content + ".png";
        if (!f.exists()) {
            f.mkdir();
        }
        try {

            Path path = Paths.get(Constants.UPLOAD_FOLDER + fileName);
            byte[] bytesPhoto = photo.getBytes();
            Files.write(path, bytesPhoto);

            return fileName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean validatePasswordForAllAccount(String password) {
        return password.matches("[a-zA-Z0-9]{6,8}");
    }

    public static String formatDateTime(Long timeStamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        return sdf.format(new Date(timeStamp));
    }
}
