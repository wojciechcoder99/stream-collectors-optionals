package collectors;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class Scrabble {
    private static final String PATH_TO_SCRABBLE_FILE = "C:\\PRIVATE\\Projects\\functional-paradigm\\stream-collectors-optionals\\src\\collectors\\scrabble";
    private static final String PATH_TO_SHAKESPEARE_FILE = "C:\\PRIVATE\\Projects\\functional-paradigm\\stream-collectors-optionals\\src\\collectors\\shakespeare";
    private static final Set<String> ALLOWED_SCRABBLE_WORDS = new HashSet<>();
    private static final Set<String> SHAKESPEARE_WORDS = new HashSet<>();
    // Use lazy init
    private static final int [] letterScores = { 1,3,3,2,1,4,2,4,1,8,5,1,3,1,1,3,10,1,1,1,1,4,4,8,4,10 };
    private static final int [] scrabbleDistribution = { 9,2,2,1,12,2,3,2,9,1,1,4,2,6,8,2,1,6,4,6,4,2,2,1,2,1 };

    private static Set<String> getAllowedScrabbleWords() {
        if (ALLOWED_SCRABBLE_WORDS.isEmpty()) {
            ALLOWED_SCRABBLE_WORDS.addAll(createSetOfWords(PATH_TO_SCRABBLE_FILE));
        }
        return ALLOWED_SCRABBLE_WORDS;
    }

    private static Set<String> getShakespeareWords() {
        if (SHAKESPEARE_WORDS.isEmpty()) {
            SHAKESPEARE_WORDS.addAll(createSetOfWords(PATH_TO_SHAKESPEARE_FILE));
        }
        return SHAKESPEARE_WORDS;
    }
    private static Set<String> createSetOfWords(String filePath) {
        Path path = Paths.get(filePath);
        Stream<String> words = null;
        try {
            words = Files.lines(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return words.collect(Collectors.toSet());
    }

    // Incorrect method to calculate score due to scrabble rules
    private static Function<String, Integer> getScore() {
        return word -> word.toLowerCase().chars()
                .map(letter -> letterScores[letter - 'a'])
                .sum();
    }

    public static void createHistogramOfWordsByScore() {
        Map<Integer, List<String>> histogram = getShakespeareWords().stream()
                .filter(getAllowedScrabbleWords()::contains)
                // TODO: that is indeed another constraint for correct words, so think about better solution
                .filter(word -> computeNumberOfBlanks(getNumberOfEachLetterInWord()).apply(word) <= 2)
                .collect(Collectors.groupingBy(getCorrectScore(getNumberOfEachLetterInWord())));

        // Get three best words
        // Hint: use entrySet method (Set<Map.Entry<Integer, List<String>>>)
        List<Map.Entry<Integer, List<String>>> top3Words = histogram.entrySet()
                .stream()
                // stateful intermediate operation
                .sorted(Map.Entry.comparingByKey(Comparator.reverseOrder()))
                // stateful intermediate operation
                .limit(3)
                .toList();

        top3Words.forEach(e -> System.out.println("Score: " + e.getKey() + " Values: " + e.getValue()));

    }

    private static Function<String, Map<Integer, Long>> getNumberOfEachLetterInWord() {
        return word -> word.chars()
                .boxed()
                .collect(Collectors.groupingBy(l -> l, Collectors.counting()));
    }

    // According to scrabble rules cannot be
    private static Function<String, Long> computeNumberOfBlanks(Function<String, Map<Integer, Long>> numberOfEachLetterInWord) {
        return word -> numberOfEachLetterInWord.apply(word)
                .entrySet()
                .stream()
                // scrabbleDistribution[entry.getKey() - 'a'] - that trick allows us to get points for particular letter
                .mapToLong(entry -> Long.max(entry.getValue() - scrabbleDistribution[entry.getKey() - 'a'],0L))
                .sum();
    }

    // According to scrabble rules we have the maximum number of letters in word for which we can sum
    // the points. For instance there are only two 'z' letters allowed in word, so we cannot consider those words
    // TODO: Get familiar with scrabble rules and try to re-write logic responsible for giving points
    private static Function<String, Integer> getCorrectScore(Function<String, Map<Integer, Long>> numberOfEachLetterInWord) {
        return word -> numberOfEachLetterInWord.apply(word)
                .entrySet()
                .stream()
                .mapToInt(entry ->
                        letterScores[entry.getKey() - 'a'] *
                                Integer.min(entry.getValue().intValue(), scrabbleDistribution[entry.getKey() - 'a']))
                .sum();
    }
}
