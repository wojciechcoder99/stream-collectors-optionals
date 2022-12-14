package parallel_streams;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

final class Parallel {

    // We have no control of displaying order
    public static void parallelIterate() {
        Stream.iterate("+", s -> s.concat("+"))
                .parallel()
                // This is stateful operation because there is needed some counter
                // to check how many elements already have been processed
                .limit(8)
                // those two methods cause side effects because neither return nor produce new value
                .peek(s -> System.out.println(s + " processed in the thread " + Thread.currentThread().getName()))
                .forEach(System.out::println);
    }

    // Important: collections are not thread safe - that means there is no scheduler
    // to determine access to the collection
    // The situation here is called race condition because thread will race to
    // get access to list
    public static void collectionsAreNotThreadSafe() {
        List<String> objects = new ArrayList<>();

        // Because of parallel processing many thread will modify the collection
        // at the same time so the result is unpredictable
        List<String> collect = Stream.iterate("+", s -> s.concat("+"))
                .parallel()
                .limit(1000)
                // to collect elements there is a dedicated method, do not use forEach for reduction
                //.forEach(objects::add);
                // That is thread safe method because makes a reduction for no-thread-safe
                // structures without any problems. We don't need additional synchronization method
                //.collect(Collectors.toList());
                // Since Java 16 we can use simply toList method
                .toList();
        // It might be 700 elements in list, 500 or exception may occur
        System.out.println(collect.size());
    }

    public static void useSynchronizedMethodToMakeCollectionThreadSafe() {
        // This method allows to create thread safe list and use in safety way
        // We are sure that results will be predictable
        List<String> strings = Collections.synchronizedList(new ArrayList<>());

        Stream.iterate("+", s -> s.concat("+"))
                .parallel()
                .limit(1000)
                .forEach(strings::add);

        System.out.println(strings.size());
    }

    public static void processingSet() {
        Set<String> strings = new HashSet<>();
        strings.add("1");
        strings.add("2");
        strings.add("3");
        strings.add("5");
        strings.add("6");
        strings.add("7");
        strings.add("8");
        strings.add("9");
        strings.add("10");

        System.out.println("Serial processing");
        strings.stream().unordered().forEach(s -> System.out.print(s + " "));
        System.out.println("");

        System.out.println("Parallel processing");
        strings.parallelStream().forEach(s -> System.out.print(s + " "));
        System.out.println("");

        System.out.println("With forEachOrdered");
        // It is worth highlighting that we may lose the benefits of parallel precessing
        // when using forEachOrder
        strings.parallelStream().forEachOrdered(s -> System.out.print(s + " "));
        System.out.println("");
    }

    // Avoid it !
    public static void statefulLambdaExpressions() {
        List<Integer> integers1 = Stream.iterate(1, e -> e + 1).limit(10000).toList();
        List<Integer> integers = new ArrayList<>();

        System.out.println("Serial");
        // Do not use stateful lambda expressions. The result depends on state
        // which might change during the execution of pipeline
        // The order in which elements are added to list is unpredictable and not deterministic
        integers1.stream()
                // DO NOT LIKE BELOW
                .map(e -> {integers.add(e); return e; })
                .forEach(e -> System.out.print(e + " "));
        System.out.println("");
        System.out.println(integers.size());

        System.out.println("Parallel");
        integers1.parallelStream()
                // DO NOT LIKE BELOW
                .map(e -> {integers.add(e); return e; })
                .forEach(e -> System.out.print(e + " "));
        System.out.println("");
        // Again the size is different from given in limit at the beginning of that method
        // because many threads have concurrent access to the list
        System.out.println(integers.size());
    }

    public static void streamsCannotBeConsumedTwice() {
        Stream<Integer> integerStream = Stream.of(1, 2, 3, 4, 5, 6, 7, 8, 9);

        integerStream.filter(n -> n > 5).forEach(System.out::println);

        // Streams are auto-closable after processing so we cannot use them twice
        // Instead we have to define new stream
        try {
            integerStream.flatMap(n -> Stream.of(n,n,n)).forEach(System.out::println);
        }
        catch (IllegalStateException e) {
            System.out.println("The same stream cannot be used twice if terminal operation has been executed!");
        }
    }

    public static void checkCharacteristics() {
        List<String> strings = new ArrayList<>();
        strings.add("one");
        strings.add("two");
        strings.add("three");
        strings.add("four");
        strings.add("five");
        strings.add("six");
        strings.add("seven");
        strings.add("eight");
        strings.add("nine");
        strings.add("ten");
        strings.add("eleven");
        strings.add("twelve");
        strings.add("thirteen");

        Stream<String> stringStream = strings.stream()
                .map(String::toUpperCase)
                .unordered();

        System.out.println(stringStream.spliterator().hasCharacteristics(Spliterator.ORDERED));
    }
}
