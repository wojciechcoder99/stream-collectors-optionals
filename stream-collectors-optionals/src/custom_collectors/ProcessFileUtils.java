package custom_collectors;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class ProcessFileUtils {
    private static final String PATH_TO_FILE = "C:\\PRIVATE\\Projects\\functional-paradigm\\stream-collectors-optionals\\src\\custom_collectors\\movies-mpaa.txt";
    private static final List<String> file = new ArrayList<>();

    public static List<String> getFile() {
      if (file.isEmpty()) {
          file.addAll(createListOfLines());
      }
      return file;
    }

    private static List<String> createListOfLines() {
        Stream<String> lines = null;
        try {
            lines = Files.lines(Paths.get(PATH_TO_FILE));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return lines.toList();
    }
}
