package eu.fox7.learning;

import java.io.File;
import java.io.IOException;

public interface Learner {
	public void addXML(File...file) throws IOException;
	public void clear();
}
