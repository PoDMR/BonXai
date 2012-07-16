package eu.fox7.jedit.textelement;

import java.util.Collection;

import org.gjt.sp.jedit.buffer.JEditBuffer;

import eu.fox7.jedit.Location;

public interface TextElement {
	public JEditBuffer getBuffer();
	public Location getClickLocation();
	public void fireAction();
	public Collection<Location> getHighlightLocations();
	public String getTooltip();
}
