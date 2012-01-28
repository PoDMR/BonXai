/*
 * Created on Aug 5, 2008
 * Modified on $Date: 2009-11-04 15:27:36 $
 */
package eu.fox7.util.datafolder;

import eu.fox7.util.datafolder.DataSet.ColumnHeader;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * @author eu.fox7
 * @version $Revision: 1.1 $
 * 
 */
public class XMLDataReader implements DataReader {

    protected Reader reader;

    private XMLDataReader() {
        super();
    }

    public XMLDataReader(Reader reader) {
        this();
        this.reader = reader;
    }

    public Data read() throws DocumentException {
        SAXReader xmlReader = new SAXReader();
        Document document = xmlReader.read(reader);
        Element dataElement = document.getRootElement();
        String name = dataElement.valueOf("@name");
        String creator = dataElement.valueOf("@creator");
        Data data = new Data(name, creator);
        try {
            // Tue Aug 05 23:49:24 CEST 2008
            DateFormat format = new SimpleDateFormat("EEE MMM dd kk:mm:ss z yyyy");
            Date date = format.parse(dataElement.valueOf("@date"));
            data.setDate(date);
            for (Iterator<?> it = dataElement.elementIterator(); it.hasNext(); ) {
                Element element = (Element) it.next();
                if (element.getName().toLowerCase().equals("description"))
                    setDescription(data, element);
                else if (element.getName().toLowerCase().equals("group"))
                    data.addGroup(parseGroup(element));
                else if (element.getName().toLowerCase().equals("dataset"))
                    data.addDataSet(parseDataSet(element));
            }
            return data;
        } catch (SecurityException e) {
            e.printStackTrace();
            throw new DocumentException(e);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            throw new DocumentException(e);
        } catch (ParseException e) {
            e.printStackTrace();
            throw new DocumentException(e);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new DocumentException(e);
        } catch (SetupException e) {
            e.printStackTrace();
            throw new DocumentException(e);
        } catch (IncompleteRowException e) {
            e.printStackTrace();
            throw new DocumentException(e);
        } catch (AddDataModeViolationException e) {
            e.printStackTrace();
            throw new DocumentException(e);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            throw new DocumentException(e);
        } catch (InstantiationException e) {
            e.printStackTrace();
            throw new DocumentException(e);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new DocumentException(e);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            throw new DocumentException(e);
        }
    }

    protected Group parseGroup(Element groupElement)
            throws SecurityException, IllegalArgumentException,
                   ClassNotFoundException, SetupException, IncompleteRowException,
                   AddDataModeViolationException, NoSuchMethodException,
                   InstantiationException, IllegalAccessException,
                   InvocationTargetException {
        String name = groupElement.valueOf("@name");
        Group group = new Group(name);
        for (Iterator<?> it = groupElement.elementIterator(); it.hasNext(); ) {
            Element element = (Element) it.next();
            if (element.getName().toLowerCase().equals("description"))
                setDescription(group, element);
            else if (element.getName().toLowerCase().equals("group"))
                group.addGroup(parseGroup(element));
            else if (element.getName().toLowerCase().equals("dataset"))
                group.addDataSet(parseDataSet(element));
        }
        return group;
    }

    protected DataSet parseDataSet(Element dataSetElement)
            throws ClassNotFoundException, SetupException, IncompleteRowException,
                   SecurityException, IllegalArgumentException,
                   AddDataModeViolationException, NoSuchMethodException,
                   InstantiationException, IllegalAccessException,
                   InvocationTargetException {
        String name = dataSetElement.valueOf("@name");
        DataSet dataSet = new DataSet(name);
        Element dhElement = (Element) dataSetElement.selectSingleNode("dh");
        for (ColumnHeader header : parseDataHeader(dhElement))
            dataSet.addColumnHeader(header.getName(), header.getType());
        for (Iterator<?> it = dataSetElement.elementIterator("dr"); it.hasNext(); ) {
            parseDataRow(dataSet, (Element) it.next());
        }
        return dataSet;
    }

    protected void parseDataRow(DataSet dataSet, Element dataRowElement)
            throws IncompleteRowException, AddDataModeViolationException,
                   SecurityException, NoSuchMethodException,
                   IllegalArgumentException, InstantiationException,
                   IllegalAccessException, InvocationTargetException {
        dataSet.startNewRow();
        int index = 0;
        for (Iterator<?> it = dataRowElement.elementIterator(); it.hasNext(); ) {
            Element dvElement = (Element) it.next();
            String dataValueStr = dvElement.getText();
            String columnName = dataSet.getColumnName(index++);
            Class<?> type = dataSet.getColumnType(columnName);
            if (type.equals(Integer.class))
                dataSet.addData(Integer.valueOf(dataValueStr));
            else if (type.equals(Double.class))
                dataSet.addData(Double.valueOf(dataValueStr));
            else if (type.equals(Float.class))
                dataSet.addData(Float.valueOf(dataValueStr));
            else if (type.equals(Long.class))
                dataSet.addData(Long.valueOf(dataValueStr));
            else if (type.equals(Boolean.class))
                dataSet.addData(Boolean.valueOf(dataValueStr));
            else if (type.equals(String.class))
                dataSet.addData(dataValueStr);
            else {
                Constructor<?> constructor = type.getConstructor(String.class);
                dataSet.addData(constructor.newInstance(dataValueStr));
            }
        }
    }

    protected List<ColumnHeader> parseDataHeader(Element dhElement)
            throws ClassNotFoundException {
        List<ColumnHeader> headers = new LinkedList<ColumnHeader>();
        int index = 0;
        for (Iterator<?> it = dhElement.elementIterator(); it.hasNext(); )
            headers.add(parseColumnHeader((Element) it.next(), index++));
        return headers;
    }

    protected ColumnHeader parseColumnHeader(Element chElement, int index)
            throws ClassNotFoundException {
        String name = chElement.valueOf("@name");
        Class<?> type = Class.forName(chElement.valueOf("@type"));
        return new ColumnHeader(name, type, index);
    }

    protected void setDescription(AbstractDataItem data,
                                  Element descriptionElement) {
        data.setDescription(descriptionElement.getText());
    }

    public void close() throws IOException {
        this.reader.close();
    }

}
