package spliterator_pattern;

import java.util.Objects;
import java.util.Spliterator;
import java.util.function.Consumer;

class PersonSpliterator implements Spliterator<Person> {

    private final Spliterator<String> lineSpliterator;
    private String name;
    private int age;
    private String city;

    public PersonSpliterator(Spliterator<String> lineSpliterator) {
        Objects.requireNonNull(lineSpliterator);
        this.lineSpliterator = lineSpliterator;
    }

    // The main method used for stepping through a sequence
    // Consumer accepts elements of the spliterator one by one
    // Each evaluating of that method provides separate element from spliterator
    @Override
    public boolean tryAdvance(Consumer<? super Person> action) {
        boolean hasElements = lineSpliterator.tryAdvance(line -> name = line) &&
            lineSpliterator.tryAdvance(line -> age = Integer.parseInt(line)) &&
            lineSpliterator.tryAdvance(line -> city = line);

        if (hasElements) {
            // By calling accept method we pass newly created element
            // to current person spliterator
            action.accept(new Person(name, city, age));
            return true;
        }
        return false;
    }

    // That method should be overridden when spliterator supports
    // parallel processing
    @Override
    public Spliterator trySplit() {
        return null;
    }

    // Three lines from line spliterator create one object
    // in person spliterator
    @Override
    public long estimateSize() {
        return lineSpliterator.estimateSize() / 3;
    }

    // Returns constant used for informing what is
    // characteristics of spliterator
    @Override
    public int characteristics() {
        return lineSpliterator.characteristics();
    }
}
