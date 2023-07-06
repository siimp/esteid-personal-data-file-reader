package ee.siimp.esteid.personaldatafile;

public class PersonDataFileUtils {

    private PersonDataFileUtils() {}

    public static String translateField(String fileName) {
        return switch (fileName) {
            case "PD1" -> "Surname";
            case "PD2" -> "First name";
            case "PD3" -> "Sex";
            case "PD4" -> "Citizenship";
            case "PD5" -> "Date and place of birth";
            case "PD6" -> "Personal identification code ";
            case "PD7" -> "Document number ";
            case "PD8" -> "Expiry date";
            case "PD9" -> "Issuance date and authority";
            case "PD10" -> "Type of residence permit";
            case "PD11" -> "Notes line 1";
            case "PD12" -> "Notes line 2";
            case "PD13" -> "Notes line 3";
            case "PD14" -> "Notes line 4";
            case "PD15" -> "Notes line 5";
            default -> "unknown";
        };
    }
}
