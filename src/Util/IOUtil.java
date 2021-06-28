package Util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class IOUtil {

    public static String readAsString(String path) throws IOException {
        Path p = Paths.get(path);
        return Files.readString(p);
    }

}
