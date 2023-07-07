package ee.siimp.esteid.personaldatafile;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.smartcardio.ATR;
import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;
import javax.smartcardio.TerminalFactory;


public class App {

    public static void main(String[] args) throws CardException, NoSuchAlgorithmException, InterruptedException {
        List<CardTerminal> terminals = TerminalFactory.getDefault().terminals().list();

        if (terminals.isEmpty()) {
            terminals = TerminalFactory.getInstance("PC/SC", null, new jnasmartcardio.Smartcardio()).terminals().list();
        }

        if (terminals.isEmpty()) {
            System.err.println("no smart card readers found");
            System.exit(0);
        }

        CardTerminal terminal = selectCardTerminalWithCard(terminals);

        Card card = terminal.connect("T=1");

        ATR atr = card.getATR();
        ChipUtils.printATRInfo(atr);

        CardChannel channel = card.getBasicChannel();

        CommandAPDU selectMasterFile = new CommandAPDU(0x00, 0xA4, 0x00, 0x0C);
        ResponseAPDU selectMfResponse = channel.transmit(selectMasterFile);
        checkResponse(selectMfResponse, "select master file");


        CommandAPDU selectPersonalDataFile = new CommandAPDU(0x00, 0xA4, 0x01, 0x0C, new byte[] { 0x50, 0x00 });
        ResponseAPDU selectPersonalDataFileResponse = channel.transmit(selectPersonalDataFile);
        checkResponse(selectPersonalDataFileResponse, "select personal data file");

        CommandAPDU readBinary = new CommandAPDU(new byte[] { 0x00, (byte) 0xB0, 0x00, 0x00, 0x00 });
        for (int i = 1; i <= ChipUtils.PERSONAL_DATA_FILE_COUNT; i++) {
            CommandAPDU selectChildEF = new CommandAPDU(0x00, 0xA4, 0x02, 0x0C, new byte[] { 0x50, (byte) i });
            ResponseAPDU selectChildEFResponse = channel.transmit(selectChildEF);
            checkResponse(selectChildEFResponse, "select child file");

            ResponseAPDU response = channel.transmit(readBinary);
            checkResponse(response, "read binary value");

            String record = new String(response.getData(), StandardCharsets.UTF_8).trim();
            String fileName = "PD" + i;
            System.out.println(String.format("%s (%s) = %s",
                    fileName, PersonDataFileUtils.translateField(fileName), record));
        }

        card.disconnect(true);
    }

    private static void checkResponse(ResponseAPDU response, String command) {
        if(ChipUtils.COMMAND_PROCESS_STATUS_SUCCESS != response.getSW()) {
            System.err.println(String.format("%s command failed: %s (%s)",
                    command, Integer.toHexString(response.getSW()).toUpperCase(), ChipUtils.translateSw(response.getSW())));
            System.exit(0);
        }
    }

    private static CardTerminal selectCardTerminalWithCard(List<CardTerminal> terminals) throws CardException {
        CardTerminal terminal = null;
        for (CardTerminal cardTerminal: terminals) {
            System.err.print(String.format("card reader: %s", cardTerminal.getName()));

            if (!cardTerminal.isCardPresent()) {
                System.err.println(" - no card in reader");
            } else {
                terminal = cardTerminal;
                System.err.println();
            }
        }

        if (terminal == null) {
            System.err.println("no card in reader");
            System.exit(0);
        }
        return terminal;
    }

}
