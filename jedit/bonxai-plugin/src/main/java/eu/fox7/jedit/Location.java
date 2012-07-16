package eu.fox7.jedit;

import javax.swing.text.Position;


public class Location {
	public Position start, end;
	
	public Location(Position start, Position end) {
		this.start = start;
		this.end = end;
	}

	public Position getStart() {
		return start;
	}
	
	public Position getEnd() {
		return end;
	}
	
	public int getStartOffset() {
		return this.start.getOffset();
	}
	
	public int getEndOffset() {
		return this.end.getOffset()+1;
	}
}
