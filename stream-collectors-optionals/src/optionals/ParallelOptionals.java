package optionals;

import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.stream.Stream;

final class ParallelOptionals {

    public static void optionalToStream_Serial_Parallel() {
        Stream<Double> doubles = Stream.iterate(1d, e -> e + 1d).limit(100_000);
//        doubles.flatMap(getDoubleStreamFunction()).forEach(System.out::println);

        // Use ThreadLocalRandom to use boxed method
        Double reduce = ThreadLocalRandom.current().doubles().limit(100_000)
                // To be able to pass a DoubleStream object to flatMap which accepts
                // Double type we have to use boxed method which just returns stream of given
                // type, in that case Double
                .boxed()
                .flatMap(getDoubleStreamFunction())
                .reduce(
                        0d,
                        Double::sum
                );
        System.out.println("Result: " + reduce);

        // Let's see reduce operation in serial and parallel approach
        double reduceSerial = doubles
                .limit(100_000)
                .flatMap(getDoubleStreamFunction())
                // Reduce is a thread-safe operation until the accumulating function is
                // thread-safe
                .reduce(
                        0d,
                        Double::sum
                );

        System.out.println("Serial result: " + reduceSerial);

        // Then we will do the same as above but with parallel stream
        Stream<Double> doublesParallel = Stream.iterate(1d, e -> e + 1d).limit(100_000);

        double reduceParallel = doublesParallel
                .parallel()
                .limit(100_000)
                .flatMap(getDoubleStreamFunction())
                // That is not thread-safe operation, so the result might be
                .reduce(
                        0d,
                        Double::sum
                );

        System.out.println("Parallel result: " + reduceParallel);

    }

    // Difference between map and flatMap from Optional class is that map takes a value as a parameter and wrap it
    // into another optional, so we get Optional<Optional<value>>
    // whereas flatMap is a bit smarter and doesn't wrap input Optional into another Optional but returns
    // just Optional
    private static Function<Double, Stream<Double>> getDoubleStreamFunction() {
        return e -> Optional.of(Math.pow(e, 2))
                // might be simplified by just calling stream method
                .map(Stream::of)
                .orElseGet(Stream::empty);
    }
}
