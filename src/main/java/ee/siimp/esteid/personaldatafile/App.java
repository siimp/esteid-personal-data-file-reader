package ee.siimp.esteid.personaldatafile;

import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;
import javax.smartcardio.TerminalFactory;

public class App {

    public static final int PERSONAL_DATA_FILE_COUNT = 15;

    public static void main(String[] args) throws CardException, NoSuchAlgorithmException {
        List<CardTerminal> terminals = TerminalFactory.getDefault().terminals().list();

        if (terminals.isEmpty()) {
            terminals = TerminalFactory.getInstance("PC/SC", null, new jnasmartcardio.Smartcardio()).terminals().list();
        }

        if (terminals.isEmpty()) {
            System.out.println("no card terminals found");
            System.exit(0);
        }

        CardTerminal terminal = terminals.get(0);
        System.out.println(String.format("using card terminal %s", terminal.getName()));

        Card card = terminal.connect("T=1");
        CardChannel channel = card.getBasicChannel();

        CommandAPDU selectMF = new CommandAPDU(0x00, 0xA4, 0x00, 0x0C);
        channel.transmit(selectMF);

        CommandAPDU selectPersonalDataFile = new CommandAPDU(0x00, 0xA4, 0x01, 0x0C, new byte[] { 0x50, 0x00 });
        channel.transmit(selectPersonalDataFile);

        CommandAPDU readBinary = new CommandAPDU(new byte[] { 0x00, (byte) 0xB0, 0x00, 0x00, 0x00 });
        for (int i = 1; i <= PERSONAL_DATA_FILE_COUNT; i++) {
            CommandAPDU selectChildEF = new CommandAPDU(0x00, 0xA4, 0x02, 0x0C, new byte[] { 0x50, (byte) i });
            channel.transmit(selectChildEF);
            ResponseAPDU response = channel.transmit(readBinary);
            String record = new String(response.getData(), Charset.forName("UTF8")).trim();
            System.out.println(String.format("PD%d = %s", i, record));
        }
    }
}
