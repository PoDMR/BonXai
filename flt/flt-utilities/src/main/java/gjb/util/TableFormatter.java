/*
 * Created on Aug 3, 2006
 * Modified on $Date: 2009-10-26 18:37:39 $
 */
package gjb.util;

/**
 * @author gjb
 * @version $Revision: 1.1 $
 * 
 */
public class TableFormatter {

    public static final String DEFAULT_NUM_FORMAT = "g";
    public static final int DEFAULT_COL_SEP = 2;
    public static final int DEFAULT_COL_WIDTH = 0;
    public static final int DEFAULT_PREC = 4;
    protected String numericalFormat = DEFAULT_NUM_FORMAT;
    protected int columnSeparation = DEFAULT_COL_SEP;
    protected int columnWidth = DEFAULT_COL_WIDTH;
    protected int precision = DEFAULT_PREC;
    
    public int getColumnSeparation() {
        return columnSeparation;
    }

    public void setColumnSeparation(int columnSeparation) {
        this.columnSeparation = columnSeparation;
    }

    public String getNumericalFormat() {
        return numericalFormat;
    }

    public void setNumericalFormat(String numericalFormat) {
        this.numericalFormat = numericalFormat;
    }

    public int getColumnWidth() {
        return columnWidth;
    }

    public void setColumnWidth(int colWidth) {
        this.columnWidth = colWidth;
    }

    public int getPrecision() {
        return precision;
    }

    public void setPrecision(int precision) {
        this.precision = precision;
    }

    public String format(String[] columnLabels, String[] rowLabels, double[][] data) {
        return format(columnLabels, rowLabels, data, getColumnWidth(), getPrecision());
    }

    public String format(String[] columnLabels, String[] rowLabels,
                         double[][] data, int colWidth, int precision) {
        int[] colWidths = new int[columnLabels.length];
        int[] precs = new int[columnLabels.length];
        for (int i = 0; i < colWidths.length; i++) {
            colWidths[i] = colWidth;
            precs[i] = precision;
        }
        return format(columnLabels, rowLabels, data, colWidths, precs);
    }

    public String format(String[] columnLabels, String[] rowLabels,
                         double[][] data, int[] colWidths, int[] precs) {
        StringBuilder str = new StringBuilder();
        int rowLabelWidth = getRowLabelWidth(rowLabels);
        String labelFormat = "%-" + rowLabelWidth + "s";
        if (rowLabelWidth > 0) {
            str.append(String.format(labelFormat, ""));
        }
        int[] widths = new int[columnLabels.length];
        for (int colNr = 0; colNr < columnLabels.length; colNr++) {
            widths[colNr] = Math.max(colWidths[colNr],
                                     columnLabels[colNr].length()) + getColumnSeparation();
            String format = "%" + widths[colNr] + "s";
            str.append(String.format(format, columnLabels[colNr]));
        }
        str.append("\n");
        for (int rowNr = 0; rowNr < data.length; rowNr++) {
            if (rowLabelWidth > 0) {
                str.append(String.format(labelFormat, rowLabels[rowNr]));
            }
            for (int colNr = 0; colNr < data[rowNr].length; colNr++) {
                String format = "%" + widths[colNr] + "." + precs[colNr] + getNumericalFormat();
                str.append(String.format(format, data[rowNr][colNr]));
            }
            str.append("\n");
        }
        return str.toString();
    }

    protected int getRowLabelWidth(String[] rowLabels) {
        if (rowLabels == null || rowLabels.length == 0)
            return 0;
        else {
            int width = 0;
            for (int rowNr = 0; rowNr < rowLabels.length; rowNr++)
                width = Math.max(width, rowLabels[rowNr].length());
            return width + getColumnSeparation();
        }
    }

}
