package eu.fox7.jedit;

import java.awt.Color;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import org.gjt.sp.jedit.buffer.JEditBuffer;
import org.gjt.sp.jedit.textarea.TextArea;

import eu.fox7.jedit.textelement.Linktype;
import eu.fox7.jedit.textelement.TextElement;

public class HighlightManager {
	private Map<JEditBuffer,Collection<Highlight>> activeHighlights = new HashMap<JEditBuffer,Collection<Highlight>>();
	private Map<Linktype,Map<TextElement,Collection<TextElement>>> links = new HashMap<Linktype,Map<TextElement,Collection<TextElement>>>();
	private Collection<TextArea> textAreas = new Vector<TextArea>();
	private Map<JEditBuffer,Collection<TextElement>> clickPositions = new HashMap<JEditBuffer,Collection<TextElement>>();
//	private BufferListener bufferListener = new BufferListener();

	public TextElement getClickTarget(JEditBuffer buffer, int offset) {
		TextElement element = null;
		Collection<TextElement> candidates = clickPositions.get(buffer);
		if (candidates!=null)
			for (TextElement candidate: candidates) {
				Location location = candidate.getClickLocation();
				if (location.getStartOffset()<=offset && location.getEndOffset()>=offset)
					if ((element == null) ||
							((element.getClickLocation().getEndOffset() - element.getClickLocation().getStartOffset()) > (location.getEndOffset() - location.getStartOffset()))) {
						element = candidate;
					}
			}
		return element;
	}
	
//	private class BufferListener extends BufferAdapter {
//		@Override
//		public void preContentInserted(JEditBuffer buffer, int startLine,
//				int offset, int numLines, int length) {
//			System.err.println("PreContentInserted: " + startLine +","+offset+" lines: " + numLines +" length: "+length);
//			Vector<Vector<Highlight>> bufferHighlights = HighlightManager.this.activeHighlights.get(buffer);
//			if (bufferHighlights!=null && bufferHighlights.size()>startLine) {
//				Vector<Highlight> lineHighlights = bufferHighlights.get(startLine);
//				//TODO handle highlights in this line
//				for (int i=0; i<numLines; ++i)
//					bufferHighlights.add(startLine+1, null);
//			}
//			
//		}
//
//		@Override
//		public void preContentRemoved(JEditBuffer buffer, int startLine,
//				int offset, int numLines, int length) {
//			System.err.println("PreContentRemoved: " + startLine +","+offset+" lines: " + numLines +" length: "+length);
////			Vector<Vector<Highlight>> bufferHighlights = HighlightManager.this.activeHighlights.get(buffer);
////			if (bufferHighlights.size()>startLine) {
////				Vector<Highlight> lineHighlights = bufferHighlights.get(startLine);
////				//TODO handle highlights in this line
////				for (int i=0; i<numLines; ++i)
////					bufferHighlights.remove(startLine+1, null);
////			}
//		}
//		
//	}

	public void addBuffer(JEditBuffer buffer) {
//		buffer.addBufferListener(this.bufferListener);
	}

	public void addTextArea(TextArea textArea) {
		textAreas.add(textArea);
	}

	public void removeTextArea(TextArea textArea) {
		textAreas.remove(textArea);
	}

	public void addTextElement(TextElement element) {
		Collection<TextElement> elements = this.clickPositions.get(element.getBuffer());
		if (elements == null) {
			elements = new LinkedList<TextElement>();
			this.clickPositions.put(element.getBuffer(), elements);
		}
		elements.add(element);
	}

	public Collection<Highlight> getHighlights(JEditBuffer buffer, int start, int end) {
		Collection<Highlight> bufferHighlights = activeHighlights.get(buffer);
		Collection<Highlight> result = new Vector<Highlight>(5);
		if (bufferHighlights!=null)
			for (Highlight highlight: bufferHighlights)
				if ((highlight.getStart()<=end) && (highlight.getEnd()>=start))
					result.add(highlight);
		
		return result;
	}

	public void removeHighlight(JEditBuffer buffer) {
		this.activeHighlights.remove(buffer);
		for (TextArea textArea: textAreas ) {
			JEditBuffer textAreaBuffer = textArea.getBuffer();
			if (buffer == textAreaBuffer)
				textArea.invalidateScreenLineRange(0, textArea.getLastScreenLine());
		}
	}

