package advanced_streams;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) throws IOException {
        Stream<String> stream1 = Files.lines(Paths.get("C:\\PRIVATE\\Projects\\functional-paradigm\\stream-collectors-optionals\\src\\advanced_streams\\TomSawyer_01"));
        Stream<String> stream2 = Files.lines(Paths.get("C:\\PRIVATE\\Projects\\functional-paradigm\\stream-collectors-optionals\\src\\advanced_streams\\TomSawyer_02"));
        Stream<String> stream3 = Files.lines(Paths.get("C:\\PRIVATE\\Projects\\functional-paradigm\\stream-collectors-optionals\\src\\advanced_streams\\TomSawyer_03"));
        Stream<String> stream4 = Files.lines(Paths.get("C:\\PRIVATE\\Projects\\functional-paradigm\\stream-collectors-optionals\\src\\advanced_streams\\TomSawyer_04"));

        // Function.identity returns always the same object which has been passed as parameter
        Stream<String> stream11 = Stream.of(stream1, stream2, stream3, stream4).flatMap(Function.identity());

        List<String> words = stream11.flatMap(line -> Pattern.compile(" ").splitAsStream(line))
                .map(String::toLowerCase)
                .distinct()
                .toList();
        System.out.println(words.size());

    }
}
