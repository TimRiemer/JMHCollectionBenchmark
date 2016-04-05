package mobi.riemer.jmhcollectionbenchmark.list;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Little JMH benchmarking project for Java 8 Collections
 *
 * @author Tim Riemer
 * @version 1.0 - 03.03.16.
 */
@State(Scope.Thread)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class ThreadUnsafeListModificationBenchmark {

    public enum ListImpl {
        Array {
            @Override
            List<Integer> create() {
                return new ArrayList<>();
            }
        },
        Linked {
            @Override
            List<Integer> create() {
                return new LinkedList<>();
            }
        };

        abstract List<Integer> create();
    }

    @Param({"100", "10000"})
    private int size;

    @Param({"Array", "Linked"})
    private ListImpl implementation;

    private List<Integer> list;

    @Setup
    public void setUp() throws Exception {
        list = implementation.create();
        for (int i = 0; i < size; i++) {
            list.add(Integer.MIN_VALUE);
        }
    }

    @Benchmark
    public List<Integer> populate() throws Exception {
        List<Integer> list = implementation.create();
        for (int i = 0; i < size; i++) {
            list.add(Integer.MIN_VALUE);
        }
        return list;
    }

    @Benchmark
    public List<Integer> iteration() {
        Iterator<Integer> iterator = list.iterator();
        while (iterator.hasNext()) {
            iterator.next();
        }
        return list;
    }

    @Benchmark
    public List<Integer> headAddRemove() {
        list.add(0, Integer.MIN_VALUE);
        list.remove(0);
        return list;
    }

    @Benchmark
    public List<Integer> middleAddRemove() {
        int index = size / 2;
        list.add(index, Integer.MIN_VALUE);
        list.remove(index);
        return list;
    }

    @Benchmark
    public List<Integer> tailAddRemove() {
        int index = size - 1;
        list.add(Integer.MIN_VALUE);
        list.remove(index);
        return list;
    }


    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(ThreadUnsafeListModificationBenchmark.class.getSimpleName())
                .warmupIterations(5)
                .measurementIterations(5)
                .forks(1)
                .jvmArgs("-ea")
                .shouldFailOnError(false) // switch to "true" to fail the complete run
                .build();

        new Runner(opt).run();
    }
}
