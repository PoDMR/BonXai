/*
 * Created on Aug 5, 2008
 * Modified on $Date: 2009-11-04 15:27:36 $
 */
package gjb.util.datafolder;

import gjb.util.datafolder.DataSet.ColumnHeader;

import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.util.Iterator;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

/**
 * @author gjb
 * @version $Revision: 1.1 $
 * 
 */
public class XMLDataWriter implements DataWriter {

    protected Writer writer;

    private XMLDataWriter() {
        super();
    }

    public XMLDataWriter(Writer writer) {
        this();
        this.writer = writer;
    }

    public void write(Data data) throws IOException {
        OutputFormat format = OutputFormat.createPrettyPrint();
        XMLWriter xmlWriter = new XMLWriter(this.writer, format);
        xmlWriter.write(toXML(data));
    }

    public void close() throws IOException {
        this.writer.close();
    }

    protected Document toXML(Data data) {
        Document doc = DocumentHelper.createDocument();
        Element dataElement = doc.addElement("data");
        dataElement.addAttribute("name", data.getName());
        dataElement.addAttribute("creator", data.getCreator());
        String dateStr = DateFormat.getDateInstance().format(data.getDate());
        dataElement.addAttribute("date", dateStr);
        if (data.hasDescription())
            toXML(dataElement, data.getDescription());
        dataElement.addAttribute("date", data.getDate().toString());
        for (Iterator<DataItem> it = data.getItemIterator(); it.hasNext();) {
            DataItem dataItem = it.next();
            toXML(dataElement, dataItem);
        }
        return doc;
    }

    protected Element toXML(Element parent, DataItem dataItem) {
        if (dataItem instanceof Group)
            return toXML(parent, (AbstractDataCollection) dataItem);
        else
            return toXML(parent, (DataSet) dataItem);
    }

    protected Element toXML(Element parent, DataSet dataSet) {
        Element dataSetElement = parent.addElement("dataset");
        dataSetElement.addAttribute("name", dataSet.getName());
        if (dataSet.hasDescription())
            toXML(dataSetElement, dataSet.getDescription());
        Element dataHeaderElement = dataSetElement.addElement("dh");
        for (Iterator<String> chIt = dataSet.getColumnNames(); chIt.hasNext();) {
            String columnName = chIt.next();
            ColumnHeader header = dataSet.columnHeaders.get(columnName);
            toXML(dataHeaderElement, header);
        }
        for (Iterator<Object[]> drIt = dataSet.getObjectRows(); drIt.hasNext();) {
            Element drElement = dataSetElement.addElement("dr");
            Object[] dr = drIt.next();
            for (int i = 0; i < dr.length; i++) {
                Element dvElement = drElement.addElement("dv");
                dvElement.addText(dr[i].toString());
            }
        }
        return dataSetElement;
    }

    protected Element toXML(Element parent, String description) {
        Element descriptionElement = parent.addElement("description");
        descriptionElement.addText(description);
        return descriptionElement;
    }

    protected Element toXML(Element parent, AbstractDataCollection group) {
        Element groupElement = parent.addElement("group");
        groupElement.addAttribute("name", group.getName());
        if (group.hasDescription())
            toXML(groupElement, group.getDescription());
        for (Iterator<DataItem> it = group.getItemIterator(); it.hasNext();) {
            DataItem dataItem = it.next();
            toXML(groupElement, dataItem);
        }
        return groupElement;
    }
    
    protected Element toXML(Element parent, ColumnHeader ch) {
        Element element = parent.addElement("ch");
        element.addAttribute("name", ch.getName());
        element.addAttribute("type", DataSet.typeToString(ch.getType()));
        return element;
    }

}
