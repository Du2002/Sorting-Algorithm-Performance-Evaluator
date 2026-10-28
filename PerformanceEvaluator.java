import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Runs every sorting algorithm on the same data,
 * measures the execution time of each, and finds the fastest one.
 */
public class PerformanceEvaluator {

    /** Holds the result for one algorithm: its name and how long it took. */
    public static class Result {
        private final String name;
        private final long nanos;

        public Result(String name, long nanos) {
            this.name = name;
            this.nanos = nanos;
        }

        public String getName() {
            return name;
        }

        public long getNanos() {
            return nanos;
        }

        public double getMillis() {
            return nanos / 1_000_000.0;
        }
    }

    /** Runs all five algorithms on copies of the data and returns their timings. */
    public List<Result> evaluate(double[] data) {
        // Algorithm name -> the method that runs it
        Map<String, Consumer<double[]>> algorithms = new LinkedHashMap<>();
        algorithms.put("Insertion Sort", SortingAlgorithms::insertionSort);
        algorithms.put("Shell Sort", SortingAlgorithms::shellSort);
        algorithms.put("Merge Sort", SortingAlgorithms::mergeSort);
        algorithms.put("Quick Sort", SortingAlgorithms::quickSort);
        algorithms.put("Heap Sort", SortingAlgorithms::heapSort);

        warmUp(algorithms, data);

        List<Result> results = new ArrayList<>();
        for (Map.Entry<String, Consumer<double[]>> entry : algorithms.entrySet()) {
            double[] copy = data.clone(); // every algorithm gets the same unsorted input

            long start = System.nanoTime();
            entry.getValue().accept(copy); // run the sort
            long end = System.nanoTime();

            results.add(new Result(entry.getKey(), end - start));
        }
        return results;
    }

    /** Returns the result with the shortest time. */
    public Result findBest(List<Result> results) {
        Result best = results.get(0);
        for (Result r : results) {
            if (r.getNanos() < best.getNanos()) {
                best = r;
            }
        }
        return best;
    }

    /**
     * Java runs code slower the very first time. Running each sort once on a
     * small sample first makes the real timings fair.
     */
    private void warmUp(Map<String, Consumer<double[]>> algorithms, double[] data) {
        int size = Math.min(data.length, 2000);
        for (Consumer<double[]> sort : algorithms.values()) {
            double[] sample = new double[size];
            System.arraycopy(data, 0, sample, 0, size);
            sort.accept(sample);
        }
    }
}