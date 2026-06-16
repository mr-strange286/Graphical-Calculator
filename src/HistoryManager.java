import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

public class HistoryManager {

    public static String saveHistory(String function) {
        String entry = LocalDateTime.now() + " : " + function;

        try {
            FileWriter writer = new FileWriter("history.txt", true);
            writer.write(entry + "\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return entry;
    }
}