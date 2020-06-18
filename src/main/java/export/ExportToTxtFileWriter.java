package export;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class ExportToTxtFileWriter implements ExportWriter {
    String pathString = "/Users/krylovalexey/Desktop/inlines/candidates.txt";
    Path path = Paths.get(pathString);

    public void export(String className) {
        try {
            Files.write(path, (className + "\n").getBytes(),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
