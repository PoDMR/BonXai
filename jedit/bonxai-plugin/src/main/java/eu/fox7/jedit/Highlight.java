package eu.fox7.jedit;

import java.awt.Color;

import javax.swing.text.Position;

import org.gjt.sp.jedit.buffer.JEditBuffer;

public class Highlight {
	public static final int LINK = 1;
	public static final int XSDTYPE = 2;
	public static final int XMLERROR = 3;
	private Position start, end;
	private int key;
	private Color color;
//	private JEditBuffer buffer;
	
	public Highlight(Position start, Position end, Color color, int key) {
		this.start = start;
		this.end = end;
		this.color = color;
		this.key = key;
	}
	
	public int getStart() {
		return start.getOffset();
	}
	
	public int getEnd() {
		return end.getOffset()+1;
	}
	
	public Color getColor() {
		return color;
	}
	
	public int getKey() {
		return key;
	}

//	public JEditBuffer getBuffer() {
//		return buffer;
//	}

}
