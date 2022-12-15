package collectors;

import java.util.*;
import java.util.stream.Collectors;

// Collectors enable gathering in mutable containers :
// - concatenation of strings
// - added elements to list, set, map
// - sum, average

final class CollectorsUtil {
    // Consciously created modifiable
    private static final List<Person> people = new ArrayList<>(
            List.of(new Person("Tom", 23), new Person("Kevin", 22), new Person("Ben", 45), new Person("Michael", 63),
                    new Person("John", 13), new Person("Allen", 32), new Person("Patrick", 23), new Person("Jeff", 42),
                    new Person("Tom", 50), new Person("Ashley", 30)));

    static void toListMethod() {
        List<String> collect = people.parallelStream()
                .filter(Objects::nonNull)
                .map(Person::name)
                // That is mutable reduction
                // To perform reductions use dedicated implementation to do that
                // because is completely thread-safe
                // Collectors class enables collect data in mutable container
                .collect(Collectors.toList());
    }

    static void maxAndAverageMethod() {
        Optional<Person> max = people.stream()
                // find the oldest person
                .max(Comparator.comparing(Person::age));
        System.out.println("Oldest: " + max.map(Person::name).orElse("Not found"));

        Double collect = people.stream().collect(Collectors.averagingInt(Person::age));
        System.out.println("Avg: " + collect);
    }

    static void collectingIntoMap() {
        Map<Boolean, List<Person>> over21Years = people.stream()
                .collect(Collectors.partitioningBy(p -> p.age() > 21));

        Map<Integer, List<Person>> byAge = people.stream()
                .collect(Collectors.groupingBy(Person::age));

        Map<Integer, Long> collect = people.stream()
                // For parallel streams function works by merging
                // keys from one map to another what is an expensive operation
                // Therefore for parallel operations use groupingByConcurrent because
                // offers better performance
                .collect(Collectors.groupingBy(Person::age,
                        // downstream collector
                        Collectors.counting()));

        Map<Integer, TreeSet<String>> collect1 = people.stream()
                .collect(Collectors.groupingBy(Person::age,
                        Collectors.mapping(Person::name,
                                // Elements are ordered using their natural ordering or by
                                // using comparator which we can pass as an argument
                                Collectors.toCollection(TreeSet::new))));

        Map<String, List<Person>> unmodifiableMap = people.stream()
                // To create unmodifiable collection use method collectAndThen
                // This is only valid pattern to build immutable collection because
                // adding to the collection has to be done at the very end of processing
                .collect(Collectors.collectingAndThen(Collectors.groupingBy(Person::name),
                        Collections::unmodifiableMap));
    }
}
