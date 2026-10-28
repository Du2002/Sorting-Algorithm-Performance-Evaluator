import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.List;

/**
 * The main window: upload a CSV, pick a numeric column,
 * run the five sorting algorithms and show their execution times.
 */
public class MainFrame extends JFrame {

    // ---------- Colours ----------
    private static final Color PRIMARY    = new Color(88, 44, 140);    // dark purple
    private static final Color ACCENT     = new Color(142, 84, 214);   // bright purple
    private static final Color SUCCESS    = new Color(39, 163, 98);    // green (unchanged)
    private static final Color BACKGROUND = new Color(247, 242, 252);  // very light purple
    private static final Color ROW_ALT    = new Color(237, 228, 248);  // striped rows (light purple)
    private static final Color BEST_ROW   = new Color(198, 239, 211);  // light green (unchanged)
    private static final Color TEXT       = new Color(48, 30, 70);     // dark purple text

    private final CsvReaderUtil csvReader = new CsvReaderUtil();
    private final PerformanceEvaluator evaluator = new PerformanceEvaluator();

    private final JButton uploadButton = new JButton("Upload CSV");
    private final JLabel fileLabel = new JLabel("No file selected");
    private final JComboBox<String> columnBox = new JComboBox<>();
    private final JButton sortButton = new JButton("Sort");
    private final DefaultTableModel tableModel =
            new DefaultTableModel(new String[]{"Algorithm", "Time (ms)"}, 0);
    private final JLabel bestLabel = new JLabel("Best performing algorithm: -");

    private String bestName = null; // name of the fastest algorithm (for highlighting)

    public MainFrame() {
        setTitle("Sorting Algorithm Performance Evaluator");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(650, 520);
        setLocationRelativeTo(null); // centre on screen
        getContentPane().setBackground(BACKGROUND);
        setLayout(new BorderLayout(10, 10));

        add(buildTopArea(), BorderLayout.NORTH);
        add(buildResultsPanel(), BorderLayout.CENTER);

        styleButton(uploadButton, ACCENT);
        styleButton(sortButton, SUCCESS);

        // Nothing to sort until a file is loaded
        columnBox.setEnabled(false);
        sortButton.setEnabled(false);

        uploadButton.addActionListener(e -> chooseFile());
        sortButton.addActionListener(e -> runSort());
    }

    /** Gives a button a solid colour, white bold text and a hand cursor. */
    private void styleButton(JButton button, Color colour) {
        button.setBackground(colour);
        button.setForeground(Color.WHITE);
        button.setFont(button.getFont().deriveFont(Font.BOLD, 14f));
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    /** Top area: purple title bar plus the controls underneath it. */
    private JPanel buildTopArea() {
        // Title bar
        JPanel header = new JPanel(new GridLayout(2, 1));
        header.setBackground(PRIMARY);
        header.setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));

