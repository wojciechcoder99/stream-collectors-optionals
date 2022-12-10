package spliterator_pattern;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Spliterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

class Main {

    public static final String PATH_TO_THE_FILE = "C:\\PRIVATE\\Projects\\functional-paradigm\\stream-collectors-optionals\\src\\spliterator_pattern\\people";

    public static void main(String[] args) throws IOException {
        Spliterator<Person> personSpliterator = new PersonSpliterator(getLineSpliterator(getPathToFile()));

        // parallel set to false indicates that parallel processing is not support
        // by the given spliterator
        StreamSupport.stream(personSpliterator, false).forEach(System.out::println);
    }

    private static Path getPathToFile() {
        return Paths.get(PATH_TO_THE_FILE);
    }

    private static Spliterator<String> getLineSpliterator(Path path) throws IOException {
        Stream<String> lines = null;
        try {
            lines = Files.lines(path);
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return lines.spliterator();
    }
}
