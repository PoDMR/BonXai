/*
 * Created on Jun 7, 2005
 * Modified on $Date: 2009-11-04 15:27:36 $
 */
package eu.fox7.util.datafolder;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * @author eu.fox7
 * @version $Revision: 1.1 $
 * 
 */
public class DataSet extends AbstractDataItem implements DataItem {

    protected LinkedHashMap<String,ColumnHeader> columnHeaders = new LinkedHashMap<String,ColumnHeader>();
    protected List<Object[]> dataRows = new LinkedList<Object[]>();
    protected Object[] currentRow;
    protected int currentIndex = 0;
    protected boolean setupDone = false;
    protected boolean isAddMode = true;

    protected DataSet(String name) {
        this.name = name;
    }

    public int getNumberOfColumns() {
        return columnHeaders.size();
    }

    public Iterator<String> getColumnNames() {
        return columnHeaders.keySet().iterator();
    }

    public String getColumnName(int index) {
        int i = 0;
        for (Iterator<String> it = columnHeaders.keySet().iterator(); it.hasNext();) {
            String columnName = it.next();
            if (i == index) {
                return columnName;
            }
            i++;
        }
        return null;
    }

    public int getColumnIndex(String columnName) {
        int index = 0;
        for (Iterator<String> it = columnHeaders.keySet().iterator(); it.hasNext();) {
            if (it.next().equals(columnName)) {
                return index;
            }
            index++;
        }
        return -1;
    }

    public Class<?> getColumnType(String name) {
        ColumnHeader header = columnHeaders.get(name);
        return header != null ? header.getType() : null;
    }

    public int getNumberOfRows() {
        return dataRows.size();
    }

    public Iterator<Object[]> getObjectRows() {
        return dataRows.iterator();
    }

    public Iterator<DataRow> getDataRows() {
        return new DataRowIterator();
    }

    public DataSet addColumnHeader(String headerName, Class<?> type)
            throws SetupException {
        if (!setupDone) {
            columnHeaders.put(headerName,
                              new ColumnHeader(headerName, type, currentIndex++));
        } else {
            throw new SetupException(headerName);
        }
        return this;
    }

    public DataSet addColumnHeader(String headerName, String typeStr)
            throws SetupException, ClassNotFoundException {
        return addColumnHeader(headerName, Class.forName(typeStr));
    }

    public void startNewRow() throws IncompleteRowException {
        if (currentRow != null) {
            for (int i = 0; i < currentRow.length; i++) {
                if (currentRow[i] == null) {
                    throw new IncompleteRowException();
                }
            }
        }
        setupDone = true;
        isAddMode = true;
        currentRow = new Object[getNumberOfColumns()];
        currentIndex = 0;
        dataRows.add(currentRow);
    }

    public DataSet addData(Object o) throws AddDataModeViolationException {
        if (isAddMode) {
            if (currentIndex >= getNumberOfColumns()) {
                try {
                    startNewRow();
                } catch (IncompleteRowException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
            currentRow[currentIndex++] = o;
        } else {
            throw new AddDataModeViolationException();
        }
        return this;
    }

    public DataSet addData(int i) throws AddDataModeViolationException {
        return addData(new Integer(i));
    }

    public DataSet addData(long l) throws AddDataModeViolationException {
        return addData(new Long(l));
    }

    public DataSet addData(float f) throws AddDataModeViolationException {
        return addData(new Float(f));
    }

    public DataSet addData(double d) throws AddDataModeViolationException {
        return addData(new Double(d));
    }

    public DataSet addData(String columnName, Object o) {
        isAddMode = false;
        int index = columnHeaders.get(columnName).getIndex();
        currentRow[index] = o;
        return this;
    }

    public DataSet addData(String columnName, int i) {
        return addData(columnName, new Integer(i));
    }

    public DataSet addData(String columnName, long l) {
        return addData(columnName, new Long(l));
    }

    public DataSet addData(String columnName, float f) {
        return addData(columnName, new Float(f));
    }

    public DataSet addData(String columnName, double d) {
        return addData(columnName, new Double(d));
    }

    protected static String typeToString(Class<?> type) {
        return type.getName();
    }

    public class DataRowIterator implements Iterator<DataRow> {

        protected Iterator<Object[]> objectIterator = getObjectRows();

        public boolean hasNext() {
            return objectIterator.hasNext();
        }

        public DataRow next() {
            return new DataRow(objectIterator.next());
        }

        public void remove() {}
        
    }

    public class DataRow {

        protected Object[] objects;

        DataRow(Object[] objects) {
            super();
            this.objects = objects;
        }

        public int getInt(String name) {
            return getInt(getColumnIndex(name));
        }

        public int getInt(int index) {
            return ((Integer) objects[index]).intValue();
        }

        public long getLong(String name) {
            return getLong(getColumnIndex(name));
        }

        public long getLong(int index) {
            return ((Long) objects[index]).longValue();
        }

        public float getFloat(String name) {
            return getFloat(getColumnIndex(name));
        }

        public float getFloat(int index) {
            return ((Float) objects[index]).floatValue();
        }

        public double getDouble(String name) {
            return getDouble(getColumnIndex(name));
        }

        public double getDouble(int index) {
            return ((Double) objects[index]).doubleValue();
        }

        public boolean getBoolean(String name) {
            return getBoolean(getColumnIndex(name));
        }

        public boolean getBoolean(int index) {
            return ((Boolean) objects[index]).booleanValue();
        }

        public String getString(String name) {
            return getString(getColumnIndex(name));
        }

        public String getString(int index) {
            return (String) objects[index];
        }

        public Object getObject(String name) {
            return getObject(getColumnIndex(name));
        }

        public Object getObject(int index) {
            return objects[index];
        }

    }

    protected static class ColumnHeader {

        protected String name;
        protected Class<?> type;
        protected int index;

        protected ColumnHeader(String name, Class<?> type, int index) {
            this.name = name;
            this.type = type;
            this.index = index;
        }

        /**
         * @return Returns the name.
         */
        public String getName() {
            return name;
        }

        /**
         * @return Returns the type.
         */
        public Class<?> getType() {
            return type;
        }

        public int getIndex() {
            return index;
        }

    }

}
