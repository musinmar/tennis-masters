package tm.lib.domain.competition;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

public class CompetitionPrinter {
    
    public static String printToString(Competition competition) {
        OutputStream outputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outputStream);
        competition.print(printStream);
        return outputStream.toString();
    }
}