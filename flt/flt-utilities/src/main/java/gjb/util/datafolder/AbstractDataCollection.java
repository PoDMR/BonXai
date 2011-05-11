/*
 * Created on Aug 5, 2008
 * Modified on $Date: 2009-11-04 15:27:36 $
 */
package gjb.util.datafolder;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class AbstractDataCollection extends AbstractDataItem {

    protected List<DataItem> dataItems = new LinkedList<DataItem>();
    protected Map<String,Group> groups = new HashMap<String,Group>();
    protected Map<String,DataSet> dataSets = new HashMap<String,DataSet>();

    AbstractDataCollection() {
        super();
    }

    public Group addGroup(String name) {
        Group group = new Group(name);
        addGroup(group);
        return group;
    }

    public void addGroup(Group group) {
        dataItems.add(group);
        groups.put(group.getName(), group);
    }

    public DataSet addDataSet(String name) {
        DataSet dataSet = new DataSet(name);
        addDataSet(dataSet);
        return dataSet;
    }

    public void addDataSet(DataSet dataSet) {
        dataItems.add(dataSet);
        dataSets.put(dataSet.getName(), dataSet);
    }

    public Iterator<DataItem> getItemIterator() {
        return dataItems.iterator();
    }

    public Set<String> getGroupNames() {
        return Collections.unmodifiableSet(groups.keySet());
    }

    public Group getGroup(String groupName) {
        return groups.get(groupName);
    }

    public Set<String> getDataSetNames() {
        return Collections.unmodifiableSet(dataSets.keySet());
    }

    public DataSet getDataSet(String dataSetName) {
        return dataSets.get(dataSetName);
    }

}