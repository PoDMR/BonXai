package eu.fox7.jedit.textelement;

import java.awt.Color;
import java.util.Collection;
import java.util.LinkedList;

import org.gjt.sp.jedit.buffer.JEditBuffer;

import eu.fox7.jedit.HighlightManager;
import eu.fox7.jedit.JEditBonxaiManager;
import eu.fox7.jedit.Location;
import eu.fox7.schematoolkit.common.Locatable;
import eu.fox7.schematoolkit.common.Position;

public abstract class AbstractXMLElement extends AbstractTextElement {
	private static final int MAXLENGTH = 100;

	public AbstractXMLElement(JEditBuffer buffer, Locatable xmlElement) {
		super(buffer);
		Location openingTag = searchTag(xmlElement.getStartPosition());
		Location closingTag = searchTag(xmlElement.getEndPosition());
		Collection<Location> locations = new LinkedList<Location>();
		locations.add(openingTag);
		if (openingTag.getEndOffset()<=closingTag.getStartOffset())
			locations.add(closingTag);
		this.init(openingTag, locations);
	}

	private Location searchTag(Position position) {
		int pos = getPosition(position.row, position.column);
		int opening = 0;
		int closing = 0;
		int length = Math.min(MAXLENGTH, pos);
		String snippet = buffer.getText(pos - length, length);
//		System.err.println("Snippet: " + snippet);
		int i=length-1;
		while (i>=0) {
			char c = snippet.charAt(i);
			if ((closing == 0) && (c=='>')) 
				closing=pos-length+i+1;
			if ((closing !=0) && (c=='<')) {
				opening=pos-length+i;
				break;
			}
			--i;
		}
		
		Location location = null;
		
		if (closing != 0) {
			location = new Location(buffer.createPosition(opening), buffer.createPosition(closing-1));
		}
		
		return location;
	}
	
	private int getPosition(int line, int column) {
		int line_offset = buffer.getLineStartOffset(Math.max( line - 1, 0 ) );
		int[] totalVirtualWidth = new int[ 1 ];
		int column_offset = buffer.getOffsetOfVirtualColumn(
				Math.max( line - 1, 0 ),
				Math.max( column - 1, 0 ),
				totalVirtualWidth );
		if ( column_offset == -1 ) {
		column_offset = totalVirtualWidth[ 0 ];
		}
		return  line_offset + column_offset;
	}

	@Override
	public void fireAction(JEditBonxaiManager bonxaiManager, HighlightManager highlightManager) {
		highlightManager.removeHighlight(1);
		highlightManager.addHighlight(this, Color.cyan, 1);
	}

}
