package ee.siimp.esteid.personaldatafile;

import javax.smartcardio.ATR;
import java.util.Arrays;
import java.util.stream.Collectors;

public class ChipUtils {
    public static final int PERSONAL_DATA_FILE_COUNT = 15;

    public static final int COMMAND_PROCESS_STATUS_SUCCESS = 0x90_00;

    // 4553544F4E49412D654944
    private static final String ESTONIA_EID_HISTORICAL_BYTES = "[0, 18, 35, 63, 83, 101, 73, 68, 15, -112, 0]";

    private ChipUtils() {}

    /**
     * Every contact card responds to reset with sequence of bytes called Answer To Reset (ATR). The
     * ATR gives information about the electrical communication protocol and the chip itself.
     * @param atr
     */
    public static void printATRInfo(ATR atr) {
        byte[] historicalBytes = atr.getHistoricalBytes();
        System.err.println("ATR info:");
        if (ESTONIA_EID_HISTORICAL_BYTES.equals(Arrays.toString(historicalBytes))) {
            System.err.println(String.format("History bytes: ESTONIA-eID"));
        }
        System.err.println(String.format("Category: 0x%02x", historicalBytes[0]));
        System.err.println(String.format("Country: 0x%02x%02x", historicalBytes[2], historicalBytes[3]));
        System.err.println(String.format("Card issuer: 0x%02x%02x%02x",
                historicalBytes[5], historicalBytes[6], historicalBytes[7]));
        System.err.println(String.format("Termination state: 0x%02x", historicalBytes[8]));
        System.err.println(String.format("SW: 0x%02x%02x", historicalBytes[9], historicalBytes[10]));
    }

    public static String translateSw(int sw) {
        String swHex = Integer.toHexString(sw).toUpperCase();
        return switch (swHex) {
            case "6A86" -> "Incorrect parameters P1 to P2";
            default -> "unknown";
        };
    }
}
