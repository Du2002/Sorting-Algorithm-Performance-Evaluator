import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * CsvReaderUtil
 * -----------------------------------------------------------------
 * HOW THE GUI USES IT:
 *   load(file)            -> user uploads a file
 *   getHeaders()          -> show column names in the dropdown
 *   isNumericColumn(i)    -> check the chosen column is numeric
 *   getNumericColumn(i)   -> get the numbers to sort
 */
public class CsvReaderUtil {

    // ===================== FIELDS (data stored in memory) =====================

    // Stores the column names, e.g. ["Name", "Age", "City"].
    // 'final' means the list object itself never changes; we only add/clear items.
    private final List<String> headers = new ArrayList<>();

    // Stores every data line. Each line is a String[] (one entry per cell),
    // e.g. ["Nimal", "25", "Colombo, Sri Lanka"].
    // Together, headers + rows = the whole table in memory.
    private final List<String[]> rows = new ArrayList<>();

    // ===================== METHOD 1: load() =====================

    /**
     * Loads the CSV file into memory.
     * Throws IOException with a friendly message if anything is wrong.
     * (The GUI catches this exception and shows the message to the user.)
     */
    public void load(File file) throws IOException {

        // --- STEP A: Validate the file (edge case: invalid file upload) ---
        // Check 1: no file chosen, or the file is not on the disk.
        if (file == null || !file.exists()) {
            throw new IOException("File does not exist.");
        }
        // Check 2: file name must end with ".csv" (toLowerCase handles ".CSV" too).
        if (!file.getName().toLowerCase().endsWith(".csv")) {
            throw new IOException("Invalid file type. Please select a .csv file.");
        }

        // --- STEP B: Clear old data ---
        // If the user uploads a second file, remove the first file's data
        // so the two files do not get mixed together.
        headers.clear();
        rows.clear();

        // --- STEP C: Open the file ---
        // BufferedReader reads the file line by line (fast and memory friendly).
        // "try-with-resources" (the brackets after 'try') automatically closes
        // the file when we finish, even if an error happens.
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {

            // The first line of a CSV file is the header line (column names).
            String line = br.readLine();

            // Edge case: empty file. readLine() returns null if there is nothing,
            // and trim().isEmpty() catches a line that has only spaces.
            if (line == null || line.trim().isEmpty()) {
                throw new IOException("The file is empty.");
            }

            // --- STEP D: Remove the BOM character ---
            // Files saved from Excel often start with an invisible character
            // (BOM = Byte Order Mark, written as \uFEFF).
            // If we keep it, the first column name would be wrong (e.g. "\uFEFFName").
            // substring(1) cuts off the first character.
            if (line.startsWith("\uFEFF")) {
                line = line.substring(1);
            }

            // --- STEP E: Save the headers ---
            // parseLine() splits the header line into separate cells.
            // trim() removes extra spaces around each column name.
            for (String h : parseLine(line)) {
                headers.add(h.trim());
            }

            // --- STEP F: Read all the data rows ---
            // Keep reading line by line until readLine() returns null (end of file).
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue; // blank line -> skip it and go to the next line
                }
                // Split the line into cells and store it as one row.
                rows.add(parseLine(line));
            }
        }

        // --- STEP G: Check that real data exists ---
        // Edge case: file has only the header line and no data rows.
        if (rows.isEmpty()) {
            throw new IOException("The file has column names but no data rows.");
        }
    }

    // ===================== METHOD 2 & 3: simple getters =====================

    // Returns the column names. The GUI uses this to fill the column dropdown.
    public List<String> getHeaders() {
        return headers;
    }

    // Returns how many data rows were loaded. The GUI can display this number.
    public int getRowCount() {
        return rows.size();
    }

    // ===================== METHOD 4: isNumericColumn() =====================

    /**
     * Decides whether a column is a "numeric column".
     * Returns true if at least 90% of the filled cells are numbers.
     * (The assignment says we must only sort numeric columns.)
     */
    public boolean isNumericColumn(int columnIndex) {

        // Safety check: the column number must exist (0 up to headers.size()-1).
        if (columnIndex < 0 || columnIndex >= headers.size()) {
            return false;
        }

        int filled = 0;   // how many cells in this column are NOT empty
        int numeric = 0;  // how many of those filled cells are numbers

        // Look at this column in every row.
        for (String[] row : rows) {

            // Edge case: some rows are shorter than the header row,
            // so this column may not exist in that row. Skip it.
            if (columnIndex >= row.length) {
                continue;
            }

            String cell = row[columnIndex].trim();

            // Empty cells are ignored (they do not count for or against).
            if (cell.isEmpty()) {
                continue;
            }

            filled++;               // this cell has a value
            if (isNumber(cell)) {   // and it is a valid number
                numeric++;
            }
        }

        // Numeric column = it has some data AND at least 90% of it is numbers.
        // The 90% rule allows a few dirty cells in real datasets, but still
        // rejects text columns like "Name" or "City".
        return filled > 0 && numeric >= 0.9 * filled;
    }

    // ===================== METHOD 5: getNumericColumn() =====================

    /**
     * Returns the values of one column as a double[] array.
     * This is the array the sorting algorithms will sort.
     * Blank and non-numeric cells are skipped.
     */
    public double[] getNumericColumn(int columnIndex) throws IOException {

        // Safety check: invalid column number.
        if (columnIndex < 0 || columnIndex >= headers.size()) {
            throw new IOException("Invalid column selected.");
        }

        // We use a List first because we do not know in advance how many
        // valid numbers there will be (some cells may be skipped).
        List<Double> values = new ArrayList<>();

        for (String[] row : rows) {
            if (columnIndex >= row.length) {
                continue; // row is shorter than expected -> skip
            }
            String cell = row[columnIndex].trim();
            if (cell.isEmpty()) {
                continue; // blank cell -> skip
            }
            if (isNumber(cell)) {
                // Convert the text (e.g. "25") into a real number (25.0).
                values.add(Double.parseDouble(cell));
            }
        }

        // Edge case: the column has no numbers at all (e.g. user picked "Name").
        if (values.isEmpty()) {
            throw new IOException("Column '" + headers.get(columnIndex)
                    + "' has no numeric values. Please select a numeric column.");
        }

        // Convert List<Double> into a plain double[] array,
        // because the sorting algorithms work on arrays.
        double[] result = new double[values.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = values.get(i);
        }
        return result;
    }

    // ===================== METHOD 6: isNumber() =====================

    /**
     * Checks if a piece of text is a valid number.
     * Uses a regex (a text pattern). Valid examples: 12, -3.5, .5, 1e5
     *
     * Pattern explained:
     *   [-+]?                 optional minus or plus sign
     *   (\d+\.?\d*            digits, optional dot, optional more digits  (12, 3.5)
     *    | \.\d+)             OR a number starting with a dot             (.5)
     *   ([eE][-+]?\d+)?       optional exponent part                      (1e5, 2E-3)
     *
     * Note: in Java strings the backslash must be doubled, so \d is written \\d.
     */
    private boolean isNumber(String s) {
        return s.matches("[-+]?(\\d+\\.?\\d*|\\.\\d+)([eE][-+]?\\d+)?");
    }

    // ===================== METHOD 7: parseLine() =====================

    /**
     * Splits one CSV line into cells.
     * We cannot just use line.split(",") because of quoted values like:
     *     Nimal,25,"Colombo, Sri Lanka"
     * split(",") would wrongly cut "Colombo, Sri Lanka" into two cells.
     * So we read the line character by character and track if we are
     * inside quotes.
     */
    private String[] parseLine(String line) {
        List<String> cells = new ArrayList<>();       // finished cells
        StringBuilder current = new StringBuilder();  // the cell being built now
        boolean inQuotes = false;                     // are we inside "..." right now?

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                // A quote switches the flag ON/OFF. The quote itself is not saved.
                inQuotes = !inQuotes;

            } else if (c == ',' && !inQuotes) {
                // A comma OUTSIDE quotes = end of this cell.
                // Save the finished cell and start a new empty one.
                cells.add(current.toString());
                current.setLength(0);

            } else {
                // Any other character (or a comma INSIDE quotes) is normal text.
                current.append(c);
            }
        }

        // The last cell has no comma after it, so add it manually at the end.
        cells.add(current.toString());

        // Convert the List into a String array and return it.
        return cells.toArray(new String[0]);
    }

}