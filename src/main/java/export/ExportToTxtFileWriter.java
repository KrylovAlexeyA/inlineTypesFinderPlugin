package export;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collection;

public class ExportToTxtFileWriter implements ExportWriter {
    private String pathString;

    public ExportToTxtFileWriter(String pathString) {
        this.pathString = pathString + "/candidates.txt";
    }

    public void export(String className) {
        try {
            Path path = Paths.get(pathString);
            if (containsOne(className, Files.readAllLines(path))) {
                return;
            }
            Files.write(path, (className + "\n").getBytes(),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean containsOne(String str, Collection<String> collection) {
        for (String el : collection) {
            if (el.contains(str)) {
                return true;
            }
        }
        return false;
    }
}
