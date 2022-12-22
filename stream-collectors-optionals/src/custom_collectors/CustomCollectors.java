package custom_collectors;

import javax.swing.*;
import java.security.KeyStore;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class CustomCollectors {
    private static final Set<Movie> movies = new HashSet<>();

    public static void processFileAndExtractMoviesAndActors() {
        ProcessFileUtils.getFile().forEach(line -> {
            String[] elements = line.split("/");
            String title =
                    elements[0].substring(0, elements[0].lastIndexOf("(")).trim();
            String releaseYear =
                    elements[0].substring(elements[0].lastIndexOf("(") + 1, elements[0].lastIndexOf(")"));

            if (releaseYear.contains(",")) {
                // with skip movies with a coma in their title
                return;
            }

            Movie movie = new Movie(title, Integer.parseInt(releaseYear));

            for (int i = 1; i < elements.length; i++) {
                String[] name = elements[i].split(", ");
                String lastName = name[0].trim();
                String firstName = "";
                if (name.length > 1) {
                    firstName = name[1].trim();
                }

                Actor actor = new Actor(lastName, firstName);
                movie.addActor(actor);
            }

            movies.add(movie);
        });
        System.out.println("Movies: " + movies.size());
    }

    public static void countNumberOfAllActors() {
        // using parallel here is safe because map operation is stateless and non-interfering
        long count = movies.parallelStream()
                .flatMap(m -> m.getActors().stream())
                .distinct()
                .count();
        System.out.println("Number of actors: " + count);
    }

    public static void findTenActorsWhoPlayedInTheMostMovies() {
        List<Map.Entry<Actor, Long>> collect1 = movies.stream()
                .flatMap(m -> m.getActors().stream())
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(10)
                .toList();

        collect1.forEach(entry -> System.out.println("Actor: " + entry.getKey() + "Movies: " + entry.getValue()));
    }

    // Let's make an assumption that it refers to not a particular year but whatever year
    // TODO rebuild that to enables using a parallel approach
    public static void findActorWhoPlayedInTheMostMoviesDuringAYear() {
        // I need sth like Map<Year, Map<Actor, Number of occurrences>>
        Map<Integer, Map<Actor, AtomicLong>> collect = movies.stream()
                .collect(Collectors.groupingBy(Movie::getReleaseYear,
                        // Defined own custom downstream collector
                        Collector.of(
                                HashMap::new, // supplier which creates a mutable container
                                (map, movie) -> {
                                    movie.getActors().forEach(actor -> map.computeIfAbsent(actor, a -> new AtomicLong()).incrementAndGet());
                                }, // accumulator - defines the way how elements are added to the container
                                (map1, map2) -> {
                                    map1.putAll(map2);
                                    return map1;
                                }, // combiner - describes the way how containers are merged in case ot parallel processing
                                Collector.Characteristics.IDENTITY_FINISH))); // indicates if collector may be used e.g. in parallel processing

        Map.Entry<Actor, AtomicLong> actorAtomicLongEntry = collect.values().stream()
                .map(actorAtomicLongMap -> actorAtomicLongMap.entrySet().stream()
                        .max(Map.Entry.comparingByValue(Comparator.comparing(AtomicLong::get))).get())
                .max(Map.Entry.comparingByValue(Comparator.comparing(AtomicLong::get)))
                .get();
        System.out.println(actorAtomicLongEntry.getKey() + "Occurrences: " + actorAtomicLongEntry.getValue().get());
    }


}