	public void removeHighlight(int key) {
		for (Entry<JEditBuffer, Collection<Highlight>> entry: this.activeHighlights.entrySet()) {
			JEditBuffer buffer = entry.getKey();
			Collection<Highlight> bufferHighlights = entry.getValue();
			for (Iterator<Highlight> it = bufferHighlights.iterator(); it.hasNext();) {
				Highlight highlight = it.next();
				if (highlight.getKey() == key) {
					for (TextArea textArea: textAreas ) {
						JEditBuffer textAreaBuffer = textArea.getBuffer();
						if (buffer == textAreaBuffer)
							textArea.invalidateScreenLineRange(0, textArea.getLastScreenLine());
					}
					it.remove();
				}
			}
		}
	}

	public void addHighlight(TextElement textElement, Color color, int key) {
		Collection<Highlight> bufferHighlights = activeHighlights.get(textElement.getBuffer());
		if (bufferHighlights == null) {
			bufferHighlights = new Vector<Highlight>();
			activeHighlights.put(textElement.getBuffer(), bufferHighlights);
		}

		for (Location location: textElement.getHighlightLocations()) {
			Highlight highlight = new Highlight(location.getStart(), location.getEnd(), color, key);
			bufferHighlights.add(highlight);
		}
		this.redraw(textElement);
	}

	public void highlightLinks(TextElement textElement, Linktype linktype, Color color, int key) {
		Collection<TextElement> textElements = this.getLinks(textElement, linktype);
		if (textElements != null)
			for (TextElement te: textElements)
				this.addHighlight(te, color, key);
	}
	
	public void highlightAllLinks(TextElement textElement, Color color, int key) {
		Collection<TextElement> textElements = new LinkedList<TextElement>();
		for (Map<TextElement, Collection<TextElement>> links2: links.values()) {
			Collection<TextElement> tes = links2.get(textElement);
			if (tes!=null)
				textElements.addAll(tes);
		}
		
		for (TextElement te: textElements)
			this.addHighlight(te, color, key);
	}

	public void addLink(TextElement source, TextElement target, Linktype type) {
		Map<TextElement, Collection<TextElement>> links = this.links.get(type);
		if (links == null) {
			links = new HashMap<TextElement, Collection<TextElement>>();
			this.links.put(type, links);
		}
		Collection<TextElement> targets = links.get(source);
		if (targets==null) {
			targets = new HashSet<TextElement>();
			links.put(source, targets);
		}
		targets.add(target);
	}

	public Collection<TextElement> getLinks(TextElement textElement, Linktype linktype) {
		Map<TextElement,Collection<TextElement>> links2 = this.links.get(linktype);
		return (links2==null)?new LinkedList<TextElement>():links2.get(textElement);
	}

	private void redraw(TextElement textElement) {
		JEditBuffer buffer = textElement.getBuffer();
		for (TextArea textArea: textAreas ) {
			JEditBuffer textAreaBuffer = textArea.getBuffer();
			if (buffer == textAreaBuffer)
				textArea.invalidateScreenLineRange(0, textArea.getLastScreenLine());
		}
		
	}

	public void removeBuffer(JEditBuffer buffer) {
//		buffer.removeBufferListener(this.bufferListener);
		this.removeHighlight(buffer);
		this.clickPositions.remove(buffer);
		
		for (Map<TextElement, Collection<TextElement>> map: this.links.values()) {
			for (Iterator<TextElement> it = map.keySet().iterator(); it.hasNext(); )
				if (it.next().getBuffer()==buffer)
					it.remove();
			for (Collection<TextElement> textElements: map.values())
				for (Iterator<TextElement> it = textElements.iterator(); it.hasNext();)
					if (it.next().getBuffer()==buffer)
						it.remove();
		}
	}

	public String getTooltip(JEditBuffer buffer, int offset) {
		TextElement textElement = this.getClickTarget(buffer, offset);
		return (textElement==null)?null:textElement.getTooltip();
	}

	public Map<JEditBuffer, Collection<Highlight>> getHighlights() {
		return this.activeHighlights;
	}

	public Map<Linktype, Map<TextElement, Collection<TextElement>>> getLinks() {
		return this.links;
	}
}
