/*
 * Created on Mar 19, 2007
 * Modified on $Date: 2009-11-09 11:52:43 $
 */
package eu.fox7.util.xml.acstring;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;

/**
 * @author eu.fox7
 * @version $Revision: 1.1 $
 * 
 */
public class AncestorChildrenDocumentIterator implements Iterator<String> {

    protected BufferedReader bufferedReader;
    protected int offsetDocument = 0;
    protected int numberOfDocumentsToRead = Integer.MAX_VALUE;
    protected int numberOfDocumentsRead = 0;
    protected String previousLine = "";
    protected String currentDocument = "";

    public AncestorChildrenDocumentIterator(String fileName) throws IOException {
        this(new File(fileName));
    }

    public AncestorChildrenDocumentIterator(File file) throws IOException {
        this(new FileReader(file));
    }

    public AncestorChildrenDocumentIterator(Reader reader) throws IOException {
        bufferedReader = new BufferedReader(reader);
        init();
    }

    public AncestorChildrenDocumentIterator(String fileName,
                                          int offsetDocument,
                                          int numberOfDocumentsToRead)
            throws IOException {
        this(new File(fileName), offsetDocument, numberOfDocumentsToRead);
    }
    
    public AncestorChildrenDocumentIterator(File file,
                                          int offsetDocument,
                                          int numberOfDocumentsToRead)
            throws IOException {
        this(new FileReader(file), offsetDocument, numberOfDocumentsToRead);
    }
    
    public AncestorChildrenDocumentIterator(Reader reader,
                                          int offsetDocument,
                                          int numberOfDocumentsToRead)
            throws IOException {
        bufferedReader = new BufferedReader(reader);
        this.offsetDocument = offsetDocument;
        this.numberOfDocumentsToRead = numberOfDocumentsToRead;
        init();
    }

    protected void init() throws IOException {
        for (int i = 0; i < getOffsetDocument(); i++)
            readDocument();
        readDocument();
    }

    public int getNumberOfDocumentsToRead() {
        return numberOfDocumentsToRead;
    }
    
    public int getOffsetDocument() {
        return offsetDocument;
    }

    protected void readDocument() throws IOException {
        StringBuilder str = new StringBuilder(previousLine);
        boolean readingDocument = !previousLine.isEmpty();
        previousLine = "";
        String line = null;
        while ((line = bufferedReader.readLine()) != null) {
            if (line.matches("^/?[\\w-:]+ ::-.*")) {
                if (!readingDocument) {
                    str.append(line).append("\n");
                    readingDocument = true;
                } else {
                    previousLine = line + "\n";
                    break;
                }
            } else {
                str.append(line).append("\n");
            }
        }
        currentDocument = str.toString();
    }

    public boolean hasNext() {
        return !currentDocument.isEmpty() &&
            numberOfDocumentsRead < getNumberOfDocumentsToRead();
    }

    public String next() {
        numberOfDocumentsRead++;
        String doc = currentDocument;
        try {
            readDocument();
        } catch (IOException e) {
            currentDocument = "";
        }
        return doc;
    }

    public void remove() {}

}
