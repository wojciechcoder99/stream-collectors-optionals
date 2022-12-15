package collectors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

final class Collectors {
    // Consciously created modifiable
    private static final List<String> people = new ArrayList<>(List.of("Tom", "Kevin", "Ben", "John", "Allen", "Patrick", "", "", "", "", ""));

    public static void toListMethod() {
        people.parallelStream()
                .filter(String::isEmpty)
    }
}
