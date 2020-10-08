package capstone.petitehero.utilities;

public class Util {

    public boolean validatePhoneNumberParent(String phoneNumber) {
        // 1234567890
        // 123-456-7890
        // 123 456 7890
        return phoneNumber.matches("(\\d{3}(\\s|\\-)?\\d{3,4}(\\s|\\-)?\\d{3,4})");
    }

}
