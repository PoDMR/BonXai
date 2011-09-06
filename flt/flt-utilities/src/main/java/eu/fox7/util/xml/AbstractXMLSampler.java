/*
 * Created on Nov 21, 2006
 * Modified on $Date: 2009-11-10 10:06:01 $
 */
package eu.fox7.util.xml;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public abstract class AbstractXMLSampler {

    public static final String DEFAULT_FILE_SUFFIX = ".xml";
    protected String fileSuffix = DEFAULT_FILE_SUFFIX;
    protected Stack<Element> ancestorPath = new Stack<Element>();
    protected int maxFiles = Integer.MAX_VALUE;
    protected int currentFiles = 0;
    protected boolean isVerbose = false;
    protected Writer writer = new OutputStreamWriter(System.err);
    protected List<File> parsedFiles = new LinkedList<File>();
    protected List<File> nonParsedFiles = new LinkedList<File>();

    public AbstractXMLSampler() {
        super();
    }

    public void parse(Reader inReader)
            throws DocumentException, ConfigurationException, IOException {
        SAXReader reader = new SAXReader();
        add(reader.read(inReader));
    }

    public void parse(URL url)
            throws DocumentException, IOException, ConfigurationException {
        SAXReader reader = new SAXReader();
        add(reader.read(url));
    }

    public void parse(File file) throws IOException, ConfigurationException {
        try {
            if (file.isFile() && file.getName().toLowerCase().endsWith(fileSuffix)) {
                if (currentFiles++ >= getMaxFiles())
                    return;
                if (isVerbose()) {
                	writer.write("# reading " + file.getName() + ", " + currentFiles + "\n");
                	writer.flush();
                }
                try {
                    parse(file.toURI().toURL());
                    parsedFiles.add(file);
                } catch (DocumentException e) {
                    nonParsedFiles.add(file);
                }
            } else if (file.isDirectory()) {
                List<File> files = Arrays.asList(file.listFiles());
                Collections.sort(files);
                for (File f : files) {
                    parse(f);
                    if (currentFiles >= getMaxFiles())
                        return;
                }
            } else if (!file.exists()){
            	throw new IOException("resource doesn't exist: " + file.getPath());
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void parse(String fileName) throws IOException, ConfigurationException {
        parse(new File(fileName));
    }

    abstract protected void add(Document document)
            throws ConfigurationException, IOException;

    protected String[] getAncestorString() {
        String[] ancStr = new String[ancestorPath.size()];
        int i = 0;
        for (Iterator<Element> elemIt = ancestorPath.iterator(); elemIt.hasNext(); )
            ancStr[i++] = elemIt.next().getQualifiedName();
        return ancStr;
    }

    protected String[] getChildString(Element element) {
        List<String> childStr = new LinkedList<String>();
        for (Iterator<?> elemIt = element.elementIterator(); elemIt.hasNext(); )
            childStr.add(((Element) elemIt.next()).getQualifiedName());
        return childStr.toArray(new String[0]);
    }

    public int getMaxFiles() {
        return maxFiles;
    }

    public void setMaxFiles(int maxFiles) {
        this.maxFiles = maxFiles;
    }

    public boolean isVerbose() {
        return isVerbose;
    }

    public void setVerbose(boolean isVerbose) {
        this.isVerbose = isVerbose;
    }

    public Writer getWriter() {
        return writer;
    }

    public void setWriter(Writer writer) {
        this.writer = writer;
    }

    public String getFileSuffix() {
        return fileSuffix;
    }

    public void setFileSuffix(String fileSuffix) {
        this.fileSuffix = fileSuffix;
    }

    public List<File> getParsedFiles() {
        return parsedFiles;
    }

    public List<File> getNonParsedFiles() {
        return nonParsedFiles;
    }

}