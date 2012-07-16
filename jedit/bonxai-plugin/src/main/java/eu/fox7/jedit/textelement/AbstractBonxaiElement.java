package eu.fox7.jedit.textelement;

import java.util.Collection;
import java.util.LinkedList;

import javax.swing.text.Position;

import org.gjt.sp.jedit.buffer.JEditBuffer;

import eu.fox7.jedit.Location;
import eu.fox7.schematoolkit.bonxai.om.BonxaiLocation;

public abstract class AbstractBonxaiElement extends AbstractTextElement {
	
	public AbstractBonxaiElement(JEditBuffer buffer, BonxaiLocation bLocation) {
		super(buffer);
		int line = bLocation.startRow-1;
		int column = bLocation.startColumn-1;
		int offset = buffer.getLineStartOffset(line) + column;
		Position start = buffer.createPosition(offset);

		line = bLocation.endRow-1;
		column = bLocation.endColumn;
		offset = buffer.getLineStartOffset(line) + column;
		Position end = buffer.createPosition(offset-1);

		Location location = new Location(start, end);
		Collection<Location> locations = new LinkedList<Location>();
		locations.add(location);
		this.init(location, locations);
	}
}
