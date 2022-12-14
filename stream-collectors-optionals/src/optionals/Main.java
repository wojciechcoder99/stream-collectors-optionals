package optionals;

import java.util.Optional;

public class Main {
    public static void main(String[] args) {
        System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "10");
        ParallelOptionals.optionalToStream_Serial_Parallel();
   }
}
