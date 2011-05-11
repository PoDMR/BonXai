package de.tudortmund.cs.bonxai.tools;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * Custom format for all log-messages from components within this application
 * @author Lars Schmidt
 */
class StatusFormatter extends Formatter {
    /**
     * Format a given LogRecord and return a String containing the neccessary
     * information
     * @param r         LogRecord with all message information
     * @return String   The resulting log-message
     */
    @Override
    public String format(final LogRecord r) {
        StringBuilder sb = new StringBuilder();
        String dateFormat = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        sb.append(sdf.format(r.getMillis())).append(" - ");
        sb.append(r.getLevel()).append(": ");
        sb.append(formatMessage(r)).append(System.getProperty("line.separator"));

        if (null != r.getThrown()) {
            sb.append("Throwable occurred: ");
            Throwable t = r.getThrown();
            PrintWriter pw = null;
            try {
                StringWriter sw = new StringWriter();
                pw = new PrintWriter(sw);
                t.printStackTrace(pw);
                sb.append(sw.toString());
            } finally {
                if (pw != null) {
                    try {
                        pw.close();
                    } catch (Exception e) {
                    }
                }
            }
        }
        return sb.toString();
    }
}