        JLabel title = new JLabel("Sorting Algorithm Performance Evaluator");
        title.setForeground(Color.WHITE);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 20f));

        JLabel subtitle = new JLabel("Upload a CSV, pick a numeric column, compare 5 algorithms, display the best-performing algorithm");
        subtitle.setForeground(new Color(200, 220, 245));

        header.add(title);
        header.add(subtitle);

        // Controls
        JPanel controls = new JPanel(new GridLayout(2, 1, 5, 5));
        controls.setBackground(BACKGROUND);
        controls.setBorder(BorderFactory.createEmptyBorder(10, 15, 0, 15));

        fileLabel.setForeground(TEXT);
        fileLabel.setFont(fileLabel.getFont().deriveFont(Font.BOLD));

        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        row1.setBackground(BACKGROUND);
        row1.add(uploadButton);
        row1.add(fileLabel);

        JLabel columnText = new JLabel("Column to sort:");
        columnText.setForeground(TEXT);
        columnText.setFont(columnText.getFont().deriveFont(Font.BOLD));

        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        row2.setBackground(BACKGROUND);
        row2.add(columnText);
        row2.add(columnBox);
        row2.add(sortButton);

        controls.add(row1);
        controls.add(row2);

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(BACKGROUND);
        top.add(header, BorderLayout.NORTH);
        top.add(controls, BorderLayout.CENTER);
        return top;
    }

    /** Centre area: results table and the "best algorithm" banner. */
    private JPanel buildResultsPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 10));
        panel.setBackground(BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 15, 15, 15));

        JTable table = new JTable(tableModel);
        table.setEnabled(false); // read-only
        table.setRowHeight(30);
        table.setFont(table.getFont().deriveFont(14f));
        table.setGridColor(new Color(210, 220, 235));

        // Table header colours
        table.getTableHeader().setBackground(PRIMARY);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(table.getFont().deriveFont(Font.BOLD, 14f));

        // Striped rows, with the fastest algorithm highlighted in green
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean selected, boolean focus, int row, int column) {
                Component c = super.getTableCellRendererComponent(t, value, selected, focus, row, column);
                String name = (String) t.getValueAt(row, 0);
                if (name.equals(bestName)) {
                    c.setBackground(BEST_ROW);
                    c.setFont(c.getFont().deriveFont(Font.BOLD));
                } else {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : ROW_ALT);
                    c.setFont(c.getFont().deriveFont(Font.PLAIN));
                }
                c.setForeground(TEXT);
                return c;
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(200, 212, 230)));

        // Banner showing the winner
        bestLabel.setOpaque(true);
        bestLabel.setBackground(PRIMARY);
        bestLabel.setForeground(Color.WHITE);
        bestLabel.setFont(bestLabel.getFont().deriveFont(Font.BOLD, 16f));
        bestLabel.setHorizontalAlignment(SwingConstants.CENTER);
        bestLabel.setBorder(BorderFactory.createEmptyBorder(12, 10, 12, 10));

        panel.add(scroll, BorderLayout.CENTER);
        panel.add(bestLabel, BorderLayout.SOUTH);
        return panel;
    }

    /** Opens the file dialog and loads the chosen CSV. */
    private void chooseFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select a CSV file");
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return; // user cancelled
        }

        File file = chooser.getSelectedFile();
        try {
            csvReader.load(file);

            columnBox.removeAllItems();
            for (String header : csvReader.getHeaders()) {
                columnBox.addItem(header);
            }
            columnBox.setEnabled(true);
            sortButton.setEnabled(true);
            fileLabel.setText(file.getName() + "  (" + csvReader.getRowCount() + " rows)");
            tableModel.setRowCount(0);
            bestName = null;
            bestLabel.setText("Best performing algorithm: -");
        } catch (Exception ex) {
            columnBox.removeAllItems();
            columnBox.setEnabled(false);
            sortButton.setEnabled(false);
            fileLabel.setText("No file selected");
            showError(ex.getMessage());
        }
    }

    /** Extracts the selected column, runs all sorts, and shows the results. */
    private void runSort() {
        int index = columnBox.getSelectedIndex();
        if (index < 0) {
            showError("Please select a column first.");
            return;
        }
        if (!csvReader.isNumericColumn(index)) {
            showError("Column '" + columnBox.getSelectedItem()
                    + "' is not numeric. Please select a numeric column.");
            return;
        }

        sortButton.setEnabled(false);
        bestLabel.setText("Sorting, please wait...");

        // Run in the background so the window doesn't freeze on big files
        new SwingWorker<List<PerformanceEvaluator.Result>, Void>() {
            private String errorMessage = null;

            @Override
            protected List<PerformanceEvaluator.Result> doInBackground() {
                try {
                    double[] data = csvReader.getNumericColumn(index);
                    return evaluator.evaluate(data);
                } catch (Exception ex) {
                    errorMessage = ex.getMessage();
                    return null;
                }
            }

            @Override
            protected void done() {
                sortButton.setEnabled(true);
                if (errorMessage != null) {
                    bestLabel.setText("Best performing algorithm: -");
                    showError(errorMessage);
                    return;
                }
                try {
                    showResults(get());
                } catch (Exception ex) {
                    showError("Something went wrong while sorting.");
                }
            }
        }.execute();
    }

    /** Fills the table and highlights the fastest algorithm. */
    private void showResults(List<PerformanceEvaluator.Result> results) {
        PerformanceEvaluator.Result best = evaluator.findBest(results);
        bestName = best.getName();

        tableModel.setRowCount(0);
        for (PerformanceEvaluator.Result r : results) {
            tableModel.addRow(new Object[]{r.getName(), String.format("%.3f", r.getMillis())});
        }
        bestLabel.setText(String.format("Best algorithm: %s (%.3f ms)", best.getName(), best.getMillis()));
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}