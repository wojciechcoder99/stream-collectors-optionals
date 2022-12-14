package parallel_streams;

import java.util.ArrayList;
import java.util.List;
import java.util.Spliterator;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) {
        System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "2");
//        Parallel.parallelIterate();
//        Parallel.collectionsAreNotThreadSafe();
//        Parallel.useSynchronizedMethodToMakeCollectionThreadSafe();
//        Parallel.processingSet();
//        Parallel.statefulLambdaExpressions();
        Parallel.streamsCannotBeConsumedTwice();
    }
}
