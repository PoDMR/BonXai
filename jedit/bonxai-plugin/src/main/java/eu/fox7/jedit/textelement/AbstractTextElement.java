package eu.fox7.jedit.textelement;

import java.awt.Color;
import java.util.Collection;

import org.gjt.sp.jedit.buffer.JEditBuffer;

import eu.fox7.jedit.BonXaiPlugin;
import eu.fox7.jedit.Highlight;
import eu.fox7.jedit.HighlightManager;
import eu.fox7.jedit.JEditBonxaiManager;
import eu.fox7.jedit.Location;

public abstract class AbstractTextElement implements TextElement {
	private Location clickLocation;
	private Collection<Location> highlightLocations;
	private String tooltip = null;
	protected JEditBuffer buffer;
	
	public AbstractTextElement(JEditBuffer buffer, Location clickLocation, Collection<Location> highlightLocations) {
		this.clickLocation = clickLocation;
		this.highlightLocations = highlightLocations;
		this.buffer = buffer;
	}
	
	protected AbstractTextElement(JEditBuffer buffer) {
		this.buffer = buffer;
	}
	
	protected void init(Location clickLocation, Collection<Location> highlightLocations) {
		this.clickLocation = clickLocation;
		this.highlightLocations = highlightLocations;
	}
	
	@Override
	public Location getClickLocation() {
		return clickLocation;
	}
	
	@Override
	public JEditBuffer getBuffer() {
		return buffer;
	}

	@Override
	public void fireAction() {
		fireAction(BonXaiPlugin.getBonxaiManager(), BonXaiPlugin.getHighlightManager());
	}

	protected void fireAction(JEditBonxaiManager bonxaiManager, HighlightManager highlightManager) {
		highlightManager.removeHighlight(Highlight.LINK);
		highlightManager.addHighlight(this, Color.CYAN, Highlight.LINK);
		highlightManager.highlightAllLinks(this, null, Highlight.LINK);
	}

	@Override
	public Collection<Location> getHighlightLocations() {
		return highlightLocations;
	}
	
	@Override
	public String getTooltip() {
		return tooltip;
	}

	public void setTooltip(String tooltip) {
		this.tooltip = tooltip;
	}
}
