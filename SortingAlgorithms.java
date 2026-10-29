/**
 * Contains the five sorting algorithms.
 * Each method sorts the given array in ascending order.
 */
public class SortingAlgorithms {

    // ---------- 1. Insertion Sort ----------
    // Takes each element and inserts it into its correct place on the left side.
    public static void insertionSort(double[] a) {
        for (int i = 1; i < a.length; i++) {
            double key = a[i];
            int j = i - 1;
            while (j >= 0 && a[j] > key) {
                a[j + 1] = a[j]; // shift bigger values to the right
                j--;
            }
            a[j + 1] = key;
        }
    }

    // ---------- 2. Shell Sort ----------
    // Insertion sort on elements far apart (the "gap"), shrinking the gap to 1.
    public static void shellSort(double[] a) {
        int n = a.length;
        for (int gap = n / 2; gap > 0; gap /= 2) {
            for (int i = gap; i < n; i++) {
                double temp = a[i];
                int j = i;
                while (j >= gap && a[j - gap] > temp) {
                    a[j] = a[j - gap];
                    j -= gap;
                }
                a[j] = temp;
            }
        }
    }

    // ---------- 3. Merge Sort ----------
    // Splits the array in half, sorts each half, then merges the two sorted halves.
    public static void mergeSort(double[] a) {
        if (a.length < 2) {
            return;
        }
        double[] temp = new double[a.length];
        mergeSort(a, temp, 0, a.length - 1);
    }

    private static void mergeSort(double[] a, double[] temp, int left, int right) {
        if (left >= right) {
            return;
        }
        int mid = (left + right) / 2;
        mergeSort(a, temp, left, mid);
        mergeSort(a, temp, mid + 1, right);
        merge(a, temp, left, mid, right);
    }

    private static void merge(double[] a, double[] temp, int left, int mid, int right) {
        int i = left;
        int j = mid + 1;
        int k = left;

        while (i <= mid && j <= right) {
            if (a[i] <= a[j]) {
                temp[k++] = a[i++];
            } else {
                temp[k++] = a[j++];
            }
        }
        while (i <= mid) {
            temp[k++] = a[i++];
        }
        while (j <= right) {
            temp[k++] = a[j++];
        }
        for (k = left; k <= right; k++) {
            a[k] = temp[k];
        }
    }

    // ---------- 4. Quick Sort ----------
    // Picks a pivot, moves smaller values left and bigger values right, repeats on both sides.
    public static void quickSort(double[] a) {
        if (a.length < 2) {
            return;
        }
        quickSort(a, 0, a.length - 1);
    }

    private static void quickSort(double[] a, int low, int high) {
        int i = low;
        int j = high;
        double pivot = a[(low + high) / 2]; // middle element as pivot

        while (i <= j) {
            while (a[i] < pivot) {
                i++;
            }
            while (a[j] > pivot) {
                j--;
            }
            if (i <= j) {
                double t = a[i];
                a[i] = a[j];
                a[j] = t;
                i++;
                j--;
            }
        }
        if (low < j) {
            quickSort(a, low, j);
        }
        if (i < high) {
            quickSort(a, i, high);
        }
    }

    // ---------- 5. Heap Sort ----------
    // Builds a max-heap, then repeatedly moves the biggest value to the end.
    public static void heapSort(double[] a) {
        int n = a.length;

        // build the max-heap
        for (int i = n / 2 - 1; i >= 0; i--) {
            heapify(a, n, i);
        }
        // move the largest to the end, shrink the heap, repeat
        for (int i = n - 1; i > 0; i--) {
            double t = a[0];
            a[0] = a[i];
            a[i] = t;
            heapify(a, i, 0);
        }
    }

    private static void heapify(double[] a, int size, int root) {
        int largest = root;
        int left = 2 * root + 1;
        int right = 2 * root + 2;

        if (left < size && a[left] > a[largest]) {
            largest = left;
        }
        if (right < size && a[right] > a[largest]) {
            largest = right;
        }
        if (largest != root) {
            double t = a[root];
            a[root] = a[largest];
            a[largest] = t;
            heapify(a, size, largest);
        }
    }
}