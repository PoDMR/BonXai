/*
 * Created on Aug 3, 2006
 * Modified on $Date: 2009-10-26 18:37:39 $
 */
package gjb.util;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author gjb
 * @version $Revision: 1.1 $
 * 
 */
public class TableFormatterTest extends TestCase {

    public static Test suite() {
        return new TestSuite(TableFormatterTest.class);
    }

    public void test_columnLabels() {
        String targetString = "  a  bc  def  ghi\n";
        String[] columnLabels = {"a", "bc", "def", "ghi"};
        TableFormatter tf = new TableFormatter();
        String tableStr = tf.format(columnLabels, null, new double[][] {});
        assertEquals("headings", targetString, tableStr);
    }

    public void test_data() {
        String targetString =
            "       a      bc     def     ghi\n" +
            "   1.000   4.000   9.000   16.00\n" +
            "   1.000   1.414   1.732   2.000\n";
        String[] columnLabels = {"a", "bc", "def", "ghi"};
        final int colWidth = 6;
        int[] colWidths = {colWidth, colWidth, colWidth, colWidth};
        final int precision = 4;
        int[] precs = {precision, precision, precision, precision};
        double[][] data = new double[2][precision];
        for (int colNr = 0; colNr < data[0].length; colNr++)
            data[0][colNr] = (colNr + 1.0)*(colNr + 1.0);
        for (int colNr = 0; colNr < data[1].length; colNr++)
            data[1][colNr] = Math.sqrt(colNr + 1.0);
        TableFormatter tf = new TableFormatter();
        assertEquals("arrays",
                     targetString,
                     tf.format(columnLabels, null, data, colWidths, precs));
        assertEquals("ints",
                     targetString,
                     tf.format(columnLabels, null, data, colWidth, precision));
    }

    public void test_rowLabels() {
        String targetString =
            "           a      bc     def     ghi\n" +
            "A      1.000   4.000   9.000   16.00\n" +
            "BC     1.000   1.414   1.732   2.000\n";
        String[] columnLabels = {"a", "bc", "def", "ghi"};
        String[] rowLabels = {"A", "BC"};
        final int colWidth = 6;
        int[] colWidths = {colWidth, colWidth, colWidth, colWidth};
        final int precision = 4;
        int[] precs = {precision, precision, precision, precision};
        double[][] data = new double[2][precision];
        for (int colNr = 0; colNr < data[0].length; colNr++)
            data[0][colNr] = (colNr + 1.0)*(colNr + 1.0);
        for (int colNr = 0; colNr < data[1].length; colNr++)
            data[1][colNr] = Math.sqrt(colNr + 1.0);
        TableFormatter tf = new TableFormatter();
        assertEquals("arrays",
                     targetString,
                     tf.format(columnLabels, rowLabels, data, colWidths, precs));
    }

}
