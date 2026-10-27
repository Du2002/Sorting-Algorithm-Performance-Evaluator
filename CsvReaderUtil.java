import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads a CSV file, keeps the column names (headers),
 * and can extract any column as an array of numbers.
 */
public class CsvReaderUtil {

    private final List<String> headers = new ArrayList<>();
    private final List<String[]> rows = new ArrayList<>();

    /** Loads the CSV file. Throws IOException with a friendly message on problems. */
    public void load(File file) throws IOException {
        if (file == null || !file.exists()) {
            throw new IOException("File does not exist.");
        }
        if (!file.getName().toLowerCase().endsWith(".csv")) {
            throw new IOException("Invalid file type. Please select a .csv file.");
        }

        headers.clear();
        rows.clear();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = br.readLine();
            if (line == null || line.trim().isEmpty()) {
                throw new IOException("The file is empty.");
            }

            // Some CSV files start with an invisible BOM character; remove it
            if (line.startsWith("\uFEFF")) {
                line = line.substring(1);
            }

            for (String h : parseLine(line)) {
                headers.add(h.trim());
            }

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue; // skip blank lines
                }
                rows.add(parseLine(line));
            }
        }

        if (rows.isEmpty()) {
            throw new IOException("The file has column names but no data rows.");
        }
    }

    public List<String> getHeaders() {
        return headers;
    }

    public int getRowCount() {
        return rows.size();
    }

    /** True if at least 90% of the filled cells in this column are numbers. */
    public boolean isNumericColumn(int columnIndex) {
        if (columnIndex < 0 || columnIndex >= headers.size()) {
            return false;
        }
        int filled = 0;
        int numeric = 0;
        for (String[] row : rows) {
            if (columnIndex >= row.length) {
                continue;
            }
            String cell = row[columnIndex].trim();
            if (cell.isEmpty()) {
                continue;
            }
            filled++;
            if (isNumber(cell)) {
                numeric++;
            }
        }
        return filled > 0 && numeric >= 0.9 * filled;
    }

    /**
     * Returns the values of one column as doubles.
     * Blank and non-numeric cells are skipped.
     */
    public double[] getNumericColumn(int columnIndex) throws IOException {
        if (columnIndex < 0 || columnIndex >= headers.size()) {
            throw new IOException("Invalid column selected.");
        }

        List<Double> values = new ArrayList<>();
        for (String[] row : rows) {
            if (columnIndex >= row.length) {
                continue; // row is shorter than expected
            }
            String cell = row[columnIndex].trim();
            if (cell.isEmpty()) {
                continue;
            }
            if (isNumber(cell)) {
                values.add(Double.parseDouble(cell));
            }
        }

        if (values.isEmpty()) {
            throw new IOException("Column '" + headers.get(columnIndex)
                    + "' has no numeric values. Please select a numeric column.");
        }

        double[] result = new double[values.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = values.get(i);
        }
        return result;
    }

    /** True only for plain numbers like 12, -3.5, .5 or 1e5. */
    private boolean isNumber(String s) {
        return s.matches("[-+]?(\\d+\\.?\\d*|\\.\\d+)([eE][-+]?\\d+)?");
    }

    /** Splits one CSV line into cells, respecting "quoted, values". */
    private String[] parseLine(String line) {
        List<String> cells = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                cells.add(current.toString());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }
        cells.add(current.toString());
        return cells.toArray(new String[0]);
    }

}